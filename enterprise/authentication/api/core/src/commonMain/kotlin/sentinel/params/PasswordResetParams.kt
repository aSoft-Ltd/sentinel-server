@file:JsExport
package sentinel.params

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
data class PasswordResetParams (
//    val loginId: String,
    val password: String,
    val passwordResetToken: String? = null
)