package sentinel

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class PersonalAccountDao(
    val name: String,
    val photo: String?,
    val password: String,
    val email: String,
    val registrationId: ObjectId,
    @BsonId val uid: ObjectId? = null
) {
    companion object {
        const val collection = "reception.accounts.personal"
    }
}