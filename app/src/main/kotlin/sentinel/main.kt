package sentinel

import io.ktor.http.HttpMethod
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.routing
import java.io.File
import sanity.installSanity
import sentinel.info.installInfo

fun main(vararg args: String) {
    val controller = SentinelAppConfiguration.parse(args.getOrNull(0)).toController()

    embeddedServer(CIO, port = 8080) {
        install(CORS) {
            anyHost()
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Delete)
        }

        routing { installSentinel(controller) }
    }.start(true)
}