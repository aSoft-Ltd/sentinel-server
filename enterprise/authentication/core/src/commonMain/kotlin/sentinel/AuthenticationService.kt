package sentinel

import koncurrent.Later

interface AuthenticationService : AuthenticationScheme {
    fun sendPasswordResetLink(email: String): Later<String>
    fun session(token: String): Later<UserSession>
}