package sentinel

import koncurrent.Later
import sentinel.params.SendPasswordResetParams

interface EmailAuthenticationService : AuthenticationService, EmailAuthenticationScheme {

    fun signOut(token: String): Later<UserSession>
    fun sendPasswordResetLink(params: SendPasswordResetParams): Later<String>
}