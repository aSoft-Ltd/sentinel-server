package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import krono.Clock
import lexi.LoggerFactory
import raven.EmailSender
import raven.MultiEmailSender
import raven.SendEmailTemplateParams
import raven.TemplatedEmailOptions

class RegistrationServiceFlixOptions(
    val scope: CoroutineScope,
    val database: Database,
    val clock: Clock,
    val sender: MultiEmailSender,
    val logger: LoggerFactory,
    val verification: TemplatedEmailOptions,
) {
    class Database(
        val registration: MongoDatabase,
        val authentication: MongoDatabase
    )
}