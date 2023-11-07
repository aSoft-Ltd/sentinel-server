package sentinel

import sanity.SanityEndpoint
import sentinel.info.SentinelInfoEndpoint

class SentinelEndpoint(private val base: String) {
    val sanity by lazy { SanityEndpoint(base) }
    val registration by lazy { RegistrationEndpoint(base) }
    val authentication by lazy { AuthenticationEndpoint(base) }
    val info by lazy { SentinelInfoEndpoint(base) }
}
