package vaadin.upload

import kotlin.js.Promise
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import web.html.HTMLElement

/**
 * Translated from `packages/upload/test/concurrent-uploads.test.js`.
 *
 * Dropped upstream cases: the queue-management, abort, error and retry groups all drive uploads
 * through the private `_createXhr` fake request and sinon fake timers, and assert on the internal
 * queue state, so they are out of reach without touching internals. The public
 * `maxConcurrentUploads` property is covered here.
 */
class ConcurrentUploadsTest {
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

    @Test
    fun maxConcurrentUploadsHasDefaultValueOfThree(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertEquals(3.0, upload.maxConcurrentUploads) }
    }

    @Test
    fun maxConcurrentUploadsAcceptsCustomValue(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.maxConcurrentUploads = 5.0
                nextUpdate(upload)
            }
            .then { assertEquals(5.0, upload.maxConcurrentUploads) }
    }

    @Test
    fun maxConcurrentUploadsAcceptsInfinity(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.maxConcurrentUploads = Double.POSITIVE_INFINITY
                nextUpdate(upload)
            }
            .then { assertEquals(Double.POSITIVE_INFINITY, upload.maxConcurrentUploads) }
    }
}
