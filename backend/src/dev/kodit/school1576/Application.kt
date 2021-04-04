package dev.kodit.school1576

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.papsign.ktor.openapigen.annotations.type.number.NumberConstraintViolation
import com.papsign.ktor.openapigen.annotations.type.string.length.LengthConstraintViolation
import dev.kodit.school1576.auth.authModule
import dev.kodit.school1576.common.AppConf
import dev.kodit.school1576.db.DatabaseFactory
import dev.kodit.school1576.web.models.FileStorageService
import dev.kodit.school1576.web.routes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.util.error
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.slf4j.event.Level
import ru.kod_it.lib.jackson.installJackson
import ru.kod_it.lib.jsonapi.openapi.installOpenApi
import ru.kod_it.lib.jsonapi.spec.*
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@ExperimentalCoroutinesApi
@FlowPreview
@Suppress("unused", "UNUSED_PARAMETER") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations)
    install(WebSockets) {
        pingPeriodMillis = Duration.ofSeconds(20).toMillis()
    }

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/api") }
    }

    install(ConditionalHeaders)

    install(ForwardedHeaderSupport) // WARNING: for security, do not include this if not behind a reverse proxy
    install(XForwardedHeaderSupport) // WARNING: for security, do not include this if not behind a reverse proxy

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        allowNonSimpleContentTypes = true
//        hosts.add("localhost:8080")
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(AutoHeadResponse)
    install(DataConversion)

    install(StatusPages) {
        exception<NumberConstraintViolation> { cause ->
            context.respondJsonApiError(
                HttpStatusCode.UnprocessableEntity,
                JsonApiError(title = "Некорретный формат запроса", detail = cause.message)
            )
        }
        exception<LengthConstraintViolation> { cause ->
            context.respondJsonApiError(
                HttpStatusCode.UnprocessableEntity,
                JsonApiError(title = "Некорретный формат запроса", detail = cause.message)
            )
        }
        exception<MissingKotlinParameterException> { cause ->
            application.log.debug(cause.toString())
            context.respondJsonApiError(JsonApiError(title = "Некорретный формат запроса"))
        }
        exception<JsonApiException> { cause ->
            context.respondJsonApiError(cause.apiError)
        }
        exception<JsonMappingException> { cause ->
            application.log.debug(cause.toString())
            context.respondJsonApiError(JsonApiError(title = "Некорректный формат JSON"))
        }
        exception<IllegalArgumentException> { cause ->
            application.log.debug(cause.toString())
            context.respondJsonApiError(JsonApiError(title = "Некорректный формат запроса"))
        }
        exception<Throwable> { cause ->
            application.log.error(cause)
            context.respondJsonApiError(
                HttpStatusCode.InternalServerError,
                JsonApiError(
                    title = "Внутреняя ошибка сервера"
                )
            )
        }
        exception<NotImplementedError> {
            context.respondJsonApiError(
                HttpStatusCode.NotImplemented,
                JsonApiError(
                    title = "Операция не реализована",
                    detail = "Операция ${call.request.uri} не реализована"
                )
            )
        }
    }

    FileStorageService.makeStorageDir()

    DatabaseFactory.init()

    installJackson()
    installOpenApi(
        url = "http://${AppConf.host}:${AppConf.port}",
        title = "School 1576 API",
        version = AppConf.version,
        serveSpec = AppConf.serveOpenApi
    )

    authModule()

    routes()
}
