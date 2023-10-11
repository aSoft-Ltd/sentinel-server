package sentinel

import raven.AddressInfo

class AuthenticationEmailConfig(
    val address: AddressInfo,
    val subject: String,
    val template: String
)