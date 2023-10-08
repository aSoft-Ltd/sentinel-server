package sentinel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kase.Successful
import kase.toFailed
import koncurrent.later.await
import kotlinx.serialization.StringFormat
import kotlinx.serialization.encodeToString
import sentinel.params.SignUpParams

fun Routing.installRegistration(service: RegistrationService, endpoint: RegistrationEndpoint, codec: StringFormat) {
    post(endpoint.status()) {
        try {
            val params = codec.decodeFromString(SignUpParams.serializer(), call.receiveText())
            val res = service.signUp(params).await()
            call.respondText(codec.encodeToString(Successful(res)), ContentType.Application.Json)
        } catch (err: Throwable) {
            call.respondText(codec.encodeToString(err.toFailed()), ContentType.Application.Json)
        }
    }
    get(endpoint.status()) {
        call.respondText("Works")
    }
}