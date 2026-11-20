package demo.pages.layout

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.icon.createIcon

internal class CompletionIcon : CustomElementAdapter() {
    private val icon = createIcon {
        icon = "vaadin:check"
        style.setProperty("--vaadin-icon-size", "1rem")
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

    companion object : CustomElementReferenceAdapter<CompletionIcon>(CompletionIcon::class.js, "vaadin-demo-completion-icon")
}
