package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import krono.Clock

interface RegistrationApiFlixConfig {
    val scope: CoroutineScope
    val db: MongoDatabase
    val clock: Clock
}