package sentinel

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class SignUpCandidate(
    val name: String,
    val email: String,
    val on: Long,
    val sent: List<Entry>,
    val verified: Boolean,
    @BsonId val uid: ObjectId? = null
) {
    data class Entry(
        val on: Long,
        val to: String,
    )

    fun updateToLinkStage(link: String): SignUpCandidate = this
}