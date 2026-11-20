package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.scroller.ScrollerElement
import io.github.shijianjs.ktjs.vaadin.scroller.createScroller
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.html.HtmlTagName

/**
 * `<demo-card-content-scroller>`: the scrolling variant of the card's content slot.
 *
 * The scroller is capped at a height the card example fixes, so the overflow indicators have something
 * to scroll: that cap belongs to this element, not to the page.
 */
internal class CardContentScroller : CustomElementAdapter(), CardTranslatable {
    private val text = HtmlTagName.div()

    private val scroller: ScrollerElement =
        createScroller { addTheme("overflow-indicators") }.apply {
            style.setProperty("max-height", "100px")
            style.setProperty("min-height", "100%")
            append(text)
        }

    init {
        cardTexts.bind(text, { "${it.longText} ${it.samiText}" }) { text.textContent = it }
        attachShadowRoot().append(scroller)
        cardTexts.apply(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        cardTexts.apply(lang)
    }

    companion object : CustomElementReferenceAdapter<CardContentScroller>(CardContentScroller::class.js, "demo-card-content-scroller")
}
