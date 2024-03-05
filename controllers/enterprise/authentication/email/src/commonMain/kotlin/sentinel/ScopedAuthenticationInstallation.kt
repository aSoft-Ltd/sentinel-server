package sentinel

import io.ktor.server.application.call
import io.ktor.server.request.header
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

fun Routing.installScopedAuthentication(controller: ScopedAuthenticationController) {
    post(controller.endpoint.signIn(), controller.codec) {
        val params = controller.codec.decodeFromString<EmailSignInParams>(call.receiveText())
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val service = controller.service(scope)
        service.signIn(params).await()
    }

    post(controller.endpoint.session(), controller.codec) {
        val params = controller.codec.decodeFromString<SessionParams>(call.receiveText())
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val service = controller.service(scope)
        service.session(params.token).await()
    }

    post(controller.endpoint.sendPasswordResetLink(), controller.codec) {
        val params = controller.codec.decodeFromString<SendPasswordResetParams>(call.receiveText())
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val service = controller.service(scope)
        service.sendPasswordResetLink(params).await()
    }

    post(controller.endpoint.resetPassword(), controller.codec) {
        val params = controller.codec.decodeFromString<PasswordResetParams>(call.receiveText())
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val service = controller.service(scope)
        service.resetPassword(params).await()
    }

    get(controller.endpoint.delete("{email}", "{password}"), controller.codec) {
        val email: String by call.parameters
        val password: String by call.parameters
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val service = controller.service(scope)
        service.delete(EmailSignInParams(email, password)).await()
    }

    get(controller.endpoint.signOut("{token}"), controller.codec) {
        val token: String by call.parameters
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val service = controller.service(scope)
        service.signOut(token).await()
    }
}