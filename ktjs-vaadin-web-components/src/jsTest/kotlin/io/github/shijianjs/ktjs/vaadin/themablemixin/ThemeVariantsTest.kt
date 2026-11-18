package io.github.shijianjs.ktjs.vaadin.themablemixin

import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.detachAll
import io.github.shijianjs.ktjs.vaadin.verticallayout.VerticalLayoutElement
import io.github.shijianjs.ktjs.vaadin.verticallayout.createVerticalLayout
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import web.html.HTMLElement

/**
 * The `theme` CRUD lives on the wrapper's [ThemePropertyMixin], not on `HTMLElement`, because only
 * the components that mix the interface in render `theme` at all. The assertions read the attribute
 * back, which is the only place the value exists upstream.
 */
class ThemeVariantsTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> track(element: T): T =
        io.github.shijianjs.ktjs.vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newButton(): ButtonElement = track(createButton())

    private fun newLayout(): VerticalLayoutElement = track(createVerticalLayout())

    @Test
    fun `a fresh component has no theme`() {
        val button = newButton()
        assertTrue(button.themes.isEmpty())
        assertFalse(button.hasTheme("primary"))
        assertNull(button.getAttribute("theme"))
    }

    @Test
    fun `addTheme writes the attribute and is idempotent`() {
        val button = newButton()
        button.addTheme("primary")
        assertEquals("primary", button.getAttribute("theme"))
        button.addTheme("primary")
        assertEquals("primary", button.getAttribute("theme"))
        assertEquals(listOf("primary"), button.themes)
    }

    @Test
    fun `addTheme keeps the existing variants in order`() {
        val button = newButton()
        button.addTheme("primary", "small")
        button.addTheme("tertiary")
        assertEquals("primary small tertiary", button.getAttribute("theme"))
        assertEquals(listOf("primary", "small", "tertiary"), button.themes)
        assertTrue(button.hasTheme("small"))
    }

    @Test
    fun `removeTheme drops the variant and ignores unknown ones`() {
        val button = newButton()
        button.setTheme("primary", "small")
        button.removeTheme("primary")
        assertEquals("small", button.getAttribute("theme"))
        button.removeTheme("primary")
        assertEquals("small", button.getAttribute("theme"))
        assertFalse(button.hasTheme("primary"))
        assertTrue(button.hasTheme("small"))
    }

    @Test
    fun `setTheme replaces the whole value and no variants remove the attribute`() {
        val button = newButton()
        button.addTheme("primary")
        button.setTheme("small", "icon-only")
        assertEquals("small icon-only", button.getAttribute("theme"))
        button.setTheme()
        assertNull(button.getAttribute("theme"))
        assertTrue(button.themes.isEmpty())
    }

    @Test
    fun `the setter normalises the attribute`() {
        val button = newButton()
        button.themes = listOf("  primary  ", "small", "primary")
        assertEquals("primary small", button.getAttribute("theme"))
        assertEquals(listOf("primary", "small"), button.themes)
    }

    @Test
    fun `the helpers chain on any themable component`() {
        val layout = newLayout()
        val returned: VerticalLayoutElement = layout.addTheme("padding").addTheme("spacing")
        assertSame(layout, returned)
        assertEquals("padding spacing", layout.getAttribute("theme"))
        layout.removeTheme("padding")
        assertEquals("spacing", layout.getAttribute("theme"))
    }
}
