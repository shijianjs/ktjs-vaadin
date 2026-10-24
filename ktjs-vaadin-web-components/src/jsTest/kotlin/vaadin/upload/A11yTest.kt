package vaadin.upload

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.shadowQuery
import vaadin.timeout
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/upload/test/a11y.test.js`.
 *
 * The announcement cases replace sinon fake timers with a real `setTimeout`, matching the rest of
 * the Kotlin suites.
 */
class A11yTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerUpload()
        registerUploadFile()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun shadowPart(element: HTMLElement, part: String): HTMLElement = shadowQuery(element, "[part=$part]")!!

    // <vaadin-upload-file>

    private fun newFileElement(): UploadFileElement {
        val element = attach(createUploadFile())
        js("(el) => { el.i18n = { file: { start: 'Start', retry: 'Retry', remove: 'Remove' } }; }")(element)
        element.file = createFile(100000, "application/uknown")
        return element
    }

    @Test
    fun startButtonHasAriaDescribedby(): Promise<Unit> {
        val file = newFileElement()
        val nameId = shadowQuery(file, "[part=name]")?.getAttribute("id")

        return nextRender(file).then {
            assertEquals(nameId, shadowPart(file, "start-button").getAttribute("aria-describedby") as String?)
        }
    }

    @Test
    fun startButtonHasAriaLabel(): Promise<Unit> {
        val file = newFileElement()

        return nextRender(file).then {
            assertEquals("Start", shadowPart(file, "start-button").getAttribute("aria-label"))
        }
    }

    @Test
    fun retryButtonHasAriaDescribedby(): Promise<Unit> {
        val file = newFileElement()
        val nameId = shadowQuery(file, "[part=name]")?.getAttribute("id")

        return nextRender(file).then {
            assertEquals(nameId, shadowPart(file, "retry-button").getAttribute("aria-describedby") as String?)
        }
    }

    @Test
    fun retryButtonHasAriaLabel(): Promise<Unit> {
        val file = newFileElement()

        return nextRender(file).then {
            assertEquals("Retry", shadowPart(file, "retry-button").getAttribute("aria-label"))
        }
    }

    @Test
    fun removeButtonHasAriaDescribedby(): Promise<Unit> {
        val file = newFileElement()
        val nameId = shadowQuery(file, "[part=name]")?.getAttribute("id")

        return nextRender(file).then {
            assertEquals(nameId, shadowPart(file, "remove-button").getAttribute("aria-describedby") as String?)
        }
    }

    @Test
    fun removeButtonHasAriaLabel(): Promise<Unit> {
        val file = newFileElement()

        return nextRender(file).then {
            assertEquals("Remove", shadowPart(file, "remove-button").getAttribute("aria-label"))
        }
    }

    // upload announcements

    /**
     * The shared announce region created by `@vaadin/a11y-base/src/announce.js`. `announce` with
     * `mode: 'alert'` removes the `aria-live` attribute and switches the region to `role="alert"`,
     * so the region is located by either marker.
     */
    private fun announceRegion(): HTMLElement {
        val children = document.body!!.children
        for (index in 0 until children.length) {
            val element = children.item(index) as HTMLElement
            if (element.hasAttribute("aria-live") || element.getAttribute("role") == "alert") return element
        }
        error("The announce region has not been created yet")
    }

    private fun announceText(): String = announceRegion().textContent ?: ""

    private fun announceRole(): String = announceRegion().getAttribute("role") ?: ""

    /** Dispatches a custom event carrying a JS [detail] on [upload]. */
    private fun fireDetail(upload: HTMLElement, type: String, detail: JsAny) {
        js("(upload, type, detail) => { upload.dispatchEvent(new CustomEvent(type, { detail })); }")(upload, type, detail)
    }

    @Test
    fun announcesUploadStart(): Promise<Unit> {
        val upload = attach(createUpload())

        return nextRender(upload)
            .then {
                fireDetail(upload, "upload-start", jsObject("""{"file":{"name":"file.js"}}"""))
                timeout(200)
            }
            .then {
                assertEquals("file.js: 0%", announceText())
                assertEquals("alert", announceRole())
            }
    }

    @Test
    fun announcesUploadSuccess(): Promise<Unit> {
        val upload = attach(createUpload())

        return nextRender(upload)
            .then {
                fireDetail(upload, "upload-success", jsObject("""{"file":{"name":"file.js"}}"""))
                timeout(200)
            }
            .then {
                assertEquals("file.js: 100%", announceText())
                assertEquals("alert", announceRole())
            }
    }

    @Test
    fun announcesFileReject(): Promise<Unit> {
        val upload = attach(createUpload())

        return nextRender(upload)
            .then {
                fireDetail(upload, "file-reject", jsObject("""{"file":{"name":"file.js"},"error":"rejected"}"""))
                timeout(200)
            }
            .then {
                assertEquals("file.js: rejected", announceText())
                assertEquals("alert", announceRole())
            }
    }

    @Test
    fun announcesUploadError(): Promise<Unit> {
        val upload = attach(createUpload())

        return nextRender(upload)
            .then {
                fireDetail(upload, "upload-error", jsObject("""{"file":{"name":"file.js","error":"error"}}"""))
                timeout(200)
            }
            .then {
                assertEquals("file.js: error", announceText())
                assertEquals("alert", announceRole())
            }
    }
}
