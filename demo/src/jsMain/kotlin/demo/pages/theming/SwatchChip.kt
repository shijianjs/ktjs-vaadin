package demo.pages.theming

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter

internal class SwatchChip : CustomElementAdapter() {
    private var cssVariable: String? = null

    /** Shows the colour held by [cssVariable]. */
    fun show(cssVariable: String) {
        this.cssVariable = cssVariable
        style.setProperty("display", "block")
        style.setProperty("width", "3rem")
        style.setProperty("height", "3rem")
        style.setProperty("border-radius", "var(--lumo-border-radius-m)")
        style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
        style.setProperty("background", "var($cssVariable)")
    }

    override fun connectedCallback() {
        cssVariable?.let { show(it) }
    }

    companion object : CustomElementReferenceAdapter<SwatchChip>(SwatchChip::class.js, "vaadin-demo-swatch-chip")
}
