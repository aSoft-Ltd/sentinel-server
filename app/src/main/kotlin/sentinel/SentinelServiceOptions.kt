package sentinel

import com.mongodb.kotlin.client.coroutine.MongoClient
import grape.MongoDatabaseConfiguration
import grape.MongoServiceOptions
import kotlinx.coroutines.CoroutineScope
import krono.SystemClock
import lexi.LoggerFactory
import raven.EmailSender
import raven.MultiEmailSender
import raven.TemplatedEmailOptions
import sanity.EventBus

class SentinelServiceOptions(
    database: MongoDatabaseConfiguration,
    val bus: EventBus,
    val logger: LoggerFactory,
    val sender: MultiEmailSender,
    val scope: CoroutineScope,
    val mongo: MongoClient,
    val verification: TemplatedEmailOptions,
    val recovery: TemplatedEmailOptions
) {
    private val clock by lazy { SystemClock() }

    val database by lazy {
        MongoServiceOptions(scope,mongo,database.maxHealthCheckToken ?: 5)
    }

    private val authenticationDb by lazy {
        mongo.getDatabase("authentication")
    }

    private val registrationDb by lazy {
        mongo.getDatabase("registration")
    }

    val registration by lazy {
        val db = RegistrationServiceFlixOptions.Database(registrationDb,authenticationDb)
        RegistrationServiceFlixOptions(scope, db, clock, sender, logger, verification)
    }

    val authentication by lazy {
        AuthenticationServiceFlixOptions(scope, authenticationDb, sender, logger, recovery)
    }
}