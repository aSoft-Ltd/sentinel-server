package sentinel

import io.ktor.server.routing.*

fun Routing.installRegistration(service: RegistrationService) {
    println("installing registration routes")
}