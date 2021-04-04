package dev.kodit.school1576.model

import dev.kodit.school1576.auth.Role
import dev.kodit.school1576.base.BaseDatabaseTest
import dev.kodit.school1576.web.models.users.UserDto
import dev.kodit.school1576.web.models.users.UserService
import dev.kodit.school1576.web.models.users.UserStatus
import dev.kodit.school1576.web.models.users.UserTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

class UserTest : BaseDatabaseTest() {

    @Test
    fun `user test`() {
        val testEmployeeDto = UserDto(
            username = "test_user",
            password = "test_password",
            surname = "Testov",
            name = "Test",
            patronymic = "Testovich",
            status = UserStatus.Active,
            role = Role.Admin,
            areas = listOf()
        )

        transaction {
            UserTable.deleteWhere { UserTable.username eq testEmployeeDto.username }
        }

        UserService.createUser(testEmployeeDto)

        val userList = UserService.userList()
        val userFromList = userList.find { it.username == testEmployeeDto.username }
        assertNotNull(userFromList)
        val userId = userFromList.id!!
        val userFromDb = UserService.get(userId)
        UserService.updateUser(userId, userFromDb.copy(surname = "Testov2"))

        val userFromDb2 = UserService.get(userId)
        assertEquals("Testov2", userFromDb2.surname)

        UserService.deleteUser(userId)
        assertFails {
            UserService.get(userId)
        }
    }
}
