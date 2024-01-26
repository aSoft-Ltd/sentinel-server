package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import krono.Clock
import lexi.LoggerFactory
import raven.MultiEmailSender
import raven.TemplatedEmailOptions
import sanity.EventBus

class EmailRegistrationServiceFlixOptions(
    val scope: CoroutineScope,
    val database: Database,
    val clock: Clock,
    val bus: EventBus,
    val topic: RegistrationTopic,
    val sender: MultiEmailSender,
    val logger: LoggerFactory,
    val verification: TemplatedEmailOptions,
) {
    class Database(
        val registration: MongoDatabase,
        val authentication: MongoDatabase
    )
}