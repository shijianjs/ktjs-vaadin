package vaadin.dialog

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
 * Translated from `packages/dialog/test/dialog.test.js` and the public part of
 * `packages/dialog/test/dialog-resizable.test.js`.
 *
 * The focus-trap, drag and resize cases need real pointer/keyboard sequences and are skipped; the
 * open/close lifecycle, the renderer callbacks, the header/footer and the modeless/overlay state are
 * translated.
 */
class DialogTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerDialog()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newDialog(): DialogElement = attach(createDialog())

    private fun dialogFixture(): DialogElement =
        attach(vaadin.fixture("<vaadin-dialog><div>Content</div></vaadin-dialog>").unsafeCast<DialogElement>())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerDialog()
        val dialog = newDialog()

        assertEquals("vaadin-dialog", dialog.tagName.lowercase())
        assertEquals("vaadin-dialog", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val dialog = newDialog()

        return nextRender(dialog).then { assertFalse(dialog.opened) }
    }

    @Test
    fun opensAndReflectsTheAttribute(): Promise<Unit> {
        val dialog = newDialog()

        return nextRender(dialog)
            .then {
                dialog.opened = true
                nextUpdate(dialog)
            }
            .then {
                assertTrue(dialog.opened)
                assertTrue(dialog.hasAttribute("opened"))
            }
    }

    @Test
    fun closesWhenOpenedIsCleared(): Promise<Unit> {
        val dialog = attach(createDialog { opened = true })

        return nextRender(dialog)
            .then {
                dialog.opened = false
                nextUpdate(dialog)
            }
            .then {
                assertFalse(dialog.opened)
                assertFalse(dialog.hasAttribute("opened"))
            }
    }

    @Test
    fun firesOpenedChanged(): Promise<Unit> {
        val dialog = newDialog()
        var fired = 0
        on(dialog, "opened-changed") { fired++ }

        return nextRender(dialog)
            .then {
                dialog.opened = true
                nextUpdate(dialog)
            }
            .then { assertTrue(fired > 0, "expected an opened-changed event, got $fired") }
    }

    @Test
    fun rendersTheHeaderTitle(): Promise<Unit> {
        val dialog = attach(createDialog { headerTitle = "Title" })

        return nextRender(dialog).then {
            assertEquals("Title", dialog.headerTitle)
            // The title is rendered into the overlay, which lives in the overlay root.
            val title = js("(dialog) => { const overlay = dialog.shadowRoot.querySelector('vaadin-dialog-overlay'); return overlay ? overlay.shadowRoot.querySelector('[part~=title]') : null; }")(dialog)
            assertNotNull(title)
        }
    }

    @Test
    fun rendersTheHeaderRenderer(): Promise<Unit> {
        val dialog = newDialog()
        var rendered = 0
        dialog.headerRenderer = { root, _ ->
            rendered++
            js("(root) => { root.textContent = 'Header'; }")(root)
        }

        return nextRender(dialog)
            .then {
                dialog.opened = true
                nextUpdate(dialog)
            }
            .then { assertTrue(rendered > 0, "expected the header renderer to run, got $rendered") }
    }

    @Test
    fun rendersTheFooterRenderer(): Promise<Unit> {
        val dialog = newDialog()
        var rendered = 0
        dialog.footerRenderer = { root, _ ->
            rendered++
            js("(root) => { root.textContent = 'Footer'; }")(root)
        }

        return nextRender(dialog)
            .then {
                dialog.opened = true
                nextUpdate(dialog)
            }
            .then { assertTrue(rendered > 0, "expected the footer renderer to run, got $rendered") }
    }

    @Test
    fun usesTheModalOverlayByDefault(): Promise<Unit> {
        val dialog = newDialog()

        return nextRender(dialog).then {
            assertFalse(dialog.modeless)
            assertFalse(dialog.hasAttribute("modeless"))
        }
    }

    @Test
    fun supportsModelessDialogs(): Promise<Unit> {
        val dialog = newDialog()

        return nextUpdate(dialog)
            .then {
                dialog.modeless = true
                nextUpdate(dialog)
            }
            .then { assertTrue(dialog.hasAttribute("modeless")) }
    }

    @Test
    fun appliesTheSizeAndPosition(): Promise<Unit> {
        val dialog = attach(createDialog { width = "300px"; height = "200px"; left = "10px"; top = "20px" })

        return nextRender(dialog).then {
            assertEquals("300px", dialog.width)
            assertEquals("200px", dialog.height)
            assertEquals("10px", dialog.left)
            assertEquals("20px", dialog.top)
        }
    }

    @Test
    fun exposesTheContentInTheLightDom(): Promise<Unit> {
        val dialog = dialogFixture()

        return nextRender(dialog)
            .then {
                dialog.opened = true
                nextUpdate(dialog)
            }
            .then {
                // The slotted content stays in the light DOM of the dialog.
                assertNotNull(js("(dialog) => dialog.querySelector('div')")(dialog))
            }
    }

    // Kotlin-only coverage

    @Test
    fun createDialogAppliesTheConfigureBlock() {
        val dialog = attach(createDialog { opened = true; noCloseOnEsc = true })

        assertTrue(dialog.opened)
        assertTrue(dialog.noCloseOnEsc)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-dialog", document.createElement(vaadin.VaadinTagName.dialog).tagName.lowercase())
        assertEquals("opened", DialogAttributes.opened)
        assertEquals("header-title", DialogAttributes.headerTitle)
        assertEquals("header-renderer", DialogAttributes.headerRenderer)
        assertEquals("footer-renderer", DialogAttributes.footerRenderer)
        assertEquals("modeless", DialogAttributes.modeless)
        assertEquals("draggable", DialogAttributes.draggable)
        assertEquals("resizable", DialogAttributes.resizable)
        assertEquals("no-close-on-esc", DialogAttributes.noCloseOnEsc)
        assertEquals("no-close-on-outside-click", DialogAttributes.noCloseOnOutsideClick)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(DialogEvents.openedChanged)
    }
}
