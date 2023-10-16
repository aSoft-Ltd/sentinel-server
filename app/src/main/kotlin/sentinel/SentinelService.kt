package sentinel

class SentinelService(
    val config: SentinelConfig
) {
    val registration by lazy { RegistrationServiceFlix(config.registration) }
}