package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import krono.SystemClock
import lexi.Logger
import lexi.LoggerFactory
import raven.Mailer
import raven.TemplatedEmailOptions

class SentinelServiceOptions(
    val logger: LoggerFactory,
    val mailer: Mailer,
    val scope: CoroutineScope,
    val db: MongoDatabase,
    val verification: TemplatedEmailOptions,
    val recovery: TemplatedEmailOptions
) {
    private val clock by lazy { SystemClock() }
    val registration by lazy {
        RegistrationServiceFlixOptions(scope, db, clock, mailer, logger, verification)
    }
    val authentication by lazy {
        AuthenticationServiceFlixOptions(scope, db, mailer, logger, recovery)
    }
}