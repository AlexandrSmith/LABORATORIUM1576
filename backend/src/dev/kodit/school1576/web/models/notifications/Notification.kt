package dev.kodit.school1576.web.models.notifications

import dev.kodit.school1576.web.models.NotificationType
import dev.kodit.school1576.web.models.project.Project
import dev.kodit.school1576.web.models.users.User
import javax.persistence.*

@Entity
@Table(name = "notifications")
data class Notification(
    @GeneratedValue @Id
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne
    @JoinColumn(name = "projects_id")
    val project: Project,

    @Enumerated(EnumType.STRING)
    val type: NotificationType,
    val quantity: Int
)
