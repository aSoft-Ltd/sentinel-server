package sentinel.transformers

import raven.Address
import sentinel.EmailRegistrationCandidateDao

fun EmailRegistrationCandidateDao.toAddress() = Address(
    email = email,
    name = name
)