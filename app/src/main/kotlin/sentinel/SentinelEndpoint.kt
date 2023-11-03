package sentinel

import sanity.SanityEndpoint

class SentinelEndpoint(base: String) {
    val sanity by lazy { SanityEndpoint(base) }
    val registration by lazy { RegistrationEndpoint(base) }
    val authentication by lazy { AuthenticationEndpoint(base) }
}