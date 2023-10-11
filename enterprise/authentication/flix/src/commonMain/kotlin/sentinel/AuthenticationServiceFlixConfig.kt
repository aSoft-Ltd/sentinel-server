package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import krono.Clock
import lexi.Logger
import raven.Mailer

class AuthenticationServiceFlixConfig(
    val scope: CoroutineScope,
    val db: MongoDatabase,
    val clock: Clock,
    val mailer: Mailer,
    val logger: Logger,
    val email: AuthenticationEmailConfig
)