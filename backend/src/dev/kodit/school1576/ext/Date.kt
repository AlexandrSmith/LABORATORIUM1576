@file:Suppress("unused")

package dev.kodit.school1576.ext

import java.time.format.DateTimeFormatter

val RussianDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
val RussianDateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
