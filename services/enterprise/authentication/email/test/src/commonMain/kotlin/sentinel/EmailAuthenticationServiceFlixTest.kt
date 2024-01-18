package sentinel

import kommander.expect
import kommander.expectFailure
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
    fun should_be_able_to_sign_out_after_signing_in() = runTest {
        val email = "flaky@test.com"
        registration.register(receiver, name = "Flaky User", email = email, password = email)
        val session = authentication.signIn(EmailSignInParams(email, email)).await()
        authentication.signOut(session.secret).await()
        val err = expectFailure { authentication.session(session.secret).await() }
        authentication.delete(EmailSignInParams(email,email)).await()
        expect(err.message).toBe("Invalid credentials")
    }

    @Test
    fun should_be_able_to_delete_an_already_registered_account() = runTest {
        val email = "deleter@lamax.com"
        registration.register(receiver, name = "Deleter", email = email, password = email)
        authentication.delete(EmailSignInParams(email,email)).await()
        val err = expectFailure { authentication.signIn(EmailSignInParams(email, email)).await() }
        expect(err.message).toBe("User with email ($email) has not been registered")
    }

    @Test
    fun should_be_able_to_sign_in_with_a_valid_credential() = runTest {
        val email = "andy@lamax.com"
        registration.register(receiver, name = "Anderson", email = email, password = email)
        val params = EmailSignInParams(email, email)
        val res = authentication.signIn(params).await()
        authentication.delete(params).await()
        expect(res.user.name).toBe("Anderson")
    }

    @Test
    fun should_be_able_to_request_the_current_session_from_a_token() = runTest {
        val email = "john@doe.com"
        registration.register(receiver, name = "John Doe", email = email, password = email)
        val params = EmailSignInParams(email, email)
        val session1 = authentication.signIn(params).await()
        val session2 = authentication.session(session1.secret).await()
        authentication.delete(params).await()
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
        authentication.delete(EmailSignInParams(email, "new")).await()
    }
}