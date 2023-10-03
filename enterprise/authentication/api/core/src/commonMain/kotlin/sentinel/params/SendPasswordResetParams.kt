@file:JsExport
package sentinel.params

import identifier.Email
import kollections.List

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
data class SendPasswordResetParams (
    val email: String,
    val url: String,
)