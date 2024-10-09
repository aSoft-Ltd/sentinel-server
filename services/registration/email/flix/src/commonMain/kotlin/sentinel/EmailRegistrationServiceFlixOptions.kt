package sentinel

import kotlinx.coroutines.CoroutineScope
import krono.Clock
import lexi.LoggerFactory
import raven.MultiEmailSender
import raven.TemplatedEmailOptions
import sanity.EventBus

class EmailRegistrationServiceFlixOptions(
    val scope: CoroutineScope,
    val database: ReceptionDatabase,
    val clock: Clock,
    val bus: EventBus,
    val topic: RegistrationTopic,
    val sender: MultiEmailSender,
    val logger: LoggerFactory,
    val verification: TemplatedEmailOptions<Any?>,
    val done: suspend (registration:EmailRegistrationCandidateDao, dao:PersonalAccountDao)->Unit,
)