package sentinel

import identifier.Brand
import raven.Body
import raven.ComponentScope

fun ComponentScope<Body>.Footer(
    brand: Brand,
    action: String,
    year: String
) = container(css.background("#f9fafb").color("#6c7f93").padding(top = "2em")) {
    container(center.max(width = "40em").padding(h = "2em")) {
        val font = "14px"
        p {
            text(css.font(size = font), "This email was sent to you because you $action at")
            br()
            text(css.color(brand.color.background), brand.name)
        }
        p {
            text(css.font(size = font), "If you did not do register this kind of action, report ")
            text(css.font(size = font).color(brand.color.background), "here")
        }
        p { text(css.font(size = font), brand.address) }
        p { text(css.font(size = font), "Copyright ${169.toChar()} $year") }
        row(css.padding(top = "1em").max(width = "20em")) {
            for (s in brand.socials) col(center) {
                a(href = s.handle) {
                    img(style = css.display("inline-block").width("30px"), src = s.icon, alt = s.handle)
                }
            }
        }
    }
    row(css.height("3em").margin(top = "3em").background(brand.color.background)) {}
}