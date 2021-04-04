package dev.kodit.school1576.web.api

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import dev.kodit.school1576.web.models.FileStorageService
import ru.kod_it.lib.jsonapi.openapi.StreamDto

fun NormalOpenAPIRoute.filesRoute() {
    route("files") {
        get<FileUrl, StreamDto>(info("Получение файла")) { params ->
            val stream = FileStorageService.getFileStream(params.filename)
            respond(StreamDto(stream))
        }
    }
}

@Path("{filename}/{uploadName}")
data class FileUrl(
    @PathParam("уникальное имя файла")
    val filename: String,
    @PathParam("имя файла под которым будет идти скачивание")
    val uploadName: String,
)
