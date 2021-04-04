package dev.kodit.school1576.web.models.chat

import dev.kodit.school1576.web.models.FileRef
import dev.kodit.school1576.web.models.project.Project
import dev.kodit.school1576.web.models.users.User
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "messages")
data class Message(
    @GeneratedValue @Id
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "project_id")
    val project: Project,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(name = "date_time")
    val dateTime: LocalDateTime,

    val text: String,

    @ManyToOne
    @JoinColumn(name = "file_id")
    val file: FileRef?,
)
