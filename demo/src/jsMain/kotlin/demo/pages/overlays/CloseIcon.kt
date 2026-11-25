package demo.pages.overlays

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import web.dom.clickEvent
import web.events.addHandler

        /**
         * `<vaadin-demo-close-icon>`: the cross button the notification examples put into their `end` slot.
         *
         * The host is what the notification slots, so `slot="end"` stays on it; the button and its icon sit
         * in the shadow root and the action is handed in as [show]'s callback.
         */

internal class CloseIcon : CustomElementAdapter() {
    private val button = createButton { textContent = "" }.addTheme("icon")
    private val icon = createIcon { icon = "lumo:cross" }

    init {
        button.append(icon)
        attachShadowRoot().append(button)
    }

    /** Sets the accessible name and the action the click runs. */
    fun show(name: String, onClick: () -> Unit) {
        button.ariaLabel = name
        button.clickEvent.addHandler { onClick() }
    }

    companion object : CloseIconDefaultReference()
}
