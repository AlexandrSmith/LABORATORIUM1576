package dev.kodit.school1576.web.models.project

import dev.kodit.school1576.auth.Role
import dev.kodit.school1576.db.ilike
import dev.kodit.school1576.ext.getResourceAsStream
import dev.kodit.school1576.ext.slugify
import dev.kodit.school1576.web.api.projects.ProjectFilter
import dev.kodit.school1576.web.api.projects.SortOrderType
import dev.kodit.school1576.web.mappers.ProjectMapper
import dev.kodit.school1576.web.mappers.SubjectAreaMapper
import dev.kodit.school1576.web.mappers.UserMapper
import dev.kodit.school1576.web.models.DownloadService
import dev.kodit.school1576.web.models.FileRefTable
import dev.kodit.school1576.web.models.FileStorageService
import dev.kodit.school1576.web.models.users.UserDto
import dev.kodit.school1576.web.models.users.UserService
import dev.kodit.school1576.web.models.users.UserSubjectAreasTable
import dev.kodit.school1576.web.models.users.UserTable
import dev.kodit.school1576.web.models.users.toUser
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mapstruct.factory.Mappers
import ru.kod_it.lib.documents.DocumentGeneratorXDocReport
import ru.kod_it.lib.documents.asMap
import ru.kod_it.lib.jsonapi.spec.jsonApiError
import ru.kod_it.lib.ktor_auth.UserBase
import java.io.File
import java.time.LocalDate

object ProjectService {
    private val mapper = Mappers.getMapper(ProjectMapper::class.java)
    private val userMapper = Mappers.getMapper(UserMapper::class.java)
    private val subjectAreaMapper = Mappers.getMapper(SubjectAreaMapper::class.java)

    private fun UpdateBuilder<Any>.from(dto: ProjectDto) {
        this[ProjectTable.name] = dto.name
        this[ProjectTable.annotation] = dto.annotation
        this[ProjectTable.researchTopic] = dto.researchTopic
        this[ProjectTable.researchObject] = dto.researchObject
        this[ProjectTable.researchSubject] = dto.researchSubject
        this[ProjectTable.researchPurpose] = dto.researchPurpose
        this[ProjectTable.researchHypothesis] = dto.researchHypothesis
        this[ProjectTable.researchMethods] = dto.researchMethods
        this[ProjectTable.relevance] = dto.relevance
        this[ProjectTable.explanatory] = dto.explanatory
        this[ProjectTable.problems] = dto.problems
        this[ProjectTable.product] = dto.product
        this[ProjectTable.workResult] = dto.workResult
        this[ProjectTable.conclusion] = dto.conclusion
        // this[ProjectTable.mark] = dto.mark
        // this[ProjectTable.stage] = dto.stage
        this[ProjectTable.learner] = dto.learner.id
    }

    private fun UpdateBuilder<Any>.from(dto: TaskDto, projectId: Long) {
        this[TaskTable.title] = dto.title ?: ""
        this[TaskTable.project] = projectId
        this[TaskTable.isDone] = dto.isDone
        this[TaskTable.result] = dto.result ?: ""
    }

    private fun UpdateBuilder<Any>.from(dto: MemberDto, projectId: Long) {
        this[MemberTable.project] = projectId
        this[MemberTable.status] = dto.status
        this[MemberTable.type] = dto.type
        this[MemberTable.user] = dto.userId
    }

    fun filteredProjectsList(filter: ProjectFilter): List<ProjectInfoDto> = transaction {
        val query = ProjectTable
            .leftJoin(UserTable)
            .leftJoin(ProjectSubjectAreasTable)
            .leftJoin(SubjectAreaTable)
            .selectAll()

        if (filter.shared != null && filter.shared) {
            query.andWhere { ProjectTable.shared eq true }
        }

        if (filter.name != null) {
            query.andWhere {
                ProjectTable.name ilike "%${filter.name}%" or
                    (UserTable.surname ilike "%${filter.name}%") or
                    (UserTable.name ilike "%${filter.name}%") or
                    (UserTable.patronymic ilike "%${filter.name}%") or
                    (SubjectAreaTable.name ilike "%${filter.name}%")
            }
        }
        if (filter.sortOrder != null) {
            when (filter.sortOrder) {
                SortOrderType.ByName -> query.sortedBy { ProjectTable.name }
                SortOrderType.ByDate -> query.sortedBy { ProjectTable.createDate }
                // SortOrderType.BySubjects -> query.sortedBy { ProjectTable.name }
                SortOrderType.ByLearner -> query.sortedBy { UserTable.surname }
                // SortOrderType.ByTeacher -> query.sortedBy { ProjectTable.name }
            }
        }
        query.toProjectList().map(mapper::toProjectInfoDto)
    }

    fun projectsList(user: UserBase): List<ProjectInfoDto> {
        return transaction {
            val userRole = UserTable.select { UserTable.id eq user.id }.single().toUser().role
            if (userRole == Role.Student) {
                return@transaction ProjectTable
                    .leftJoin(UserTable)
                    .select { ProjectTable.learner eq user.id }.toProjectList()
                    .map(mapper::toProjectInfoDto)
            } else if (userRole == Role.Admin) {
                return@transaction ProjectTable
                    .leftJoin(UserTable)
                    .selectAll().toProjectList()
                    .map(mapper::toProjectInfoDto)
            } else {
                val projects = MemberTable
                    .select { MemberTable.user eq user.id }
                    .map { it[MemberTable.project] }
                return@transaction ProjectTable
                    .leftJoin(UserTable)
                    .select { ProjectTable.id inList projects }.toProjectList()
                    .map(mapper::toProjectInfoDto)
            }
        }
    }

    fun createProject(data: ProjectDto): Long {
        return transaction {
            val projectId = ProjectTable.insert {
                it.from(data)
                it[createDate] = LocalDate.now()
                it[stage] = data.stage
                it[mark] = 5
                it[shared] = false
                it[permissionToPublish] = false
            }[ProjectTable.id]
            data.subjectAreas.forEach { subject ->
                ProjectSubjectAreasTable.insert {
                    it[project_id] = projectId.value
                    it[subject_area_id] = subject.id
                }
            }
            // data.tasks.forEach { task ->
            //     TaskTable.insert {
            //         it.from(task, projectId)
            //     }
            // }
            projectId.value
        }
    }

    fun adminProjectList(filter: ProjectFilter, user: UserBase): List<ProjectDto> = transaction {
        val userRole = UserTable.select { UserTable.id eq user.id }.single().toUser().role
        if (userRole == Role.Admin) {
            val query = ProjectTable
                .leftJoin(UserTable)
                .leftJoin(ProjectSubjectAreasTable)
                .leftJoin(SubjectAreaTable)
                .selectAll()
            if (filter.name != null) {
                query.andWhere {
                    ProjectTable.name ilike "%${filter.name}%" or
                        (UserTable.surname ilike "%${filter.name}%") or
                        (UserTable.name ilike "%${filter.name}%") or
                        (UserTable.patronymic ilike "%${filter.name}%") or
                        (SubjectAreaTable.name ilike "%${filter.name}%")
                }
            }
            val projects = query.map { it[ProjectTable.id] }.distinct()
            projects.map { projectById(it.value, user) }
        } else {
            emptyList()
        }
    }

    fun projectById(projectId: Long, user: UserBase): ProjectDto {
        return transaction {
            val userRole = UserTable.select { UserTable.id eq user.id }.single().toUser().role
            val projects = if (userRole != Role.Admin)
                MemberTable
                    .select { MemberTable.user eq user.id }
                    .map { it[MemberTable.project] }
            else ProjectTable.selectAll().map { it[ProjectTable.id].value }

            val project = ProjectTable
                .leftJoin(UserTable)
                .leftJoin(ProjectFilesTable)
                .leftJoin(FileRefTable)
                .select {
                    (
                        (ProjectTable.id eq projectId)
                            and (
                                (ProjectTable.learner eq user.id)
                                    or (ProjectTable.id inList projects)
                                )
                        )
                }
                .toProjectList()
                .singleOrNull() ?: jsonApiError(
                detail = "Проект не найден",
                status = HttpStatusCode.NotFound,
            )
            val tasks = TaskTable.innerJoin(ProjectTable).leftJoin(UserTable).select { TaskTable.project eq projectId }.orderBy(
                TaskTable.id
            ).toTaskList().map { it.toTaskDto() }

            // TODO Разобраться почему mapper не работал с isDone

            val members = MemberTable
                .leftJoin(UserTable)
                .leftJoin(ProjectTable)
                .select { MemberTable.project eq projectId }
                .map { toMemberDto(it) }
            mapper.toProjectDto(project, members, tasks)
        }
    }

    fun mainProjectById(projectId: Long): ProjectDto {
        return transaction {
            val project = ProjectTable
                .leftJoin(UserTable)
                .leftJoin(ProjectFilesTable)
                .leftJoin(FileRefTable)
                .select { (ProjectTable.id eq projectId and (ProjectTable.shared eq true)) }
                .toProjectList()
                .singleOrNull() ?: jsonApiError(
                detail = "Проект не найден",
                status = HttpStatusCode.NotFound,
            )
            val tasks = TaskTable.innerJoin(ProjectTable).leftJoin(UserTable).select { TaskTable.project eq projectId }.orderBy(
                TaskTable.id
            ).toTaskList().map { it.toTaskDto() }

            val members = MemberTable
                .leftJoin(UserTable)
                .leftJoin(ProjectTable)
                .select { MemberTable.project eq projectId }
                .map { toMemberDto(it) }
            mapper.toProjectDto(project, members, tasks)
        }
    }

    fun updateProject(id: Long, data: ProjectDto) {
        transaction {
            ProjectTable.update({ ProjectTable.id eq data.id }) {
                it.from(data)
            }
            ProjectSubjectAreasTable.deleteWhere { ProjectSubjectAreasTable.project_id eq id }
            data.subjectAreas.forEach { subject ->
                ProjectSubjectAreasTable.insert {
                    it[project_id] = id
                    it[subject_area_id] = subject.id
                }
            }

            val newTasks = data.tasks.filter { it.id != null }
            val absoluteNewTasks = data.tasks.filter { it.id == null }
            val oldTasksIds = TaskTable.select { TaskTable.project eq data.id }.map { it[TaskTable.id].value }

            absoluteNewTasks.forEach { task ->
                TaskTable.insert { it.from(task, data.id) }
            }
            oldTasksIds.minus(newTasks.map { it.id }).forEach { taskId ->
                TaskTable.deleteWhere { TaskTable.id eq taskId!! }
            }
            newTasks.forEach { task ->
                TaskTable.update({ TaskTable.id eq task.id!! }) { it.from(task, data.id) }
            }
        }
    }

    fun updateMark(id: Long, data: Int) {
        transaction {
            ProjectTable.update({ ProjectTable.id eq id }) {
                it[ProjectTable.mark] = data
            }
        }
    }

    fun setDeadline(id: Long, data: LocalDate) {
        transaction {
            ProjectTable.update({ ProjectTable.id eq id }) {
                it[ProjectTable.deadline] = data
            }
        }
    }

    fun share(id: Long, data: Boolean) {
        transaction {
            ProjectTable.update({ ProjectTable.id eq id }) {
                it[ProjectTable.shared] = data
            }
        }
    }

    fun permissionToPublish(id: Long, data: Boolean) {
        transaction {
            println(data)
            ProjectTable.update({ ProjectTable.id eq id }) {
                it[ProjectTable.permissionToPublish] = data
            }
        }
    }

    fun lockOrUnlock(id: Long, data: Boolean) {
        transaction {
            ProjectTable.update({ ProjectTable.id eq id }) {
                it[ProjectTable.locked] = data
            }
        }
    }

    fun deleteProject(id: Long) {
        transaction {
            MemberTable.deleteWhere { MemberTable.project eq id }
            ProjectSubjectAreasTable.deleteWhere { ProjectSubjectAreasTable.project_id eq id }
            val tasks = TaskTable.select { TaskTable.project eq id }.map { it[TaskTable.id].value }
            TaskFilesTable.deleteWhere { TaskFilesTable.task_id inList tasks }
            TaskTable.deleteWhere { TaskTable.project eq id }
            ProjectTable.deleteWhere { ProjectTable.id eq id }
        }
    }

    fun projectName(projectId: Long): String = transaction {
        ProjectTable.slice(ProjectTable.name).select { ProjectTable.id eq projectId }.single()[ProjectTable.name]
    }

    fun projectMembers(projectId: Long): Set<Long> = transaction {
        val student = ProjectTable.slice(ProjectTable.learner).select { ProjectTable.id eq projectId }.single()[ProjectTable.learner]
        MemberTable.slice(MemberTable.user).select { MemberTable.project eq projectId }.map { it[MemberTable.user] }.toSet() + student
    }

    fun stageUp(id: Long, stage: Int) {
        transaction {
            ProjectTable.update({ ProjectTable.id eq id }) {
                it[ProjectTable.stage] = stage
                it[ProjectTable.waitingForStageUp] = false
            }
        }
    }

    fun decline(id: Long) {
        transaction {
            ProjectTable.update({ ProjectTable.id eq id }) {
                it[ProjectTable.waitingForStageUp] = false
            }
        }
    }

    fun waitingForStageUp(id: Long) {
        transaction {
            ProjectTable.update({ ProjectTable.id eq id }) {
                it[ProjectTable.waitingForStageUp] = true
            }
        }
    }

    fun subjectAreasList(): List<SubjectAreaDto> {
        return transaction {
            SubjectAreaTable
                .selectAll().toSubjectAreaList()
                .map(subjectAreaMapper::toSubjectAreaDto)
        }
    }

    fun createSubjectArea(subjectAreaDto: SubjectAreaDto): Long {
        return transaction {
            SubjectAreaTable.insert {
                it[name] = subjectAreaDto.title
                it[description] = subjectAreaDto.description
            }[SubjectAreaTable.id].value
        }
    }

    fun deleteSubjectArea(id: Long) {
        transaction {
            SubjectAreaTable.deleteWhere { SubjectAreaTable.id eq id }
        }
    }

    fun userListByArea(role: Role, ids: List<Long>?): List<UserDto> {
        return UserService.userList {
            it.andWhere { UserTable.role eq role }
            if (ids != null) {
                it.andWhere { UserSubjectAreasTable.subject_area_id inList ids }
            }
        }.map(userMapper::toUserDto)
    }

    // fun teachersListByArea(ids: List<Long>?): List<UserDto> {
    //     return UserService.userList {
    //         it.andWhere { UserTable.role eq Role.Teacher }
    //         if (ids != null) {
    //             it.andWhere { UserSubjectAreasTable.subject_area_id inList ids }
    //         }
    //     }.map(userMapper::toUserDto)
    // }
    //
    // fun expertsListByArea(ids: List<Long>?): List<UserDto> {
    //     return UserService.userList {
    //         it.andWhere { UserTable.role eq Role.Expert }
    //         if (ids != null) {
    //             it.andWhere { UserSubjectAreasTable.subject_area_id inList ids }
    //         }
    //     }.map(userMapper::toUserDto)
    // }

    fun filesList(id: Long): List<FileInfo> {
        TODO("Not yet implemented")
    }

    fun pinFile(id: Long, pinned: Boolean) {
        transaction {
            FileRefTable.update({ FileRefTable.id eq id }) {
                it[FileRefTable.pinned] = pinned
            }
        }
    }

    fun addFile(projectId: Long, body: FileDto) {
        val fileId = FileStorageService.saveInputStream(body.file)
        transaction {
            if (body.taskId == null) {
                ProjectFilesTable.insert {
                    it[project_id] = projectId
                    it[file_id] = fileId
                }
            } else {
                TaskFilesTable.insert {
                    it[task_id] = body.taskId
                    it[file_id] = fileId
                }
            }
        }
    }

    fun deleteFile(ownerId: Long, fileId: Long) {
        transaction {
            val notFound = ProjectFilesTable.select {
                ProjectFilesTable.project_id eq ownerId and (ProjectFilesTable.file_id eq fileId)
            }.empty() && TaskFilesTable.select { TaskFilesTable.task_id eq ownerId and (TaskFilesTable.file_id eq fileId) }.empty()
            if (notFound) jsonApiError(
                "Не найден такой файл в проекте",
                status = HttpStatusCode.UnprocessableEntity
            )
            FileStorageService.deleteFile(fileId)
        }
    }

    fun membersList(projectId: Long): List<MemberDto> {
        return transaction {
            MemberTable.select { MemberTable.project eq projectId }
                .toMemberList().map(mapper::toMemberDto)
        }
    }

    fun inviteMember(projectId: Long, userIds: List<Long>, type: MemberType) {
        transaction {
            MemberTable
                .deleteWhere {
                    MemberTable.project eq projectId and
                        (MemberTable.user inList userIds)
                }
            userIds.forEach { userId ->
                MemberTable.insert {
                    it[MemberTable.project] = projectId
                    it[MemberTable.user] = userId
                    it[MemberTable.status] = MemberStatus.Invited
                    it[MemberTable.type] = type
                }
            }
        }
    }

    fun accept(projectId: Long, user: UserBase, status: Boolean) {
        transaction {
            if (status) {
                MemberTable
                    .update(
                        {
                            MemberTable.project eq projectId and (MemberTable.user eq user.id)
                        }
                    ) {
                        it[MemberTable.status] = MemberStatus.Accepted
                    }
            } else {
                MemberTable
                    .deleteWhere {
                        MemberTable.project eq projectId and
                            (MemberTable.user eq user.id)
                    }
            }
        }
    }

    // ===============================================================================================

    suspend fun generateReport(projectId: Long, user: UserBase) = withContext(Dispatchers.IO) {
        val project = projectById(projectId, user).let(mapper::toProjectReportDto)

        val template = getResourceAsStream("templates/report.docx") ?: error("Отсутствует файл templates/report.docx")
        val doc = File.createTempFile("report_", null)

        DocumentGeneratorXDocReport.generateDocument(template, project.asMap(), doc.outputStream()) {
            // load("examinations", JournalReport.Examination::class.java, true)
        }

        DownloadService.createExportFile(
            "Проект ${project.name.slugify()}.docx",
            doc
        )
    }
}
