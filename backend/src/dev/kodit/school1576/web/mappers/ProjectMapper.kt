package dev.kodit.school1576.web.mappers

import dev.kodit.school1576.web.models.FileRef
import dev.kodit.school1576.web.models.project.*
import dev.kodit.school1576.web.models.users.User
import dev.kodit.school1576.web.models.users.UserShortTableDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mapstruct.*
import org.mapstruct.factory.Mappers
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Mapper(uses = [SubjectAreaMapper::class])
interface ProjectMapper {

    @Mappings(
        Mapping(
            target = "resultFiles",
            expression = "java(fileRefListToFileInfoList(project.getFiles(), project, null))"
        ),
        Mapping(
            target = "subjectAreas",
            expression = "java(subjectAreasList(project))"
        )
    )
    fun toProjectDto(project: Project, members: List<MemberDto>, tasks: List<TaskDto>): ProjectDto

    fun toProjectInfoDto(project: Project): ProjectInfoDto

    fun toTaskDto(task: Task): TaskDto

    @Mappings(Mapping(target = "id", source = "user"))
    fun toMemberDto(member: Member): MemberDto

    @JvmDefault
    fun fileRefToFileInfo(file: FileRef, project: Project, task: Task? = null): FileInfo {
        return FileInfo(
            projectId = project.id,
            taskId = task?.id,
            fileId = file.id,
            filename = file.uploadName,
            url = file.url,
            pinned = file.pinned
        )
    }

    @JvmDefault
    fun fileRefListToFileInfoList(file: List<FileRef>, project: Project, task: Task? = null): List<FileInfo> {
        return file.map { fileRefToFileInfo(it, project, task) }
    }

    @JvmDefault
    fun subjectAreasList(project: Project) = transaction {
        ProjectSubjectAreasTable
            .innerJoin(SubjectAreaTable)
            .innerJoin(ProjectTable)
            .select { ProjectSubjectAreasTable.project_id eq project.id }
            .toSubjectAreaList()
            .map(Mappers.getMapper(SubjectAreaMapper::class.java)::toSubjectAreaDto)
    }

    @JvmDefault
    fun userToId(user: User): Long = user.id

    @Mappings(
        Mapping(target = "student", source = "learner"),
        Mapping(target = "teacher", expression = "java(getTeacher(project))"),
        Mapping(target = "expert", expression = "java(getExpert(project))"),
    )
    fun toProjectReportDto(project: ProjectDto): ProjectReportDto

    @JvmDefault
    fun subjectAreasListToString(subjectAreas: List<SubjectAreaDto>) = subjectAreas.joinToString(", ") { it.title }
    @JvmDefault
    fun studentToString(student: UserShortTableDto): String = student.fullName
    @JvmDefault
    fun getTeacher(project: ProjectDto): String = project.members.find { it.type == MemberType.Teacher }?.fullName ?: ""
    @JvmDefault
    fun getExpert(project: ProjectDto): String {
        return project.members.filter { it.type == MemberType.Expert }.joinToString(", ") { it.fullName }
    }
    @JvmDefault
    fun formatDate(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}
