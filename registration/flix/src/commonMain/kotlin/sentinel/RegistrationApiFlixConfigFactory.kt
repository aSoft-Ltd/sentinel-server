package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import krono.Clock
import sentinel.internal.RegistrationApiFlixConfigImpl

fun RegistrationApiFlixConfig(
    scope: CoroutineScope,
    db: MongoDatabase,
    clock: Clock
): RegistrationApiFlixConfig = RegistrationApiFlixConfigImpl(scope, db, clock)