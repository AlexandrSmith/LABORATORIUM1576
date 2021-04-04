package dev.kodit.school1576.auth

import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineResponseContext
import ru.kod_it.lib.ktor_auth.UserBase
import ru.kod_it.lib.ktor_auth.user

val OpenAPIPipelineResponseContext<*>.user: UserBase
    get() = pipeline.context.user
