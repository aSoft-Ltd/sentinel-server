package sentinel

import kotlinx.serialization.Serializable

@Serializable
data class OwnerConfiguration(
    val name: String,
    val address: String,
    val domain: String
)