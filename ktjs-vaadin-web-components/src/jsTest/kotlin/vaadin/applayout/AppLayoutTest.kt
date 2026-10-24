package vaadin.applayout

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
import vaadin.computedStyle
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/app-layout/test/app-layout.test.js` and the public part of
 * `packages/app-layout/test/drawer-toggle.test.js`.
 *
 * The animation and focus-management cases of the overlay drawer are skipped: they depend on
 * `sendKeys` and on transition events. The section, drawer state and attribute cases are translated.
 */
class AppLayoutTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerAppLayout()
        registerDrawerToggle()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newAppLayout(): AppLayoutElement = attach(createAppLayout())

    private fun drawer(host: AppLayoutElement): HTMLElement =
        assertNotNull(js("(host) => host.shadowRoot.querySelector('[part=drawer]')")(host) as HTMLElement?)

    private fun toggle(host: AppLayoutElement): HTMLElement =
        assertNotNull(js("(host) => host.querySelector('[slot=drawer-toggle]')")(host) as HTMLElement?)

    private fun layoutFixture(): AppLayoutElement = attach(
        fixture(
            """
            <vaadin-app-layout>
              <vaadin-drawer-toggle slot="drawer-toggle"></vaadin-drawer-toggle>
              <h1 slot="navbar">Title</h1>
              <nav slot="drawer">Menu</nav>
              <main>Content</main>
            </vaadin-app-layout>
            """.trimIndent(),
        ).unsafeCast<AppLayoutElement>(),
    )

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerAppLayout()
        val host = newAppLayout()

        assertEquals("vaadin-app-layout", host.tagName.lowercase())
        assertEquals("vaadin-app-layout", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun drawerToggleIsDefinedInTheCustomElementRegistry() {
        val ctor = registerDrawerToggle()
        val element = attach(createDrawerToggle())

        assertEquals("vaadin-drawer-toggle", element.tagName.lowercase())
        assertEquals("vaadin-drawer-toggle", js("(ctor) => ctor.is")(ctor) as String)
    }

    // primary section

    @Test
    fun usesNavbarAsTheDefaultPrimarySection(): Promise<Unit> {
        val host = newAppLayout()

        return nextRender(host).then { assertEquals("navbar", host.primarySection) }
    }

    @Test
    fun reflectsThePrimarySection(): Promise<Unit> {
        val host = newAppLayout()

        return nextUpdate(host)
            .then {
                host.primarySection = "drawer"
                nextUpdate(host)
            }
            .then { assertEquals("drawer", host.getAttribute("primary-section")) }
    }

    @Test
    fun firesPrimarySectionChanged(): Promise<Unit> {
        val host = newAppLayout()
        var fired = 0
        on(host, "primary-section-changed") { fired++ }

        return nextRender(host)
            .then {
                host.primarySection = "drawer"
                nextUpdate(host)
            }
            .then { assertTrue(fired > 0, "expected a primary-section-changed event, got $fired") }
    }

    @Test
    fun fallsBackToNavbarForAnInvalidSection(): Promise<Unit> {
        val host = newAppLayout()

        return nextUpdate(host)
            .then {
                host.primarySection = "invalid"
                nextUpdate(host)
            }
            .then { assertEquals("navbar", host.primarySection) }
    }

    // drawer

    @Test
    fun drawerIsOpenedByDefaultWithoutOverlay(): Promise<Unit> {
        val host = layoutFixture()

        return nextRender(host).then {
            // Without overlay the drawer is part of the layout and stays visible.
            assertTrue(host.drawerOpened)
            assertFalse(host.overlay)
        }
    }

    @Test
    fun togglesTheDrawerOnToggleClick(): Promise<Unit> {
        val host = layoutFixture()

        return nextRender(host)
            .then {
                js("(button) => button.click()")(toggle(host))
                nextUpdate(host)
            }
            .then {
                assertFalse(host.drawerOpened)
                js("(button) => button.click()")(toggle(host))
                nextUpdate(host)
            }
            .then { assertTrue(host.drawerOpened) }
    }

    @Test
    fun reflectsDrawerOpenedToTheAttribute(): Promise<Unit> {
        val host = layoutFixture()

        return nextUpdate(host)
            .then {
                host.drawerOpened = true
                nextUpdate(host)
            }
            .then { assertTrue(host.hasAttribute("drawer-opened")) }
    }

    @Test
    fun firesDrawerOpenedChanged(): Promise<Unit> {
        val host = layoutFixture()
        var fired = 0
        on(host, "drawer-opened-changed") { fired++ }

        return nextRender(host)
            .then {
                // The drawer starts opened, so closing it is what changes the property.
                host.drawerOpened = false
                nextUpdate(host)
            }
            .then { assertTrue(fired > 0, "expected a drawer-opened-changed event, got $fired") }
    }

    @Test
    fun setsAriaExpandedOnTheToggle(): Promise<Unit> {
        val host = layoutFixture()

        return nextRender(host)
            .then {
                host.drawerOpened = true
                nextUpdate(host)
            }
            .then { assertEquals("true", toggle(host).getAttribute("aria-expanded")) }
    }

    @Test
    fun hidesTheDrawerVisibilityWhenClosed(): Promise<Unit> {
        val host = layoutFixture()

        return nextRender(host)
            .then {
                host.drawerOpened = true
                nextUpdate(host)
            }
            .then {
                assertEquals("visible", computedStyle(drawer(host), "visibility"))
                host.drawerOpened = false
                nextUpdate(host)
            }
            .then { assertEquals("hidden", computedStyle(drawer(host), "visibility")) }
    }

    // Kotlin-only coverage

    @Test
    fun createAppLayoutAppliesTheConfigureBlock() {
        val host = attach(createAppLayout { primarySection = "drawer" })

        assertEquals("drawer", host.primarySection)
    }

    @Test
    fun createDrawerToggleAppliesTheConfigureBlock() {
        val element = attach(createDrawerToggle { ariaLabelProperty = "Open menu" })

        assertEquals("Open menu", element.getAttribute("aria-label"))
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-app-layout", document.createElement(vaadin.VaadinTagName.appLayout).tagName.lowercase())
        assertEquals(
            "vaadin-drawer-toggle",
            document.createElement(vaadin.VaadinTagName.drawerToggle).tagName.lowercase(),
        )
        assertEquals("drawer-opened", AppLayoutAttributes.drawerOpened)
        assertEquals("primary-section", AppLayoutAttributes.primarySection)
        assertEquals("close-drawer-on", AppLayoutAttributes.closeDrawerOn)
        assertEquals("overlay", AppLayoutAttributes.overlay)
        assertEquals("aria-label", DrawerToggleAttributes.ariaLabel)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(AppLayoutEvents.drawerOpenedChanged)
    }

    @Test
    fun staticCloseOverlayDrawerEventIsExposed() {
        // The static helper only dispatches a window-level event, so calling it must be harmless.
        AppLayout.dispatchCloseOverlayDrawerEvent()
    }

    @Test
    fun closeDrawerOnDefaultsToNavigation(): Promise<Unit> {
        val host = newAppLayout()

        return nextRender(host).then {
            assertEquals("vaadin-router-location-changed", host.closeDrawerOn)
        }
    }
}
