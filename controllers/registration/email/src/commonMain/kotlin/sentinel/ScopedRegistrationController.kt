package sentinel

import kotlinx.serialization.StringFormat

class ScopedRegistrationController(
    val service: suspend (domain: String) -> EmailRegistrationService,
    val endpoint: EmailRegistrationEndpoint,
    val codec: StringFormat,
)