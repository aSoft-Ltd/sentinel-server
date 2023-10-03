@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel.params

import kollections.List
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * @param loginId can be a username/email/phone
 */
@Serializable
data class UserAccountParams(
    val loginId: String,
    val password: String,
    val permissions: List<String>,
    val registrationToken: String? = null
)