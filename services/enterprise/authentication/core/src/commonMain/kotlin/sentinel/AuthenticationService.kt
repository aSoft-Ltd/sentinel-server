package sentinel

import koncurrent.Later

interface AuthenticationService : AuthenticationScheme {
    fun session(token: String): Later<UserSession>
}