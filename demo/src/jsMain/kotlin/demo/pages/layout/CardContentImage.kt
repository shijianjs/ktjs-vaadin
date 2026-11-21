package demo.pages.layout

import demo.ui.Images
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import web.html.HtmlTagName

/**
 * `<demo-card-content-image>`: the media-and-text variant of the card's content slot.
 *
 * The photo is the one the official card example ships; the caption under it is the same short Lapland
 * paragraph, so the two variants of the slot read alike.
 */
internal class CardContentImage : CustomElementAdapter(), CardTranslatable {
    private val caption = HtmlTagName.div()

    init {
        cardTexts.bind(caption, { it.shortText }) { caption.textContent = it }
        attachShadowRoot().append(
            HtmlTagName.div().apply {
                append(
                    HtmlTagName.img().apply {
                        src = Images.lapland
                        alt = ""
                        style.setProperty("max-width", "100%")
                        style.setProperty("border-radius", "var(--lumo-border-radius-m)")
                    },
                    caption,
                )
            },
        )
        cardTexts.apply(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        cardTexts.apply(lang)
    }

    companion object : CardContentImageDefaultReference(name = "demo-card-content-image")
}
