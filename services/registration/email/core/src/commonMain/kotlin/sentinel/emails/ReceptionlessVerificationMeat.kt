package sentinel.emails

import identifier.Brand
import raven.Body
import raven.ComponentScope

internal fun ComponentScope<Body>.ReceptionlessVerificationMeat(
    brand: Brand,
    greeting: String,
    link: String
) = container(css.max(width = "40em").padding(v = "2em", h = "2em")) {
    p { text(css.font(size = "1.6em", weight = "bold"), greeting) }
    VerificationMeatContent(brand, link)
}