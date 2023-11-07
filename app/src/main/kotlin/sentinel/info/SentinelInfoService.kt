package sentinel.info

import koncurrent.later
import koncurrent.later.await

class SentinelInfoService(
    private val options: SentinelInfoServiceOptions
) {

    fun healthCheck() = options.scope.later {
        SentinelHealthStatus(
            database = options.database.health().await()
        )
    }
}