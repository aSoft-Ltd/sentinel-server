@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import sentinel.fields.SetPasswordFields
import sentinel.tools.loadVerificationParams
import sentinel.tools.removeVerificationParams
import sentinel.tools.save
import sentinel.transformers.toParams
import symphony.toForm
import symphony.toSubmitConfig
import kotlin.js.JsExport

class SetPasswordScene(private val config: RegistrationSceneConfig<RegistrationApi>) {
    private var successFunction: (() -> Unit)? = null

    private val cache = config.cache

    fun initialize(onSuccess: () -> Unit) {
        successFunction = onSuccess
    }

    fun deInitialize() {
        successFunction = null
        form.fields.finish()
    }

    val form = SetPasswordFields().toForm(
        heading = "Make your account secure",
        details = "Set up your password",
        config = config.toSubmitConfig()
    ) {
        onSubmit { output ->
            cache.loadVerificationParams().then {
                output.toParams(it).getOrThrow()
            }.andThen {
                cache.save(it)
            }.andThen {
                config.api.createUserAccount(it)
            }
        }

        onSuccess {
            cache.removeVerificationParams()
            successFunction?.invoke()
        }
    }
}