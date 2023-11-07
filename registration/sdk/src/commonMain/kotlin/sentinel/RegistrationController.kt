package sentinel

import kotlinx.serialization.StringFormat

class RegistrationController(
    val service: RegistrationService,
    val endpoint: RegistrationEndpoint,
    val codec: StringFormat
)