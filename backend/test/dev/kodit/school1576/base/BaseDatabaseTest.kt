package dev.kodit.school1576.base

import dev.kodit.school1576.db.DatabaseFactory
import org.junit.Before

abstract class BaseDatabaseTest {
//    lateinit var testDb: Database

    @Before
    fun dbInit() {
//        val templateDb = Database.connect(
//                DatabaseConfig.getJdbcUrlFor("template1"),
//                user = DatabaseConfig.user
//        )
//        transaction {
//            SchemaUtils.createDatabase(DatabaseConfig.name)
//        }
//        TransactionManager.closeAndUnregister(templateDb)
//        testDb = Database.connect(DatabaseConfig.jdbcUrl, user = DatabaseConfig.user, password = DatabaseConfig.password)
        DatabaseFactory.init()
    }
}
