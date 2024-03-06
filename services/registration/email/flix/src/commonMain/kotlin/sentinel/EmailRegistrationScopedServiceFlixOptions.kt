package sentinel

import kotlinx.coroutines.CoroutineScope
import krono.Clock
import lexi.LoggerFactory
import raven.MultiEmailSender
import raven.TemplatedEmailOptions
import sanity.EventBus

class EmailRegistrationScopedServiceFlixOptions(
    val scope: CoroutineScope,
    val database: ReceptionDatabase,
    val parent: String,
    val clock: Clock,
    val bus: EventBus,
    val topic: RegistrationTopic,
    val sender: MultiEmailSender,
    val logger: LoggerFactory,
    val verification: TemplatedEmailOptions,
)