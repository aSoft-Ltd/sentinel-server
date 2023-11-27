package sentinel

import kotlinx.serialization.StringFormat

class RegistrationController(
    val service: RegistrationService,
    val endpoint: RegistrationRoutes,
    val codec: StringFormat
)