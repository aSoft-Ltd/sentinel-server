package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import krono.Clock
import raven.Mailer

interface RegistrationApiFlixConfig {
    val scope: CoroutineScope
    val db: MongoDatabase
    val clock: Clock
    val mailer: Mailer
    val email: RegistrationEmailConfig
}