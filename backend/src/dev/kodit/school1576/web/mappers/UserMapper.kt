package dev.kodit.school1576.web.mappers

import dev.kodit.school1576.web.models.users.User
import dev.kodit.school1576.web.models.users.UserAuthDto
import dev.kodit.school1576.web.models.users.UserDto
import dev.kodit.school1576.web.models.users.UserInfo
import dev.kodit.school1576.web.models.users.UserTableDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(uses = [SubjectAreaMapper::class])
interface UserMapper {
    @Mappings(
        Mapping(target = "password", ignore = true),
        Mapping(target = "areas", source = "subjectAreas"),
    )
    fun toUserDto(user: User): UserDto
    fun toUserInfo(user: User): UserInfo
    fun toUserTableDto(user: User): UserTableDto
    fun toUserAuthDto(user: User): UserAuthDto
}
