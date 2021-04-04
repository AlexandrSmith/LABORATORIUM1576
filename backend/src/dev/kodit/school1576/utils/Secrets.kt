package dev.kodit.school1576.utils

import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

typealias Secret = Map<String, String>

private val secrets by lazy {
    val secretFile = System.getenv("SECRETS_FILE")?.let { File(it) } ?: return@lazy emptyMap()
    if (!secretFile.isFile) {
        return@lazy emptyMap()
    }
    globalJsonMapper.readValue<Map<String, Secret>>(secretFile)
}

fun getSecret(subject: String, key: String): String? {
    return secrets[subject]?.get(key)
}
