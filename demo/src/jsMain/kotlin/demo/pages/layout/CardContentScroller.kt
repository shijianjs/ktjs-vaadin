package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import io.github.shijianjs.ktjs.vaadin.scroller.ScrollerElement
import io.github.shijianjs.ktjs.vaadin.scroller.createScroller
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HtmlTagName

/**
 * `<demo-card-content-scroller>`: the scrolling variant of the card's content slot.
 *
 * The scroller is capped at a height the card example fixes, so the overflow indicators have something
 * to scroll: that cap belongs to this element, not to the page.
 */
internal class CardContentScroller : CustomElementAdapter() {
    private val scroller: ScrollerElement =
        createScroller { addTheme("overflow-indicators") }.apply {
            style.setProperty("max-height", "100px")
            style.setProperty("min-height", "100%")
            append(HtmlTagName.div { textContent = "${laplandLongText()} ${laplandSamiText()}" })
        }

    init {
        attachShadowRoot().append(scroller)
    }

    companion object : CustomElementReference<CardContentScroller>(CardContentScroller::class.js) {
        val NAME: TagName<CardContentScroller> = TagName("demo-card-content-scroller")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<demo-card-content-scroller>`. */
internal fun newCardContentScroller(): CardContentScroller {
    CardContentScroller.define()
    return CardContentScroller.NAME()
}
