package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import krono.SystemClock
import lexi.Logger
import lexi.LoggingConfiguration
import net.peanuuutz.tomlkt.Toml
import okio.FileSystem
import okio.Path.Companion.toPath
import raven.Mailer

@Serializable
class SentinelAppConfiguration(
    val logging: LoggingConfiguration?
) {
    companion object {
        fun parse(file: File): SentinelAppConfiguration {
            val text = file.readText()
            println("Using\n$text")
            val codec = Toml { ignoreUnknownKeys = true }
            return codec.decodeFromString(serializer(), text)
        }
    }

    fun toOptions(
        scope: CoroutineScope,
        db: MongoDatabase,
        mailer: Mailer,
        email: RegistrationEmailConfig,
    ): SentinelConfig {
        val logger = logging?.toLogger(FileSystem.SYSTEM, Clock.System, "/app/root/logs".toPath()) ?: run {
            println("[WARNING] You have not cofigured any logger")
            Logger()
        }
        return SentinelConfig(
            logger = logger,
            registration = RegistrationServiceFlixConfig(scope, db, SystemClock(), mailer, logger, email)
        )
    }

    fun toService(
        scope: CoroutineScope,
        db: MongoDatabase,
        mailer: Mailer,
        email: RegistrationEmailConfig,
    ) = SentinelService(
        registration = RegistrationServiceFlix(toOptions(scope, db, mailer, email).registration)
    )
}