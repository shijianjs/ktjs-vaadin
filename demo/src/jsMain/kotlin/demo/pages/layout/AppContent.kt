package demo.pages.layout

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.element.invoke

/** `<vaadin-demo-app-content>`: padding filler for the app layout examples' content area. */
internal class AppContent : CustomElementAdapter() {
    /** Sets the text the content area shows. */
    fun show(text: String) {
        textContent = text
    }

    override fun connectedCallback() {
        style.setProperty("display", "block")
        style.setProperty("padding", "var(--lumo-space-m)")
    }

    companion object : AppContentDefaultReference()
}
