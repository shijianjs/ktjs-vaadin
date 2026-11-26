package demo.pages.layout

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.icon.IconCustomProperties
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import web.cssom.rem

/**
 * `<vaadin-demo-completion-icon>`: the check that marks a completed wizard step in its summary.
 *
 * The icon is hidden until the step's button opens the next panel, and the step toggles `hidden` on
 * the host, so the host is the icon's wrapper rather than something the page has to reach into.
 */

internal class CompletionIcon : CustomElementAdapter() {
    private val icon = createIcon {
        icon = "vaadin:check"
        inlineStyle[IconCustomProperties.SIZE] = 1.rem
    }

    private var startedHidden = false

    init {
        attachShadowRoot().append(icon)
    }

    /** The step's button clears `hidden`; a reconnect must not put it back. */
    override fun connectedCallback() {
        if (startedHidden) return
        startedHidden = true
        toggleAttribute("hidden", true)
    }

    companion object : CompletionIconDefaultReference()
}
