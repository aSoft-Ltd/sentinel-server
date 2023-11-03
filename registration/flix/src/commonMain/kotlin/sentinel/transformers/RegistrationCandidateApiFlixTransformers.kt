package sentinel.transformers

import raven.Address
import sentinel.RegistrationCandidateDao

fun RegistrationCandidateDao.toAddress() = Address(
    email = email,
    name = name
)