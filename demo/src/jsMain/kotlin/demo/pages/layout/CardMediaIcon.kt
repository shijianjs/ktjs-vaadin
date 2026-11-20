package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.icon.createIcon

/**
 * `<demo-card-media-icon>`: the card's media slot filled with the photo icon, tinted so it reads as an
 * image rather than as a button.
 */

internal class CardMediaIcon : CustomElementAdapter() {
    private val icon = createIcon {
        icon = "lumo:photo"
        style.setProperty("background", "var(--lumo-primary-color-10pct)")
        style.setProperty("color", "var(--lumo-primary-color)")
    }

    init {
        attachShadowRoot().append(icon)
    }

    /** The host is the slot the card reads; an attribute may not be set before the element connects. */
    override fun connectedCallback() {
        slot = "media"
    }

    companion object : CustomElementReferenceAdapter<CardMediaIcon>(CardMediaIcon::class.js, "demo-card-media-icon")
}
