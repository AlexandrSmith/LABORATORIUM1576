package dev.kodit.school1576.web.models

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.io.File
import java.time.Duration
import java.util.*

data class ExportFileDto(
    val name: String,
    val uuid: String
)

data class ExportFile(val name: String, val file: File, val uuid: String = UUID.randomUUID().toString()) {
    fun toDto() = ExportFileDto(name, uuid)
}

object DownloadService {
    private val scope = CoroutineScope(Dispatchers.IO)
    var exportFiles: MutableMap<String, ExportFile> = mutableMapOf()
        private set

    fun getExportFile(uuid: String) = exportFiles[uuid]

    fun createExportFile(name: String, file: File, duration: Duration = Duration.ofMinutes(5)): ExportFile {
        val exportFile = ExportFile(name, file)
        scope.launch {
            delay(duration)
            exportFiles.remove(exportFile.uuid)
            if (file.exists()) {
                file.delete()
            }
        }
        exportFiles[exportFile.uuid] = exportFile
        return exportFile
    }
}
