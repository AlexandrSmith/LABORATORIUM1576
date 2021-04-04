package dev.kodit.school1576.web

import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import dev.kodit.school1576.web.api.chat.chatRouter
import dev.kodit.school1576.web.api.downloadRoute
import dev.kodit.school1576.web.api.filesRoute
import dev.kodit.school1576.web.api.newsRouter
import dev.kodit.school1576.web.api.projects.projectsRoute
import dev.kodit.school1576.web.api.users.usersRoute
import io.ktor.application.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import ru.kod_it.lib.jsonapi.openapi.jsonApiRouting

@FlowPreview
@ExperimentalCoroutinesApi
fun Application.routes() {
    jsonApiRouting {
        // intercept(ApplicationCallPipeline.Setup) {
        //     println("COOKIES: ${call.request.cookies.rawCookies}")
        // }
        usersRoute()
        projectsRoute()
        newsRouter()
        filesRoute()
        downloadRoute()

        route("health-check") {
            get<Unit, String> {
                respond("ok")
            }
        }
    }
    chatRouter()
}
