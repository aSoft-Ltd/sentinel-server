package sentinel

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import kash.Currency
import koncurrent.Later
import koncurrent.later
import koncurrent.later.await
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import raven.Address
import raven.FactoryParams
import sentinel.daos.PasswordResetSessionDao
import sentinel.daos.SessionDao
import sentinel.exceptions.InvalidCredentialsAuthenticationException
import sentinel.exceptions.UserNotRegisteredForAuthenticationException
import sentinel.params.EmailSignInParams
import sentinel.params.PasswordResetParams
import sentinel.params.SendPasswordResetParams
import sentinel.transformers.toCorporate
import sentinel.transformers.toIndividual

class EmailAuthenticationServiceFlix(private val options: EmailAuthenticationServiceFlixOptions) : EmailAuthenticationService {
    private val col = options.database.getCollection<PersonalAccountDao>(PersonalAccountDao.collection)
    private val sender = options.sender
    private val logger by options.logger
    private val actions by lazy { AuthenticationActionMessage() }
    override fun signIn(params: EmailSignInParams): Later<UserSession> = options.scope.later {
        val tracer = logger.trace(actions.signIn(params.email))
        val person = col.find(eq(PersonalAccountDao::email.name, params.email)).toList().firstOrNull() ?: run {
            throw UserNotRegisteredForAuthenticationException(params.email).also { tracer.failed(it) }
        }
        if (person.password != params.password) {
            throw InvalidCredentialsAuthenticationException().also { tracer.failed(it) }
        }

        val user = person.toIndividual()
        val company = loadCompanyFor(person).toCorporate()

        val session = SessionDao(
            token = ObjectId.get().toHexString()?.chunked(4)?.joinToString("-") ?: throw RuntimeException("Failed to create a session token"),
            user = ObjectId(user.uid),
            company = ObjectId(company.uid)
        )

        options.database.getCollection<SessionDao>(SessionDao.collection).insertOne(session)

        UserSession(
            user = user,
            secret = session.token,
            company = company,
            currency = Currency.TZS,
            timezone = "UTC",
            salesTax = 0
        ).also { tracer.passed() }
    }

    private suspend fun loadCompanyFor(user: PersonalAccountDao): BusinessAccountDao {
        val pbrCollection = options.database.getCollection<PersonBusinessRelationDao>(PersonBusinessRelationDao.collection)
        val exp = RuntimeException("User with email ${user.email}, seems to not have a business")
        val relation = pbrCollection.find(eq(PersonBusinessRelationDao::person.name, user.uid)).firstOrNull() ?: run {
            throw exp.also { logger.error("failed to load relation", it) }
        }
        val busCollection = options.database.getCollection<BusinessAccountDao>(BusinessAccountDao.collection)
        val business = busCollection.find(eq("_id", relation.business)).firstOrNull() ?: run {
            throw exp.also { logger.error("failed to load company", it) }
        }
        return business
    }

    override fun session(token: String): Later<UserSession> = options.scope.later {
        val tracer = logger.trace(actions.session())
        val sessionCollection = options.database.getCollection<SessionDao>(SessionDao.collection)
        val session = sessionCollection.find<SessionDao>(eq(SessionDao::token.name, token)).firstOrNull() ?: run {
            throw InvalidCredentialsAuthenticationException().also { tracer.failed(it) }
        }

        val user = col.find(eq("_id", session.user)).firstOrNull()?.toIndividual() ?: run {
            throw IllegalStateException("somehow a user with a session is not registered").also { tracer.failed(it) }
        }

        val companyCollection = options.database.getCollection<BusinessAccountDao>(BusinessAccountDao.collection)
        val company = companyCollection.find(eq("_id", session.company)).firstOrNull()?.toCorporate() ?: run {
            throw IllegalStateException("somehow a company with a session is not registered").also { tracer.failed(it) }
        }

        UserSession(
            user = user,
            secret = session.token,
            company = company,
            currency = Currency.TZS,
            timezone = "UTC",
            salesTax = 0
        ).also { tracer.passed() }
    }

    override fun signOut(token: String): Later<UserSession> = options.scope.later {
        val tracer = logger.trace(actions.signOut(token))
        val session = session(token).await()
        options.database.getCollection<SessionDao>(SessionDao.collection).deleteMany(eq(SessionDao::token.name, token))
        tracer.passed()
        session
    }

    override fun sendPasswordResetLink(params: SendPasswordResetParams): Later<String> = options.scope.later {
        val email = params.email
        val tracer = logger.trace(actions.sendPasswordResetLink(email))
        val person = col.find(eq(PersonalAccountDao::email.name, email)).toList().firstOrNull() ?: run {
            throw UserNotRegisteredForAuthenticationException(email).also { tracer.failed(it) }
        }

        val token = ObjectId.get()
        val insert = async {
            val collection = options.database.getCollection<PasswordResetSessionDao>(PasswordResetSessionDao.collection)
            val dao = PasswordResetSessionDao(
                person = person.uid,
                token = token,
                email = email
            )
            collection.insertOne(dao)
        }

        val send = async {
            val link = "${params.link}?token=${token.toHexString().chunked(4).joinToString("-")}"
            val fp = FactoryParams(Address(email = email, name = person.name), link, params.meta)
            sender.send(options.email.factory(fp)).await()
        }

        insert.await(); send.await()
        tracer.passed()
        email
    }

    override fun resetPassword(params: PasswordResetParams): Later<PasswordResetParams> = options.scope.later {
        val log = logger.trace(actions.resetPassword(params.passwordResetToken))
        val token = params.passwordResetToken?.replace("-", "") ?: run {
            throw InvalidCredentialsAuthenticationException().also { log.failed(it) }
        }
        val resetCollection = options.database.getCollection<PasswordResetSessionDao>(PasswordResetSessionDao.collection)
        val session = resetCollection.find(eq(PasswordResetSessionDao::token.name, ObjectId(token))).firstOrNull() ?: run {
            throw InvalidCredentialsAuthenticationException().also { log.failed(it) }
        }

        val peopleCollection = options.database.getCollection<PersonalAccountDao>(PersonalAccountDao.collection)
        peopleCollection.updateOne(eq("_id", session.person), Updates.set(PersonalAccountDao::password.name, params.password))
        log.passed()
        params
    }

    override fun delete(params: EmailSignInParams): Later<EmailSignInParams> = options.scope.later {
        val tracer = logger.trace(actions.delete(params.email))
        val person = col.find(eq(PersonalAccountDao::email.name, params.email)).toList().firstOrNull() ?: run {
            throw UserNotRegisteredForAuthenticationException(params.email).also { tracer.failed(it) }
        }
        if (person.password != params.password) {
            throw InvalidCredentialsAuthenticationException().also { tracer.failed(it) }
        }
        col.deleteOne(eq(PersonalAccountDao::email.name, params.email))
        val sessionCollection = options.database.getCollection<SessionDao>(SessionDao.collection)
        sessionCollection.deleteMany(eq(SessionDao::user.name, person.uid))
        tracer.passed()
        params
    }
}