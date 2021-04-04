package dev.kodit.school1576.utils

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val globalJsonMapper by lazy {
    val mapper = jacksonObjectMapper()
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    mapper.registerModule(JavaTimeModule())
    mapper
}
