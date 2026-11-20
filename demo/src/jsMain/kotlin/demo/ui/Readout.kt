package demo.ui

import io.github.shijianjs.ktjs.demo.page.Readout as KitReadout
import io.github.shijianjs.ktjs.dom.element.invoke
import web.html.HTMLElement
import web.html.HtmlTagName

/**
 * Text that is updated in place, showing the current state of an example.
 *
 * The box and its colour come from the kit; the `prefix / value / suffix` form is this demo's
 * convention for state lines, so it stays here.
 */
class Readout(initial: String) {
    private val readout = KitReadout.NAME { show(initial) }

    val root: HTMLElement get() = readout.root

    fun set(text: String) {
        readout.set(text)
    }

    /** Renders `prefix value suffix`, with [value] emphasised. */
    fun set(prefix: String, value: String, suffix: String = "") {
        readout.root.replaceChildren(
            HtmlTagName.span { textContent = prefix },
            HtmlTagName.strong { textContent = value },
            HtmlTagName.span { textContent = suffix },
        )
    }
}
