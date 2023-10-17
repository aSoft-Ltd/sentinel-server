package sentinel

import kotlinx.serialization.Serializable
import raven.TemplatedWrapperEmailConfiguration

@Serializable
class RegistrationServiceConfiguration(
    val verification: TemplatedWrapperEmailConfiguration?
) {
    fun toOptions() = verification?.toOptions("registration verification")
}