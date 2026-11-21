package demo.pages.overlays

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.dom.attribute.tokenListAttribute
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon

internal class IconButton : CustomElementAdapter() {
    private val button = createButton { textContent = "" }.apply { addTheme("icon") }

    init {
        attachShadowRoot().append(button)
    }

    /** Sets the accessible name and the icons the button shows. */
    fun show(name: String, vararg iconNames: String) {
        button.setAttribute("aria-label", name)
        button.textContent = ""
        iconNames.forEach { button.append(createIcon { icon = it }) }
    }

    /** Adds [themes] to the wrapped button, not to the host. */
    fun addTheme(vararg themes: String) {
        button.tokenListAttribute("theme").add(*themes)
    }

    companion object : IconButtonDefaultReference()
}
