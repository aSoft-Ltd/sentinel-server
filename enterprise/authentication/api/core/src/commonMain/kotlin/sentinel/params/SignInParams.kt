@file:JsExport
@file:Suppress("OPT_IN_USAGE")

package sentinel.params

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
data class SignInParams(
    val email: String,
    val password: String
)