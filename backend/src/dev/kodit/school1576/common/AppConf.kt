package dev.kodit.school1576.common

import com.typesafe.config.ConfigFactory
import dev.kodit.school1576.ext.getResourceAsStream
import io.ktor.config.HoconApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import java.util.*

@KtorExperimentalAPI
@Suppress("MemberVisibilityCanBePrivate")
object AppConf {
    private val properties by lazy {
        val properties = Properties()
        properties.load(getResourceAsStream("application.properties"))
        properties
    }
    val appConfig = HoconApplicationConfig(ConfigFactory.load())

    val appUrl: String = appConfig.property("common.appUrl").getString()

    val version: String by properties

    val host: String by lazy {
        val hostStr = appConfig.property("ktor.deployment.host").getString()
        if (hostStr == "0.0.0.0")
            return@lazy "localhost"
        else
            return@lazy hostStr
    }
    val port: String = appConfig.property("ktor.deployment.port").getString()

    val serveOpenApi: Boolean = appConfig.property("openapi.serve").getString().toBoolean()

    val filesStorageDir = appConfig.property("files.storageDir").getString()
    val sessionStorageDir = appConfig.property("auth.sessionStorageDir").getString()
}
