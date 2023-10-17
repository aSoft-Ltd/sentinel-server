package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import lexi.Logger
import raven.Mailer
import raven.TemplatedEmailOptions

class AuthenticationServiceFlixOptions(
    val scope: CoroutineScope,
    val db: MongoDatabase,
    val mailer: Mailer,
    val logger: Logger,
    val email: TemplatedEmailOptions
)