package sentinel

import kotlinx.serialization.Serializable
import raven.TemplatedWrapperEmailConfiguration

@Serializable
class RegistrationServiceConfiguration(
    val verification: TemplatedWrapperEmailConfiguration?
) {
    fun toOptions(
        brand: String,
        domain: String,
        address: String
    ) = verification?.toOptions(brand, domain, address, "registration verification")
}