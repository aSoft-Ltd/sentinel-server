package sentinel.params

import kotlinx.serialization.Serializable

@Serializable
data class SendVerificationLinkParams(
    val email: String,
    val link: String
)