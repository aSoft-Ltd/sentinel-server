package sentinel.fields

import kase.catching
import kotlinx.serialization.Serializable
import neat.required
import sentinel.params.SignUpParams

@Serializable
class SignUpOutput(
    var name: String? = "",
    var email: String? = ""
) {
    fun toParams() = catching {
        SignUpParams(
            name = this::name.required,
            email = this::email.required
        )
    }
}