package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter

/**
 * `<demo-card-slot-text>`: a text node for one of the card's named slots (title, subtitle, …).
 *
 * The slot name is an attribute of the host, so the card reads it exactly as it reads any other slotted
 * node; the text is the host's own child, which keeps the element from being a wrapper around a wrapper.
 * The text is handed in by the page's text table, so a language switch rewrites it in place.
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

    companion object : CustomElementReferenceAdapter<CardSlotText>(CardSlotText::class.js, "demo-card-slot-text")
}

/** Creates a defined `<demo-card-slot-text>` for [slotName]. */
internal fun newCardSlotText(slotName: String, text: String): CardSlotText {
    // The companion registers the tag in its `init`; reading `NAME` upgrades the element the page already spells.
    CardSlotText.NAME
    return CardSlotText.NAME().also { it.show(slotName, text) }
}

/**
 * `<demo-card-header>`: the custom header of the official header example — a kicker above the title.
 *
 * The kicker is the small uppercase line, and the title is the same `<h2>` the kit's heading block draws,
 * so the header reads like the rest of the demo pages. Both lines come from the shared card text table.
 */
