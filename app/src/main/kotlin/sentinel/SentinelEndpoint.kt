package sentinel

import sanity.SanityRoutes
import sentinel.info.SentinelInfoEndpoint

class SentinelEndpoint(private val base: String) {
    val sanity by lazy { SanityRoutes(base) }
    val registration by lazy { RegistrationRoutes(base) }
    val authentication by lazy { AuthenticationRoutes(base) }
    val info by lazy { SentinelInfoEndpoint(base) }
}
