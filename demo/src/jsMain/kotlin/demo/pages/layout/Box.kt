package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter

/**
 * `<demo-layout-box>`: the tinted placeholder the layout examples are built out of.
 *
 * It is a placeholder, not a component of the library: the layout pages show how `vaadin-horizontal-layout`
 * and `vaadin-vertical-layout` arrange children, and these boxes are the children. Callers set what the
 * demo needs on top — an `align-self`, a `slot`, a `flex-grow` — through the element's inline style, which
 * is why the box keeps its own rules in one place instead of in every page.
 */
internal class Box : CustomElementAdapter() {
    private var height: String? = null

    /**
     * Painted on connect: the constructor of a custom element may not set the element's attributes.
     *
     * `height` is read here rather than in [show], because the setter may run before the element is
     * connected.
     */
    override fun connectedCallback() {
        style.setProperty("padding", "var(--lumo-space-s) var(--lumo-space-m)")
        style.setProperty("border-radius", "var(--lumo-border-radius-m)")
        style.setProperty("background", "var(--lumo-primary-color-10pct)")
        height?.let { style.setProperty("height", it) }
    }

    /** Sets the placeholder's text, and the height the layout example asks for. */
    fun show(text: String, height: String? = null) {
        textContent = text
        this.height = height
        height?.let { style.setProperty("height", it) }
    }

    companion object : CustomElementReferenceAdapter<Box>(Box::class.js, "demo-layout-box")
}

