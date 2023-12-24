package sentinel

import koncurrent.Later
import koncurrent.later.then
import koncurrent.later.andThen
import koncurrent.later.andZip
import koncurrent.later.zip
import koncurrent.later.catch
import sentinel.params.SendVerificationLinkParams

interface RegistrationService : RegistrationScheme {

    fun sendVerificationLink(params: SendVerificationLinkParams): Later<String>
}