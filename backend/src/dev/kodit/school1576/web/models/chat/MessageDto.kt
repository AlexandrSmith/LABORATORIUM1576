package dev.kodit.school1576.web.models.chat

import dev.kodit.school1576.web.models.users.UserTable
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime

data class MessageDto(
    val id: Long?,
    // val projectId: Long,
    val userId: Long,
    val fullName: String,
    val dateTime: LocalDateTime,
    val text: String,
    // val file: FileRef?
)

data class NewMessageDto(
    val dateTime: LocalDateTime?,
    val text: String,
    // val file: FileRef?
)

fun toMessageDto(resultRow: ResultRow): MessageDto = MessageDto(
    id = resultRow[MessageTable.id].value,
    dateTime = resultRow[MessageTable.dateTime],
    text = resultRow[MessageTable.text],
    userId = resultRow[UserTable.id].value,
    // projectId = resultRow[MessageTable.project],
    fullName = listOfNotNull(
        resultRow[UserTable.surname],
        resultRow[UserTable.name].firstOrNull(),
        resultRow[UserTable.patronymic]?.firstOrNull()
    ).joinToString(" "),
    // file = resultRow[MessageTable.file]
)
