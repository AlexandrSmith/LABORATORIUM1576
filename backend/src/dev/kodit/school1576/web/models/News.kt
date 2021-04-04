package dev.kodit.school1576.web.models

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "news")
data class News(
    @GeneratedValue @Id
    val id: Long,

    @Column(name = "date_time")
    val dateTime: LocalDateTime,
    val title: String,
    val author: String,
    @Column(length = 0)
    val text: String,

    @OneToOne
    @JoinColumn(name = "image_id")
    val image: FileRef?
)
