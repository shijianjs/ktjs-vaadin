package vaadin.upload

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
import web.html.HTMLElement

/**
 * Translated from `packages/upload/test/slots.test.js`.
 *
 * Dropped upstream cases: the "open file dialog on button click / touchend" cases drive the file
 * dialog through a stubbed `click` on the internal input and a synthetic `makeSoloTouchEvent`,
 * which needs a real trusted input sequence; the "reset file input value" case reassigns the
 * read-only `value` of the input by replacing its prototype. They depend on internal DOM plumbing
 * rather than the public API. The `_isDefault` custom-label cases depend on the non-public
 * `_isDefault` flag used by the Flow counterpart and are therefore also dropped.
 */
class SlotsTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerUpload()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newUpload(): UploadElement = attach(createUpload())

    private fun slotText(upload: UploadElement, selector: String): String =
        js("(u, s) => u.querySelector(s).textContent")(upload, selector) as String

    private fun slotDisabled(upload: UploadElement, selector: String): Boolean =
        js("(u, s) => u.querySelector(s).disabled")(upload, selector) as Boolean

    // add button

    @Test
    fun setsDefaultButtonTextContent(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then {
            assertEquals("Upload Files...", slotText(upload, "[slot=add-button]"))
        }
    }

    @Test
    fun updatesDefaultButtonTextOnMaxFilesChange(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.maxFiles = 1.0
                nextUpdate(upload)
            }
            .then { assertEquals("Upload File...", slotText(upload, "[slot=add-button]")) }
    }

    @Test
    fun disablesTheButtonWhenMaxFilesAdded(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                // Enabled with default maxFiles value
                assertFalse(slotDisabled(upload, "[slot=add-button]"))
                upload.maxFiles = 1.0
                assertFalse(slotDisabled(upload, "[slot=add-button]"))
                upload.files = arrayOf(createFile(100, "image/jpeg"))
                nextUpdate(upload)
            }
            .then { assertTrue(slotDisabled(upload, "[slot=add-button]")) }
    }

    @Test
    fun doesNotModifyCustomButtonTextContent(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                js("(u) => { const b = document.createElement('button'); b.setAttribute('slot', 'add-button'); b.textContent = 'Add'; u.appendChild(b); }")(upload)
                nextRender(upload)
            }
            .then {
                assertEquals("Add", slotText(upload, "[slot=add-button]"))
                upload.maxFiles = 1.0
                nextUpdate(upload)
            }
            .then { assertEquals("Add", slotText(upload, "[slot=add-button]")) }
    }

    // drop label icon

    @Test
    fun createsDefaultDropLabelIconElement(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then {
            assertNotNull(upload.querySelector("vaadin-upload-icon[slot=drop-label-icon]"))
        }
    }

    // drop label

    @Test
    fun setsDefaultDropLabelTextContent(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertEquals("Drop files here", slotText(upload, "[slot=drop-label]")) }
    }

    @Test
    fun updatesDefaultDropLabelTextOnMaxFilesChange(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.maxFiles = 1.0
                nextUpdate(upload)
            }
            .then { assertEquals("Drop file here", slotText(upload, "[slot=drop-label]")) }
    }

    @Test
    fun doesNotModifyCustomDropLabelTextContent(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                js("(u) => { const l = document.createElement('strong'); l.setAttribute('slot', 'drop-label'); l.textContent = 'Drop image here'; u.appendChild(l); }")(upload)
                nextRender(upload)
            }
            .then {
                assertEquals("Drop image here", slotText(upload, "[slot=drop-label]"))
                upload.maxFiles = 1.0
                nextUpdate(upload)
            }
            .then { assertEquals("Drop image here", slotText(upload, "[slot=drop-label]")) }
    }
}
