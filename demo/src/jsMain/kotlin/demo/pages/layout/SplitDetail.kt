package demo.pages.layout

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import web.html.HTMLElement

internal class SplitDetail : CustomElementAdapter() {
    init {
        attachShadowRoot().append(fields())
    }

    override fun connectedCallback() {
        style.setProperty("display", "block")
        style.setProperty("padding", "var(--lumo-space-s)")
    }

    /** Label above input, both grey blocks: enough shape to read as a form. */
    private fun fields(): HTMLElement =
        HtmlTagName.div {
            repeat(10) {
                val field =
                    HtmlTagName.div {
                        style.setProperty("margin-bottom", "var(--lumo-space-s)")
                    }
                val label =
                    HtmlTagName.div {
                        style.setProperty("width", "40%")
                        style.setProperty("height", "8px")
                        style.setProperty("margin-bottom", "4px")
                        style.setProperty("border-radius", "2px")
                        style.setProperty("background", "var(--lumo-contrast-20pct)")
                    }
                val input =
                    HtmlTagName.div {
                        style.setProperty("height", "24px")
                        style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
                        style.setProperty("border-radius", "var(--lumo-border-radius-s)")
                    }
                field.append(label, input)
                append(field)
            }
        }

    companion object : CustomElementReferenceAdapter<SplitDetail>(SplitDetail::class.js, "vaadin-demo-split-detail")
}
