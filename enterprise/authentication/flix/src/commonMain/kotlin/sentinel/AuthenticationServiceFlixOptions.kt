package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import lexi.LoggerFactory
import raven.EmailSender
import raven.TemplatedEmailOptions

class AuthenticationServiceFlixOptions(
    val scope: CoroutineScope,
    val database: MongoDatabase,
    val sender: EmailSender,
    val logger: LoggerFactory,
    val email: TemplatedEmailOptions
)