package sentinel

import kotlinx.serialization.Serializable
import raven.TemplatedWrapperEmailConfiguration

@Serializable
class AuthenticationServiceConfiguration(
    val recovery: TemplatedWrapperEmailConfiguration?
) {
    fun toOptions(
        brand: String,
        domain: String,
        address: String
    ) = recovery?.toOptions(brand, domain, address, "authentication recovery")
}