package sentinel

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import koncurrent.Later
import koncurrent.TODOLater
import koncurrent.later
import koncurrent.later.await
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import krono.currentJavaLocalDateTime
import org.bson.types.ObjectId
import raven.EmailDraft
import sentinel.exceptions.UserDidNotBeginRegistrationException
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams
import sentinel.transformers.toAddressInfo
import sentinel.transformers.toDao
import yeti.Template

class RegistrationApiFlix(private val config: RegistrationApiFlixConfig) : RegistrationApi {

    private val col = config.db.getCollection<RegistrationCandidate>("registration.candidates")
    private val mailer = config.mailer

    override fun signUp(params: SignUpParams) = config.scope.later {
        col.insertOne(params.toDao(config.clock))
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
    override fun sendVerificationLink(email: String): Later<String> = config.scope.later {
        val emailAndLink = email.split(">")
        val e = emailAndLink.getOrNull(0) ?: throw IllegalArgumentException("email missing")
        val l = emailAndLink.getOrNull(1) ?: throw IllegalArgumentException("link was not embedded in argument. Use email>link (e.g. andy@lamax.com>http://test.com)")
        val candidates = col.find(eq(RegistrationCandidate::email.name, e)).toList()
        if (candidates.isEmpty()) throw UserDidNotBeginRegistrationException(e)
        val candidate = candidates.first()
        val token = ObjectId().toHexString().chunked(4).joinToString("-")
        coroutineScope {
            val updateTask = async {
                val query = eq(RegistrationCandidate::email.name, e)
                val entry = VerificationToken(
                    on = config.clock.currentJavaLocalDateTime(),
                    to = l,
                    text = token
                )
                val update = Updates.addToSet(RegistrationCandidate::tokens.name, entry)
                col.updateOne(query, update)
            }
            val sendTask = async {
                val message = EmailDraft(
                    subject = config.email.subject,
                    body = Template(config.email.template).compile(
                        "email" to e,
                        "name" to candidate.name,
                        "token" to token
                    )
                )
                mailer.send(draft = message, from = config.email.address, to = candidate.toAddressInfo()).await()
            }
            updateTask.await()
            sendTask.await()
        }
        email
    }

    override fun verify(params: VerificationParams): Later<VerificationParams> = config.scope.later {

        TODO()
    }

    override fun createUserAccount(params: UserAccountParams): Later<UserAccountParams> = TODOLater()
}