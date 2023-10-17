package sentinel

import koncurrent.Later
import sentinel.params.SendVerificationLinkParams

interface RegistrationService : RegistrationScheme {

    fun sendVerificationLink(params: SendVerificationLinkParams): Later<String>
}