package sentinel

import kotlinx.serialization.StringFormat

class AuthenticationController(
    val service: EmailAuthenticationService,
    val endpoint: EmailAuthenticationEndpoint,
    val codec: StringFormat
)