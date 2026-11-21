package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import web.html.HtmlTagName

/**
 * `<demo-card-header>`: the custom header of the official header example — a kicker above the title.
 *
 * The kicker is the small uppercase line, and the title is the same `<h2>` the kit's heading block draws,
 * so the header reads like the rest of the demo pages. Both lines come from the shared card text table.
 */

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

    companion object : CardHeaderDefaultReference(name = "demo-card-header")
}
