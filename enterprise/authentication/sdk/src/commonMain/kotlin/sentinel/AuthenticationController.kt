package sentinel

import kotlinx.serialization.StringFormat

class AuthenticationController(
    val service: AuthenticationService,
    val endpoint: AuthenticationEndpoint,
    val codec: StringFormat
)