package demo.pages.layout

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.scroller.createScroller

/**
 * `<vaadin-demo-app-drawer>`: the drawer of the app layout examples, a scroller holding the main
 * side navigation.
 */

internal class AppDrawer : CustomElementAdapter() {
    private val sideNav = AppSideNav.NAME()

    init {
        attachShadowRoot().append(createScroller().apply { append(sideNav) })
    }

    override fun connectedCallback() {
        slot = "drawer"
    }

    /** Writes the shared side navigation in [lang]. */
    fun applyTranslation(lang: Lang) {
        sideNav.applyTranslation(lang)
    }

    companion object : AppDrawerDefaultReference()
}
