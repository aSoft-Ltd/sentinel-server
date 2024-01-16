package sentinel.transformers

import org.bson.types.ObjectId
import sentinel.BusinessAccountDao
import sentinel.PersonalAccountDao
import sentinel.params.UserAccountParams

fun UserAccountParams.toPersonDao(registrationId: ObjectId,name: String) = PersonalAccountDao(
    name = name,
    photo = null,
    password = password,
    email = loginId,
    registrationId = registrationId
)

fun UserAccountParams.toBusinessDao(name: String) = BusinessAccountDao(
    name = name,
    logo = null,
)