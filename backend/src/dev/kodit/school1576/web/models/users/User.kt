package dev.kodit.school1576.web.models.users

import dev.kodit.school1576.auth.Role
import dev.kodit.school1576.web.models.project.SubjectArea
import ru.kod_it.lib.krush.annotation.KrushIgnore
import ru.kod_it.lib.krush.annotation.ManyToManyOptions
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue
    val id: Long,
    val username: String,
    val password: String?,

    val surname: String,
    val name: String,
    val patronymic: String?,
    @Enumerated(EnumType.STRING)
    val status: UserStatus,

    @Enumerated(EnumType.STRING)
    val role: Role,

    @ManyToMany
    @ManyToManyOptions("users_subject_areas", "user_id", "subject_area_id")
    val subjectAreas: List<SubjectArea> = emptyList(),
) {
    @delegate:KrushIgnore
    val fullName by lazy { listOfNotNull(surname, name, patronymic).joinToString(" ") }

    @delegate:KrushIgnore
    val shortName by lazy {
        listOfNotNull(
            surname,
            name.firstOrNull()?.let { "$it." },
            patronymic?.firstOrNull()?.let { "$it." }
        ).joinToString(" ")
    }

    @KrushIgnore
    val roleTitle = role.title

    @KrushIgnore
    val permissions = setOf(role)
}

enum class UserStatus {
    Active,
    Blocked
}
