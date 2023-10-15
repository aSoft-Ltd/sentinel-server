package sentinel

import lexi.Logger

class SentinelConfig(
    val logger: Logger,
    val registration: RegistrationServiceFlixConfig
)