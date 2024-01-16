package sentinel

import sentinel.params.SendVerificationCodeParams
import koncurrent.Later

interface PhoneRegistrationService : RegistrationService, PhoneRegistrationScheme {
    fun sendVerificationLink(params: SendVerificationCodeParams): Later<String>
}