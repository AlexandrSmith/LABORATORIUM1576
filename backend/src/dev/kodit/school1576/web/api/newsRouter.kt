package dev.kodit.school1576.web.api

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.patch
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.route
import dev.kodit.school1576.web.models.News
import dev.kodit.school1576.web.models.NewsService
import dev.kodit.school1576.web.models.project.NewsDto
import dev.kodit.school1576.web.shared.api.PathId
import ru.kod_it.lib.jsonapi.openapi.respondJsonApi
import ru.kod_it.lib.jsonapi.openapi.respondJsonApiError
import ru.kod_it.lib.jsonapi.openapi.respondJsonApiNoContent
import ru.kod_it.lib.jsonapi.spec.JsonApiDocument
import ru.kod_it.lib.jsonapi.spec.JsonApiError

fun NormalOpenAPIRoute.newsRouter() {

    route("news") {
        get<Unit, JsonApiDocument<List<News>>>(info("Новости")) { _ ->
            respondJsonApi(NewsService.list())
        }
        post<Unit, Unit, NewsDto>(info("Новости")) { _, body ->
            NewsService.create(body)
            respondJsonApiNoContent()
        }
        route("{id}") {
            get<PathId, JsonApiDocument<News>>(
                info("Проект")
            ) { params ->
                val news = NewsService.get(params.id)
                if (news != null) {
                    respondJsonApi(news)
                } else {
                    respondJsonApiError(JsonApiError())
                }
            }
            patch<PathId, Unit, NewsDto>(info("Новость")) { params, body ->
                NewsService.update(params.id, body)
                respondJsonApiNoContent()
            }
            delete<PathId, Unit>(info("Удалить новость")) { params ->
                NewsService.delete(params.id)
                respondJsonApiNoContent()
            }
        }
    }
}
