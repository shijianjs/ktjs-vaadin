package demo.pages.theming

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.invoke

        /**
         * `<vaadin-demo-swatch-chip>`: a colour chip bound to one Lumo custom property.
         *
         * The element itself is the chip, so the property lands on its own style instead of on a child.
         */

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

    companion object : SwatchChipDefaultReference()
}
