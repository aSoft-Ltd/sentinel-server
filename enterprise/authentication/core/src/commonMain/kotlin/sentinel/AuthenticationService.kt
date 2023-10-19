package sentinel

import koncurrent.Later
import sentinel.params.SendPasswordResetParams

interface AuthenticationService : AuthenticationScheme {
    fun sendPasswordResetLink(params: SendPasswordResetParams): Later<String>
    fun session(token: String): Later<UserSession>
}