package dev.kodit.school1576.web.models.project

import com.papsign.ktor.openapigen.content.type.multipart.FormDataRequest
import com.papsign.ktor.openapigen.content.type.multipart.NamedFileInputStream
import com.papsign.ktor.openapigen.content.type.multipart.PartEncoding
import dev.kodit.school1576.web.models.FileRefTable
import dev.kodit.school1576.web.models.toFileRefList
import dev.kodit.school1576.web.models.users.UserShortTableDto
import dev.kodit.school1576.web.models.users.UserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import javax.persistence.EnumType
import javax.persistence.Enumerated

//  Краткая информация по проекту
data class ProjectInfoDto(
    val id: Long,
    val name: String,
    // Аннотация
    val annotation: String,
    val mark: Int,
    // Предметная область (несколько)
    val subjectAreas: List<SubjectArea>,
    // Учитель (руководитель)
    // val teacher: MemberInfoDto,
    // Эксперты
    val stage: Int,
    val waitingForStageUp: Boolean,
    val createDate: LocalDate,
    val learner: UserShortTableDto,
    val experts: List<MemberDto>?,
)

//  Проект
data class ProjectDto(
    val id: Long,

    val learner: UserShortTableDto,

    val createDate: LocalDate,

    val name: String,
    // Аннотация
    val annotation: String,
    // Предметная область (несколько)
    val subjectAreas: List<SubjectAreaDto>,
    // Учитель и Эксперты
    val members: List<MemberDto>,

    // Тема
    val researchTopic: String,
    // Объект
    val researchObject: String,
    // Предмет
    val researchSubject: String,
    // Цель
    val researchPurpose: String,
    // Гипотеза
    val researchHypothesis: String,
    // Методы
    val researchMethods: String,
    val locked: Boolean,

    val relevance: String,
    val explanatory: String?,
    val problems: String?,
    val product: String?,

    // Задачи
    val tasks: List<TaskDto>,

    val deadline: LocalDate?,
    val shared: Boolean?,
    val permissionToPublish: Boolean?,
    // Результат работы
    val workResult: String,
    // Выводы
    val conclusion: String,
    // Вложения
    val resultFiles: List<FileInfo>,
    // Оценка
    val mark: Int,
    // Этап
    val stage: Int,
    // На проверку
    val waitingForStageUp: Boolean,
)

data class TaskDto(
    val id: Long?,
    val title: String?,
    val projectId: Long?,
    // Задача сделана?
    val isDone: Boolean,
    // Результат
    val result: String?,
    // Вложения
    val files: List<FileInfo>
)

fun Task.toTaskDto(): TaskDto {
    val taskId = this.id
    val files = transaction {
        TaskFilesTable.leftJoin(FileRefTable).select { TaskFilesTable.task_id eq taskId }.toFileRefList()
    }
    return TaskDto(
        id = taskId,
        title = this.title,
        projectId = this.project.id,
        isDone = this.isDone,
        result = this.result,
        files = files.map { file ->
            FileInfo(
                projectId = project.id,
                taskId = this.id,
                fileId = file.id,
                filename = file.uploadName,
                url = file.url,
                pinned = file.pinned
            )
        }
    )
}

data class SubjectAreaDto(
    val id: Long,
    val title: String,
    val description: String
)

data class MemberDto(
    val id: Long,
    @Enumerated(EnumType.ORDINAL)
    val type: MemberType,
    @Enumerated(EnumType.ORDINAL)
    val status: MemberStatus,
    val userId: Long,
    val fullName: String
)

enum class MemberType {
    Teacher,
    Expert
}

enum class MemberStatus {
    Invited,
    Accepted
}

data class ExpertInfoDto(
    val id: Long,

)

data class FileInfo(
    val projectId: Long,
    val taskId: Long?,
    val fileId: Long?,
    val filename: String,
    val pinned: Boolean,
    val url: String,
)

@FormDataRequest
data class FileDto(
    val projectId: Long,
    val taskId: Long?,
    @PartEncoding("*/*")
    val file: NamedFileInputStream,
)

@FormDataRequest
data class NewsDto(
    val id: Long,
    val dateTime: String,
    val title: String,
    val author: String,
    val text: String,

    @PartEncoding("*/*")
    val image: NamedFileInputStream?,
)

fun toMemberDto(resultRow: ResultRow): MemberDto = MemberDto(
    id = resultRow[MemberTable.id].value,
    status = resultRow[MemberTable.status],
    type = resultRow[MemberTable.type],
    userId = resultRow[UserTable.id].value,
    fullName = listOfNotNull(resultRow[UserTable.surname], resultRow[UserTable.name], resultRow[UserTable.patronymic])
        .joinToString(" ")
)

// ============================================================================

data class ProjectReportDto(

    val createDate: String,
    val name: String,
    // Аннотация
    val annotation: String,
    // Предметная область (несколько)
    val subjectAreas: String,

    val student: String,
    val teacher: String,
    val expert: String,

    // Тема
    val researchTopic: String,
    // Объект
    val researchObject: String,
    // Предмет
    val researchSubject: String,
    // Цель
    val researchPurpose: String,
    // Гипотеза
    val researchHypothesis: String,
    // Методы
    val researchMethods: String,

    // Задачи
    val tasks: List<TaskDto>,

    // Результат работы
    val workResult: String,
    // Выводы
    val conclusion: String,
    // Оценка
    val mark: Int,
)
