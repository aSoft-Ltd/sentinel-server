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
    fun toOptions(defaultbrand: Brand, clock: SystemClock): TemplatedEmailOptions<Any?> {
        val service = "registration verification"
        val from = recovery.toAddress(service)
        val subject = recovery.toSubject(service)

        return TemplatedEmailOptions { params, link, _brand ->
            val greeting = "Hello ${params.to.name},"
            val brand = _brand ?: defaultbrand
            SendEmailParams(
                from = from,
                to = params.to,
                subject = "Your ${brand.name} account verification",,
                body = EmailTemplate(
                    plain = "$greeting, here is your verification token. \n$link",
                    html = RecoveryEmails.recovery(
                        brand = brand,
                        label = brand.name,
                        greeting = greeting,
                        receptionist = null,
                        link = params.link,
                        year = clock.currentInstant().atSystemZone().year.toString()
                    ).toHtmlString(" ")
                )
            )
        }
    }
}