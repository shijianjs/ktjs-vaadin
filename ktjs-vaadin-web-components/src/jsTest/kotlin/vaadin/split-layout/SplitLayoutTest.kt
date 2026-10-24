package vaadin.splitlayout

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/split-layout/test/split-layout.test.js`.
 *
 * The upstream suite also drags the splitter with synthetic pointer tracks and asserts pixel
 * sizes; those require the shadow-DOM splitter and a layout engine, so they are not translated
 * (see the coverage notes in AGENTS.md). Slot assignment, orientation and i18n are covered here.
 */
class SplitLayoutTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun div(id: String, slot: String? = null, text: String = id): HTMLElement =
        document.createElement("div").also { element ->
            element.setAttribute("id", id)
            element.textContent = text
            slot?.let { element.setAttribute("slot", it) }
        }

    private fun slotOf(element: HTMLElement): String? = element.getAttribute("slot")

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerSplitLayout()
        val layout = attach(createSplitLayout())

        assertEquals("vaadin-split-layout", layout.tagName.lowercase())
        assertEquals("vaadin-split-layout", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun orientationDefaultsToHorizontal() {
        assertEquals("horizontal", attach(createSplitLayout()).orientation)
    }

    @Test
    fun orientationReflectsToTheAttribute(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        layout.orientation = "vertical"

        return nextFrame().then { assertEquals("vertical", layout.getAttribute("orientation")) }
    }

    @Test
    fun contentElementsAreDistributed(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        val first = div("first")
        val second = div("second")
        layout.appendChild(first)
        layout.appendChild(second)

        return nextFrame().then {
            assertEquals(layout, first.parentNode)
            assertEquals(layout, second.parentNode)
            assertEquals("primary", slotOf(first))
            assertEquals("secondary", slotOf(second))
        }
    }

    @Test
    fun preDefinedSlotsAreRespected(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        val second = div("second", "secondary")
        val first = div("first", "primary")
        layout.appendChild(second)
        layout.appendChild(first)

        return nextFrame().then {
            assertEquals("primary", slotOf(first))
            assertEquals("secondary", slotOf(second))
        }
    }

    @Test
    fun missingPrimarySlotIsAssigned(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        val second = div("second", "secondary")
        val first = div("first")
        layout.appendChild(second)
        layout.appendChild(first)

        return nextFrame().then {
            assertEquals("primary", slotOf(first))
            assertEquals("secondary", slotOf(second))
        }
    }

    @Test
    fun missingSecondarySlotIsAssigned(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        val second = div("second")
        val first = div("first", "primary")
        layout.appendChild(second)
        layout.appendChild(first)

        return nextFrame().then {
            assertEquals("primary", slotOf(first))
            assertEquals("secondary", slotOf(second))
        }
    }

    @Test
    fun assignedSlotSurvivesReorderingWithOnePreDefinedSlot(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        val second = div("second")
        val first = div("first", "primary")
        layout.appendChild(second)
        layout.appendChild(first)

        return nextFrame()
            .then {
                layout.prepend(first)
                nextFrame()
            }
            .then {
                assertEquals("primary", slotOf(first))
                assertEquals("secondary", slotOf(second))
            }
    }

    @Test
    fun slotsSwapWhenChildrenWithoutPreDefinedSlotsInvertOrder(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        val second = div("second")
        val first = div("first")
        layout.appendChild(second)
        layout.appendChild(first)

        return nextFrame()
            .then {
                assertEquals("primary", slotOf(second))
                assertEquals("secondary", slotOf(first))
                layout.prepend(first)
                nextFrame()
            }
            .then {
                assertEquals("secondary", slotOf(second))
                assertEquals("primary", slotOf(first))
            }
    }

    @Test
    fun slotsAreAssignedOnlyForDirectChildren(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        val first = div("first")
        val nested = createSplitLayout()
        nested.setAttribute("id", "second")
        val nestedFirst = div("nested-first", "primary")
        val nestedSecond = div("nested-second", "secondary")
        nested.appendChild(nestedFirst)
        nested.appendChild(nestedSecond)
        layout.appendChild(first)
        layout.appendChild(nested)

        return nextFrame().then {
            assertEquals("primary", slotOf(first))
            assertEquals("secondary", slotOf(nested))
            assertEquals("primary", slotOf(nestedFirst))
            assertEquals("secondary", slotOf(nestedSecond))
        }
    }

    @Test
    fun removingAContentElementClearsItsSlot(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        val first = div("first")
        val second = div("second")
        layout.appendChild(first)
        layout.appendChild(second)

        return nextFrame()
            .then {
                layout.removeChild(first)
                nextFrame()
            }
            .then {
                assertFalse(first.hasAttribute("slot"))
                assertEquals("primary", slotOf(second))
            }
    }

    @Test
    fun movingContentElementsBetweenLayoutsReassignsSlots(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        val first = div("first")
        val second = div("second")
        layout.appendChild(first)
        layout.appendChild(second)

        val other = attach(createSplitLayout())

        return nextFrame()
            .then {
                other.appendChild(second)
                other.appendChild(first)
                nextFrame()
            }
            .then {
                assertEquals("primary", slotOf(second))
                assertEquals("secondary", slotOf(first))
            }
    }

    @Test
    fun i18nDefaultsToTheSeparatorLabel(): Promise<Unit> {
        val layout = attach(createSplitLayout())

        return nextFrame().then {
            assertEquals("Resize separator", js("(layout) => layout.i18n.separator")(layout) as String)
        }
    }

    @Test
    fun i18nCanBeReplaced(): Promise<Unit> {
        val layout = attach(createSplitLayout())
        layout.i18n = js("({ separator: 'Resize' })")

        return nextFrame().then {
            assertEquals("Resize", js("(layout) => layout.i18n.separator")(layout) as String)
        }
    }

    @Test
    fun splitterDragendEventTypeIsExposed() {
        assertNotNull(SplitLayoutEvents.splitterDragend)
    }

    @Test
    fun focusingBeforeConnectingDoesNotThrow() {
        createSplitLayout().focus()
        assertTrue(true)
    }
}
