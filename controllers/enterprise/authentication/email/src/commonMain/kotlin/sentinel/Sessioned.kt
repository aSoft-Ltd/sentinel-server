package sentinel

class Sessioned<out P>(
    val session: UserSession,
    val params: P
) {
    fun <R> map(transform: (P) -> R) = Sessioned(session, transform(params))
}