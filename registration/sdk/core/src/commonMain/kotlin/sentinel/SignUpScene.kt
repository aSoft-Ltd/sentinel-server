@file:JsExport
@file:Suppress("NON_EXPORTABLE_TYPE")

package sentinel

import cinematic.BaseScene
import koncurrent.FailedLater
import koncurrent.Later
import koncurrent.toLater
import sentinel.fields.SignUpFields
import sentinel.tools.loadSignUpParams
import sentinel.tools.removeSignUpParams
import sentinel.tools.save
import symphony.toForm
import symphony.toSubmitConfig
import kotlin.js.JsExport

class SignUpScene(private val config: RegistrationSceneConfig<RegistrationApi>) : BaseScene() {

    private val api get() = config.api

    private val cache get() = config.cache

    fun initialize() = restorePreviousSession()

    val form = SignUpFields().toForm(
        heading = "Create an account",
        details = "Signup in less than two minutes",
        config = config.toSubmitConfig()
    ) {
        onSubmit { output ->
            output.toLater().then {
                it.toParams().getOrThrow()
            }.andThen {
                cache.save(it)
            }.andThen {
                api.signUp(it)
            }.andThen {
                api.sendVerificationLink(it.email)
            }
        }
    }

    private fun restorePreviousSession() = cache.loadSignUpParams().then {
        form.fields.apply {
            email.set(it.email)
            name.set(it.name)
        }
    }

    fun resendVerificationLink(): Later<String> {
        val email = form.fields.email.output ?: return FailedLater(IllegalArgumentException("Email is not entered"))
        return api.sendVerificationLink(email)
    }
}