package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.vaadin.applayout.createDrawerToggle
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.lumostyles.LumoCustomProperties
import web.cssom.`var`
import web.html.HTMLElement

/**
 * `<vaadin-demo-app-navbar>`: the navbar of the app layout examples — drawer toggle plus the
 * application title. The host carries the slot, the nodes sit in its shadow root.
 */

internal class AppNavbar : CustomElementAdapter() {
    private val toggle = createDrawerToggle().apply { inlineStyle.color = `var`(LumoCustomProperties.BODY_TEXT_COLOR) }

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
                inlineStyle.gap = `var`(LumoCustomProperties.SPACE_S)
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

    companion object : AppNavbarDefaultReference()
}
