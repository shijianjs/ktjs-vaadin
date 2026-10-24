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
import vaadin.nextFrame
import vaadin.nextRender
import web.html.HTMLElement

/**
 * Translated from `packages/upload/test/file-list.test.js`.
 *
 * The upstream suite injects a fake XHR through the private `_createXhr` member to keep the
 * uploads from reaching the network; here `noAuto` is set instead, which keeps the files in the
 * list without starting a transfer. The "custom file list" cases define a `LitElement` wrapper in
 * the test and are dropped, since the Kotlin tests do not pull in lit.
 */
class FileListTest {
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

    @Test
    fun rendersFiles(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                assertEquals(0, fileListItems(upload).size)
                val files = createFiles(2)
                addFilesViaInput(upload, files)
                nextFrame().then { files }
            }
            .then { files ->
                val items = fileListItems(upload)
                assertEquals(2, items.size)
                assertEquals(files[1].name, itemFileName(items[0]))
                assertEquals(files[0].name, itemFileName(items[1]))
            }
    }

    @Test
    fun removesFiles(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                addFilesViaInput(upload, createFiles(2))
                nextFrame()
            }
            .then {
                removeFile(upload, 1)
                nextFrame()
            }
            .then {
                assertEquals(1, fileListItems(upload).size)
                removeFile(upload, 0)
                nextFrame()
            }
            .then { assertEquals(0, fileListItems(upload).size) }
    }

    @Test
    fun propagatesDisabledToTheRenderedFileItems(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                addFilesViaInput(upload, createFiles(1))
                nextFrame()
            }
            .then {
                upload.disabled = true
                nextFrame()
            }
            .then {
                assertTrue(fileListItems(upload)[0].hasAttribute("disabled"))
                upload.disabled = false
                nextFrame()
            }
            .then { assertFalse(fileListItems(upload)[0].hasAttribute("disabled")) }
    }

    @Test
    fun doesNotOverflowContent(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.style.width = "180px"
                upload.files = createFiles(1)
                nextRender(upload)
            }
            .then {
                val item = fileListItems(upload)[0]
                assertEquals(0, item.scrollWidth - item.offsetWidth)
            }
    }
}
