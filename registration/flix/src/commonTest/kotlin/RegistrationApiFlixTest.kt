import com.mongodb.kotlin.client.coroutine.MongoClient
import kommander.expect
import kommander.expectFailure
import koncurrent.later.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import krono.SystemClock
import sentinel.RegistrationApi
import sentinel.RegistrationApiFlix
import sentinel.RegistrationApiFlixConfig
import sentinel.exceptions.UserDidNotBeginRegistrationException
import sentinel.params.SignUpParams
import kotlin.test.Test

class RegistrationApiFlixTest {

    val api: RegistrationApi by lazy {
        val scope = CoroutineScope(SupervisorJob())
        val client = MongoClient.create("mongodb://root:pass@127.0.0.1:27017/")
        val db = client.getDatabase("test-trial")
        RegistrationApiFlix(RegistrationApiFlixConfig(scope, db, SystemClock()))
    }


    @Test
    fun should_be_able_to_begin_the_registration_process() = runTest {
        val res = api.signUp(SignUpParams("Anderson", "andy@lamax.com")).await()
        expect(res.email).toBe("andy@lamax.com")
    }

    @Test
    fun should_be_able_to_send_email_verification_for_a_user_who_has_began_the_registration_process() = runTest {
        val res = api.signUp(SignUpParams("Anderson", "anderson@lamax.com")).await()
        api.sendVerificationLink("${res.email}>https://test.com").await()
    }

    @Test
    fun should_fail_to_send_an_email_verification_for_a_user_who_has_not_began_the_registration_process() = runTest {
        val email = "juma@yahoo.com>https://test.com"
        val exp = expectFailure { api.sendVerificationLink(email).await() }
        expect(exp.message).toBe(UserDidNotBeginRegistrationException("juma@yahoo.com").message)
    }
}