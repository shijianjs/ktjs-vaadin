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
import vaadin.nextFrame
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.query
import vaadin.shadowQuery
import web.html.HTMLElement

/**
 * Translated from `packages/upload/test/i18n.test.js`.
 *
 * Dropped upstream cases: the file status / error translations and the custom `formatSize` /
 * `formatTime` formatters drive the upload through the private `_createXhr` fake request, so they
 * are out of reach without touching internals. The button, drop label and queued-file aria-label
 * translations are covered here.
 */
class I18nTest {
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

    private fun slotText(upload: UploadElement, selector: String): String = query(upload, selector)!!.textContent ?: ""

    private fun assertTranslations(
        i18n: JsAny?,
        addMany: String,
        addOne: String,
        dropMany: String,
        dropOne: String,
    ): Promise<Unit> {
        val upload = attach(createUpload())
        if (i18n != null) upload.i18n = i18n

        return nextRender(upload)
            .then {
                assertEquals(addMany, slotText(upload, "[slot=add-button]"))
                assertEquals(dropMany, slotText(upload, "[slot=drop-label]"))
                upload.maxFiles = 1.0
                nextUpdate(upload)
            }
            .then {
                assertEquals(addOne, slotText(upload, "[slot=add-button]"))
                assertEquals(dropOne, slotText(upload, "[slot=drop-label]"))
            }
    }

    @Test
    fun translatesUploadButtonAndDropAreaForDefaultI18n(): Promise<Unit> =
        assertTranslations(
            null,
            addMany = "Upload Files...",
            addOne = "Upload File...",
            dropMany = "Drop files here",
            dropOne = "Drop file here",
        )

    @Test
    fun translatesUploadButtonAndDropAreaForCustomI18n(): Promise<Unit> {
        val i18n = jsObject(
            """{"addFiles":{"one":"Datei hochladen...","many":"Dateien hochladen..."},""" +
                """"dropFiles":{"one":"Dateien hier ablegen","many":"Dateien hier ablegen"}}""",
        )
        return assertTranslations(
            i18n,
            addMany = "Dateien hochladen...",
            addOne = "Datei hochladen...",
            dropMany = "Dateien hier ablegen",
            dropOne = "Dateien hier ablegen",
        )
    }

    @Test
    fun translatesUploadButtonAndDropAreaForPartialI18n(): Promise<Unit> {
        val i18n = jsObject(
            """{"addFiles":{"one":"PDF hochladen...","many":"PDFs hochladen..."},""" +
                """"dropFiles":{"one":"PDF hier ablegen","many":"PDFs hier ablegen"}}""",
        )
        return assertTranslations(
            i18n,
            addMany = "PDFs hochladen...",
            addOne = "PDF hochladen...",
            dropMany = "PDFs hier ablegen",
            dropOne = "PDF hier ablegen",
        )
    }

    @Test
    fun translatesFileButtonAriaLabelsForAQueuedFile(): Promise<Unit> {
        val upload = attach(createUpload())
        js("(u) => { u.i18n = { file: { start: 'Start', retry: 'Retry', remove: 'Remove' } }; }")(upload)
        upload.noAuto = true

        return nextRender(upload)
            .then {
                addFilesViaInput(upload, arrayOf(createFile(100)))
                nextFrame()
            }
            .then {
                val item = fileListItems(upload)[0]
                val startButton = shadowQuery(item, "[part=start-button]")!!
                assertEquals("Start", startButton.getAttribute("aria-label"))
                val removeButton = shadowQuery(item, "[part=remove-button]")!!
                assertEquals("Remove", removeButton.getAttribute("aria-label"))
            }
    }
}
