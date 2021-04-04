@file:Suppress("MemberVisibilityCanBePrivate")

package dev.kodit.school1576.db

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import io.ktor.util.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import java.io.File

object DatabaseConfig {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    val host = appConfig.property("db.host").getString()
    val port = appConfig.property("db.port").getString()
    val name = appConfig.property("db.name").getString()
    val user = appConfig.property("db.user").getString()
    val passwordFile = File(appConfig.property("db.passwordFile").getString())
    val password by lazy {
        if (passwordFile.isFile)
            return@lazy passwordFile.readText().trim()
        appConfig.property("db.password").getString()
    }
    val jdbcUrl = getJdbcUrlFor(name)

    fun getJdbcUrlFor(dbName: String): String {
        return "jdbc:postgresql://$host:$port/$dbName"
    }
}

@KtorExperimentalAPI
object DatabaseFactory {
    fun init() {
        val flyway = Flyway.configure()
            .dataSource(DatabaseConfig.jdbcUrl, DatabaseConfig.user, DatabaseConfig.password)
            .outOfOrder(true)
            .baselineVersion("0")
            .baselineOnMigrate(true)
            .load()
        flyway.migrate()
        Database.connect(hikari())
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = DatabaseConfig.jdbcUrl
        config.username = DatabaseConfig.user
        config.password = DatabaseConfig.password
        config.maximumPoolSize = 10
        config.isAutoCommit = false
//        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }
}
