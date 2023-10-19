package sentinel

import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.routing.Routing
import kase.response.post
import koncurrent.later.await
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import sentinel.params.PasswordResetParams
import sentinel.params.SendPasswordResetParams
import sentinel.params.SessionParams
import sentinel.params.SignInParams

fun Routing.installAuthentication(service: AuthenticationService, endpoint: AuthenticationEndpoint, codec: StringFormat) {
    post(endpoint.signIn(), codec) {
        val params = codec.decodeFromString<SignInParams>(call.receiveText())
        service.signIn(params).await()
    }

    post(endpoint.session(), codec) {
        val params = codec.decodeFromString<SessionParams>(call.receiveText())
        service.session(params.token).await()
    }

    post(endpoint.sendPasswordResetLink(), codec) {
        val params = codec.decodeFromString<SendPasswordResetParams>(call.receiveText())
        service.sendPasswordResetLink(params).await()
    }

    post(endpoint.resetPassword(), codec) {
        val params = codec.decodeFromString<PasswordResetParams>(call.receiveText())
        service.resetPassword(params).await()
    }
}