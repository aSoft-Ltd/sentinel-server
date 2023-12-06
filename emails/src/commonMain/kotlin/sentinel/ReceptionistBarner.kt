package sentinel

import identifier.Brand
import raven.Body
import raven.ComponentScope

fun ComponentScope<Body>.ReceptionistBarner(
    brand: Brand,
    greeting: String,
    receptionist: String,
) = container(css.background(color = brand.color.background).color(brand.color.foreground)) {
    container(css.max(width = "50em").position("relative")) {
        row {
            col(css.width("10%")) { Logo(brand) }
            col(css.width("90%").text(align = "left")) {
                label(css.font(size = "1.6em"), greeting)
            }
        }
        img(
            src = receptionist,
            alt = "receptionist",
            style = css.position("absolute").max(width = "50%").right("0").top("max(8%,3em)")
        )
    }
}