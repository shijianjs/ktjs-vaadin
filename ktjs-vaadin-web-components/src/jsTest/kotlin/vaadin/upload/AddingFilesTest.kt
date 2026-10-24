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
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.html.HTMLElement

/**
 * Translated from `packages/upload/test/adding-files.test.js`.
 *
 * The upstream suite injects a fake XHR through the private `_createXhr` member and drives the
 * upload through sinon fake timers; here `noAuto` keeps the files out of the transfer. Dropped
 * cases: everything that calls the protected `_addFile` / `_removeFile` members or that waits on a
 * fake upload, plus the drop cases that build `FileSystemFileEntry` / `FileSystemDirectoryEntry`
 * objects and real uploads. The dragover / dragleave state machine and the drop-effect cases are
 * covered.
 */
class AddingFilesTest {
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

    private fun newUpload(): UploadElement = attach(createUpload()).also { it.noAuto = true }

    // files property

    @Test
    fun prependsFilesToFilesArrayWhenAddingViaInput(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                assertEquals(0, upload.files.size)
                val files = createFiles(2, 512, "application/x-octet-stream")
                addFilesViaInput(upload, files)
                nextUpdate(upload).then { files }
            }
            .then { files ->
                assertEquals(files[1].name, upload.files[0].name)
                assertEquals(files[0].name, upload.files[1].name)
            }
    }

    @Test
    fun notifiesFilesPropertyChanges(): Promise<Unit> {
        val upload = newUpload()
        var count = 0

        return nextRender(upload)
            .then {
                on(upload, "files-changed") { count++ }
                upload.files = createFiles(2)
                nextUpdate(upload)
            }
            .then { assertEquals(1, count) }
    }

    @Test
    fun doesNotValidateFilesAssignedToTheFilesProperty(): Promise<Unit> {
        val upload = newUpload()
        var rejects = 0

        return nextRender(upload)
            .then {
                upload.maxFileSize = 511.0
                nextUpdate(upload)
            }
            .then {
                on(upload, "file-reject") { rejects++ }
                upload.files = createFiles(2)
                nextUpdate(upload)
            }
            .then {
                assertEquals(0, rejects)
                assertEquals(2, upload.files.size)
            }
    }

    @Test
    fun allowsFilesNotMatchingTheAcceptFilterToBeAssigned(): Promise<Unit> {
        val upload = newUpload()
        var rejects = 0

        return nextRender(upload)
            .then {
                upload.accept = "image/*"
                nextUpdate(upload)
            }
            .then {
                on(upload, "file-reject") { rejects++ }
                upload.files = createFiles(2)
                nextUpdate(upload)
            }
            .then {
                assertEquals(0, rejects)
                assertEquals(2, upload.files.size)
            }
    }

    @Test
    fun allowsMoreFilesThanMaxFilesToBeAssigned(): Promise<Unit> {
        val upload = newUpload()
        var rejects = 0

        return nextRender(upload)
            .then {
                upload.maxFiles = 1.0
                nextUpdate(upload)
            }
            .then {
                on(upload, "file-reject") { rejects++ }
                upload.files = createFiles(2)
                nextUpdate(upload)
            }
            .then {
                assertEquals(0, rejects)
                assertEquals(2, upload.files.size)
                assertTrue(upload.maxFilesReached)
            }
    }

    @Test
    fun updatesMaxFilesReachedWhenFilesAreAssigned(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.maxFiles = 1.0
                upload.files = arrayOf(createFile(512))
                nextUpdate(upload)
            }
            .then {
                assertTrue(upload.maxFilesReached)
                upload.files = emptyArray()
                nextUpdate(upload)
            }
            .then { assertFalse(upload.maxFilesReached) }
    }

    @Test
    fun syncsFilesClearedByAssignmentAfterAddingFilesViaInput(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.maxFiles = 1.0
                addFilesViaInput(upload, arrayOf(createFile(512)))
                nextUpdate(upload)
            }
            .then {
                assertTrue(upload.maxFilesReached)
                upload.files = emptyArray()
                nextUpdate(upload)
            }
            .then { assertFalse(upload.maxFilesReached) }
    }

    // Dragging over the element

    private fun dndEvent(upload: HTMLElement, type: String): web.events.Event =
        js(
            "(upload, type) => {" +
                " const e = new Event(type, { cancelable: true });" +
                " e.dataTransfer = { items: [], files: [], dropEffect: 'none' };" +
                " upload.dispatchEvent(e);" +
                " return e;" +
                "}",
        )(upload, type) as web.events.Event

    private fun defaultPrevented(event: web.events.Event): Boolean = js("(e) => e.defaultPrevented")(event) as Boolean

    private fun dropEffect(event: web.events.Event): String = js("(e) => e.dataTransfer.dropEffect")(event) as String

    @Test
    fun preventsDefaultOnDragover(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertTrue(defaultPrevented(dndEvent(upload, "dragover"))) }
    }

    @Test
    fun preventsDefaultOnDragleave(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertTrue(defaultPrevented(dndEvent(upload, "dragleave"))) }
    }

    @Test
    fun setsDragoverAttributeOnDragover(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                assertFalse(upload.hasAttribute("dragover"))
                dndEvent(upload, "dragover")
                nextUpdate(upload)
            }
            .then {
                assertTrue(upload.hasAttribute("dragover"))
                assertEquals("true", upload.getAttribute("dragover"))
                assertEquals("true", upload.getAttribute("dragover-valid"))
            }
    }

    @Test
    fun removesDragoverAttributeOnDragleave(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                dndEvent(upload, "dragover")
                nextUpdate(upload)
            }
            .then {
                assertTrue(upload.hasAttribute("dragover"))
                dndEvent(upload, "dragleave")
                nextUpdate(upload)
            }
            .then {
                assertFalse(upload.hasAttribute("dragover"))
                assertFalse(upload.hasAttribute("dragover-valid"))
            }
    }

    @Test
    fun doesNotHaveDragoverValidAttributeWhenMaxFilesAdded(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.maxFiles = 1.0
                upload.files = arrayOf(createFile(100, "image/jpeg"))
                nextUpdate(upload)
            }
            .then {
                dndEvent(upload, "dragover")
                nextUpdate(upload)
            }
            .then {
                assertTrue(upload.hasAttribute("dragover"))
                assertFalse(upload.hasAttribute("dragover-valid"))
            }
    }

    @Test
    fun doesNotHaveDragoverValidAttributeWhenDisabled(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.disabled = true
                upload.files = arrayOf(createFile(100, "image/jpeg"))
                nextUpdate(upload)
            }
            .then {
                dndEvent(upload, "dragover")
                nextUpdate(upload)
            }
            .then {
                assertTrue(upload.hasAttribute("dragover"))
                assertFalse(upload.hasAttribute("dragover-valid"))
            }
    }

    @Test
    fun setsDropEffectToCopyWhenDropIsAllowed(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then {
            val event = dndEvent(upload, "dragover")
            assertEquals("copy", dropEffect(event))
        }
    }

    @Test
    fun setsDropEffectToNoneWhenMaxFilesReached(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.maxFiles = 1.0
                upload.files = arrayOf(createFile(100, "image/jpeg"))
                nextUpdate(upload)
            }
            .then {
                val event = dndEvent(upload, "dragover")
                assertEquals("none", dropEffect(event))
            }
    }

    @Test
    fun setsDropEffectToNoneWhenDisabled(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.disabled = true
                nextUpdate(upload)
            }
            .then {
                val event = dndEvent(upload, "dragover")
                assertEquals("none", dropEffect(event))
            }
    }
}
