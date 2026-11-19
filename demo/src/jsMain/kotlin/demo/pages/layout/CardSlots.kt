package demo.pages.layout

import demo.tr
import io.github.shijianjs.ktjs.demo.page.heading
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HtmlTagName

/**
 * `<demo-card-slot-text>`: a text node for one of the card's named slots (title, subtitle, …).
 *
 * The slot name is an attribute of the host, so the card reads it exactly as it reads any other slotted
 * node; the text is the host's own child, which keeps the element from being a wrapper around a wrapper.
 */
internal class CardSlotText : CustomElementAdapter() {
    private var slotName: String = ""

    /** Sets the slot the card reads this node from; the attribute may not be set before connecting. */
    override fun connectedCallback() {
        if (slotName.isNotEmpty()) slot = slotName
    }

    /** Sets the slot name and the text it shows. */
    fun show(slotName: String, text: String) {
        this.slotName = slotName
        textContent = text
        slot = slotName
    }

    companion object : CustomElementReference<CardSlotText>(CardSlotText::class.js) {
        val NAME: TagName<CardSlotText> = TagName("demo-card-slot-text")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<demo-card-slot-text>` for [slotName]. */
internal fun newCardSlotText(slotName: String, text: String): CardSlotText {
    CardSlotText.define()
    return CardSlotText.NAME().also { it.show(slotName, text) }
}

/**
 * `<demo-card-header>`: the custom header of the official header example — a kicker above the title.
 *
 * The kicker is the small uppercase line, and the title is the same `<h2>` the kit's heading block draws,
 * so the header reads like the rest of the demo pages.
 */
internal class CardHeader : CustomElementAdapter() {
    init {
        attachShadowRoot().append(
            HtmlTagName.div().apply {
                style.setProperty("line-height", "1.25")
                append(
                    HtmlTagName.div().apply {
                        style.setProperty("font-size", "0.8125rem")
                        style.setProperty("text-transform", "uppercase")
                        textContent = tr("异域北方", "The Exotic North")
                    },
                    heading(2, tr("拉普兰", "Lapland")),
                )
            },
        )
    }

    override fun connectedCallback() {
        slot = "header"
    }

    companion object : CustomElementReference<CardHeader>(CardHeader::class.js) {
        val NAME: TagName<CardHeader> = TagName("demo-card-header")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<demo-card-header>`. */
internal fun newCardHeader(): CardHeader {
    CardHeader.define()
    return CardHeader.NAME()
}
