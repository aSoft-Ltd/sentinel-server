package sentinel.transformers

import raven.AddressInfo
import sentinel.RegistrationCandidateDao

fun RegistrationCandidateDao.toAddressInfo() = AddressInfo(
    email = email,
    name = name
)