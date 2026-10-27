package vaadin.upload

import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
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
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/upload/test/properties.test.js`.
 *
 * The upstream `nodrop` default assertion compares against `isTouch` from
 * `@vaadin/component-base/src/browser-utils.js`; the same detection is evaluated here instead of
 * importing the module.
 */
class PropertiesTest {
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

    private fun isTouch(): Boolean =
        js("() => { try { document.createEvent('TouchEvent'); return true; } catch (_) { return false; } }")() as Boolean

    // defaults

    @Test
    fun disabledIsFalseByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertFalse(upload.disabled) }
    }

    @Test
    fun nodropIsBasedOnTouchDeviceSupportByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertEquals(isTouch(), upload.nodrop) }
    }

    @Test
    fun targetIsEmptyStringByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertEquals("", upload.target) }
    }

    @Test
    fun timeoutIsZeroByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertEquals(0.0, upload.timeout) }
    }

    @Test
    fun maxFilesIsInfinityByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertEquals(Double.POSITIVE_INFINITY, upload.maxFiles) }
    }

    @Test
    fun maxFilesReachedIsFalseByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertFalse(upload.maxFilesReached) }
    }

    @Test
    fun maxFilesReachedIsFalseBeforeTheElementIsAttached() {
        registerUpload()
        val element = document.createElement(vaadin.VaadinTagName.upload)

        assertFalse(element.maxFilesReached)
    }

    @Test
    fun acceptIsEmptyStringByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertEquals("", upload.accept) }
    }

    @Test
    fun maxFileSizeIsInfinityByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertEquals(Double.POSITIVE_INFINITY, upload.maxFileSize) }
    }

    @Test
    fun noAutoIsFalseByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertFalse(upload.noAuto) }
    }

    @Test
    fun withCredentialsIsFalseByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertFalse(upload.withCredentials) }
    }

    @Test
    fun uploadFormatIsRawByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertEquals("raw", upload.uploadFormat.unsafeCast<String>()) }
    }

    @Test
    fun isNotInDragoverStateByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then {
            assertFalse(upload.hasAttribute("dragover"))
            assertFalse(upload.hasAttribute("dragover-valid"))
        }
    }

    @Test
    fun doesNotHaveMaxFilesReachedAttributeByDefault(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload).then { assertFalse(upload.hasAttribute("max-files-reached")) }
    }

    // attributes

    private fun newFromAttributes(): UploadElement {
        val element = createUpload()
        element.setAttribute("disabled", "")
        element.setAttribute("nodrop", "")
        element.setAttribute("no-auto", "")
        element.setAttribute("with-credentials", "")
        element.setAttribute("timeout", "500")
        element.setAttribute("max-files", "5")
        element.setAttribute("max-file-size", "1000")
        element.setAttribute("max-concurrent-uploads", "2")
        element.setAttribute("headers", "{\"X-Foo\": \"Bar\"}")
        return attach(element)
    }

    @Test
    fun setsDisabledPropertyFromAttribute(): Promise<Unit> {
        val upload = newFromAttributes()

        return nextRender(upload).then { assertTrue(upload.disabled) }
    }

    @Test
    fun setsNodropPropertyFromAttribute(): Promise<Unit> {
        val upload = newFromAttributes()

        return nextRender(upload).then { assertTrue(upload.nodrop) }
    }

    @Test
    fun setsNoAutoPropertyFromAttribute(): Promise<Unit> {
        val upload = newFromAttributes()

        return nextRender(upload).then { assertTrue(upload.noAuto) }
    }

    @Test
    fun setsWithCredentialsPropertyFromAttribute(): Promise<Unit> {
        val upload = newFromAttributes()

        return nextRender(upload).then { assertTrue(upload.withCredentials) }
    }

    @Test
    fun setsTimeoutPropertyFromAttribute(): Promise<Unit> {
        val upload = newFromAttributes()

        return nextRender(upload).then { assertEquals(500.0, upload.timeout) }
    }

    @Test
    fun setsMaxFilesPropertyFromAttribute(): Promise<Unit> {
        val upload = newFromAttributes()

        return nextRender(upload).then { assertEquals(5.0, upload.maxFiles) }
    }

    @Test
    fun setsMaxFileSizePropertyFromAttribute(): Promise<Unit> {
        val upload = newFromAttributes()

        return nextRender(upload).then { assertEquals(1000.0, upload.maxFileSize) }
    }

    @Test
    fun setsMaxConcurrentUploadsPropertyFromAttribute(): Promise<Unit> {
        val upload = newFromAttributes()

        return nextRender(upload).then { assertEquals(2.0, upload.maxConcurrentUploads) }
    }

    @Test
    fun parsesHeadersPropertyFromJsonStringAttribute(): Promise<Unit> {
        val upload = newFromAttributes()

        return nextRender(upload).then { assertEquals("Bar", js("(u) => u.headers['X-Foo']")(upload) as String) }
    }

    // reflection

    @Test
    fun togglesDisabledAttributeOnDisabledPropertyChange(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.disabled = true
                nextUpdate(upload)
            }
            .then {
                assertTrue(upload.hasAttribute("disabled"))
                upload.disabled = false
                nextUpdate(upload)
            }
            .then { assertFalse(upload.hasAttribute("disabled")) }
    }

    @Test
    fun togglesNodropAttributeOnNodropPropertyChange(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.nodrop = true
                nextUpdate(upload)
            }
            .then {
                assertTrue(upload.hasAttribute("nodrop"))
                upload.nodrop = false
                nextUpdate(upload)
            }
            .then { assertFalse(upload.hasAttribute("nodrop")) }
    }

    @Test
    fun togglesMaxFilesReachedAttributeWhenFilesReachMaxFiles(): Promise<Unit> {
        val upload = newUpload()

        return nextRender(upload)
            .then {
                upload.maxFiles = 1.0
                upload.files = arrayOf(createTrackedFile(100, "image/jpeg"))
                nextUpdate(upload)
            }
            .then {
                assertTrue(upload.hasAttribute("max-files-reached"))
                upload.files = emptyArray()
                nextUpdate(upload)
            }
            .then { assertFalse(upload.hasAttribute("max-files-reached")) }
    }

    @Test
    fun notifiesMaxFilesReachedPropertyChanges(): Promise<Unit> {
        val upload = newUpload()
        val values = mutableListOf<Boolean>()

        return nextRender(upload)
            .then {
                on(upload, "max-files-reached-changed") { event ->
                    values += js("(e) => e.detail.value")(event) as Boolean
                }
                upload.maxFiles = 1.0
                upload.files = arrayOf(createTrackedFile(100, "image/jpeg"))
                nextUpdate(upload)
            }
            .then {
                assertEquals(1, values.size)
                assertTrue(values[0])
            }
    }
}
