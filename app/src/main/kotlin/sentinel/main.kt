package sentinel

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import krono.SystemClock
import lexi.ConsoleAppender
import lexi.Logger
import raven.AddressInfo
import raven.LocalMemoryMailbox
import raven.MockMailer
import raven.MockMailerConfig

fun main() {
    val scope = CoroutineScope(SupervisorJob())
    val client = MongoClient.create("mongodb://root:pass@mongo:27017/")
    val db = client.getDatabase("test-trial")
    val clock = SystemClock()
    val mailer = MockMailer(MockMailerConfig(box = LocalMemoryMailbox()))
    val email = RegistrationEmailConfig(
        address = AddressInfo(email = "registration@test.com", name = "Tester"),
        subject = "Please Verify Your Email",
        template = "Hi {{name}}, here is your token {{token}}"
    )
    val logger = Logger(ConsoleAppender())
    val config = RegistrationServiceFlixConfig(scope, db, clock, mailer, logger, email)
    val service = RegistrationServiceFlix(config)
    val endpoint = RegistrationEndpoint("/api/v1")
    val json = Json {}

    embeddedServer(CIO, port = 8080) {
        install(CORS) {
            anyHost()
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Delete)
        }
        routing {
            installRegistration(service = service, endpoint, json)
        }
    }.start(true)
}