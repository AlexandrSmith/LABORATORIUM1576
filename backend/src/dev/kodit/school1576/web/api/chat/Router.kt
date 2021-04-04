package dev.kodit.school1576.web.api.chat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import dev.kodit.school1576.common.Connection
import dev.kodit.school1576.web.models.NotificationService
import dev.kodit.school1576.web.models.NotificationType
import dev.kodit.school1576.web.models.chat.ChatService
import dev.kodit.school1576.web.models.chat.NewMessageDto
import dev.kodit.school1576.web.models.users.UserService
import io.ktor.application.Application
import io.ktor.application.log
import io.ktor.auth.authenticate
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.send
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.websocket.webSocket
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import ru.kod_it.lib.ktor_auth.user
import java.time.LocalDateTime
import java.util.Collections

@FlowPreview
@ExperimentalCoroutinesApi
fun Application.chatRouter() {
    val mapper = ObjectMapper().registerModules(KotlinModule(), JavaTimeModule())
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    val chatConnections = Collections.synchronizedSet<Connection>(LinkedHashSet())
    // val notifyConnections = ConcurrentHashMap<Int, Connection>()

    routing {
        authenticate {
            route("ws") {
                webSocket("/notifications") {
                    send(mapper.writeValueAsString(ChatService.getNotificationHistory(call.user.id)))
                    val notifyChannel = NotificationService.subscribe(call.user.id)
                    notifyChannel.collect {
                        send(withContext(Dispatchers.IO) { mapper.writeValueAsString(it) })
                    }
                }

                webSocket("/chat/{id}") {
                    val projectId = call.parameters["id"]?.toLong()
                    val userAccess = ChatService.userHasAccess(projectId, this.call.user.id)
                    if (projectId == null || !userAccess) {
                        this.close(reason = CloseReason(CloseReason.Codes.GOING_AWAY, "404"))
                    } else {
                        val thisConnection = Connection(this, projectId)
                        chatConnections.add(thisConnection)

                        val notificationFlow = NotificationService.subscribe(call.user.id)

                        withContext(Dispatchers.IO) {
                            try {
                                send(
                                    withContext(Dispatchers.IO) {
                                        mapper.writeValueAsString(ChatService.getChatHistory(projectId))
                                    }
                                )

                                val notificationJob = async {
                                    notificationFlow.collect { event ->
                                        if (event.fromUser != null) ChatService.addNotification(
                                            projectId,
                                            event.fromUser,
                                            event.type
                                        )

                                        if (event.type != NotificationType.Message) {
                                            val createdMessage = ChatService.newMessage(
                                                NewMessageDto(
                                                    LocalDateTime.now(),
                                                    event.message
                                                ),
                                                projectId,
                                                UserService.getNotificationUserId()
                                            )
                                            chatConnections.asSequence().filter { it.projectId == projectId }
                                                .forEach {
                                                    it.session.send(mapper.writeValueAsString(createdMessage))
                                                }
                                        }
                                    }
                                }

                                val incomingJob = async {
                                    for (event in incoming) {
                                        val message = withContext(Dispatchers.IO) {
                                            mapper.readValue<NewMessageDto>(
                                                event.data
                                            )
                                        }
                                        if (message.text == "release-notifications") {
                                            ChatService.releaseNotifications(projectId, call.user.id)
                                        } else {
                                            ChatService.addNotification(
                                                projectId,
                                                call.user.id,
                                                NotificationType.Message
                                            )
                                            val createdMessage = ChatService.newMessage(
                                                message,
                                                projectId,
                                                call.user.id
                                            )
                                            NotificationService.sendMessage(
                                                projectId,
                                                createdMessage.text,
                                                call.user.id
                                            )
                                            chatConnections.asSequence().filter { it.projectId == projectId }
                                                .forEach {
                                                    it.session.send(mapper.writeValueAsString(createdMessage))
                                                }
                                        }
                                    }
                                }

                                incomingJob.invokeOnCompletion {
                                    if (notificationJob.isActive)
                                        notificationJob.cancel("Closing notificationJob because incomingJob is closed")
                                }
                                notificationJob.invokeOnCompletion {
                                    if (incomingJob.isActive)
                                        incomingJob.cancel("Closing incomingJob because notificationJob is closed")
                                }

                                awaitAll(notificationJob, incomingJob)
                            } catch (e: CancellationException) {
                                log.debug("CancellationException ${e.message}")
                            } catch (e: Exception) {
                                log.error("Exception", e)
                            } finally {
                                chatConnections -= thisConnection
                            }
                        }
                    }
                }
            }
        }
    }
}
