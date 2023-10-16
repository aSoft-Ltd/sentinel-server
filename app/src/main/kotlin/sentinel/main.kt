package sentinel

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.http.HttpMethod
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.routing
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import raven.AddressInfo
import raven.FlixServerMailer
import raven.installMailer

fun main() {
    val scope = CoroutineScope(SupervisorJob())
    val client = MongoClient.create("mongodb://root:pass@mongo:27017/")
    val db = client.getDatabase("test-trial")
    val config = SentinelAppConfiguration.parse(File("/app/root/config.toml")).toOptions(
        scope = scope,
        db = db,
        email = RegistrationEmailConfig(
            address = AddressInfo(email = "registration@test.com", name = "Tester"),
            subject = "Please Verify Your Email",
            template = "Hi {{name}}, here is your token {{token}}"
        )
    )
    val service = SentinelService(config)
    val endpoint = SentinelEndpoint("/api/v1")
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
            installRegistration(service = service.registration, endpoint.registration, json)
            when (val mailer = config.mailer) {
                is FlixServerMailer -> installMailer(mailer, endpoint = endpoint.mailer)
            }
        }
    }.start(true)
}