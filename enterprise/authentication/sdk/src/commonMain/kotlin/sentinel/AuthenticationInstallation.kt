package sentinel

import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.routing.Routing
import kase.response.post
import koncurrent.later.await
import kotlinx.serialization.StringFormat
import sentinel.params.SignInParams

fun Routing.installAuthentication(service: AuthenticationService, endpoint: AuthenticationEndpoint, codec: StringFormat) {
    post(endpoint.signIn(), codec) {
        val params = codec.decodeFromString(SignInParams.serializer(), call.receiveText())
        service.signIn(params).await()
    }
}