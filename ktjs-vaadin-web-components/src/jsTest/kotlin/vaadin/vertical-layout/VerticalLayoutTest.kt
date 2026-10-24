package vaadin.verticallayout

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/vertical-layout/test/vertical-layout.test.js`.
 *
 * The upstream "layout improvements disabled" suite only asserts flex and min-height computed
 * styles; [childrenDoNotGetFlexOrMinHeight] keeps those two checks.
 */
class VerticalLayoutTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerVerticalLayout()
        val layout = attach(createVerticalLayout())

        assertEquals("vaadin-vertical-layout", layout.tagName.lowercase())
        assertEquals("vaadin-vertical-layout", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun boxSizingIsBorderBox() {
        val layout = attach(createVerticalLayout())
        assertEquals("border-box", js("(element) => getComputedStyle(element).boxSizing")(layout) as String)
    }

    @Test
    fun spacingThemeDoesNotOccupySpaceOutside() {
        val wrapper = attach(document.createElement("div"))
        val layout = createVerticalLayout()
        layout.appendChild(document.createElement("div"))
        wrapper.appendChild(layout)

        layout.setAttribute("theme", "spacing")
        wrapper.style.height = "200px"
        layout.style.minHeight = "200px"

        assertEquals(200, js("(element) => element.scrollHeight")(wrapper) as Int)
    }

    @Test
    fun childrenDoNotGetFlexOrMinHeight(): Promise<Unit> {
        val layout = attach(createVerticalLayout())
        val plain = document.createElement("div")
        val fullHeight = document.createElement("div")
        fullHeight.setAttribute("data-height-full", "")
        layout.appendChild(plain)
        layout.appendChild(fullHeight)

        return nextFrame().then {
            listOf(plain, fullHeight).forEach { child ->
                assertEquals("0 1 auto", js("(element) => getComputedStyle(element).flex")(child) as String)
                assertEquals("auto", js("(element) => getComputedStyle(element).minHeight")(child) as String)
            }
        }
    }

    @Test
    fun themeAttributeIsExposedAsAConstant() {
        val layout = attach(createVerticalLayout())
        layout.setAttribute(VerticalLayoutAttributes.theme, "spacing")
        assertEquals("spacing", layout.getAttribute("theme"))
    }
}
