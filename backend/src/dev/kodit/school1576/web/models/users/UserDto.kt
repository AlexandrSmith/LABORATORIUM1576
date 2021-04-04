package dev.kodit.school1576.web.models.users

import com.papsign.ktor.openapigen.annotations.type.string.length.MinLength
import com.papsign.ktor.openapigen.annotations.type.string.trim.Trim
import dev.kodit.school1576.auth.Role
import ru.kod_it.lib.ktor_auth.UserBase
import ru.kod_it.lib.ktor_auth.rbac.PermissionBase

data class UserDto(
    val id: Long? = null,
    @Trim
    @MinLength(5, "Email не может быть меньше чем 5 символов")
    val username: String,
    @Trim
    // @MinLength(1, "Пароль не может быть меньше чем 1 символа")
    val password: String? = null,
    @Trim
    @MinLength(1, "Фамилия не может быть пустой")
    val surname: String,
    @Trim
    @MinLength(1, "Имя не может быть пустым")
    val name: String,
    @Trim
    val patronymic: String?,
    val status: UserStatus,
    val role: Role,
    val areas: List<Long>
)

data class UserShortTableDto(
    val id: Long,
    val fullName: String,
)

data class UserTableDto(
    val id: Long,
    val fullName: String,
    val username: String,
    val status: UserStatus,
    val role: Role,
    val roleTitle: String,
)

data class UserInfo(
    val id: Long,
    val username: String,
    val fullName: String,
    val shortName: String,
    val role: Role,
)

data class UserAuthDto(
    override val id: Long,
    override val permissions: Set<PermissionBase>
) : UserBase {
    override fun toAuthPayloadDto() = Unit
}

data class RoleDto(
    val name: String,
    val title: String
)
