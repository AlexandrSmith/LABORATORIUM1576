package dev.kodit.school1576.web.api

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import dev.kodit.school1576.web.models.DownloadService
import ru.kod_it.lib.jsonapi.openapi.StreamDto
import ru.kod_it.lib.jsonapi.openapi.respondJsonApiNoContent
import ru.kod_it.lib.jsonapi.spec.jsonApiError

fun NormalOpenAPIRoute.downloadRoute() {
    route("download") {
        get<DownloadLink, StreamDto> { params ->
            val exportFile = DownloadService.getExportFile(params.uuid) ?: jsonApiError("Не найден такой файл")
            if (exportFile.file.exists())
                respond(StreamDto(exportFile.file.inputStream()))
            else
                respondJsonApiNoContent()
            exportFile.file.delete()
        }
    }
}

@Path("{uuid}/{fileName}")
data class DownloadLink(
    @PathParam("Идентификатор файла") val uuid: String,
    @PathParam("Имя файла") val fileName: String
)
