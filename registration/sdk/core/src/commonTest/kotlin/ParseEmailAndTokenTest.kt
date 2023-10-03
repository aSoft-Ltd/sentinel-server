import kommander.expect
import kommander.expectFailure
import kotlinx.coroutines.test.runTest
import sentinel.VerificationScene
import kotlin.test.Test

class ParseEmailAndTokenTest {

    @Test
    fun should_parse_email_and_token_if_they_are_all_present() = runTest {
        val link = "https://picortex.com/verify?email=andy@lama.com&token=token-1"
        val token = VerificationScene.parseToken(link).getOrThrow()
        expect(token).toBe("token-1")
    }

    @Test
    fun should_fail_to_parse_link_when_it_does_not_have_an_email() = runTest {
        val link = "https://picortex.com/verify"
        val error = expectFailure { VerificationScene.parseToken(link).getOrThrow() }
        expect(error.message).toBe(VerificationScene.TOKEN_NOT_FOUND_IN_LINK.message)
    }
}