package sentinel

import kotlinx.serialization.StringFormat

class RegistrationController(
    val service: EmailRegistrationService,
    val endpoint: EmailRegistrationEndpoint,
    val codec: StringFormat
)