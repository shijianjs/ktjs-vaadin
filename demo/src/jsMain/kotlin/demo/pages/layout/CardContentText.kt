package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HtmlTagName

/** `<demo-card-content-text>`: the short Lapland paragraph the card example puts in its content slot. */
internal class CardContentText : CustomElementAdapter(), CardTranslatable {
    private val text = HtmlTagName.div()

    init {
        cardTexts.bind(text, { it.shortText }) { text.textContent = it }
        attachShadowRoot().append(text)
        cardTexts.apply(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        cardTexts.apply(lang)
    }

    companion object : CustomElementReference<CardContentText>(CardContentText::class.js) {
        val NAME: TagName<CardContentText> = TagName("demo-card-content-text")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<demo-card-content-text>`. */
internal fun newCardContentText(): CardContentText {
    CardContentText.define()
    return CardContentText.NAME()
}
