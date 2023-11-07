package sentinel.info

import io.ktor.server.routing.Routing
import kase.response.get
import koncurrent.later.await

internal fun Routing.installInfo(controller: SentinelInfoController) {
    get(controller.endpoint.healthCheck(),controller.codec) {
        controller.service.healthCheck().await()
    }
}