package sentinel

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import koncurrent.Later
import koncurrent.TODOLater
import koncurrent.later
import kotlinx.coroutines.flow.toList
import sentinel.exceptions.UserDidNotBeginRegistrationException
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams
import sentinel.transformers.toDao
import java.lang.IllegalArgumentException

class RegistrationApiFlix(config: RegistrationApiFlixConfig) : RegistrationApi {

    private val scope = config.scope
    private val clock = config.clock
    private val col = config.db.getCollection<SignUpCandidate>("registration.candidates")

    override fun signUp(params: SignUpParams) = scope.later {
        col.insertOne(params.toDao(clock))
        params
    }

    /**
     * To Preserve API structure with the client side, we will encode the email together with the link with a right carat ('>')
     *
     * Example
     * ```kotlin
     * sendVerificationLink("andylamax@gmail.com>https://test.com")
     * ```
     */
    override fun sendVerificationLink(email: String): Later<String> = scope.later {
        val emailAndLink = email.split(">")
        val e = emailAndLink.getOrNull(0) ?: throw IllegalArgumentException("email missing")
        val l = emailAndLink.getOrNull(1) ?: throw IllegalArgumentException("link was not embedded in argument. Use email>link (e.g. andy@lamax.com>http://test.com)")
        val candidates = col.find(Filters.eq(SignUpCandidate::email.name, e)).toList()
        if (candidates.isEmpty()) throw UserDidNotBeginRegistrationException(e)
        val query = Filters.eq(SignUpCandidate::email.name, e)
        val update = Updates.addToSet(SignUpCandidate::sent.name, SignUpCandidate.Entry(clock.currentMillisAsLong(), l))
        col.updateOne(query, update)
        email
    }

    override fun verify(params: VerificationParams): Later<VerificationParams> = TODOLater()

    override fun createUserAccount(params: UserAccountParams): Later<UserAccountParams> = TODOLater()
}