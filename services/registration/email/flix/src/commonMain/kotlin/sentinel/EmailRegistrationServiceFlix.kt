package sentinel

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Updates.set
import koncurrent.Later
import koncurrent.later
import koncurrent.later.await
import kotlinx.coroutines.flow.toList
import krono.currentJavaLocalDateTime
import org.bson.types.ObjectId
import raven.Address
import raven.FactoryParams
import sentinel.exceptions.InvalidTokenForRegistrationException
import sentinel.exceptions.UserWithEmailAlreadyBeganRegistrationException
import sentinel.exceptions.UserWithEmailAlreadyCompletedRegistrationException
import sentinel.exceptions.UserWithEmailDidNotBeginRegistrationException
import sentinel.params.EmailSignUpParams
import sentinel.params.EmailVerificationParams
import sentinel.params.SendVerificationLinkParams
import sentinel.params.UserAccountParams
import sentinel.transformers.toAddress
import sentinel.transformers.toBusinessDao
import sentinel.transformers.toDao
import sentinel.transformers.toPersonDao

class EmailRegistrationServiceFlix(private val options: EmailRegistrationServiceFlixOptions) : EmailRegistrationService {

    private val collection by lazy { Collection() }
    private val sender = options.sender
    private val logger by options.logger
    private val actions by lazy { EmailRegistrationActionMessage() }

    inner class Collection {
        val candidate by lazy {
            options.database.registration.getCollection<EmailRegistrationCandidateDao>(EmailRegistrationCandidateDao.collection)
        }

        val personal by lazy {
            options.database.authentication.getCollection<PersonalAccountDao>(PersonalAccountDao.collection)
        }

        val business by lazy {
            options.database.authentication.getCollection<BusinessAccountDao>(BusinessAccountDao.collection)
        }

        val relation by lazy {
            options.database.authentication.getCollection<PersonBusinessRelationDao>(PersonBusinessRelationDao.collection)
        }
    }

    private suspend fun candidateWith(email: String): EmailRegistrationCandidateDao? {
        val col = collection.candidate
        val candidates = col.find<EmailRegistrationCandidateDao>(eq(EmailRegistrationCandidateDao::email.name, email)).toList()
        return candidates.firstOrNull()
    }

    override fun signUp(params: EmailSignUpParams) = options.scope.later {
        val tracer = logger.trace(actions.signUp(params.email))
        if(collection.personal.find(eq(PersonalAccountDao::email.name,params.email)).toList().isNotEmpty()) {
            throw UserWithEmailAlreadyCompletedRegistrationException(params.email).also { tracer.failed(it) }
        }
        val candidate = candidateWith(email = params.email)
        if (candidate != null) throw when (candidate.verified) {
            true -> UserWithEmailAlreadyCompletedRegistrationException(params.email)
            false -> UserWithEmailAlreadyBeganRegistrationException(params.email)
        }.also {
            tracer.failed(it)
        }
        collection.candidate.insertOne(params.toDao(options.clock))
        tracer.passed()
        params
    }

    override fun sendVerificationLink(params: SendVerificationLinkParams): Later<String> = options.scope.later {
        val tracer = logger.trace(actions.sendVerificationLink(params.email))
        val email = params.email
        val link = params.link
        val col = collection.candidate
        val candidates = col.find(eq(EmailRegistrationCandidateDao::email.name, email)).toList()
        if (candidates.isEmpty()) throw UserWithEmailDidNotBeginRegistrationException(email).also { tracer.failed(it) }
        val candidate = candidates.first()
        val token = ObjectId().toHexString().chunked(4).joinToString("-")

        val query = eq(EmailRegistrationCandidateDao::email.name, email)
        val entry = VerificationTokenDao(
            on = options.clock.currentJavaLocalDateTime(),
            to = link,
            text = token
        )
        val update = Updates.addToSet(EmailRegistrationCandidateDao::tokens.name, entry)
        col.updateOne(query, update)

        val fp = FactoryParams(candidate.toAddress(), "${params.link}?token=$token", params.meta)
        sender.send(options.verification.factory(fp)).await()
        tracer.passed()
        params.email
    }

    fun sendFakeVerificationLink(params: SendVerificationLinkParams, name: String) = options.scope.later {
        val to = Address(email = params.email, name = name)
        val fp = FactoryParams(to, "${params.link}?token=fake-token", "test")
        sender.send(options.verification.factory(fp)).await()
        params
    }

    override fun verify(params: EmailVerificationParams): Later<EmailVerificationParams> = options.scope.later {
        val tracer = logger.trace(actions.verify(params.email))
        val candidate = candidateWith(params.email) ?: throw UserWithEmailDidNotBeginRegistrationException(params.email).also { tracer.failed(it) }
        if (params.token !in candidate.tokens.map { it.text }) {
            throw InvalidTokenForRegistrationException(params.token).also { tracer.failed(it) }
        }
        val query = eq(EmailRegistrationCandidateDao::email.name, params.email)
        val update = set(EmailRegistrationCandidateDao::verified.name, true)
        collection.candidate.updateOne(query, update)
        tracer.passed()
        params
    }

    override fun createUserAccount(params: UserAccountParams): Later<UserAccountParams> = options.scope.later {
        val tracer = logger.trace(actions.createAccount(params.loginId))
        val candidate = candidateWith(params.loginId) ?: throw UserWithEmailDidNotBeginRegistrationException(params.loginId).also { tracer.failed(it) }
        val tokens = candidate.tokens.map { it.text }
        if (!tokens.contains(params.registrationToken)) {
            throw InvalidTokenForRegistrationException(params.registrationToken).also { tracer.failed(it) }
        }

        val people = collection.personal.find(eq(PersonalAccountDao::email.name, params.loginId)).toList()
        if (people.isNotEmpty()) {
            throw UserWithEmailAlreadyCompletedRegistrationException(params.loginId).also { tracer.failed(it) }
        }

        val person = collection.personal.insertOne(params.toPersonDao(candidate.uid!!, candidate.name))

        val business = collection.business.insertOne(params.toBusinessDao(candidate.name))

        val pbr = PersonBusinessRelationDao(
            business = business.insertedId!!.asObjectId().value,
            person = person.insertedId!!.asObjectId().value
        )
        collection.relation.insertOne(pbr)
        collection.candidate.deleteMany(eq(EmailRegistrationCandidateDao::email.name, params.loginId))
        tracer.passed()
        params
    }

    override fun abort(email: String): Later<String> = options.scope.later {
        val tracer = logger.trace(actions.abort(email))
        candidateWith(email) ?: throw UserWithEmailDidNotBeginRegistrationException(email).also { tracer.failed(it) }
        collection.candidate.deleteMany(eq(EmailRegistrationCandidateDao::email.name, email))
        tracer.passed()
        email
    }
}