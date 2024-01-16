package sentinel.emails

import identifier.Brand
import raven.Body
import raven.ComponentScope

internal fun ComponentScope<Body>.ReceptionistVerificationMeat(
    brand: Brand,
    label: String,
    link: String,
) = container(css.max(width = "40em").padding(top = "6em", bottom = "2em", left = "2em", right = "2em")) {
    row {
        col(center) {
            text(css.font(size = "3em", weight = "bold"), label)
        }
    }
    VerificationMeatContent(brand, link)
}