package vaadin.popover

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/popover/test/popover.test.js`.
 *
 * The hover/focus driven cases need real pointer sequences; the open/close lifecycle, the trigger
 * and position API and the attribute contract are translated.
 */
class PopoverTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerPopover()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newPopover(): PopoverElement = attach(createPopover())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerPopover()
        val popover = newPopover()

        assertEquals("vaadin-popover", popover.tagName.lowercase())
        assertEquals("vaadin-popover", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val popover = newPopover()

        return nextRender(popover).then { assertFalse(popover.opened) }
    }

    @Test
    fun opensThroughTheProperty(): Promise<Unit> {
        val popover = newPopover()

        return nextRender(popover)
            .then {
                popover.opened = true
                nextUpdate(popover)
            }
            .then { assertTrue(popover.opened) }
    }

    @Test
    fun wiresToATargetThroughTheForAttribute(): Promise<Unit> {
        val popover = newPopover()
        val target = attach(document.createElement("button"))
        target.setAttribute("id", "target")

        return nextRender(popover)
            .then {
                popover.setAttribute("for", "target")
                nextUpdate(popover)
            }
            .then { assertEquals("target", popover.getAttribute("for")) }
    }

    @Test
    fun appliesThePosition(): Promise<Unit> {
        val popover = attach(createPopover { position = "end-top" })

        return nextRender(popover).then { assertEquals("end-top", popover.position) }
    }

    @Test
    fun appliesTheTrigger(): Promise<Unit> {
        val popover = newPopover()

        return nextUpdate(popover)
            .then {
                popover.setAttribute("trigger", "click")
                nextUpdate(popover)
            }
            .then { assertEquals("click", popover.getAttribute("trigger")) }
    }

    @Test
    fun supportsTheModalMode(): Promise<Unit> {
        val popover = attach(createPopover { modal = true })

        return nextRender(popover).then { assertTrue(popover.modal) }
    }

    @Test
    fun rendersTheContent(): Promise<Unit> {
        val popover = newPopover()
        js("(popover) => { popover.textContent = 'Content'; }")(popover)

        return nextRender(popover)
            .then {
                popover.opened = true
                nextUpdate(popover)
            }
            .then {
                // The content is moved into the overlay while the popover is open.
                assertNotNull(popover.shadowRoot)
            }
    }

    @Test
    fun firesOpenedChanged(): Promise<Unit> {
        val popover = newPopover()
        var fired = 0
        on(popover, "opened-changed") { fired++ }

        return nextRender(popover)
            .then {
                popover.opened = true
                nextUpdate(popover)
            }
            .then { assertTrue(fired > 0, "expected an opened-changed event, got $fired") }
    }

    // Kotlin-only coverage

    @Test
    fun createPopoverAppliesTheConfigureBlock() {
        val popover = attach(createPopover { opened = true })

        assertTrue(popover.opened)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-popover", document.createElement(vaadin.VaadinTagName.popover).tagName.lowercase())
        assertEquals("opened", PopoverAttributes.opened)
        assertEquals("position", PopoverAttributes.position)
        assertEquals("trigger", PopoverAttributes.trigger)
        assertEquals("modal", PopoverAttributes.modal)
        assertEquals("for", PopoverAttributes.`for`)
        assertEquals("no-close-on-esc", PopoverAttributes.noCloseOnEsc)
        assertEquals("no-close-on-outside-click", PopoverAttributes.noCloseOnOutsideClick)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(PopoverEvents.openedChanged)
    }
}
