package sentinel

import io.ktor.server.application.call
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

fun Routing.installRegistration(controller: RegistrationController) {
    post(controller.endpoint.signUp(), controller.codec) {
        val params = controller.codec.decodeFromString(EmailSignUpParams.serializer(), call.receiveText())
        controller.service.signUp(params).await()
    }

    post(controller.endpoint.sendEmailVerificationLink(), controller.codec) {
        val params = controller.codec.decodeFromString(SendVerificationLinkParams.serializer(), call.receiveText())
        controller.service.sendVerificationLink(params).await()
    }

    post(controller.endpoint.verifyEmail(), controller.codec) {
        val params = controller.codec.decodeFromString(EmailVerificationParams.serializer(), call.receiveText())
        controller.service.verify(params).await()
    }

    post(controller.endpoint.createAccount(), controller.codec) {
        val params = controller.codec.decodeFromString(UserAccountParams.serializer(), call.receiveText())
        controller.service.createUserAccount(params).await()
    }

    get(controller.endpoint.abort("{email}"), controller.codec) {
        val email: String by call.parameters
        controller.service.abort(email).await()
    }

    get(controller.endpoint.status(), controller.codec) {
        "Healthy"
    }
}