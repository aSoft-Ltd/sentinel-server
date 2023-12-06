package sentinel

import identifier.Brand
import kotlinx.serialization.Serializable
import krono.SystemClock
import krono.currentInstant
import raven.EmailTemplate
import raven.SendEmailParams
import raven.TemplatedEmailOptions
import raven.TemplatedWrapperEmailConfiguration
import raven.toHtmlString

@Serializable
class AuthenticationServiceConfiguration(
    val recovery: TemplatedWrapperEmailConfiguration
) {
    fun toOptions(brand: Brand, clock: SystemClock): TemplatedEmailOptions {
        val service = "registration verification"
        val from = recovery.toAddress(service)
        val subject = recovery.toSubject(service)

        return TemplatedEmailOptions { to, link ->
            val greeting = "Hello ${to.name},"
            SendEmailParams(
                from = from,
                to = to,
                subject = subject,
                body = EmailTemplate(
                    plain = "$greeting, here is your verification token. \n$link",
                    html = RecoveryEmails.recovery(
                        brand = brand,
                        label = brand.name,
                        greeting = greeting,
                        receptionist = null,
                        link = link,
                        year = clock.currentInstant().atSystemZone().year.toString()
                    ).toHtmlString(" ")
                )
            )
        }
    }
}