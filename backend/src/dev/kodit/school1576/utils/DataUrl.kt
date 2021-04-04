package dev.kodit.school1576.utils

import io.ktor.http.ContentType
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.lang.Exception
import java.util.*

fun dataUrlToByteArray(dataUrl: String): ByteArray = Base64.getDecoder().decode(dataUrl.substringAfter(','))

fun dataUrlToBlob(dataUrl: String) = ExposedBlob(dataUrlToByteArray(dataUrl))

data class DataUrl(val dataUrl: String) {
    companion object {
        fun isDataUrl(dataUrl: String): Boolean = dataUrl.startsWith("data:")
    }

    val contentType: ContentType by lazy {
        ContentType.parse(
            dataUrl.substringBefore(",")
                .substringBefore(";")
                .substringAfter("data:")
        )
    }

    fun getData(): ByteArray {
        return try {
            dataUrlToByteArray(dataUrl)
        } catch (e: Exception) {
            error("Ошибка разбора base64")
        }
    }
}
