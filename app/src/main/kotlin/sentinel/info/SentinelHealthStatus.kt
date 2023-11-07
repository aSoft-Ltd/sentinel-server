package sentinel.info

import grape.health.HealthStatus
import kotlinx.serialization.Serializable

@Serializable
data class SentinelHealthStatus(
    val database: HealthStatus
)