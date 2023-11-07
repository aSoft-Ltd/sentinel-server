package sentinel

import io.ktor.server.routing.Routing
import sanity.installSanity
import sentinel.info.installInfo

internal fun Routing.installSentinel(controller: SentinelController) {
    installSanity(controller.sanity)
    installRegistration(controller.registration)
    installAuthentication(controller.authentication)
    installInfo(controller.info)
}