package sentinel

import kotlinx.serialization.Serializable
import raven.TemplatedWrapperEmailConfiguration

@Serializable
class AuthenticationServiceConfiguration(
    val recovery: TemplatedWrapperEmailConfiguration?
) {
    fun toOptions() = recovery?.toOptions("authentication recovery")
}