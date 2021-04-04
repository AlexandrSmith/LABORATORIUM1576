package dev.kodit.school1576.web.models.chat

import dev.kodit.school1576.web.mappers.ChatMapper
import dev.kodit.school1576.web.models.NotificationType
import dev.kodit.school1576.web.models.notifications.NotificationTable
import dev.kodit.school1576.web.models.notifications.toNotificationDto
import dev.kodit.school1576.web.models.project.MemberTable
import dev.kodit.school1576.web.models.project.ProjectTable
import dev.kodit.school1576.web.models.users.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mapstruct.factory.Mappers
import java.time.LocalDateTime

object ChatService {
    val mapper = Mappers.getMapper(ChatMapper::class.java)

    fun getChatHistory(id: Long): List<MessageDto> = transaction {

        val projectUser = UserTable.alias("project_user")
        val messageUser = UserTable.alias("message_user")

        MessageTable
            .leftJoin(UserTable)
            .select { MessageTable.project eq id }
            .orderBy(MessageTable.dateTime)
            .map { toMessageDto(it) }
    }

    fun newMessage(message: NewMessageDto, projectId: Long, userId: Long): MessageDto = transaction {
        val messageId = MessageTable.insert {
            it[MessageTable.project] = projectId
            it[MessageTable.user] = userId
            it[MessageTable.dateTime] = LocalDateTime.now()
            it[MessageTable.text] = message.text
        }[MessageTable.id]
        return@transaction MessageTable
            .leftJoin(UserTable)
            .select { MessageTable.id eq messageId }
            .single().let { toMessageDto(it) }
    }

    fun userHasAccess(projectId: Long?, userId: Long) = transaction {
        if (projectId != null) {
            MemberTable.select { MemberTable.project eq projectId and (MemberTable.user eq userId) }.any() ||
                ProjectTable.select { ProjectTable.id eq projectId and (ProjectTable.learner eq userId) }.any()
        } else false
    }

    fun releaseNotifications(projectId: Long, userId: Long) {
        transaction {
            NotificationTable.deleteWhere { NotificationTable.project eq projectId and (NotificationTable.user eq userId) }
        }
    }

    fun addNotification(projectId: Long, fromUserId: Long, notificationType: NotificationType) {
        transaction {
            val users = MemberTable
                .select { MemberTable.project eq projectId and (MemberTable.user neq fromUserId) }
                .map { it[MemberTable.user] }.union(
                    ProjectTable
                        .select { ProjectTable.id eq projectId and (ProjectTable.learner neq fromUserId) }
                        .map { it[ProjectTable.learner] }
                )

            users.forEach { userId ->
                val userProjectNotificationQuantity = NotificationTable
                    .select { NotificationTable.user eq userId and (NotificationTable.project eq projectId) }
                    .singleOrNull()?.let { it[NotificationTable.quantity] }
                if (userProjectNotificationQuantity != null) {
                    NotificationTable
                        .update({ NotificationTable.user eq userId and (NotificationTable.project eq projectId) }) {
                            it[quantity] = userProjectNotificationQuantity + 1
                        }
                } else {
                    NotificationTable.insert {
                        it[user] = userId
                        it[project] = projectId
                        it[type] = notificationType
                        it[quantity] = 1
                    }
                }
            }
        }
    }

    fun getNotificationHistory(userId: Long) = transaction {
        NotificationTable.select { NotificationTable.user eq userId }.map {
            toNotificationDto(
                it
            )
        }
    }
}
