package sentinel

import kotlinx.serialization.json.Json
import sanity.SanityController
import sentinel.info.SentinelInfoController

internal class SentinelController(
    val service: SentinelService,
    val endpoint: SentinelEndpoint
) {
    val codec by lazy { Json { } }

    val sanity by lazy {
        SanityController(service.sanity, endpoint.sanity)
    }
    val registration by lazy {
        RegistrationController(service.registration, endpoint.registration, codec)
    }
    val authentication by lazy {
        AuthenticationController(service.authentication, endpoint.authentication, codec)
    }
    val info by lazy {
        SentinelInfoController(service.info, endpoint.info, codec)
    }
}