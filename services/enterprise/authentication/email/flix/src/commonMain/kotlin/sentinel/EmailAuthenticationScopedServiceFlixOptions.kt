package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import lexi.LoggerFactory
import raven.MultiEmailSender
import raven.TemplatedEmailOptions

class EmailAuthenticationScopedServiceFlixOptions(
    val scope: CoroutineScope,
    val database: MongoDatabase,
    val sender: MultiEmailSender,
    val parent: String,
    val logger: LoggerFactory,
    val email: TemplatedEmailOptions
)