package sentinel

import kommander.expect
import kommander.expectFailure
import kommander.toContain
import koncurrent.later.await
import kotlinx.coroutines.test.runTest
import raven.EmailReceiver
import raven.MockEmailSender
import sentinel.exceptions.InvalidTokenForRegistrationException
import sentinel.exceptions.UserWithEmailAlreadyCompletedRegistrationException
import sentinel.exceptions.UserWithEmailDidNotBeginRegistrationException
import sentinel.params.EmailSignUpParams
import sentinel.params.EmailVerificationParams
import sentinel.params.SendVerificationLinkParams
import kotlin.test.Test

abstract class EmailRegistrationServiceFlixTest(
    private val service: EmailRegistrationService,
    private val receiver: EmailReceiver,
    private val sender: MockEmailSender,
    private val meta: String = ""
) {

    private val link = "https://test.com"

    private fun String.toSendVerificationLinkParams() = SendVerificationLinkParams(this, link, meta)

    @Test
    fun should_be_able_to_abort_registration() = runTest {
        val email = "aborter@test.com"
        service.signUp(EmailSignUpParams("Test Aborter", email)).await()
        service.abort(email).await()
        val err = expectFailure { service.sendVerificationLink(email.toSendVerificationLinkParams()).await() }
        expect(err.message).toBe("User with email ($email) has not began registration yet")
    }

    @Test
    fun should_be_able_to_send_email_verification_for_a_user_who_has_began_the_registration_process() = runTest {
        val res = service.signUp(EmailSignUpParams("Pepper Pots", "pepper@lamax.com")).await()
        val params = res.email.toSendVerificationLinkParams()
        val email = receiver.anticipate()
        service.sendVerificationLink(params).await()
        val message = email.await()
        service.abort("pepper@lamax.com").await()
        expect(message.body).toContain("Hi Pepper Pots")
    }

    @Test
    fun should_be_able_to_complete_registration() = runTest {
        val params1 = EmailSignUpParams("Tony Stark", "tony@stark.com")
        val res = service.signUp(params1).await()
        val params2 = res.email.toSendVerificationLinkParams()

        val email = receiver.anticipate()
        service.sendVerificationLink(params2).await()
        val link = email.await().body.split(" ").last()
        val token = link.split("=").last()

        service.verify(EmailVerificationParams(email = res.email, token = token)).await()

        val exp = expectFailure { service.signUp(params1).await() }
        service.abort("tony@stark.com")
        expect(exp.message).toBe(UserWithEmailAlreadyCompletedRegistrationException(params1.email).message)
    }

    @Test
    fun should_be_able_to_verify_multiple_times() = runTest {
        val params1 = EmailSignUpParams("Steve Rogers", "steve@rogers.com")
        val res = service.signUp(params1).await()
        val params2 = res.email.toSendVerificationLinkParams()

        val email = receiver.anticipate()
        service.sendVerificationLink(params2).await()
        val link = email.await().body.split(" ").last()
        val token = link.split("=").last()

        repeat(10) {
            service.verify(EmailVerificationParams(email = res.email, token = token)).await()
        }

        val exp = expectFailure { service.signUp(params1).await() }

        service.abort("steve@rogers.com").await()

        expect(exp.message).toBe(UserWithEmailAlreadyCompletedRegistrationException(params1.email).message)
    }

    @Test
    fun should_be_able_to_complete_registration_with_any_token() = runTest {
        val params1 = EmailSignUpParams("Jason Vorhes", "jason@vorhes.com")
        val res = service.signUp(params1).await()
        val params2 = params1.email.toSendVerificationLinkParams()

        sender.outbox.clear()
        repeat(10) { service.sendVerificationLink(params2).await() }

        val email = sender.outbox.random()
        val link = email.body.split(" ").last()
        val token = link.split("=").last()

        service.verify(EmailVerificationParams(email = res.email, token = token)).await()

        val exp = expectFailure { service.signUp(params1).await() }
        service.abort(params1.email).await()
        expect(exp.message).toBe(UserWithEmailAlreadyCompletedRegistrationException(params1.email).message)
    }

    @Test
    fun should_fail_to_verify_a_rogue_token() = runTest {
        val res = service.signUp(EmailSignUpParams("Wanda Max", "wanda@max.com")).await()
        val params1 = res.email.toSendVerificationLinkParams()
        service.sendVerificationLink(params1).await()
        val params2 = EmailVerificationParams(email = res.email, token = "garbage")
        val exp = expectFailure { service.verify(params2).await() }
        service.abort("wanda@max.com").await()
        expect(exp.message).toBe(InvalidTokenForRegistrationException(params2.token).message)
    }

    @Test
    fun should_fail_to_send_an_email_verification_for_a_user_who_has_not_began_the_registration_process() = runTest {
        val params = "juma@yahoo.com".toSendVerificationLinkParams()
        val exp = expectFailure { service.sendVerificationLink(params).await() }
        expect(exp.message).toBe(UserWithEmailDidNotBeginRegistrationException("juma@yahoo.com").message)
    }
}