package sentinel

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class PersonBusinessRelationDao(
    val business: ObjectId,
    val person: ObjectId,
    @BsonId val uid: ObjectId? = null
) {
    companion object {
        const val collection = "reception.relations.person.business"
    }
}