package sentinel

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.header
import io.ktor.util.pipeline.PipelineContext
import sentinel.exceptions.MissingAuthenticationException

fun PipelineContext<*,ApplicationCall>.bearerToken(): String {
    val auth = call.request.header("Authorization") ?: throw MissingAuthenticationException()
    return auth.replace("Bearer ","")
}