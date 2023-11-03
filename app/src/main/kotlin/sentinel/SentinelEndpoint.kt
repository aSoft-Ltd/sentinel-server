package sentinel

import raven.FlixMailEndpoint
import sanity.SanityEndpoint

class SentinelEndpoint(base: String) {
    val sanity by lazy { SanityEndpoint(base) }
    val mailer by lazy { FlixMailEndpoint(base) }
    val registration by lazy { RegistrationEndpoint(base) }
    val authentication by lazy { AuthenticationEndpoint(base) }
}