package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import lexi.LoggerFactory
import lexi.LoggingConfiguration
import net.peanuuutz.tomlkt.Toml
import okio.FileSystem
import okio.Path.Companion.toPath
import raven.ConsoleEmailSender
import raven.MailFactoryConfiguration
import raven.MailSenderFactory
import raven.emailSender
import sanity.EventBus

@Serializable
class SentinelAppConfiguration(
    val logging: LoggingConfiguration?,
    val mail: MailFactoryConfiguration?,
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
        bus: EventBus,
    ): SentinelServiceOptions {
        val logger = logging?.toLogger(FileSystem.SYSTEM, Clock.System, "/app/root/logs".toPath()) ?: run {
            println("[WARNING] You have not configured any logger")
            LoggerFactory()
        }
        val sender = mail?.toFactory(bus) ?: run {
            println("[WARNING] Defaulting to a console mailing service because you have not configured a mailer")
            val fc = MailSenderFactory()
            fc.add(ConsoleEmailSender())
            fc
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
            sender = sender.build(),
            db = db,
            verification = verification,
            recovery = recovery
        )
    }
}