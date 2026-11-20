package demo.pages.layout

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import web.html.HTMLElement

/**
 * `<vaadin-demo-split-master>`: the fake master list of the split layout examples — sixteen rows of
 * six grey cells, which is the shape the docs example uses.
 */

internal class SplitMaster : CustomElementAdapter() {
    init {
        attachShadowRoot().append(cells())
    }

    override fun connectedCallback() {
        style.setProperty("display", "block")
        style.setProperty("padding", "var(--lumo-space-s)")
    }

    /** Six grey cells per row, sixteen rows. */
    private fun cells(): HTMLElement =
        HtmlTagName.div {
            repeat(16) {
                val row =
                    HtmlTagName.div {
                        style.setProperty("display", "grid")
                        style.setProperty("grid-template-columns", "repeat(6, 1fr)")
                        style.setProperty("gap", "4px")
                        style.setProperty("margin-bottom", "4px")
                    }
                repeat(6) {
                    row.append(
                        HtmlTagName.div {
                            style.setProperty("height", "12px")
                            style.setProperty("border-radius", "2px")
                            style.setProperty("background", "var(--lumo-contrast-10pct)")
                        },
                    )
                }
                append(row)
            }
        }

    companion object : CustomElementReferenceAdapter<SplitMaster>(SplitMaster::class.js, "vaadin-demo-split-master")
}
