package sentinel

import com.mongodb.client.model.Filters.eq
import kash.Currency
import koncurrent.Later
import koncurrent.TODOLater
import koncurrent.later
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import sentinel.exceptions.InvalidCredentialsAuthenticationException
import sentinel.exceptions.UserNotRegisteredForAuthenticationException
import sentinel.params.PasswordResetParams
import sentinel.params.SignInParams
import sentinel.transformers.toCorporate
import sentinel.transformers.toIndividual

class AuthenticationServiceFlix(private val config: AuthenticationServiceFlixConfig) : AuthenticationApi {
    private val col = config.db.getCollection<PersonalAccountDao>(PersonalAccountDao.collection)
    private val mailer = config.mailer
    private val logger by config.logger
    private val actions by lazy { AuthenticationActionMessage() }
    override fun signIn(params: SignInParams): Later<UserSession> = config.scope.later {
        val action = actions.signIn(params.email)
        val person = col.find(eq(PersonalAccountDao::email.name, params.email)).toList().firstOrNull() ?: run {
            throw UserNotRegisteredForAuthenticationException(params.email).also { logger.error(action.failed, it) }
        }
        if (person.password != params.password) {
            throw InvalidCredentialsAuthenticationException().also { logger.error(action.failed, it) }
        }

        UserSession(
            user = person.toIndividual(),
            secret = "${params.email}:${params.password}",
            company = loadCompanyFor(person).toCorporate(),
            currency = Currency.TZS,
            timezone = "UTC",
            salesTax = 0
        ).also { logger.info(action.passed) }
    }

    private suspend fun loadCompanyFor(user: PersonalAccountDao): BusinessAccountDao {
        val pbrCollection = config.db.getCollection<PersonBusinessRelationDao>(PersonBusinessRelationDao.collection)
        val exp = RuntimeException("User with email ${user.email}, seems to not have a business")
        val relation = pbrCollection.find(eq(PersonBusinessRelationDao::person.name, user.uid)).firstOrNull() ?: run {
            throw exp.also { logger.error("failed to load relation", it) }
        }
        val busCollection = config.db.getCollection<BusinessAccountDao>(BusinessAccountDao.collection)
        val business = busCollection.find(eq("_id", relation.business)).firstOrNull() ?: run {
            throw exp.also { logger.error("failed to load company", it) }
        }
        return business
    }

    override fun session(): Later<UserSession> = TODOLater()

    override fun signOut(): Later<Unit> = TODOLater()

    override fun sendPasswordResetLink(email: String): Later<String> = TODOLater()

    override fun resetPassword(params: PasswordResetParams): Later<PasswordResetParams> = TODOLater()
}