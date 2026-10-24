package vaadin.overlay

import vaadin.shadowQuery
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
 * Translated from `packages/overlay/test/overlay.test.js`.
 *
 * The focus/positioning cases need layout and real focus sequences; the open/close lifecycle, the
 * renderer and the attribute API are translated.
 */
class OverlayTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerOverlay()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newOverlay(): OverlayElement = attach(createOverlay())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerOverlay()
        val overlay = newOverlay()

        assertEquals("vaadin-overlay", overlay.tagName.lowercase())
        assertEquals("vaadin-overlay", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val overlay = newOverlay()

        return nextRender(overlay).then { assertFalse(overlay.opened) }
    }

    @Test
    fun opensAndReflectsTheAttribute(): Promise<Unit> {
        val overlay = newOverlay()

        return nextRender(overlay)
            .then {
                overlay.opened = true
                nextUpdate(overlay)
            }
            .then {
                assertTrue(overlay.opened)
                assertTrue(overlay.hasAttribute("opened"))
            }
    }

    @Test
    fun firesOpenedChanged(): Promise<Unit> {
        val overlay = newOverlay()
        var fired = 0
        on(overlay, "opened-changed") { fired++ }

        return nextRender(overlay)
            .then {
                overlay.opened = true
                nextUpdate(overlay)
            }
            .then { assertTrue(fired > 0, "expected an opened-changed event, got $fired") }
    }

    @Test
    fun rendersTheContentIntoTheOverlayPart(): Promise<Unit> {
        val overlay = newOverlay()
        js("(overlay) => { overlay.textContent = 'Content'; }")(overlay)

        return nextRender(overlay)
            .then {
                overlay.opened = true
                nextUpdate(overlay)
            }
            .then { assertNotNull(shadowQuery(overlay, "[part=content]")) }
    }

    @Test
    fun acceptsACustomRenderer(): Promise<Unit> {
        val overlay = newOverlay()
        var rendered = 0
        overlay.renderer = { root, _, _ ->
            rendered++
            js("(root) => { root.textContent = 'Rendered'; }")(root)
        }

        return nextRender(overlay)
            .then {
                overlay.opened = true
                nextUpdate(overlay)
            }
            .then { assertTrue(rendered > 0, "expected the renderer to run, got $rendered") }
    }

    @Test
    fun usesTheModalModeByDefault(): Promise<Unit> {
        val overlay = newOverlay()

        return nextRender(overlay).then {
            assertFalse(overlay.modeless)
            assertFalse(overlay.withBackdrop)
        }
    }

    @Test
    fun supportsTheBackdrop(): Promise<Unit> {
        val overlay = newOverlay()

        return nextUpdate(overlay)
            .then {
                overlay.withBackdrop = true
                nextUpdate(overlay)
            }
            .then { assertTrue(overlay.withBackdrop) }
    }

    // Kotlin-only coverage

    @Test
    fun createOverlayAppliesTheConfigureBlock() {
        val overlay = attach(createOverlay { opened = true })

        assertTrue(overlay.opened)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-overlay", document.createElement(vaadin.VaadinTagName.overlay).tagName.lowercase())
        assertEquals("opened", OverlayAttributes.opened)
        assertEquals("modeless", OverlayAttributes.modeless)
        assertEquals("with-backdrop", OverlayAttributes.withBackdrop)
        assertEquals("owner", OverlayAttributes.owner)
        assertEquals("focus-trap", OverlayAttributes.focusTrap)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(OverlayEvents.openedChanged)
    }
}
