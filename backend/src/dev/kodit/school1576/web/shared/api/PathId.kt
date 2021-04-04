package dev.kodit.school1576.web.shared.api

import com.papsign.ktor.openapigen.annotations.parameters.PathParam

data class PathId(@PathParam("Идентификатор") val id: Long)
