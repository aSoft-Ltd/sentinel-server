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
import sanity.LocalBus
import sanity.installSanity

fun main() {
    val scope = CoroutineScope(SupervisorJob())
    val bus = LocalBus()
    val client = MongoClient.create("mongodb://root:pass@mongo:27017/")
    val db = client.getDatabase("test-trial")
    val options = SentinelAppConfiguration.parse(File("/app/root/config.toml")).toOptions(
        scope = scope,
        db = db,
    )
    val service = SentinelService(options)
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
            installSanity(bus, endpoint.sanity)
            installRegistration(service = service.registration, endpoint.registration, json)
            installAuthentication(service = service.authentication, endpoint.authentication, json)
        }
    }.start(true)
}