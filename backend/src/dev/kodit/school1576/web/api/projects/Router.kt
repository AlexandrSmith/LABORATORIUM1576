package dev.kodit.school1576.web.api.projects

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.*
import com.papsign.ktor.openapigen.route.route
import dev.kodit.school1576.auth.Role
import dev.kodit.school1576.auth.user
import dev.kodit.school1576.web.models.ExportFileDto
import dev.kodit.school1576.web.models.NotificationService
import dev.kodit.school1576.web.models.project.FileDto
import dev.kodit.school1576.web.models.project.FileInfo
import dev.kodit.school1576.web.models.project.MemberDto
import dev.kodit.school1576.web.models.project.MemberType
import dev.kodit.school1576.web.models.project.ProjectDto
import dev.kodit.school1576.web.models.project.ProjectInfoDto
import dev.kodit.school1576.web.models.project.ProjectService
import dev.kodit.school1576.web.models.project.SubjectAreaDto
import dev.kodit.school1576.web.models.users.UserDto
import dev.kodit.school1576.web.shared.api.PathId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.kod_it.lib.jsonapi.openapi.respondJsonApi
import ru.kod_it.lib.jsonapi.openapi.respondJsonApiNoContent
import ru.kod_it.lib.jsonapi.spec.JsonApiDocument
import ru.kod_it.lib.ktor_auth.authenticate
import ru.kod_it.lib.ktor_auth.user
import java.time.LocalDate

@ExperimentalCoroutinesApi
fun NormalOpenAPIRoute.projectsRoute() {

    route("projects") {

        route("main") {
            get<ProjectFilter, JsonApiDocument<List<ProjectInfoDto>>>(info("Проекты (с фильтром)")) { params ->
                respondJsonApi(ProjectService.filteredProjectsList(params))
            }
            route("{id}") {
                get<PathId, JsonApiDocument<ProjectDto>>(
                    info("Проект")
                ) { params ->
                    respondJsonApi(ProjectService.mainProjectById(params.id))
                }
            }
        }

        authenticate {

            get<Unit, JsonApiDocument<List<ProjectInfoDto>>>(info("Проекты")) {
                respondJsonApi(ProjectService.projectsList(user))
            }

            post<Unit, JsonApiDocument<Long>, JsonApiDocument<ProjectDto>>(
                info("Новый проект")
            ) { _, body ->
                respondJsonApi(ProjectService.createProject(body.data))
            }

            route("admin").get<ProjectFilter, JsonApiDocument<List<ProjectDto>>>() { params ->
                respondJsonApi(ProjectService.adminProjectList(params, user))
            }

            route("subject-areas") {
                get<Unit, JsonApiDocument<List<SubjectAreaDto>>>(
                    info("Список предметных областей")
                ) {
                    respondJsonApi(ProjectService.subjectAreasList())
                }
                post<Unit, JsonApiDocument<Long>, JsonApiDocument<SubjectAreaDto>>(
                    info("Новая предметная область")
                ) { _, body ->
                    respondJsonApi(ProjectService.createSubjectArea(body.data))
                }
                route("{id}") {
                    delete<PathId, Unit>(
                        info("Удалить предметную область")
                    ) { params ->
                        ProjectService.deleteSubjectArea(params.id)
                        respondJsonApiNoContent()
                    }
                }
            }

            route("teachers") {
                get<FilterParams, JsonApiDocument<List<UserDto>>>(
                    info("Учителя для предметной области")
                ) { params ->
                    respondJsonApi(ProjectService.userListByArea(Role.Teacher, params.subjectIds))
                }
            }

            route("experts") {
                get<FilterParams, JsonApiDocument<List<UserDto>>>(
                    info("Эксперты для предметной области")
                ) { params ->
                    respondJsonApi(ProjectService.userListByArea(Role.Expert, params.subjectIds))
                }
            }

            route("{id}") {
                get<PathId, JsonApiDocument<ProjectDto>>(
                    info("Проект")
                ) { params ->
                    respondJsonApi(ProjectService.projectById(params.id, user))
                }
                patch<PathId, Unit, JsonApiDocument<ProjectDto>>(
                    info("Редактирование проекта")
                ) { params, body ->
                    ProjectService.updateProject(params.id, body.data)
                    respondJsonApiNoContent()
                }

                delete<PathId, Unit>(info("Удалить проект")) { params ->
                    ProjectService.deleteProject(params.id)
                    respondJsonApiNoContent()
                }

                route("update-mark") {
                    patch<PathId, Unit, JsonApiDocument<Int>>(
                        info("Оценка")
                    ) { params, body ->
                        ProjectService.updateMark(params.id, body.data)
                        respondJsonApiNoContent()
                    }
                }

                route("set-deadline") {
                    patch<PathId, Unit, JsonApiDocument<LocalDate>>(
                        info("Дедлайн")
                    ) { params, body ->
                        ProjectService.setDeadline(params.id, body.data)
                        NotificationService.sendDeadline(params.id, body.data, user.id)
                        respondJsonApiNoContent()
                    }
                }

                route("share") {
                    patch<PathId, Unit, JsonApiDocument<Boolean>>(
                        info("Опубликовать")
                    ) { params, body ->
                        ProjectService.share(params.id, body.data)
                        NotificationService.sendShare(params.id, body.data, user.id)
                        respondJsonApiNoContent()
                    }
                }
                route("permission_to_publish") {
                    patch<PathId, Unit, JsonApiDocument<Boolean>>(
                        info("Разрешить публикацию")
                    ) { params, body ->
                        ProjectService.permissionToPublish(params.id, body.data)
                        // NotificationService.sendShare(params.id, body.data, user.id)
                        respondJsonApiNoContent()
                    }
                }

                route("lock-or-unlock") {
                    patch<PathId, Unit, JsonApiDocument<Boolean>>(
                        info("Заблокировать или разблокировать")
                    ) { params, body ->
                        ProjectService.lockOrUnlock(params.id, body.data)
                        NotificationService.sendLockUnlock(params.id, body.data, user.id)
                        respondJsonApiNoContent()
                    }
                }

                route("stage-up") {
                    post<PathId, Unit, JsonApiDocument<Int>>(
                        info("Принят этап")
                    ) { params, body ->
                        ProjectService.stageUp(params.id, body.data)
                        NotificationService.sendStageUp(params.id, body.data, user.id)
                        respondJsonApiNoContent()
                    }
                }

                route("decline") {
                    post<PathId, Unit, Unit>(
                        info("Отклонить")
                    ) { params, _ ->
                        ProjectService.decline(params.id)
                        NotificationService.sendDecline(params.id, user.id)
                        respondJsonApiNoContent()
                    }
                }

                route("waiting-for-stage-up") {
                    post<PathId, Unit, JsonApiDocument<Int>>(
                        info("На проверку")
                    ) { params, body ->
                        ProjectService.waitingForStageUp(params.id)
                        NotificationService.sendWaitingForStageUp(params.id, body.data, user.id)
                        respondJsonApiNoContent()
                    }
                }

                route("files") {
                    get<PathId, JsonApiDocument<List<FileInfo>>>(
                        info("Получить файлы проекта")
                    ) { params ->
                        respondJsonApi(ProjectService.filesList(params.id))
                    }

                    post<PathId, Unit, FileDto>(
                        info("Загрузить файл")
                    ) { params, body ->
                        ProjectService.addFile(params.id, body)
                        respondJsonApiNoContent()
                    }

                    route("{fileId}") {
                        patch<FilePathParam, Unit, JsonApiDocument<Boolean>>(
                            info("Прикрепить файл")
                        ) { params, body ->
                            ProjectService.pinFile(params.fileId, body.data)
                            respondJsonApiNoContent()
                        }

                        delete<FilePathParam, Unit>(
                            info("Удалить файл")
                        ) { params ->
                            ProjectService.deleteFile(params.id, params.fileId)
                            respondJsonApiNoContent()
                        }
                    }
                }

                route("member") {
                    get<PathId, JsonApiDocument<List<MemberDto>>>(
                        info("Участники проекта")
                    ) { params ->
                        respondJsonApi(ProjectService.membersList(params.id))
                    }

                    route("invite") {
                        post<InviteParams, Unit, Unit>(
                            info("Пригласить участника")
                        ) { params, _ ->
                            ProjectService.inviteMember(params.id, params.userIds, params.type)
                            NotificationService.sendInvite(params.id, params.userIds, user.id)
                            respondJsonApiNoContent()
                        }
                    }
                    route("accept") {
                        post<PathId, Unit, JsonApiDocument<Boolean>>(
                            info("Взять/отказаться от проекта (учителю или эксперту)")
                        ) { params, body ->
                            ProjectService.accept(params.id, user, body.data)
                            NotificationService.acceptInvite(params.id, user.id, body.data)
                            respondJsonApiNoContent()
                        }
                    }
                }

                route("report") {
                    get<PathId, JsonApiDocument<ExportFileDto>>(info("Сформировать отчет")) { params ->
                        val file = ProjectService.generateReport(params.id, user)
                        respondJsonApi(file.toDto())
                    }
                }
            }
        }
    }
}

data class FilePathParam(
    @PathParam("Идентификатор") val id: Long,
    @PathParam("Идентификатор файла") val fileId: Long
)

data class FilterParams(@PathParam("Предметные области") val subjectIds: List<Long>?)
data class InviteParams(
    @PathParam("Проект") val id: Long,
    @PathParam("Пользователи") val userIds: List<Long>,
    @PathParam("Тип") val type: MemberType,
)

data class ProjectFilter(
    @PathParam("Название") val name: String?,
    @PathParam("Только опубликованные") val shared: Boolean?,
    @PathParam("Порядок сортировки") val sortOrder: SortOrderType?
)

enum class SortOrderType {
    ByName,
    ByDate,
    BySubjects,
    ByLearner,
    ByTeacher
}
