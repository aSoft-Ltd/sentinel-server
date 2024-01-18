package sentinel

import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.routing.Routing
import io.ktor.server.util.getValue
import kase.response.get
import kase.response.post
import koncurrent.later.await
import kotlinx.serialization.decodeFromString
import sentinel.params.EmailSignInParams
import sentinel.params.PasswordResetParams
import sentinel.params.SendPasswordResetParams
import sentinel.params.SessionParams

fun Routing.installAuthentication(controller: AuthenticationController) {
    post(controller.endpoint.signIn(), controller.codec) {
        val params = controller.codec.decodeFromString<EmailSignInParams>(call.receiveText())
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

    get(controller.endpoint.delete("{email}", "{password}"), controller.codec) {
        val email: String by call.parameters
        val password: String by call.parameters
        controller.service.delete(EmailSignInParams(email, password)).await()
    }

    get(controller.endpoint.signOut("{token}"), controller.codec) {
        val token: String by call.parameters
        controller.service.signOut(token).await()
    }
}