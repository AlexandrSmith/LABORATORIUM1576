package dev.kodit.school1576.web.models.users

import dev.kodit.school1576.auth.Role
import dev.kodit.school1576.ext.getLogger
import dev.kodit.school1576.web.mappers.UserMapper
import dev.kodit.school1576.web.models.project.SubjectAreaTable
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mapstruct.factory.Mappers
import ru.kod_it.lib.jsonapi.spec.jsonApiError
import ru.kod_it.lib.ktor_auth.AuthService
import ru.kod_it.lib.ktor_auth.OAuthUserInfo
import ru.kod_it.lib.ktor_auth.UserBase
import ru.kod_it.lib.ktor_auth.UserServiceBase

object UserService : UserServiceBase {
    private val logger = getLogger()
    private val mapper = Mappers.getMapper(UserMapper::class.java)

    private fun UpdateBuilder<Any>.from(dto: UserDto) {
        this[UserTable.username] = dto.username
        if (!dto.password.isNullOrEmpty())
            this[UserTable.password] = AuthService.getHash(dto.password)

        this[UserTable.surname] = dto.surname
        this[UserTable.name] = dto.name
        this[UserTable.patronymic] = dto.patronymic
        this[UserTable.status] = dto.status
        this[UserTable.role] = dto.role
    }

    fun getNotificationUserId() = transaction {
        UserTable.select { UserTable.username eq "notification" }.single()[UserTable.id].value
    }

    fun getUserFullName(id: Long) = transaction {
        UserTable.select { UserTable.id eq id }.singleOrNull()?.toUser()?.fullName
    }

    private fun getUsers(where: (SqlExpressionBuilder.() -> Op<Boolean>)? = null): List<User> = transaction {
        UserTable
            .leftJoin(UserSubjectAreasTable)
            .leftJoin(SubjectAreaTable)
            .selectAll()
            .also {
                if (where != null)
                    it.andWhere { where() }
            }
            .orderBy(UserTable.id)
            .toList()
            .toUserList()
    }

    fun userList(block: (Query) -> Unit): List<User> = transaction {
        UserTable
            .leftJoin(UserSubjectAreasTable)
            .leftJoin(SubjectAreaTable)
            .selectAll()
            .also(block)
            .orderBy(UserTable.id)
            .toUserList()
    }

    fun userList(): List<UserDto> {
        return getUsers().map(mapper::toUserDto)
    }

    fun get(userId: Long): UserDto {
        return getUsers { UserTable.id eq userId }.singleOrNull()?.let(mapper::toUserDto) ?: jsonApiError(
            detail = "Не найден такой пользователь",
            status = HttpStatusCode.NotFound,
        )
    }

    fun getInfo(userId: Long): UserInfo {
        return getUsers { UserTable.id eq userId }.singleOrNull()?.let(mapper::toUserInfo) ?: jsonApiError(
            detail = "Не найден такой пользователь",
            status = HttpStatusCode.NotFound,
        )
    }

    fun createUser(userDto: UserDto): Long {
        return transaction {
            val userId = UserTable.insert {
                it.from(userDto)
            }[UserTable.id].value
            userDto.areas.forEach { area ->
                UserSubjectAreasTable.insert {
                    it[subject_area_id] = area
                    it[user_id] = userId
                }
            }
            userId
        }
    }

    fun updateUser(userId: Long, userDto: UserDto, user: UserBase) {
        transaction {
            if (user.permissions.contains(Role.Admin)) {
                UserSubjectAreasTable.deleteWhere { UserSubjectAreasTable.user_id eq userId }
                userDto.areas.forEach { area ->
                    UserSubjectAreasTable.insert {
                        it[subject_area_id] = area
                        it[user_id] = userId
                    }
                }
                UserTable.update({ UserTable.id eq userId }) { it.from(userDto) }
            } // TODO else if?
            if (userId == user.id) {
                UserTable.update({ UserTable.id eq userId }) {
                    it[UserTable.surname] = userDto.surname
                    it[UserTable.name] = userDto.name
                    it[UserTable.patronymic] = userDto.patronymic
                }
            }
        }
    }

    fun deleteUser(userId: Long) {
        transaction {
            UserSubjectAreasTable.deleteWhere { UserSubjectAreasTable.user_id eq userId }
            UserTable.deleteWhere { UserTable.id eq userId }
        }
    }

    // ========================================================================

    override fun findByCredentials(username: String, password: String): UserBase? {
        val user = getUsers { UserTable.username eq username }.singleOrNull() ?: return null
        if (user.status == UserStatus.Blocked) jsonApiError(
            title = "Ошибка аутентификации",
            detail = "Ваш аккаунт заблокирован. Обратитесь к администратору для получения доступа",
            code = "auth.login",
            status = HttpStatusCode.UnprocessableEntity,
        )
        if (user.password.isNullOrEmpty()) jsonApiError(
            title = "Ошибка аутентификации",
            detail = "Вход в ваш аккаунт возможен только через аккаунт Microsoft",
            code = "auth.login",
            status = HttpStatusCode.UnprocessableEntity,
        )
        if (AuthService.verifyPassword(password, user.password))
            return mapper.toUserAuthDto(user)
        return null
    }

    override fun findById(userId: Long): UserBase? {
        return getUsers { UserTable.id eq userId }.singleOrNull()?.let(mapper::toUserAuthDto)
    }

    fun findByOAuthId(info: OAuthUserInfo): UserBase {
        logger.debug("findByOAuthId $info")
        val email = info.email ?: jsonApiError(
            title = "Ошибка аутентификации",
            detail = "Не возможно пройти аутентификацию так как отсутстувет информация о email",
            code = "auth.login",
            status = HttpStatusCode.UnprocessableEntity,
        )
        var user = getUsers { UserTable.username eq email }.singleOrNull()
        if (user != null) {
            if (user.status == UserStatus.Blocked) jsonApiError(
                title = "Ошибка аутентификации",
                detail = "Ваш аккаунт заблокирован. Обратитесь к администратору для получения доступа.",
                code = "auth.login",
                status = HttpStatusCode.UnprocessableEntity,
            )
        } else {
            val id = createUser(
                UserDto(
                    username = email,
                    status = UserStatus.Active,
                    surname = info.familyName ?: "",
                    name = info.givenName ?: "",
                    patronymic = null,
                    areas = listOf(),
                    role = Role.Student
                )
            )
            user = getUsers { UserTable.id eq id }.single()
        }
        return mapper.toUserAuthDto(user)
    }
}
