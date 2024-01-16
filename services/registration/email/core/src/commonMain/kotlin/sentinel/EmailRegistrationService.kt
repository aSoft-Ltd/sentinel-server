package sentinel

import sentinel.params.SendVerificationLinkParams
import koncurrent.Later

interface EmailRegistrationService : RegistrationService, EmailRegistrationScheme {
    fun sendVerificationLink(params: SendVerificationLinkParams): Later<String>
}