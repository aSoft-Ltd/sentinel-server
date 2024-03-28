package sentinel

class EmailRegistrationCompletedEvent(
    val name: String,
    val email: String,
    val password: String,
    val scope: String?,
)