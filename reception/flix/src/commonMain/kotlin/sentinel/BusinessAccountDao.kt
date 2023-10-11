package sentinel

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class BusinessAccountDao(
    val name: String,
    val logo: String?,
    @BsonId val uid: ObjectId? = null
) {
    companion object {
        const val collection = "reception.accounts.business"
    }
}