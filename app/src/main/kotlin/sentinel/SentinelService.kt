package sentinel

class SentinelService(
    val options: SentinelServiceOptions
) {
    val registration by lazy { RegistrationServiceFlix(options.registration) }
    val authentication by lazy { AuthenticationServiceFlix(options.authentication) }
}