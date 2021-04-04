package dev.kodit.school1576.web.api.users

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.*
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.tag
import dev.kodit.school1576.auth.Role
import dev.kodit.school1576.auth.user
import dev.kodit.school1576.web.models.users.RoleDto
import dev.kodit.school1576.web.models.users.UserDto
import dev.kodit.school1576.web.models.users.UserInfo
import dev.kodit.school1576.web.models.users.UserService
import dev.kodit.school1576.web.shared.api.PathId
import ru.kod_it.lib.jsonapi.openapi.ApiTag
import ru.kod_it.lib.jsonapi.openapi.respondJsonApi
import ru.kod_it.lib.jsonapi.openapi.respondJsonApiNoContent
import ru.kod_it.lib.jsonapi.spec.JsonApiDocument
import ru.kod_it.lib.ktor_auth.authenticate
import ru.kod_it.lib.ktor_auth.rbac.permissions
import ru.kod_it.lib.ktor_auth.user

fun NormalOpenAPIRoute.usersRoute() {

    route("users").tag(ApiTag("Пользователи")) {
        authenticate {

            route("info") {
                get<Unit, JsonApiDocument<UserInfo>>(info("Информация о текущем пользователе")) {
                    respondJsonApi(UserService.getInfo(user.id))
                }
            }

            route("{id}") {
                get<PathId, JsonApiDocument<UserDto>>(
                    info("Пользователь")
                ) { params ->
                    respondJsonApi(UserService.get(params.id))
                }
                patch<PathId, Unit, JsonApiDocument<UserDto>>(
                    info("Редактирование пользователя")
                ) { params, body ->
                    UserService.updateUser(params.id, body.data, this.pipeline.context.user)
                    respondJsonApiNoContent()
                }
            }

            permissions(Role.Admin) {
                get<Unit, JsonApiDocument<List<UserDto>>>(info("Пользователи")) { _ ->
                    respondJsonApi(UserService.userList())
                }
                post<Unit, Unit, JsonApiDocument<UserDto>>(
                    info("Новый пользователь")
                ) { _, body ->
                    UserService.createUser(body.data)
                    respondJsonApiNoContent()
                }
                route("{id}") {
                    delete<PathId, Unit>(info("Удалить пользователя")) { params ->
                        UserService.deleteUser(params.id)
                        respondJsonApiNoContent()
                    }
                }
            }
        }

        route("roles") {
            get<Unit, JsonApiDocument<List<RoleDto>>>(info("Роли")) { _ ->
                respondJsonApi(Role.values().map(Role::toRoleDto))
            }
        }
    }
}
