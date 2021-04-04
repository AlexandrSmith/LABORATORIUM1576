package dev.kodit.school1576.web.models.notifications

import dev.kodit.school1576.web.models.NotificationType
import org.jetbrains.exposed.sql.ResultRow

data class NotificationDto(
    val user: Long,
    val project: Long,
    val type: NotificationType,
    val quantity: Int
)

fun toNotificationDto(resultRow: ResultRow): NotificationDto = NotificationDto(
    user = resultRow[NotificationTable.user],
    project = resultRow[NotificationTable.project],
    type = resultRow[NotificationTable.type],
    quantity = resultRow[NotificationTable.quantity]
)
