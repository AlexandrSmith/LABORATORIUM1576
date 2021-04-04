package dev.kodit.school1576.web.models

import dev.kodit.school1576.web.models.project.ProjectService
import dev.kodit.school1576.web.models.users.UserService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import java.time.LocalDate

@ExperimentalCoroutinesApi
object NotificationService {
    private val broadcastChannel = BroadcastChannel<Notification>(Channel.BUFFERED)

    fun subscribe(userId: Long): Flow<Notification> {
        return broadcastChannel.openSubscription().receiveAsFlow().filter {
            it.fromUser != userId && (it.toUsers.isEmpty() || it.toUsers.contains(userId))
        }
    }

    suspend fun sendMessage(projectId: Long, message: String, userId: Long) {
        val members = ProjectService.projectMembers(projectId)
        val projectName = ProjectService.projectName(projectId)
        sentNotification(NotificationType.Message, message, projectName, projectId, userId, members)
    }

    suspend fun sendInvite(projectId: Long, usersIds: List<Long>, userId: Long) {
        val inviterFullName = UserService.getUserFullName(userId)
        usersIds.forEach { id ->
            val userFullName = UserService.getUserFullName(id)
            val members = ProjectService.projectMembers(projectId)
            val projectName = ProjectService.projectName(projectId)
            val message = "$inviterFullName пригласил $userFullName присоединиться к проекту"

            sentNotification(NotificationType.Invite, message, projectName, projectId, userId, members)
        }
    }

    suspend fun acceptInvite(projectId: Long, userId: Long, status: Boolean) {
        if (!status) {
            val userFullName = UserService.getUserFullName(userId)
            val members = ProjectService.projectMembers(projectId)
            val projectName = ProjectService.projectName(projectId)
            val message = "$userFullName отклонил(а) приглашение присоединиться к проекту"

            sentNotification(NotificationType.Invite, message, projectName, projectId, userId, members)
        }
    }

    suspend fun sendStageUp(projectId: Long, stage: Int, userId: Long) {
        val fullName = UserService.getUserFullName(userId)
        val projectName = ProjectService.projectName(projectId)
        val stageUpMessage = if (stage == 1) "приглашение присоединиться к проекту" else "этап №${stage - 1}"
        val message = "$fullName принял(а) $stageUpMessage"
        val members = ProjectService.projectMembers(projectId)
        sentNotification(NotificationType.StageUp, message, projectName, projectId, userId, members)
    }

    suspend fun sendDecline(projectId: Long, userId: Long) {
        val fullName = UserService.getUserFullName(userId)
        val projectName = ProjectService.projectName(projectId)
        val message = "$fullName отклонил(а) запрос на проверку этапа"
        val members = ProjectService.projectMembers(projectId)
        sentNotification(NotificationType.StageUp, message, projectName, projectId, userId, members)
    }

    suspend fun sendDeadline(projectId: Long, date: LocalDate, userId: Long) {
        val fullName = UserService.getUserFullName(userId)
        val projectName = ProjectService.projectName(projectId)
        val message = "$fullName изменил(а) сроки выполнения этапа: $date"
        val members = ProjectService.projectMembers(projectId)
        sentNotification(NotificationType.Deadline, message, projectName, projectId, userId, members)
    }

    suspend fun sendLockUnlock(projectId: Long, status: Boolean, userId: Long) {
        val fullName = UserService.getUserFullName(userId)
        val projectName = ProjectService.projectName(projectId)
        val locked = if (status) "заблокировал(а)" else "разблокировал(а)"
        val message = "$fullName $locked проект"
        val members = ProjectService.projectMembers(projectId)
        sentNotification(NotificationType.Deadline, message, projectName, projectId, userId, members)
    }

    suspend fun sendShare(projectId: Long, status: Boolean, userId: Long) {
        val fullName = UserService.getUserFullName(userId)
        val projectName = ProjectService.projectName(projectId)
        val locked = if (status) "опубликовал(а)" else "скрыл(а)"
        val message = "$fullName $locked проект"
        val members = ProjectService.projectMembers(projectId)
        sentNotification(NotificationType.Deadline, message, projectName, projectId, userId, members)
    }

    suspend fun sendWaitingForStageUp(projectId: Long, stage: Int, userId: Long) {
        val fullName = UserService.getUserFullName(userId)
        val message = "$fullName просит проверить этап №$stage"
        val members = ProjectService.projectMembers(projectId)
        val projectName = ProjectService.projectName(projectId)
        sentNotification(NotificationType.WaitingForStageUp, message, projectName, projectId, userId, members)
    }

    suspend fun sentNotification(
        type: NotificationType,
        message: String,
        projectName: String,
        projectId: Long,
        fromUser: Long,
        toUsers: Set<Long>
    ) {
        broadcastChannel.send(
            Notification(
                type,
                message,
                projectName = projectName,
                projectId = projectId,
                fromUser,
                toUsers
            )
        )
    }
}
