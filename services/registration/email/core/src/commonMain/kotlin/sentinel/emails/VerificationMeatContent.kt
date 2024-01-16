package sentinel.emails

import identifier.Brand
import raven.ComponentScope
import raven.Container

internal fun ComponentScope<Container>.VerificationMeatContent(brand: Brand, link: String) {
    p { text("We are thrilled to have you join us") }
    p {
        text(
            """
                To be able to proceed with your registration while keeping your data secure, 
                we need you to verify your email by clicking the "verify email" button below.
            """.trimIndent()
        )
    }
    container(center.padding(v = "1.5em")) {
        button(css.background(brand.color.background), href = link) {
            label(css.color(brand.color.foreground), "Verify Email")
        }
    }
    p {
        text("Thanks!")
        br()
        text("~ The ${brand.name} team")
    }
}