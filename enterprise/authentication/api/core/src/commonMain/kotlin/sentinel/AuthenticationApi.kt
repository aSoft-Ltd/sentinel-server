@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import koncurrent.Later
import sentinel.params.PasswordResetParams
import sentinel.params.SendPasswordResetParams
import sentinel.params.SignInParams
import kotlin.js.JsExport

interface AuthenticationApi {
    fun signIn(params: SignInParams): Later<UserSession>
    fun session(): Later<UserSession>
    fun signOut(): Later<Unit>

    fun sendPasswordResetLink(email: String): Later<String>
    fun resetPassword(params: PasswordResetParams): Later<PasswordResetParams>
}