package vaadin.tabs

import vaadin.a11ybase.ListOrientations
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/** Translated from `packages/tabs/test/tabs.test.js` and `packages/tabs/test/tab.test.js`. */
class TabsTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun tabsFixture(): TabsElement {
        val tabs = attach(createTabs())
        listOf("Foo", "Bar", "Some").forEach { tabs.appendChild(createTab { textContent = it }) }
        tabs.appendChild(document.createElement("span"))
        tabs.appendChild(createTab { textContent = "Baz"; disabled = true })
        tabs.appendChild(createTab { textContent = "Baz" })
        return tabs
    }

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerTabs()
        val tabs = tabsFixture()

        assertEquals("vaadin-tabs", tabs.tagName.lowercase())
        assertEquals("vaadin-tabs", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun selectedDefaultsToZero() {
        assertEquals(0.0, tabsFixture().selected)
    }

    @Test
    fun selectedReflectsToTheAttribute(): Promise<Unit> {
        val tabs = tabsFixture()
        tabs.selected = 1.0
        return nextUpdate(tabs).then { assertEquals("1", tabs.getAttribute("selected")) }
    }

    @Test
    fun orientationDefaultsToHorizontal() {
        assertEquals("horizontal", tabsFixture().orientation.unsafeCast<String>())
    }

    @Test
    fun orientationReflectsToTheAttribute(): Promise<Unit> {
        val tabs = tabsFixture()
        tabs.orientation = ListOrientations.vertical
        return nextUpdate(tabs).then { assertEquals("vertical", tabs.getAttribute("orientation")) }
    }

    @Test
    fun itemsContainOnlyTabs(): Promise<Unit> {
        val tabs = tabsFixture()
        return nextUpdate(tabs).then {
            val items = tabs.items!!
            assertEquals(5, items.size)
            items.forEach { assertEquals("vaadin-tab", it.tagName.lowercase()) }
        }
    }

    @Test
    fun hasTablistRole() {
        assertEquals("tablist", tabsFixture().getAttribute("role"))
    }

    @Test
    fun tabIsSelectedByDefaultFalse() {
        val tab = attach(createTab { textContent = "Foo" })
        assertFalse(tab.selected)
        assertFalse(tab.disabled)
        assertEquals("tab", tab.getAttribute("role"))
    }

    @Test
    fun tabSelectedReflectsToTheAttribute(): Promise<Unit> {
        val tab = attach(createTab { textContent = "Foo" })
        tab.selected = true
        return nextUpdate(tab).then { assertTrue(tab.hasAttribute("selected")) }
    }

    @Test
    fun tabDisabledReflectsToTheAttribute(): Promise<Unit> {
        val tab = attach(createTab { textContent = "Foo" })
        tab.disabled = true
        return nextUpdate(tab).then { assertTrue(tab.hasAttribute("disabled")) }
    }
}
