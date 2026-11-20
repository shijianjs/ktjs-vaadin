package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import web.html.HtmlTagName

internal class CardHeader : CustomElementAdapter(), CardTranslatable {
    private val kicker = HtmlTagName.div().apply {
        style.setProperty("font-size", "0.8125rem")
        style.setProperty("text-transform", "uppercase")
    }

    private val headingNode = heading(2, "")

    init {
        cardTexts.bind(kicker, { it.exoticNorth }) { kicker.textContent = it }
        cardTexts.bind(headingNode, { it.lapland }) { headingNode.textContent = it }
        attachShadowRoot().append(
            HtmlTagName.div().apply {
                style.setProperty("line-height", "1.25")
                append(kicker, headingNode)
            },
        )
        cardTexts.apply(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        cardTexts.apply(lang)
    }

    override fun connectedCallback() {
        slot = "header"
    }

    companion object : CustomElementReferenceAdapter<CardHeader>(CardHeader::class.js, "demo-card-header")
}
