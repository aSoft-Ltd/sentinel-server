package sentinel

import identifier.Brand
import raven.bodyMarkup
import raven.css
import sentinel.emails.ReceptionistVerificationMeat
import sentinel.emails.ReceptionlessVerificationMeat

object VerificationEmails {
    fun verification(
        brand: Brand,
        greeting: String,
        label: String,
        receptionist: String?,
        link: String,
        year: String,
    ) = bodyMarkup(css().font(family = "Inter,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Helvetica Neue,Ubuntu,sans-serif;")) {
        if (receptionist != null) {
            ReceptionistBarner(brand, greeting, receptionist)
            ReceptionistVerificationMeat(brand, label, link)
        } else {
            ReceptionlessBarner(brand, label)
            ReceptionlessVerificationMeat(brand, greeting, link)
        }
        Footer(brand, "registered", year)
    }
}