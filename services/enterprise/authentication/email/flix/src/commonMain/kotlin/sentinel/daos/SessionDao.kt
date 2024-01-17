package sentinel.daos

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class SessionDao(
    val token: String,
    val user: ObjectId,
    val company: ObjectId,
    @BsonId val uid: ObjectId? = null
) {
    companion object {
        const val collection = "authentication.sessions"
    }
}