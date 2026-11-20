package demo.pages.layout

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import web.html.HtmlTagName

/**
 * `<vaadin-demo-nav-link>`: a demo navigation link that stays on the current hash route. The host is
 * a wrapper around the anchor, because an element cannot take over the `a` tag itself.
 */

internal class NavLink : CustomElementAdapter() {
    private val anchor = HtmlTagName.a().apply {
        href = "#/app-layout"
        style.setProperty("display", "flex")
        style.setProperty("align-items", "center")
        style.setProperty("padding", "0 1rem")
        style.setProperty("font-weight", "500")
        style.setProperty("text-decoration", "none")
        style.setProperty("color", "inherit")
    }

    init {
        attachShadowRoot().append(anchor)
    }

    /** Sets the label; with an [icon] the label moves to `aria-label` and the icon stands in for it. */
    fun show(label: String, icon: String? = null) {
        anchor.textContent = ""
        if (icon == null) {
            anchor.textContent = label
        } else {
            anchor.setAttribute("aria-label", label)
            anchor.append(createIcon { this.icon = icon })
        }
    }

    companion object : CustomElementReferenceAdapter<NavLink>(NavLink::class.js, "vaadin-demo-nav-link")
}
