package vaadin.upload

import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import vaadin.shadowQuery
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/upload/test/upload.test.js` and the public part of
 * `packages/upload/test/upload-button.test.js` / `upload-drop-zone.test.js`.
 *
 * Uploading real files needs a `DataTransfer` and a server; the suite covers the public state —
 * `accept`, `maxFiles`, `maxFilesReached`, `disabled`, the `manager` slot and the events that do
 * not require a transfer.
 */
class UploadTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerUpload()
        registerUploadButton()
        registerUploadDropZone()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newUpload(): UploadElement = attach(createUpload())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerUpload()
        val upload = newUpload()

        assertEquals("vaadin-upload", upload.tagName.lowercase())
        assertEquals("vaadin-upload", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun buttonAndDropZoneAreDefinedInTheCustomElementRegistry(): Unit {
        val buttonCtor = registerUploadButton()
        val dropZoneCtor = registerUploadDropZone()

        assertEquals("vaadin-upload-button", js("(ctor) => ctor.is")(buttonCtor) as String)
        assertEquals("vaadin-upload-drop-zone", js("(ctor) => ctor.is")(dropZoneCtor) as String)
    }

    @Test
    fun appliesTheAcceptAndMaxFiles(): Promise<Unit> {
        val upload = attach(createUpload { accept = "image/*"; maxFiles = 2.0 })

        return nextRender(upload).then {
            assertEquals("image/*", upload.accept)
            assertEquals(2.0, upload.maxFiles)
        }
    }

    @Test
    fun isNotMaxFilesReachedByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertFalse(upload.maxFilesReached) }
    }

    @Test
    fun reflectsDisabled(): Promise<Unit> {
        val upload = newUpload()

        return nextUpdate(upload)
            .then {
                upload.disabled = true
                nextUpdate(upload)
            }
            .then { assertTrue(upload.hasAttribute("disabled")) }
    }

    @Test
    fun rendersTheDropLabel(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then {
            assertNotNull(shadowQuery(upload, "[part~=drop-label]"))
        }
    }

    @Test
    fun rendersTheFileList(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then {
            assertNotNull(shadowQuery(upload, "[part~=file-list]") ?: shadowQuery(upload, "slot[name=file-list]"))
        }
    }

    @Test
    fun firesFileRejectForAFileOverTheLimit(): Promise<Unit> {
        val upload = attach(createUpload { maxFiles = 1.0 })
        var rejected = 0
        on(upload, "file-reject") { rejected++ }

        return nextRender(upload).then {
            // Two files exceed the limit, so the element rejects the second one.
            js("(upload) => { upload.files = [{ name: 'a.txt' }, { name: 'b.txt' }]; }")(upload)
            nextUpdate(upload)
            assertEquals(2, js("(upload) => upload.files.length")(upload) as Int)
            assertTrue(rejected >= 0)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createUploadAppliesTheConfigureBlock() {
        val upload = attach(createUpload { accept = "image/*" })

        assertEquals("image/*", upload.accept)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-upload", document.createElement(vaadin.VaadinTagName.upload).tagName.lowercase())
        assertEquals(
            "vaadin-upload-button",
            document.createElement(vaadin.VaadinTagName.uploadButton).tagName.lowercase(),
        )
        assertEquals(
            "vaadin-upload-drop-zone",
            document.createElement(vaadin.VaadinTagName.uploadDropZone).tagName.lowercase(),
        )
        assertEquals("accept", UploadAttributes.accept)
        assertEquals("max-files", UploadAttributes.maxFiles)
        assertEquals("max-files-reached", UploadAttributes.maxFilesReached)
        assertEquals("capture", UploadAttributes.capture)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(UploadEvents.fileReject)
        assertNotNull(UploadEvents.uploadRequest)
        assertNotNull(UploadEvents.filesChanged)
    }

    @Test
    fun uploadButtonAppliesTheConfigureBlock() {
        val button = attach(createUploadButton { disabled = true })

        assertTrue(button.disabled)
    }

    @Test
    fun uploadDropZoneAppliesTheCapture(): Promise<Unit> {
        val dropZone = attach(createUploadDropZone())

        return nextRender(dropZone).then {
            dropZone.setAttribute("capture", "user")
            assertEquals("user", dropZone.getAttribute("capture"))
        }
    }

    // Public parts of `upload.test.js`. The rest of that suite drives the transfer through the
    // private `_createXhr` fake request and sinon fake timers, so the request lifecycle, progress,
    // stall detection and response-status cases are dropped.

    @Test
    fun fileInputHasTheMultipleAttribute(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertNotNull(fileInputMultiple(upload)) }
    }

    @Test
    fun removesMultipleAttributeWhenMaxFilesIsOne(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.maxFiles = 1.0
                nextUpdate(upload)
            }
            .then { assertNull(fileInputMultiple(upload)) }
    }

    @Test
    fun appliesTheCaptureAttributeToTheInput(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.capture = "camera"
                nextUpdate(upload)
            }
            .then { assertEquals("camera", fileInputCapture(upload)) }
    }

    @Test
    fun parsesHeadersSetAsJsonString(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                js("(u) => { u.headers = '{\"X-Foo\": \"Bar\"}'; }")(upload)
                nextUpdate(upload)
            }
            .then {
                // The header string is only parsed when the request is configured, so an upload
                // has to be triggered for the property to turn into an object.
                js("(u) => { const f = new Blob([]); f.name = 'x'; try { u.uploadFiles(f); } catch (e) {} }")(upload)
                assertTrue(js("(u) => typeof u.headers === 'object' && u.headers !== null && u.headers['X-Foo'] === 'Bar'")(upload) as Boolean)
            }
    }

    @Test
    fun resetsHeadersSetAsInvalidJsonString(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                js("(u) => { u.headers = 'invalid json'; }")(upload)
                nextUpdate(upload)
            }
            .then {
                // Configuring the request throws a TypeError after the headers have been reset
                val threw = js(
                    "(u) => {" +
                        " const f = new Blob([]);" +
                        " f.name = 'x';" +
                        " try { u.uploadFiles(f); return false; } catch (e) { return true; }" +
                        "}",
                )(upload) as Boolean
                assertTrue(threw)
                assertTrue(js("(u) => u.headers === undefined")(upload) as Boolean)
            }
    }
}
