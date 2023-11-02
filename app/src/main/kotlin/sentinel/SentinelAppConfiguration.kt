package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import krono.SystemClock
import lexi.Logger
import lexi.LoggerFactory
import lexi.LoggingConfiguration
import net.peanuuutz.tomlkt.Toml
import okio.FileSystem
import okio.Path.Companion.toPath
import raven.MailingConfiguration
import raven.MockMailer

@Serializable
class SentinelAppConfiguration(
    val logging: LoggingConfiguration?,
    val mail: MailingConfiguration?,
    val registration: RegistrationServiceConfiguration?,
    val authentication: AuthenticationServiceConfiguration?
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
    ): SentinelServiceOptions {
        val logger = logging?.toLogger(FileSystem.SYSTEM, Clock.System, "/app/root/logs".toPath()) ?: run {
            println("[WARNING] You have not configured any logger")
            LoggerFactory()
        }
        val mailer = mail?.toMailer(scope) ?: run {
            println("[WARNING] Defaulting to mock mailing service because you have not configured a mailer")
            MockMailer()
        }

        val verification = registration?.toOptions() ?: run {
            throw IllegalArgumentException("Missing registration verification configuration")
        }

        val recovery = authentication?.toOptions() ?: run {
            throw IllegalArgumentException("Missing authentication recovery configuration")
        }

        return SentinelServiceOptions(
            scope = scope,
            logger = logger,
            mailer = mailer,
            db = db,
            verification = verification,
            recovery = recovery
        )
    }
}