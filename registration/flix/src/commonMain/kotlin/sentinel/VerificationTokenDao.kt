package sentinel

import java.time.LocalDateTime

data class VerificationTokenDao(
    val on: LocalDateTime,
    val to: String,
    val text: String,
)