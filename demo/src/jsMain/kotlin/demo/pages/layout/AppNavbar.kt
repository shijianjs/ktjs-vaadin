package demo.pages.layout

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.vaadin.applayout.createDrawerToggle
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import web.html.HTMLElement

internal class AppNavbar : CustomElementAdapter() {
    private val toggle = createDrawerToggle().apply { style.setProperty("color", "var(--lumo-body-text-color)") }

    private val caption = heading(3, "").apply {
        style.setProperty("margin", "0")
        style.setProperty("white-space", "nowrap")
    }

    /** The heading a page binds its title to; the caption lives in the shadow root. */
    val captionNode: HTMLElement get() = caption

    init {
        attachShadowRoot().append(
            createHorizontalLayout().apply {
                style.setProperty("align-items", "center")
                style.setProperty("gap", "var(--lumo-space-s)")
                append(toggle, caption)
            },
        )
    }

    /** Sets the application title next to the drawer toggle. */
    fun show(title: String) {
        caption.textContent = title
    }

    override fun connectedCallback() {
        slot = "navbar"
    }

    companion object : CustomElementReferenceAdapter<AppNavbar>(AppNavbar::class.js, "vaadin-demo-app-navbar")
}
