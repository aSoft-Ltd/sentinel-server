package sentinel

import kotlinx.serialization.StringFormat

class ScopedRegistrationController(
    val service: suspend (domain: String) -> EmailRegistrationService,
    val resolver: String,
    val endpoint: EmailRegistrationEndpoint,
    val codec: StringFormat,
)