package sentinel.transformers

import identifier.CorporateDto
import identifier.IndividualDto
import sentinel.BusinessAccountDao
import sentinel.PersonalAccountDao

fun PersonalAccountDao.toIndividual() = IndividualDto(
    uid = uid!!.toHexString(),
    name = name
)

fun BusinessAccountDao.toCorporate() = CorporateDto(
    uid = uid!!.toHexString(),
    name = name,
    image = logo
)