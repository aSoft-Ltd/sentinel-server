@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import identifier.CorporateDto
import identifier.IndividualDto
import kash.Currency
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
data class UserSession(
    val user: IndividualDto,
    val secret: String,
    val company: CorporateDto,
    val currency: Currency,
    val timezone: String,
    val salesTax: Int
)