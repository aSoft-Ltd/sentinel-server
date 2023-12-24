package sentinel

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Updates.set
import koncurrent.Later
import koncurrent.later.then
import koncurrent.later.andThen
import koncurrent.later.andZip
import koncurrent.later.zip
import koncurrent.later.catch
import koncurrent.later
import koncurrent.later.await
import kotlinx.coroutines.flow.toList
import krono.currentJavaLocalDateTime
import org.bson.types.ObjectId
import raven.Address
import raven.EmailTemplate
import raven.SendEmailParams
import sentinel.exceptions.InvalidTokenForRegistrationException
import sentinel.exceptions.UserAlreadyBeganRegistrationException
import sentinel.exceptions.UserAlreadyCompletedRegistrationException
import sentinel.exceptions.UserDidNotBeginRegistrationException
import sentinel.params.SendVerificationLinkParams
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams
import sentinel.transformers.toAddress
import sentinel.transformers.toBusinessDao
import sentinel.transformers.toDao
import sentinel.transformers.toPersonDao
import yeti.Template

class RegistrationServiceFlix(private val options: RegistrationServiceFlixOptions) : RegistrationService {

    private val collection by lazy { Collection() }
    private val sender = options.sender
    private val logger by options.logger
    private val actions by lazy { RegistrationActionMessage() }

    inner class Collection {
        val candidate by lazy {
            options.database.registration.getCollection<RegistrationCandidateDao>(RegistrationCandidateDao.collection)
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

    private suspend fun candidateWith(email: String): RegistrationCandidateDao? {
        val col = collection.candidate
        val candidates = col.find<RegistrationCandidateDao>(eq(RegistrationCandidateDao::email.name, email)).toList()
        return candidates.firstOrNull()
    }

    override fun signUp(params: SignUpParams) = options.scope.later {
        val tracer = logger.trace(actions.signUp(params.email))
        val candidate = candidateWith(email = params.email)
        if (candidate != null) throw when (candidate.verified) {
            true -> UserAlreadyCompletedRegistrationException(params.email)
            false -> UserAlreadyBeganRegistrationException(params.email)
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
        val candidates = col.find(eq(RegistrationCandidateDao::email.name, email)).toList()
        if (candidates.isEmpty()) throw UserDidNotBeginRegistrationException(email).also { tracer.failed(it) }
        val candidate = candidates.first()
        val token = ObjectId().toHexString().chunked(4).joinToString("-")

        val query = eq(RegistrationCandidateDao::email.name, email)
        val entry = VerificationTokenDao(
            on = options.clock.currentJavaLocalDateTime(),
            to = link,
            text = token
        )
        val update = Updates.addToSet(RegistrationCandidateDao::tokens.name, entry)
        col.updateOne(query, update)

        sender.send(options.verification.params(candidate.toAddress(), "${params.link}?token=$token")).await()
        tracer.passed()
        params.email
    }

    fun sendFakeVerificationLink(params: SendVerificationLinkParams, name: String) = options.scope.later {
        val to = Address(email = params.email, name = name)
        sender.send(options.verification.params(to, "${params.link}?token=fake-token")).await()
        params
    }

    override fun verify(params: VerificationParams): Later<VerificationParams> = options.scope.later {
        val tracer = logger.trace(actions.verify(params.email))
        val candidate = candidateWith(params.email) ?: throw UserDidNotBeginRegistrationException(params.email).also { tracer.failed(it) }
        if (params.token !in candidate.tokens.map { it.text }) {
            throw InvalidTokenForRegistrationException(params.token).also { tracer.failed(it) }
        }
        val query = eq(RegistrationCandidateDao::email.name, params.email)
        val update = set(RegistrationCandidateDao::verified.name, true)
        collection.candidate.updateOne(query, update)
        tracer.passed()
        params
    }

    override fun createUserAccount(params: UserAccountParams): Later<UserAccountParams> = options.scope.later {
        val tracer = logger.trace(actions.createAccount(params.loginId))
        val candidate = candidateWith(params.loginId) ?: throw UserDidNotBeginRegistrationException(params.loginId).also { tracer.failed(it) }
        val tokens = candidate.tokens.map { it.text }
        if (!tokens.contains(params.registrationToken)) {
            throw InvalidTokenForRegistrationException(params.registrationToken).also { tracer.failed(it) }
        }

        val people = collection.personal.find(eq(PersonalAccountDao::email.name, params.loginId)).toList()
        if (people.isNotEmpty()) {
            throw UserAlreadyCompletedRegistrationException(params.loginId).also { tracer.failed(it) }
        }

        val person = collection.personal.insertOne(params.toPersonDao(candidate.uid!!, candidate.name))

        val business = collection.business.insertOne(params.toBusinessDao(candidate.name))

        val pbr = PersonBusinessRelationDao(
            business = business.insertedId!!.asObjectId().value,
            person = person.insertedId!!.asObjectId().value
        )
        collection.relation.insertOne(pbr)
        tracer.passed()
        params
    }
}