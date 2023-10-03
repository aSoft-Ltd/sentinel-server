package sentinel.transformers

import kase.catching
import kollections.iListOf
import neat.required
import sentinel.fields.SetPasswordOutput
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams

fun SetPasswordOutput.toParams(params: VerificationParams) = catching {
    UserAccountParams(
        loginId = params.email,
        password = this::password1.required,
        permissions = iListOf(),
        registrationToken = params.token
    )
}