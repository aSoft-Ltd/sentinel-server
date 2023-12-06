package sentinel

import identifier.Brand
import raven.ComponentScope

internal fun ComponentScope<*>.Logo(brand: Brand) = img(
    src = brand.logo,
    alt = "logo",
    style = css.display("inline-block").max(width = "80px")
)