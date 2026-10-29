package vaadin.upload

import js.objects.unsafeJso
import kotlin.js.Promise
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.query
import vaadin.shadowQuery
import web.html.HTMLElement

/**
 * Covers the hand-written parts of the upload wrapper: [UploadManagerOptions] with
 * [createUploadManager], the [UploadFile.error] field and the typed `i18n` accessor added to the
 * file list mixin.
 *
 * Upstream has no suite for the manager class on its own; the cases follow the constructor
 * documented in `packages/upload/src/vaadin-upload-manager.js` and use the manager through the
 * public `vaadin-upload-file-list` API, since its `items` property is marked `@readonly`.
 */
class UploadManagerTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    @Test
    fun createsManagerFromOptions() {
        val manager =
            createUploadManager(
                unsafeJso {
                    target = "/api/upload"
                    method = UploadMethods.put
                    timeout = 5000.0
                    maxFiles = 2.0
                    maxFileSize = 1024.0
                    accept = "image/*"
                    noAuto = true
                    withCredentials = true
                    uploadFormat = UploadFormats.multipart
                    maxConcurrentUploads = 1.0
                    formDataName = "attachment"
                    disabled = true
                },
            )

        assertEquals("/api/upload", manager.target)
        assertEquals(UploadMethods.put, manager.method)
        assertEquals(5000.0, manager.timeout)
        assertEquals(2.0, manager.maxFiles)
        assertEquals(1024.0, manager.maxFileSize)
        assertEquals("image/*", manager.accept)
        assertTrue(manager.noAuto)
        assertTrue(manager.withCredentials)
        assertEquals(UploadFormats.multipart, manager.uploadFormat)
        assertEquals(1.0, manager.maxConcurrentUploads)
        assertEquals("attachment", manager.formDataName)
        assertTrue(manager.disabled)
        assertFalse(manager.maxFilesReached)
    }

    /**
     * [UploadFile.error] is not part of the upstream `.d.ts` but the docs examples assign it and
     * the file list renders it.
     */
    @Test
    fun rendersTheErrorMessageOfATrackedFile(): Promise<Unit> {
        val upload = attach(createUpload()).also { it.noAuto = true }
        val broken = createTrackedFile()
        broken.status = "error"
        broken.error = "Upload failed"

        upload.files = arrayOf(broken)

        return nextRender(upload).then {
            val item = query(upload, "vaadin-upload-file")!!
            assertEquals("Upload failed", shadowQuery(item, "[part=error]")!!.textContent)
        }
    }

    /**
     * The generated file list mixin declares `i18n` untyped because `custom-elements.json` only
     * records `Object`; this covers the typed [UploadFileListMixin.i18nProperty] accessor.
     */
    @Test
    fun fileListTypedI18nLabelsTheRemoveButton(): Promise<Unit> {
        val manager = createUploadManager(unsafeJso { noAuto = true })
        manager.files = arrayOf(createTrackedFile())

        val list = attach(createUploadFileList())
        list.manager = manager
        list.i18nProperty = unsafeJso { file = unsafeJso { remove = "Delete file" } }

        return nextRender(list).then {
            val item = query(list, "vaadin-upload-file")!!
            assertEquals("Delete file", shadowQuery(item, "[part=remove-button]")!!.getAttribute("aria-label"))
        }
    }
}
