package dev.kodit.school1576.ext

import io.ktor.application.Application
import org.apache.tika.Tika
import java.io.InputStream
import java.util.Base64

fun getResourceAsStream(name: String): InputStream? = Application::class.java.classLoader.getResourceAsStream(name)
fun getResourceAsString(name: String): String? = Application::class.java.classLoader.getResource(name)?.readText()

private val tika = Tika()

fun getResourceAsDataUrl(name: String): String? {
    val data = getResourceAsStream(name)?.readAllBytes() ?: return null
    val type = tika.detect(name)
    val dataText = Base64.getEncoder().encodeToString(data)
    return "data:$type;base64,$dataText"
}
