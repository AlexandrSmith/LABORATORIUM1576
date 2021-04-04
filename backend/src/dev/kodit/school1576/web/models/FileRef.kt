package dev.kodit.school1576.web.models

import ru.kod_it.lib.krush.annotation.KrushIgnore
import javax.persistence.*

@Entity
@Table(name = "files")
data class FileRef(
    @Id @GeneratedValue
    val id: Long,
    val filename: String,
    @Column(name = "upload_name")
    val uploadName: String,

    val pinned: Boolean
) {
    @KrushIgnore
    val url: String = "/api/files/$filename/$uploadName"
}
