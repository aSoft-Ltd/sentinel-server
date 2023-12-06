package sentinel

import identifier.Brand
import raven.Body
import raven.ComponentScope

fun ComponentScope<Body>.ReceptionlessBarner(
    brand: Brand,
    label: String,
) = row(css.background(color = brand.color.background).color(brand.color.foreground)) {
    col(center.width("100%").padding(v = "1em")) {
        Logo(brand)
        br()
        label(css.font(size = "3em"), label)
    }
}