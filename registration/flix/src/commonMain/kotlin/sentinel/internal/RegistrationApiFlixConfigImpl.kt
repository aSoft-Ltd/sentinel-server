package sentinel.internal

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import krono.Clock
import sentinel.RegistrationApiFlixConfig

@PublishedApi
internal class RegistrationApiFlixConfigImpl(
    override val scope: CoroutineScope,
    override val db: MongoDatabase,
    override val clock: Clock,
) : RegistrationApiFlixConfig