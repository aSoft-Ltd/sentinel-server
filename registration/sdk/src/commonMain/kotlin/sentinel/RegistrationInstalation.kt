package sentinel

import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.routing.Routing
import kase.response.get
import kase.response.post
import koncurrent.later.await
import sentinel.params.SendVerificationLinkParams
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams

fun Routing.installRegistration(controller: RegistrationController) {
    post(controller.endpoint.signUp(), controller.codec) {
        val params = controller.codec.decodeFromString(SignUpParams.serializer(), call.receiveText())
        controller.service.signUp(params).await()
    }

    post(controller.endpoint.sendEmailVerificationLink(), controller.codec) {
        val params = controller.codec.decodeFromString(SendVerificationLinkParams.serializer(), call.receiveText())
        controller.service.sendVerificationLink(params).await()
    }

    post(controller.endpoint.verifyEmail(), controller.codec) {
        val params = controller.codec.decodeFromString(VerificationParams.serializer(), call.receiveText())
        controller.service.verify(params).await()
    }

    post(controller.endpoint.createAccount(), controller.codec) {
        val params = controller.codec.decodeFromString(UserAccountParams.serializer(), call.receiveText())
        controller.service.createUserAccount(params).await()
    }

    get(controller.endpoint.status(), controller.codec) {
        "Healthy"
    }

    get(controller.endpoint.sendEmailVerificationLink()+"/{path}",controller.codec) {
        val path = call.parameters["path"]
        val params = SendVerificationLinkParams(
            email = "andylamax@programmer.net",
//            email = "andylamax@gmail.com",
            link = "http://192.168.1.119:20031/${path}"
        )
        (controller.service as RegistrationServiceFlix).sendFakeVerificationLink(params,"Anderson Lameck").await()
    }
}