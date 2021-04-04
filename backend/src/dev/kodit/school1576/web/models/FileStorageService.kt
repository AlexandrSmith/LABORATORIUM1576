package dev.kodit.school1576.web.models

import com.papsign.ktor.openapigen.content.type.multipart.NamedFileInputStream
import dev.kodit.school1576.common.AppConf
import dev.kodit.school1576.ext.getLogger
import io.ktor.http.HttpStatusCode
import io.ktor.util.error
import org.apache.commons.io.FilenameUtils
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kod_it.lib.jsonapi.spec.jsonApiError
import java.io.File
import java.io.InputStream
import java.util.UUID

object FileStorageService {
    private val logger = getLogger()

    fun makeStorageDir() {
        val result = File(AppConf.filesStorageDir).mkdirs()
        logger.info("makeStorageDir $result")
    }

    fun getFileStream(filename: String): InputStream {
        val file = File(AppConf.filesStorageDir, filename)
        if (!file.isFile) jsonApiError("Файл не существует", status = HttpStatusCode.NotFound)
        return file.inputStream()
    }

    fun saveInputStream(inputStream: NamedFileInputStream): Long {
        val uploadName = inputStream.name ?: jsonApiError("не указано имя файла")
        return saveInputStream(inputStream, uploadName)
    }

    fun saveInputStream(inputStream: InputStream, uploadName: String): Long {
        logger.debug("saveInputStream fileName $uploadName")
        val extension = FilenameUtils.getExtension(uploadName)
        logger.debug("saveInputStream extension $extension")
        val filename = UUID.randomUUID().toString() + "." + extension
        logger.debug("saveInputStream result name $filename")
        val file = File(AppConf.filesStorageDir, filename)
        try {
            inputStream.transferTo(file.outputStream())
            return transaction {
                FileRefTable.insert {
                    it[FileRefTable.filename] = filename
                    it[FileRefTable.uploadName] = uploadName
                }[FileRefTable.id].value
            }
        } catch (e: Exception) {
            logger.error(e)
            file.delete()
            jsonApiError("Возникла ошибка при сохранении файла $uploadName")
        }
    }

    fun deleteFile(id: Long) {
        transaction {
            FileRefTable.deleteWhere { FileRefTable.id eq id }
        }
    }
}
