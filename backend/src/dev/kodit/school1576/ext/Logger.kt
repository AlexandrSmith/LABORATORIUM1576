package dev.kodit.school1576.ext

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Any.getLogger(): Logger = LoggerFactory.getLogger(
    if (this::class.isCompanion) javaClass.declaringClass else javaClass
)
