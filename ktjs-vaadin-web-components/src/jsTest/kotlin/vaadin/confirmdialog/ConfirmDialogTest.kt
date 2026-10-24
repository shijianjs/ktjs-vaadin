package vaadin.confirmdialog

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
 * Translated from `packages/confirm-dialog/test/confirm-dialog.test.js`.
 *
 * The focus and keyboard cases are skipped; the open/close lifecycle, the three actions and their
 * events, and the text/visibility properties are translated.
 */
class ConfirmDialogTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerConfirmDialog()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newDialog(): ConfirmDialogElement = attach(createConfirmDialog())

    private fun button(host: ConfirmDialogElement, name: String): HTMLElement =
        assertNotNull(
            js("(host, name) => host.querySelector('vaadin-button[slot=\"' + name + '-button\"]') || host.shadowRoot.querySelector('vaadin-button[slot=\"' + name + '-button\"]')")(
                host,
                name,
            ) as HTMLElement?,
        )

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerConfirmDialog()
        val dialog = newDialog()

        assertEquals("vaadin-confirm-dialog", dialog.tagName.lowercase())
        assertEquals("vaadin-confirm-dialog", js("(ctor) => ctor.is")(ctor) as String)
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
    fun rendersTheHeaderAndMessage(): Promise<Unit> {
        val dialog = attach(createConfirmDialog { header = "Delete?"; message = "This cannot be undone" })

        return nextRender(dialog)
            .then {
                dialog.opened = true
                nextUpdate(dialog)
            }
            .then {
                assertEquals("Delete?", dialog.header)
                assertEquals("This cannot be undone", dialog.message)
                // The header is slotted into the dialog as the `header` slot.
                assertNotNull(js("(dialog) => dialog.shadowRoot.querySelector('slot[name=header]')")(dialog))
            }
    }

    @Test
    fun firesConfirmAndCloses(): Promise<Unit> {
        val dialog = attach(createConfirmDialog { opened = true })
        var confirmed = 0
        on(dialog, "confirm") { confirmed++ }

        return nextRender(dialog).then {
            js("(button) => button.click()")(button(dialog, "confirm"))
            nextUpdate(dialog)
            assertTrue(confirmed > 0, "expected a confirm event, got $confirmed")
            assertFalse(dialog.opened)
        }
    }

    @Test
    fun firesCancelAndCloses(): Promise<Unit> {
        val dialog = attach(createConfirmDialog { opened = true })
        var cancelled = 0
        on(dialog, "cancel") { cancelled++ }

        return nextRender(dialog).then {
            js("(button) => button.click()")(button(dialog, "cancel"))
            nextUpdate(dialog)
            assertTrue(cancelled > 0, "expected a cancel event, got $cancelled")
            assertFalse(dialog.opened)
        }
    }

    @Test
    fun firesRejectAndCloses(): Promise<Unit> {
        val dialog = attach(createConfirmDialog { opened = true })
        var rejected = 0
        on(dialog, "reject") { rejected++ }

        return nextRender(dialog).then {
            js("(button) => button.click()")(button(dialog, "reject"))
            nextUpdate(dialog)
            assertTrue(rejected > 0, "expected a reject event, got $rejected")
            assertFalse(dialog.opened)
        }
    }

    @Test
    fun hidesTheOptionalButtons(): Promise<Unit> {
        val dialog = attach(createConfirmDialog { cancelButtonVisible = false; rejectButtonVisible = false })

        return nextRender(dialog)
            .then {
                dialog.opened = true
                nextUpdate(dialog)
            }
            .then {
                assertFalse(dialog.cancelButtonVisible)
                assertFalse(dialog.rejectButtonVisible)
                // The buttons stay in the DOM and are hidden through the `hidden` attribute.
                val hidden = js("(dialog) => { const cancel = dialog.querySelector('vaadin-button[slot=cancel-button]'); return cancel ? cancel.hasAttribute('hidden') : null; }")(dialog)
                assertEquals(true, hidden)
            }
    }

    @Test
    fun appliesTheButtonTexts(): Promise<Unit> {
        val dialog = attach(
            createConfirmDialog {
                confirmText = "Yes"
                cancelText = "No"
                rejectText = "Later"
            },
        )

        return nextRender(dialog).then {
            assertEquals("Yes", dialog.confirmText)
            assertEquals("No", dialog.cancelText)
            assertEquals("Later", dialog.rejectText)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createConfirmDialogAppliesTheConfigureBlock() {
        val dialog = attach(createConfirmDialog { header = "Delete?" })

        assertEquals("Delete?", dialog.header)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-confirm-dialog",
            document.createElement(vaadin.VaadinTagName.confirmDialog).tagName.lowercase(),
        )
        assertEquals("header", ConfirmDialogAttributes.header)
        assertEquals("message", ConfirmDialogAttributes.message)
        assertEquals("confirm-text", ConfirmDialogAttributes.confirmText)
        assertEquals("cancel-text", ConfirmDialogAttributes.cancelText)
        assertEquals("reject-text", ConfirmDialogAttributes.rejectText)
        assertEquals("cancel-button-visible", ConfirmDialogAttributes.cancelButtonVisible)
        assertEquals("reject-button-visible", ConfirmDialogAttributes.rejectButtonVisible)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(ConfirmDialogEvents.confirm)
        assertNotNull(ConfirmDialogEvents.cancel)
        assertNotNull(ConfirmDialogEvents.reject)
    }
}
