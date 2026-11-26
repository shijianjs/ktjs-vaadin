package demo.pages.theming

import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.vaadin.lumostyles.LumoCustomProperties
import web.cssom.`var`
import web.cssom.Border
import web.cssom.LineStyle
import web.cssom.px

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
        inlineStyle.borderRadius = `var`(LumoCustomProperties.BORDER_RADIUS_M)
        inlineStyle.border = Border(1.px, LineStyle.solid, `var`(LumoCustomProperties.CONTRAST_20PCT))
        style.setProperty("background", "var($cssVariable)")
    }

    override fun connectedCallback() {
        cssVariable?.let { show(it) }
    }

    companion object : SwatchChipDefaultReference()
}
