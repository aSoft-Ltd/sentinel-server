package sentinel.internal

import sentinel.RegistrationApi
import sentinel.RegistrationSceneConfig
import sentinel.RegistrationScenes
import sentinel.SetPasswordScene
import sentinel.SignUpScene
import sentinel.VerificationScene

@PublishedApi
internal class RegistrationScenesImpl(private val config: RegistrationSceneConfig<RegistrationApi>) : RegistrationScenes {
    override val signUp by lazy { SignUpScene(config) }
    override val verification by lazy { VerificationScene(config) }
    override val password by lazy { SetPasswordScene(config) }
}