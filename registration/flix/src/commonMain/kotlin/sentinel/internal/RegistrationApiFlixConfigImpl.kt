package sentinel.internal

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import krono.Clock
import raven.Mailer
import sentinel.RegistrationApiFlixConfig
import sentinel.RegistrationEmailConfig

@PublishedApi
internal class RegistrationApiFlixConfigImpl(
    override val scope: CoroutineScope,
    override val db: MongoDatabase,
    override val clock: Clock,
    override val mailer: Mailer,
    override val email: RegistrationEmailConfig
) : RegistrationApiFlixConfig