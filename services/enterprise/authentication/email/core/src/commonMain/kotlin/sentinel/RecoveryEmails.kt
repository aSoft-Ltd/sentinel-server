package sentinel

import identifier.Brand
import raven.bodyMarkup
import raven.css
import sentinel.emails.ReceptionistRecoveryMeat
import sentinel.emails.ReceptionlessRecoveryMeat

object RecoveryEmails {
    fun recovery(
        brand: Brand,
        greeting: String,
        label: String,
        receptionist: String?,
        link: String,
        year: String,
    ) = bodyMarkup(css().font(family = "Inter,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Helvetica Neue,Ubuntu,sans-serif;")) {
        if (receptionist != null) {
            ReceptionistBarner(brand, greeting, receptionist)
            ReceptionistRecoveryMeat(brand, label, link)
        } else {
            ReceptionlessBarner(brand, label)
            ReceptionlessRecoveryMeat(brand, greeting, link)
        }
        Footer(brand, "attempted to recover you account", year)
    }
}