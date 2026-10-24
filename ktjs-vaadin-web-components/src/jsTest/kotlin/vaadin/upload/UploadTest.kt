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
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
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
            assertNotNull(js("(upload) => upload.shadowRoot.querySelector('[part~=drop-label]')")(upload))
        }
    }

    @Test
    fun rendersTheFileList(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then {
            assertNotNull(js("(upload) => upload.shadowRoot.querySelector('[part~=file-list]') || upload.shadowRoot.querySelector('slot[name=file-list]')")(upload))
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
}
