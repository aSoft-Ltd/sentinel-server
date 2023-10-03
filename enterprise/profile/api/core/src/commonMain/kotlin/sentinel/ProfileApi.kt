@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import identifier.OrganisationProfileApi
import identifier.PersonalProfileApi
import kotlin.js.JsExport

interface ProfileApi {
    val personal: PersonalProfileApi
    val organisation: OrganisationProfileApi
}