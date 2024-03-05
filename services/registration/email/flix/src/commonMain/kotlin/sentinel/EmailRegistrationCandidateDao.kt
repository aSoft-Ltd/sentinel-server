package sentinel

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class EmailRegistrationCandidateDao(
    val name: String,
    val email: String,
    val on: LocalDateTime,
    val tokens: List<VerificationTokenDao>,
    val verified: Boolean,
    val scope: ObjectId? = null,
    @BsonId val uid: ObjectId? = null
) {
    companion object {
        const val collection = "registration.candidates"
    }
}