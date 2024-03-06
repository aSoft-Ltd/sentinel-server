package sentinel

import kotlinx.serialization.StringFormat

class ScopedAuthenticationController(
    val service: suspend (domain: String) -> EmailAuthenticationService,
    val resolver: String,
    val endpoint: EmailAuthenticationEndpoint,
    val codec: StringFormat,
)