@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel.fields

import neat.Invalid
import neat.Valid
import neat.execute
import neat.min
import neat.required
import symphony.Fields
import symphony.password
import kotlin.js.JsExport

class SetPasswordFields : Fields<SetPasswordOutput>(SetPasswordOutput()) {

    val password1 = password(
        name = output::password1,
        label = "Password",
        hint = "secure-password"
    ) {
        min(8)
        required()
    }

    val password2 = password(
        name = output::password2,
        label = "Confirm password",
        hint = "secure-password",
    ) {
        min(8)
        execute {
            when {
                password1.output == it -> Valid(it)
                else -> Invalid(it, listOf("Passwords do not match"))
            }
        }.required()
    }
}