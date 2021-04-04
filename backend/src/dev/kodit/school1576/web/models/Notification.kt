package dev.kodit.school1576.web.models

import com.fasterxml.jackson.annotation.JsonIgnore

enum class NotificationType {
    StageUp,
    WaitingForStageUp,
    Invite,
    Message,
    Deadline,
}

data class Notification(
    val type: NotificationType,
    val message: String,
    val projectName: String,
    val projectId: Long,
    val fromUser: Long? = null,
    @JsonIgnore
    val toUsers: Set<Long> = emptySet(),
)
