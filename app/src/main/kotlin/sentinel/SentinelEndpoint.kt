package sentinel

import raven.FlixMailEndpoint

class SentinelEndpoint(base: String) {
    val mailer by lazy { FlixMailEndpoint(base) }
    val registration by lazy { RegistrationEndpoint(base) }
    val authentication by lazy { AuthenticationEndpoint(base) }
}