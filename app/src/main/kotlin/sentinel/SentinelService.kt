package sentinel

import grape.MongoService
import koncurrent.later
import koncurrent.later.await
import sanity.SanityHandler
import sentinel.info.SentinelInfoService
import sentinel.info.SentinelInfoServiceOptions

class SentinelService(
    private val options: SentinelServiceOptions
) {
    val database by lazy { MongoService(options.database) }
    val sanity by lazy { SanityHandler(options.bus, maxClientsPerIp = 1) }
    val registration by lazy { RegistrationServiceFlix(options.registration) }
    val authentication by lazy { AuthenticationServiceFlix(options.authentication) }
    val info by lazy {
        val opts = SentinelInfoServiceOptions(options.scope, database)
        SentinelInfoService(opts)
    }
}