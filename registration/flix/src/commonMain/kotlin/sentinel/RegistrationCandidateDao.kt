package sentinel

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class RegistrationCandidateDao(
    val name: String,
    val email: String,
    val on: LocalDateTime,
    val tokens: List<VerificationTokenDao>,
    val verified: Boolean,
    @BsonId val uid: ObjectId? = null
) {
    companion object {
        const val collection = "registration.candidates"
    }
}