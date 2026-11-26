package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.lumostyles.LumoCustomProperties
import web.cssom.`var`

/**
 * `<demo-card-media-icon>`: the card's media slot filled with the photo icon, tinted so it reads as an
 * image rather than as a button.
 */

internal class CardMediaIcon : CustomElementAdapter() {
    private val icon = createIcon {
        icon = "lumo:photo"
        inlineStyle.background = `var`(LumoCustomProperties.PRIMARY_COLOR_10PCT)
        inlineStyle.color = `var`(LumoCustomProperties.PRIMARY_COLOR)
    }

    init {
        attachShadowRoot().append(icon)
    }

    /** The host is the slot the card reads; an attribute may not be set before the element connects. */
    override fun connectedCallback() {
        slot = "media"
    }

    companion object : CardMediaIconDefaultReference(name = "demo-card-media-icon")
}
