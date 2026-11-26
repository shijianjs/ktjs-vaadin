package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.lumostyles.LumoCustomProperties
import web.cssom.`var`

/** `<vaadin-demo-app-content>`: padding filler for the app layout examples' content area. */
internal class AppContent : CustomElementAdapter() {
    /** Sets the text the content area shows. */
    fun show(text: String) {
        textContent = text
    }

    override fun connectedCallback() {
        style.setProperty("display", "block")
        inlineStyle.padding = `var`(LumoCustomProperties.SPACE_M)
    }

    companion object : AppContentDefaultReference()
}
