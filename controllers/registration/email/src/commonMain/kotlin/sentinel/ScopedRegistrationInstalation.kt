package sentinel

import io.ktor.server.application.call
import io.ktor.server.request.header
import io.ktor.server.request.receiveText
import io.ktor.server.routing.Routing
import io.ktor.server.util.getValue
import kase.response.get
import kase.response.post
import koncurrent.later.await
import sentinel.params.EmailSignUpParams
import sentinel.params.SendVerificationLinkParams
import sentinel.params.UserAccountParams
import sentinel.params.EmailVerificationParams

fun Routing.installScopedRegistration(controller: ScopedRegistrationController) {
    post(controller.endpoint.signUp(), controller.codec) {
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val params = controller.codec.decodeFromString(EmailSignUpParams.serializer(), call.receiveText())
        val service = controller.service(scope)
        service.signUp(params).await()
    }

    post(controller.endpoint.sendEmailVerificationLink(), controller.codec) {
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val params = controller.codec.decodeFromString(SendVerificationLinkParams.serializer(), call.receiveText())
        val service = controller.service(scope)
        service.sendVerificationLink(params).await()
    }

    post(controller.endpoint.verifyEmail(), controller.codec) {
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val params = controller.codec.decodeFromString(EmailVerificationParams.serializer(), call.receiveText())
        val service = controller.service(scope)
        service.verify(params).await()
    }

    post(controller.endpoint.createAccount(), controller.codec) {
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val params = controller.codec.decodeFromString(UserAccountParams.serializer(), call.receiveText())
        val service = controller.service(scope)
        service.createUserAccount(params).await()
    }

    get(controller.endpoint.abort("{email}"), controller.codec) {
        val scope = call.request.header("x-monitor-scope") ?: throw IllegalArgumentException("No scope provided")
        val email: String by call.parameters
        val service = controller.service(scope)
        service.abort(email).await()
    }

    get(controller.endpoint.status(), controller.codec) {
        "Healthy"
    }
}