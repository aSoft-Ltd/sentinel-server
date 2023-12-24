package sentinel

import koncurrent.Later
import koncurrent.later.then
import koncurrent.later.andThen
import koncurrent.later.andZip
import koncurrent.later.zip
import koncurrent.later.catch
import sentinel.params.SendPasswordResetParams

interface AuthenticationService : AuthenticationScheme {
    fun sendPasswordResetLink(params: SendPasswordResetParams): Later<String>
    fun session(token: String): Later<UserSession>
}