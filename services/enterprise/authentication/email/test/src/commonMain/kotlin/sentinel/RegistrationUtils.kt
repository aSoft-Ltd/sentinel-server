package sentinel

import koncurrent.later.await
import raven.EmailReceiver
import sentinel.params.EmailSignUpParams
import sentinel.params.EmailVerificationParams
import sentinel.params.SendVerificationLinkParams
import sentinel.params.UserAccountParams

suspend fun EmailRegistrationService.register(
    receiver: EmailReceiver,
    name: String,
    email: String,
    password: String,
) {
    val params1 = EmailSignUpParams(name, email)
    val res = signUp(params1).await()
    val params2 = SendVerificationLinkParams(email = res.email, link = "https://test.com")

    val mail = receiver.anticipate()
    sendVerificationLink(params2).await()

    val token = mail.await().body.split("=").last()

    verify(EmailVerificationParams(email = res.email, token = token)).await()

    createUserAccount(UserAccountParams(email, password, token)).await()
}