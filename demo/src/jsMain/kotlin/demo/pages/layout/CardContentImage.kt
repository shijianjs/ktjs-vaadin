package demo.pages.layout

import demo.ui.Images
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HtmlTagName

/**
 * `<demo-card-content-image>`: the media-and-text variant of the card's content slot.
 *
 * The photo is the one the official card example ships; the caption under it is the same short Lapland
 * paragraph, so the two variants of the slot read alike.
 */
internal class CardContentImage : CustomElementAdapter() {
    init {
        attachShadowRoot().append(
            HtmlTagName.div().apply {
                append(
                    HtmlTagName.img().apply {
                        src = Images.lapland
                        alt = ""
                        style.setProperty("max-width", "100%")
                        style.setProperty("border-radius", "var(--lumo-border-radius-m)")
                    },
                    HtmlTagName.div { textContent = laplandShortText() },
                )
            },
        )
    }

    companion object : CustomElementReference<CardContentImage>(CardContentImage::class.js) {
        val NAME: TagName<CardContentImage> = TagName("demo-card-content-image")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<demo-card-content-image>`. */
internal fun newCardContentImage(): CardContentImage {
    CardContentImage.define()
    return CardContentImage.NAME()
}
