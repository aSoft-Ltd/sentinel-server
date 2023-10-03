@file:JsExport

package sentinel.params

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
data class SignUpParams(
    val name: String,
    val email: String
)