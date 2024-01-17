package sentinel.emails

import identifier.Brand
import raven.ComponentScope
import raven.Container

internal fun ComponentScope<Container>.RecoveryMeatContent(brand: Brand, link: String) {
    p { text("We will get your account recovered") }
    p {
        text(
            """
                To be able to proceed with your recovery process while keeping your data secure, 
                we need you to change your password by clicking "change password" button below.
            """.trimIndent()
        )
    }
    container(center.padding(v = "1.5em")) {
        button(css.background(brand.color.background), href = link) {
            label(css.color(brand.color.foreground), "Change Password")
        }
    }
    p {
        text("Thanks!")
        br()
        text("~ The ${brand.name} team")
    }
}