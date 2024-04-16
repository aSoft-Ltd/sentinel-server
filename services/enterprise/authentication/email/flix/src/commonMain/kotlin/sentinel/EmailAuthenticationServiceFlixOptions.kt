package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import lexi.LoggerFactory
import raven.MultiEmailSender
import raven.TemplatedEmailOptions
import sanity.EventBus

class EmailAuthenticationServiceFlixOptions(
    val scope: CoroutineScope,
    val database: MongoDatabase,
    val sender: MultiEmailSender,
    val logger: LoggerFactory,
    val email: TemplatedEmailOptions,
    val bus: EventBus,
    val topic: AuthenticationTopic
)