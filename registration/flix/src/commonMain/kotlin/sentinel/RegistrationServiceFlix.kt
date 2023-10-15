package sentinel

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Updates.set
import koncurrent.Later
import koncurrent.later
import koncurrent.later.await
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import krono.currentJavaLocalDateTime
import org.bson.types.ObjectId
import raven.EmailDraft
import sentinel.exceptions.InvalidTokenForRegistrationException
import sentinel.exceptions.UserAlreadyBeganRegistrationException
import sentinel.exceptions.UserAlreadyCompletedRegistrationException
import sentinel.exceptions.UserDidNotBeginRegistrationException
import sentinel.params.SendVerificationLinkParams
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams
import sentinel.transformers.toAddressInfo
import sentinel.transformers.toBusinessDao
import sentinel.transformers.toDao
import sentinel.transformers.toPersonDao
import yeti.Template

class RegistrationServiceFlix(private val config: RegistrationServiceFlixConfig) : RegistrationService {

    private val col = config.db.getCollection<RegistrationCandidateDao>(RegistrationCandidateDao.collection)
    private val mailer = config.mailer
    private val logger by config.logger
    private val actions by lazy { RegistrationActionMessage() }

    private suspend fun candidateWith(email: String): RegistrationCandidateDao? {
        val candidates = col.find<RegistrationCandidateDao>(eq(RegistrationCandidateDao::email.name, email)).toList()
        return candidates.firstOrNull()
    }

    override fun signUp(params: SignUpParams) = config.scope.later {
        val action = actions.signUp(params.email)
        logger.info(action.begin)
        val candidate = candidateWith(email = params.email)
        if (candidate != null) when (candidate.verified) {
            true -> UserAlreadyCompletedRegistrationException(params.email)
            false -> UserAlreadyBeganRegistrationException(params.email)
        }.also {
            logger.error(action.failed, it)
            throw it
        }
        col.insertOne(params.toDao(config.clock))
        logger.info(action.passed)
        params
    }

    override fun sendVerificationLink(params: SendVerificationLinkParams): Later<String> = config.scope.later {
        val action = actions.sendVerificationLink(params.email)
        logger.info(action.begin)
        val email = params.email
        val link = params.link
        val candidates = col.find(eq(RegistrationCandidateDao::email.name, email)).toList()
        if (candidates.isEmpty()) throw UserDidNotBeginRegistrationException(email).also { logger.error(action.failed, it) }
        val candidate = candidates.first()
        val token = ObjectId().toHexString().chunked(4).joinToString("-")
        coroutineScope {
            val updateTask = async {
                val query = eq(RegistrationCandidateDao::email.name, email)
                val entry = VerificationTokenDao(
                    on = config.clock.currentJavaLocalDateTime(),
                    to = link,
                    text = token
                )
                val update = Updates.addToSet(RegistrationCandidateDao::tokens.name, entry)
                col.updateOne(query, update)
            }
            val sendTask = async {
                val message = EmailDraft(
                    subject = config.email.subject,
                    body = Template(config.email.template).compile(
                        "email" to email,
                        "name" to candidate.name,
                        "token" to token
                    )
                )
                mailer.send(draft = message, from = config.email.address, to = candidate.toAddressInfo()).await()
            }
            updateTask.await()
            sendTask.await()
        }
        logger.info(action.passed)
        params.email
    }

    override fun verify(params: VerificationParams): Later<VerificationParams> = config.scope.later {
        val action = actions.verify(params.email)
        logger.info(action.begin)
        val candidate = candidateWith(params.email) ?: throw UserDidNotBeginRegistrationException(params.email).also { logger.error(action.failed, it) }
        if (candidate.verified) {
            throw UserAlreadyCompletedRegistrationException(params.email).also { logger.error(action.failed, it) }
        }
        if (candidate.tokens.last().text != params.token) {
            throw InvalidTokenForRegistrationException(params.token).also { logger.error(action.failed, it) }
        }
        val query = eq(RegistrationCandidateDao::email.name, params.email)
        val update = set(RegistrationCandidateDao::verified.name, true)
        col.updateOne(query, update)
        logger.info(action.passed)
        params
    }

    override fun createUserAccount(params: UserAccountParams): Later<UserAccountParams> = config.scope.later {
        val action = actions.createAccount(params.loginId)
        logger.info(action.begin)
        val candidate = candidateWith(params.loginId) ?: throw UserDidNotBeginRegistrationException(params.loginId).also { logger.error(action.failed, it) }
        val tokens = candidate.tokens.map { it.text }
        if (!tokens.contains(params.registrationToken)) {
            throw InvalidTokenForRegistrationException(params.registrationToken).also { logger.error(action.failed, it) }
        }

        val personalCollection = config.db.getCollection<PersonalAccountDao>(PersonalAccountDao.collection)
        val people = personalCollection.find(eq(PersonalAccountDao::email.name, params.loginId)).toList()
        if (people.isNotEmpty()) {
            throw UserAlreadyCompletedRegistrationException(params.loginId).also { logger.error(action.failed, it) }
        }

        val person = personalCollection.insertOne(params.toPersonDao(candidate.uid!!, candidate.name))

        val businessCollection = config.db.getCollection<BusinessAccountDao>(BusinessAccountDao.collection)
        val business = businessCollection.insertOne(params.toBusinessDao(candidate.name))

        val personBusiness = config.db.getCollection<PersonBusinessRelationDao>(PersonBusinessRelationDao.collection)
        val pbr = PersonBusinessRelationDao(
            business = business.insertedId!!.asObjectId().value,
            person = person.insertedId!!.asObjectId().value
        )
        personBusiness.insertOne(pbr)
        logger.info(action.passed)
        params
    }
}