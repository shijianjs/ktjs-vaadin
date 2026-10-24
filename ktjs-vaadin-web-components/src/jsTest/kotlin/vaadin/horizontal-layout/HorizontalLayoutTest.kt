package vaadin.horizontallayout

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/horizontal-layout/test/horizontal-layout.test.js`.
 *
 * The upstream suite also asserts flex and min-width computed styles for the disabled
 * "layout improvements" mode; those are covered by [childrenDoNotGetFlexOrMinWidth].
 */
class HorizontalLayoutTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun div(slot: String? = null): HTMLElement = document.createElement("div").also { element ->
        slot?.let { element.setAttribute("slot", it) }
    }

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerHorizontalLayout()
        val layout = attach(createHorizontalLayout())

        assertEquals("vaadin-horizontal-layout", layout.tagName.lowercase())
        assertEquals("vaadin-horizontal-layout", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun boxSizingIsBorderBox() {
        val layout = attach(createHorizontalLayout())
        assertEquals("border-box", js("(element) => getComputedStyle(element).boxSizing")(layout) as String)
    }

    @Test
    fun spacingThemeDoesNotOccupySpaceOutside() {
        val wrapper = attach(document.createElement("div"))
        val layout = createHorizontalLayout()
        layout.appendChild(div())
        wrapper.appendChild(layout)

        layout.setAttribute("theme", "spacing")
        wrapper.style.width = "200px"
        layout.style.minWidth = "200px"

        assertEquals(200, js("(element) => element.scrollWidth")(wrapper) as Int)
    }

    @Test
    fun childrenDoNotGetFlexOrMinWidth(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val plain = div()
        val fullWidth = div()
        fullWidth.setAttribute("data-width-full", "")
        layout.appendChild(plain)
        layout.appendChild(fullWidth)

        return nextFrame().then {
            listOf(plain, fullWidth).forEach { child ->
                assertEquals("0 1 auto", js("(element) => getComputedStyle(element).flex")(child) as String)
                assertEquals("auto", js("(element) => getComputedStyle(element).minWidth")(child) as String)
            }
        }
    }

    @Test
    fun startSlotSetsHasStartAttribute(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        layout.appendChild(div())

        return nextFrame().then { assertTrue(layout.hasAttribute("has-start")) }
    }

    @Test
    fun removingTheStartChildClearsHasStartAttribute(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val child = div()
        layout.appendChild(child)

        return nextFrame()
            .then {
                assertTrue(layout.hasAttribute("has-start"))
                layout.removeChild(child)
            }
            .then { nextFrame() }
            .then { assertFalse(layout.hasAttribute("has-start")) }
    }

    @Test
    fun lastStartChildAttributeFollowsTheLastChild(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val first = div()
        val second = div()
        layout.appendChild(first)

        return nextFrame()
            .then {
                assertTrue(first.hasAttribute("last-start-child"))
                layout.appendChild(second)
            }
            .then { nextFrame() }
            .then {
                assertFalse(first.hasAttribute("last-start-child"))
                assertTrue(second.hasAttribute("last-start-child"))
            }
    }

    @Test
    fun removingTheLastStartChildClearsTheAttribute(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val child = div()
        layout.appendChild(child)

        return nextFrame()
            .then {
                assertTrue(child.hasAttribute("last-start-child"))
                layout.removeChild(child)
            }
            .then { nextFrame() }
            .then { assertFalse(child.hasAttribute("last-start-child")) }
    }

    @Test
    fun middleSlotSetsHasMiddleAttribute(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        layout.appendChild(div("middle"))

        return nextFrame().then { assertTrue(layout.hasAttribute("has-middle")) }
    }

    @Test
    fun removingTheMiddleChildClearsHasMiddleAttribute(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val child = div("middle")
        layout.appendChild(child)

        return nextFrame()
            .then {
                assertTrue(layout.hasAttribute("has-middle"))
                layout.removeChild(child)
            }
            .then { nextFrame() }
            .then { assertFalse(layout.hasAttribute("has-middle")) }
    }

    @Test
    fun firstMiddleChildAttributeFollowsTheFirstChild(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val first = div("middle")
        val second = div("middle")
        layout.appendChild(first)

        return nextFrame()
            .then {
                assertTrue(first.hasAttribute("first-middle-child"))
                layout.insertBefore(second, first)
            }
            .then { nextFrame() }
            .then {
                assertFalse(first.hasAttribute("first-middle-child"))
                assertTrue(second.hasAttribute("first-middle-child"))
            }
    }

    @Test
    fun lastMiddleChildAttributeFollowsTheLastChild(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val first = div("middle")
        val second = div("middle")
        layout.appendChild(first)

        return nextFrame()
            .then {
                assertTrue(first.hasAttribute("last-middle-child"))
                layout.appendChild(second)
            }
            .then { nextFrame() }
            .then {
                assertFalse(first.hasAttribute("last-middle-child"))
                assertTrue(second.hasAttribute("last-middle-child"))
            }
    }

    @Test
    fun removingTheOnlyMiddleChildClearsBothBoundaryAttributes(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val child = div("middle")
        layout.appendChild(child)

        return nextFrame()
            .then {
                assertTrue(child.hasAttribute("first-middle-child"))
                assertTrue(child.hasAttribute("last-middle-child"))
                layout.removeChild(child)
            }
            .then { nextFrame() }
            .then {
                assertFalse(child.hasAttribute("first-middle-child"))
                assertFalse(child.hasAttribute("last-middle-child"))
            }
    }

    @Test
    fun endSlotSetsHasEndAttribute(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        layout.appendChild(div("end"))

        return nextFrame().then { assertTrue(layout.hasAttribute("has-end")) }
    }

    @Test
    fun removingTheEndChildClearsHasEndAttribute(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val child = div("end")
        layout.appendChild(child)

        return nextFrame()
            .then {
                assertTrue(layout.hasAttribute("has-end"))
                layout.removeChild(child)
            }
            .then { nextFrame() }
            .then { assertFalse(layout.hasAttribute("has-end")) }
    }

    @Test
    fun firstEndChildAttributeFollowsTheFirstChild(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val first = div("end")
        val second = div("end")
        layout.appendChild(first)

        return nextFrame()
            .then {
                assertTrue(first.hasAttribute("first-end-child"))
                layout.insertBefore(second, first)
            }
            .then { nextFrame() }
            .then {
                assertFalse(first.hasAttribute("first-end-child"))
                assertTrue(second.hasAttribute("first-end-child"))
            }
    }

    @Test
    fun removingTheFirstEndChildClearsTheAttribute(): Promise<Unit> {
        val layout = attach(createHorizontalLayout())
        val child = div("end")
        layout.appendChild(child)

        return nextFrame()
            .then {
                assertTrue(child.hasAttribute("first-end-child"))
                layout.removeChild(child)
            }
            .then { nextFrame() }
            .then { assertFalse(child.hasAttribute("first-end-child")) }
    }

    @Test
    fun themeAttributeIsExposedAsAConstant() {
        val layout = attach(createHorizontalLayout())
        layout.setAttribute(HorizontalLayoutAttributes.theme, "spacing")
        assertEquals("spacing", layout.getAttribute("theme"))
    }
}
