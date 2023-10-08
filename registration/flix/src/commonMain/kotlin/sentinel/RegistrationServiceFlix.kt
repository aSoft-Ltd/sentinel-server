package sentinel

import koncurrent.Later
import koncurrent.TODOLater
import sentinel.params.SendVerificationLinkParams
import sentinel.params.SignUpParams
import sentinel.params.UserAccountParams
import sentinel.params.VerificationParams

class RegistrationServiceFlix : RegistrationService {
    override fun signUp(params: SignUpParams): Later<SignUpParams> = TODOLater()

    override fun sendVerificationLink(params: SendVerificationLinkParams): Later<String> = TODOLater()

    override fun verify(params: VerificationParams): Later<VerificationParams> = TODOLater()

    override fun createUserAccount(params: UserAccountParams): Later<UserAccountParams> = TODOLater()
}