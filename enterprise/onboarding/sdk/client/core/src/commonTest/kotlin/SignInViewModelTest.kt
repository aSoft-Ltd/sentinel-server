import authenticator.SignInParams
import authenticator.signin.SignInState
import authenticator.signin.UnInitialized
import kommander.expect
import kommander.toBe
import kommander.expect
import kotlinx.coroutines.test.runTest
import picortex.PiCortexAppScenes
import kotlin.test.Ignore
import kotlin.test.Test

class SignInViewModelTest {
    val scope: PiCortexAppScenes = PiCortexAppScopeTest()
    private val vm = scope.signIn

    @Test
    fun should_be_in_a_show_form_state_with_null_credentials_when_intent_with_null_credentials_is_posted() = runTest {
        expect<SignInState>(vm.ui.value).toBe<UnInitialized>()
    }

    @Test
    @Ignore // Migrate these tests properly
    fun should_be_in_a_conundrum_state_when_a_user_has_more_then_one_space() = runTest {
        val credentials = SignInParams("user1@test.com", "pass2")
//        val credentials = SignInParams("johnk@gmail.me", "1xhvulepzh")
//        val expectation = vm.expect {
//            (submitSignInForm(credentials) as Job).join()
//        }
//
//        val state = expectation.toBeIn<SignInState.Form>()
//        println(expectation.value.joinToString("\n") { it.toString() })
//        vm.expect(SignInIntent.Submit(credentials))
//        val state = vm.ui.value as SignInState.Form
//        with(Dispatchers.Default) {
//            delay(2000)
//        }
//        expect(state.status).toBe<Feedback.Success>()
    }

    @Test
    fun should_get_instance_of_scope() {

    }
}