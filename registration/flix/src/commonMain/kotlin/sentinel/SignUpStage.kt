package sentinel

sealed class SignUpStage {
    data class Began(val on: Long) : SignUpStage()
    data class LinkSent(val entries: List<Entry>) : SignUpStage() {
        data class Entry(
            val on: Long,
            val to: String,
        )
    }

    data class Verified(val on: Long) : SignUpStage()
}