package dev.kodit.school1576.auth

import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.tag
import dev.kodit.school1576.common.AppConf
import dev.kodit.school1576.web.models.users.UserService
import io.ktor.application.Application
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.sessions.directorySessionStorage
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import ru.kod_it.lib.jsonapi.openapi.jsonApiRouting
import ru.kod_it.lib.ktor_auth.AuthCookie
import ru.kod_it.lib.ktor_auth.AuthTag
import ru.kod_it.lib.ktor_auth.installAuth
import ru.kod_it.lib.ktor_auth.microsoftOAuthProvider
import ru.kod_it.lib.ktor_auth.oauthRoute
import ru.kod_it.lib.ktor_auth.rbac.installRoleAuthorization
import java.io.File

fun Application.authModule() {
    val storage = directorySessionStorage(File(AppConf.sessionStorageDir), false)
    installAuth(UserService, storage = storage) {
        // domain = AppConf.host
        path = "/"
    }
    installRoleAuthorization()

    val microsoftClientId = System.getenv("MICROSOFT_CLIENT_ID") ?: ""
    val microsoftSecretKey = System.getenv("MICROSOFT_SECRET_KEY") ?: ""

    jsonApiRouting {
        route("auth").tag(AuthTag) {
            // authRoute<Unit, AuthCookie>(UserService, { AuthCookie(it) })

            route("oauth") {
                oauthRoute(
                    HttpClient(Apache) {
                        install(Logging) {
                            level = LogLevel.ALL
                        }
                    },
                    "${AppConf.appUrl}/auth",
                    listOf(
                        microsoftOAuthProvider(microsoftClientId, microsoftSecretKey)
                    )
                ) { info ->
                    val userInfo = UserService.findByOAuthId(info)
                    sessions.set(AuthCookie(userInfo.id))
                }
            }
        }
    }
}
