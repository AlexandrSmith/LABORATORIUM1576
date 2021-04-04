package dev.kodit.school1576.web.models.chat

import dev.kodit.school1576.web.models.project.Project
import javax.persistence.*

@Entity
@Table(name = "chats")
data class Chat(
    @GeneratedValue @Id
    val id: Long,

    @OneToOne
    @JoinColumn(name = "project_id")
    val project: Project,
)
