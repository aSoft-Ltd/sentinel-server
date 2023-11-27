package sentinel

import kotlinx.serialization.StringFormat

class AuthenticationController(
    val service: AuthenticationService,
    val endpoint: AuthenticationRoutes,
    val codec: StringFormat
)