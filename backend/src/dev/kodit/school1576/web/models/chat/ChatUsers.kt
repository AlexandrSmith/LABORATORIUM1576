package dev.kodit.school1576.web.models.chat

import dev.kodit.school1576.web.models.users.User
import javax.persistence.*

@Entity
@Table(name = "chat_users")
data class ChatUsers(
    @GeneratedValue @Id
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "chat_id")
    val chat: Chat,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,
)
