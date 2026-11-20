package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter

        /**
         * `<vaadin-demo-scroller-action>`: the tall icon-prefixed button of the horizontal mobile scroller
         * example. The host is the button's wrapper, so the icon's `prefix` slot survives the wrap.
         */

internal class ScrollerAction : CustomElementAdapter() {
    private val button = createButton { textContent = "" }

    init {
        button.style.setProperty("height", "6.25rem")
        attachShadowRoot().append(button)
    }

    /** Sets the label and the prefix icon. */
    fun show(label: String, icon: String) {
        updateLabel(label)
        button.append(createIcon { this.icon = icon }.apply { slot = "prefix" })
    }

    /** Rewrites the label; the icon stays. */
    fun updateLabel(label: String) {
        button.textContent = label
    }

    companion object : CustomElementReferenceAdapter<ScrollerAction>(ScrollerAction::class.js, "vaadin-demo-scroller-action")
}
