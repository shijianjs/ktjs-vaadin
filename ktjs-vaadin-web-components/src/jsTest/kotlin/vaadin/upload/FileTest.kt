package vaadin.upload

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/upload/test/file.test.js`.
 *
 * Dropped upstream cases: the `focus-ring` cases that use `sendKeys({ press: 'Tab' })` need a real
 * keyboard sequence, which the browser test runner does not provide; the programmatic variants of
 * those cases are kept.
 */
class FileTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerUploadFile()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newFileElement(): UploadFileElement {
        val element = attach(createUploadFile())
        js("(el) => { el.i18n = { file: { start: 'Start', retry: 'Retry', remove: 'Remove' } }; }")(element)
        element.file = createFile(100000, "application/unknown")
        return element
    }

    private fun tabIndexOf(element: HTMLElement): Int = js("(el) => el.tabIndex")(element) as Int

    // state attributes

    @Test
    fun isNotUploadingByDefault(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file).then { assertFalse(file.hasAttribute("uploading")) }
    }

    @Test
    fun reflectsUploading(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.uploading = true
                nextUpdate(file)
            }
            .then { assertTrue(file.hasAttribute("uploading")) }
    }

    @Test
    fun isNotIndeterminateByDefault(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file).then { assertFalse(file.hasAttribute("indeterminate")) }
    }

    @Test
    fun reflectsIndeterminate(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.indeterminate = true
                nextUpdate(file)
            }
            .then { assertTrue(file.hasAttribute("indeterminate")) }
    }

    @Test
    fun isNotCompleteByDefault(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file).then { assertFalse(file.hasAttribute("complete")) }
    }

    @Test
    fun reflectsComplete(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.complete = true
                nextUpdate(file)
            }
            .then { assertTrue(file.hasAttribute("complete")) }
    }

    @Test
    fun isNotErrorByDefault(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file).then { assertFalse(file.hasAttribute("error")) }
    }

    @Test
    fun reflectsError(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.errorMessage = "Server error"
                nextUpdate(file)
            }
            .then { assertTrue(file.hasAttribute("error")) }
    }

    @Test
    fun isNotDisabledByDefault(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file).then { assertFalse(file.hasAttribute("disabled")) }
    }

    @Test
    fun reflectsDisabled(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.disabled = true
                nextUpdate(file)
            }
            .then { assertTrue(file.hasAttribute("disabled")) }
    }

    // focus

    @Test
    fun isFocusableByDefault(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                // Show the "Start" button
                file.held = true
                nextUpdate(file)
            }
            .then {
                js("(el) => el.focus()")(file)
                assertTrue(file.hasAttribute("focused"))
                assertEquals(file, document.activeElement)
            }
    }

    @Test
    fun isNotFocusableWhenDisabled(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.held = true
                file.disabled = true
                nextUpdate(file)
            }
            .then {
                js("(el) => el.focus()")(file)
                assertFalse(file.hasAttribute("focused"))
                assertTrue(document.activeElement !== file)
            }
    }

    @Test
    fun reflectsTabindexToAttribute(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.held = true
                nextUpdate(file)
            }
            .then {
                assertEquals("0", file.getAttribute("tabindex"))
                file.tabIndex = 1
                nextUpdate(file)
            }
            .then { assertEquals("1", file.getAttribute("tabindex")) }
    }

    @Test
    fun removesTabindexAttributeWhenDisabled(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.held = true
                nextUpdate(file)
            }
            .then {
                assertEquals("0", file.getAttribute("tabindex"))
                file.disabled = true
                nextUpdate(file)
            }
            .then { assertFalse(file.hasAttribute("tabindex")) }
    }

    @Test
    fun addsFocusRingToTheHostOnProgrammaticFocus(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.held = true
                nextUpdate(file)
            }
            .then {
                js("(el) => el.focus()")(file)
                assertTrue(file.hasAttribute("focus-ring"))
            }
    }

    @Test
    fun doesNotAddFocusRingOnFocusWithFocusVisibleFalse(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.held = true
                nextUpdate(file)
            }
            .then {
                js("(el) => el.focus({ focusVisible: false })")(file)
                assertFalse(file.hasAttribute("focus-ring"))
            }
    }

    // disabled

    @Test
    fun doesNotDisableButtonsByDefault(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file).then {
            val buttons = js("(el) => Array.from(el.shadowRoot.querySelectorAll('[part$=\"-button\"]'))")(file) as Array<HTMLElement>
            buttons.forEach { assertFalse(js("(b) => b.disabled")(it) as Boolean) }
        }
    }

    @Test
    fun disablesAllButtonsWhenDisabled(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.disabled = true
                nextUpdate(file)
            }
            .then {
                val buttons = js("(el) => Array.from(el.shadowRoot.querySelectorAll('[part$=\"-button\"]'))")(file) as Array<HTMLElement>
                buttons.forEach { assertTrue(js("(b) => b.disabled")(it) as Boolean) }
            }
    }

    @Test
    fun isFocusableByDefaultThroughTabindex(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file).then { assertEquals(0, tabIndexOf(file)) }
    }

    @Test
    fun isNotFocusableWhenDisabledThroughTabindex(): Promise<Unit> {
        val file = newFileElement()

        return vaadin.nextRender(file)
            .then {
                file.disabled = true
                nextUpdate(file)
            }
            .then { assertEquals(-1, tabIndexOf(file)) }
    }
}
