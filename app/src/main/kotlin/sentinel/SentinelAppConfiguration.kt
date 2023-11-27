package sentinel

import grape.MongoDatabaseConfiguration
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import lexi.LoggerFactory
import lexi.LoggingConfiguration
import net.peanuuutz.tomlkt.Toml
import okio.FileSystem
import okio.Path.Companion.toPath
import raven.BusEmailTopic
import raven.ConsoleEmailSender
import raven.MultiEmailSenderFactoryConfiguration
import raven.MultiEmailSenderFactory
import sanity.LocalBus

@Serializable
internal class SentinelAppConfiguration(
    val database: MongoDatabaseConfiguration,
    val logging: LoggingConfiguration?,
    val mail: MultiEmailSenderFactoryConfiguration?,
    val registration: RegistrationServiceConfiguration?,
    val authentication: AuthenticationServiceConfiguration?
) {
    companion object {
        fun parse(path: String?): SentinelAppConfiguration {
            if (path == null) throw IllegalArgumentException("Config path was not provided")
            val file = File(path)
            if (!file.exists()) throw IllegalArgumentException("Config file not found at $path")
            val text = file.readText()
            println("Using configuration\n\n$text")
            val codec = Toml { ignoreUnknownKeys = true }
            return codec.decodeFromString(serializer(), text)
        }
    }

    private fun toOptions(): SentinelServiceOptions {
        val scope = CoroutineScope(SupervisorJob())
        val bus = LocalBus()
        val codec = Json { ignoreUnknownKeys = true }

        val logger = logging?.toLogger(FileSystem.SYSTEM, Clock.System, "/app/root/logs".toPath()) ?: run {
            println("[WARNING] You have not configured any logger")
            LoggerFactory()
        }

        val sender = mail?.toFactory(bus, BusEmailTopic(), codec) ?: run {
            println("[WARNING] Defaulting to a console mailing service because you have not configured a mailer")
            val fc = MultiEmailSenderFactory()
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
            database = database,
            scope = scope,
            logger = logger,
            sender = sender.build(),
            mongo = database.toClient(),
            verification = verification,
            recovery = recovery,
            bus = bus
        )
    }

    private fun toService() = SentinelService(toOptions())

    fun toController() = SentinelController(toService(), SentinelEndpoint("/api/v1"))
}