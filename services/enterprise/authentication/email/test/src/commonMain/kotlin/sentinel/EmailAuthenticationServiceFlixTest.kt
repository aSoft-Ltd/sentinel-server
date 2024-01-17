package sentinel

import kommander.expect
import koncurrent.later.await
import kotlinx.coroutines.test.runTest
import raven.EmailReceiver
import sentinel.params.EmailSignInParams
import sentinel.params.PasswordResetParams
import sentinel.params.SendPasswordResetParams
import kotlin.test.Test

abstract class EmailAuthenticationServiceFlixTest(
    val registration: EmailRegistrationService,
    val authentication: EmailAuthenticationService,
    val receiver: EmailReceiver
) {

    @Test
    fun should_be_able_to_sign_in_with_a_valid_credential() = runTest {
        val email = "andy@lamax.com"
        registration.register(receiver, name = "Anderson", email = email, password = email)
        val res = authentication.signIn(EmailSignInParams(email, email)).await()
        expect(res.user.name).toBe("Anderson")
    }

    @Test
    fun should_be_able_to_request_the_current_session_from_a_token() = runTest {
        val email = "john@doe.com"
        registration.register(receiver, name = "John Doe", email = email, password = email)
        val session1 = authentication.signIn(EmailSignInParams(email, email)).await()
        val session2 = authentication.session(session1.secret).await()
        expect(session1.secret).toBe(session2.secret)
    }

    @Test
    fun should_be_able_to_recover_a_user_password() = runTest {
        val email = "jane@doe.com"
        registration.register(receiver, name = "Jane Doe", email = email, password = email)
        val mail = receiver.anticipate()
        val params = SendPasswordResetParams(email = "jane@doe.com", link = "http://test.com")
        authentication.sendPasswordResetLink(params).await()
        val token = mail.await().body.split("=").last()
        authentication.resetPassword(PasswordResetParams("new", token)).await()
        authentication.signIn(EmailSignInParams(email, "new")).await()
    }
}