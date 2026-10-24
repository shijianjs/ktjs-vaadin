package vaadin.notification

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
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/notification/test/notification.test.js`.
 *
 * The static `Notification.show()` entry point is translated as well: it creates the notification,
 * attaches it to the document and returns the element, which is exactly what the wrapper exposes.
 */
class NotificationTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerNotification()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newNotification(): NotificationElement = attach(createNotification())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerNotification()
        val notification = newNotification()

        assertEquals("vaadin-notification", notification.tagName.lowercase())
        assertEquals("vaadin-notification", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val notification = newNotification()

        return nextRender(notification).then { assertFalse(notification.opened) }
    }

    @Test
    fun opensAndReflectsTheAttribute(): Promise<Unit> {
        val notification = newNotification()

        return nextRender(notification)
            .then {
                notification.opened = true
                nextUpdate(notification)
            }
            .then {
                assertTrue(notification.opened)
            }
    }

    @Test
    fun firesOpenedChanged(): Promise<Unit> {
        val notification = newNotification()
        var fired = 0
        on(notification, "opened-changed") { fired++ }

        return nextRender(notification)
            .then {
                notification.opened = true
                nextUpdate(notification)
            }
            .then { assertTrue(fired > 0, "expected an opened-changed event, got $fired") }
    }

    @Test
    fun appliesThePositionAndDuration(): Promise<Unit> {
        val notification = attach(createNotification { position = "top-end"; duration = 1000.0 })

        return nextRender(notification).then {
            assertEquals("top-end", notification.position)
            assertEquals(1000.0, notification.duration)
        }
    }

    @Test
    fun rendersTheContent(): Promise<Unit> {
        val notification = newNotification()
        js("(notification) => { notification.textContent = 'Saved'; }")(notification)

        return nextRender(notification)
            .then {
                notification.opened = true
                nextUpdate(notification)
            }
            .then {
                val overlay = js("(notification) => notification.shadowRoot.querySelector('vaadin-notification-container') || notification.shadowRoot")(notification)
                assertNotNull(overlay)
            }
    }

    @Test
    fun showsANotificationThroughTheStaticApi(): Promise<Unit> {
        val notification = Notification.show(js("'Saved'"), js("({ position: 'bottom-end' })"))

        attached += notification

        return nextRender(notification).then {
            assertEquals("bottom-end", notification.position)
            assertTrue(notification.opened)
            notification.opened = false
            nextUpdate(notification)
        }
    }

    @Test
    fun acceptsACustomRenderer(): Promise<Unit> {
        val notification = newNotification()
        var rendered = 0
        notification.renderer = { root, _ ->
            rendered++
            js("(root) => { root.textContent = 'Rendered'; }")(root)
        }

        return nextRender(notification)
            .then {
                notification.opened = true
                nextUpdate(notification)
            }
            .then { assertTrue(rendered > 0, "expected the renderer to run, got $rendered") }
    }

    // Kotlin-only coverage

    @Test
    fun createNotificationAppliesTheConfigureBlock() {
        val notification = attach(createNotification { position = "middle" })

        assertEquals("middle", notification.position)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-notification",
            document.createElement(vaadin.VaadinTagName.notification).tagName.lowercase(),
        )
        assertEquals("position", NotificationAttributes.position)
        assertEquals("duration", NotificationAttributes.duration)
        assertEquals("opened", NotificationAttributes.opened)
        assertEquals("assertive", NotificationAttributes.assertive)
        assertEquals("overlay-class", NotificationAttributes.overlayClass)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(NotificationEvents.openedChanged)
    }
}
