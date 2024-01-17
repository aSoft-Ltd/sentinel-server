package sentinel

import koncurrent.Later
import sentinel.params.SendPasswordResetParams

interface EmailAuthenticationService : AuthenticationService, EmailAuthenticationScheme {
    fun sendPasswordResetLink(params: SendPasswordResetParams): Later<String>
}