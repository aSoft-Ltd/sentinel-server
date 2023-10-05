package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import krono.Clock
import raven.Mailer
import sentinel.internal.RegistrationApiFlixConfigImpl

fun RegistrationApiFlixConfig(
    scope: CoroutineScope,
    db: MongoDatabase,
    clock: Clock,
    mailer: Mailer,
    email: RegistrationEmailConfig
): RegistrationApiFlixConfig = RegistrationApiFlixConfigImpl(scope, db, clock, mailer, email)