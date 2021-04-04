package dev.kodit.school1576.auth

import dev.kodit.school1576.web.models.users.RoleDto
import ru.kod_it.lib.ktor_auth.rbac.PermissionBase

enum class Role(override val title: String) : PermissionBase {
    Admin("Администратор"),
    Teacher("Учитель"),
    Student("Ученик"),
    Expert("Эксперт");

    fun toRoleDto() = RoleDto(name, title)
}
