package sentinel

import lexi.Logger
import raven.Mailer

class SentinelConfig(
    val logger: Logger,
    val mailer: Mailer,
    val registration: RegistrationServiceFlixConfig
)