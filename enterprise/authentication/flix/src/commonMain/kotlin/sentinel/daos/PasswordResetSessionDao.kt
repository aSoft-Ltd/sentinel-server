package sentinel.daos

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class PasswordResetSessionDao(
    val person: ObjectId?,
    val token: ObjectId,
    val email: String,
    @BsonId val uid: ObjectId? = null
) {
    companion object {
        const val collection = "authentication.recoveries"
    }
}