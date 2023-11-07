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

fun Routing.installAuthentication(controller: AuthenticationController) {
    post(controller.endpoint.signIn(), controller.codec) {
        val params = controller.codec.decodeFromString<SignInParams>(call.receiveText())
        controller.service.signIn(params).await()
    }

    post(controller.endpoint.session(), controller.codec) {
        val params = controller.codec.decodeFromString<SessionParams>(call.receiveText())
        controller.service.session(params.token).await()
    }

    post(controller.endpoint.sendPasswordResetLink(), controller.codec) {
        val params = controller.codec.decodeFromString<SendPasswordResetParams>(call.receiveText())
        controller.service.sendPasswordResetLink(params).await()
    }

    post(controller.endpoint.resetPassword(), controller.codec) {
        val params = controller.codec.decodeFromString<PasswordResetParams>(call.receiveText())
        controller.service.resetPassword(params).await()
    }
}