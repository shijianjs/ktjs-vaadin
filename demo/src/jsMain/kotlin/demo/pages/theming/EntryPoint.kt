package demo.pages.theming

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.Blocks.codeText
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import demo.ui.SMALL
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter

        /**
         * `<vaadin-demo-entry-point>`: a stylesheet entry-point constant and the value behind it.
         *
         * The element puts its two nodes into itself rather than into a shadow root, because the paragraph
         * and the code block are kit pieces whose look comes from the page's sheet.
         */

internal class EntryPoint : CustomElementAdapter() {
    private var name: String? = null
    private var value: String? = null
    private var built = false

    /** Sets the constant and its value; the row is built once, whichever call comes first. */
    fun show(name: String, value: String) {
        this.name = name
        this.value = value
        build()
    }

    override fun connectedCallback() {
        build()
    }

    private fun build() {
        val name = name ?: return
        val value = value ?: return
        if (built) return
        built = true
        append(
            kitRow(HtmlTagName.p { className = SMALL; textContent = name }, codeText(value)),
        )
    }

    companion object : CustomElementReferenceAdapter<EntryPoint>(EntryPoint::class.js, "vaadin-demo-entry-point")
}
