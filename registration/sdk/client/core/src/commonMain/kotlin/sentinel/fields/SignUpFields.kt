@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel.fields

import neat.required
import symphony.Fields
import symphony.email
import symphony.text
import kotlin.js.JsExport

class SignUpFields : Fields<SignUpOutput>(SignUpOutput()) {

    val name = text(
        name = output::name,
        label = "Name",
        hint = "Enter your personal name"
    ) { required() }

    val email = email(
        name = output::email,
        label = "Email Address",
        hint = "Enter your email",
    ) { required() }
}