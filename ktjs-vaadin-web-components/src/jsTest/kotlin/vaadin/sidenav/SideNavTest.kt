package vaadin.sidenav

import vaadin.shadowQuery
import vaadin.queryAll
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
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/side-nav/test/side-nav.test.js` and the public part of
 * `packages/side-nav/test/side-nav-item.test.js`.
 *
 * Navigation is not triggered here: `onNavigate` and `location` depend on the router integration,
 * which the upstream suite covers with a stub. The collapse, expansion and attribute cases are
 * translated.
 */
class SideNavTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerSideNav()
        registerSideNavItem()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newSideNav(): SideNavElement = attach(createSideNav())

    private fun toggleButton(host: SideNavElement): HTMLElement =
        assertNotNull(shadowQuery(host, "[part~=toggle-button]"))

    private fun navFixture(): SideNavElement = attach(
        fixture(
            """
            <vaadin-side-nav collapsible>
              <vaadin-side-nav-item path="/inbox">Inbox</vaadin-side-nav-item>
              <vaadin-side-nav-item path="/sent" expanded>
                Sent
                <vaadin-side-nav-item slot="children" path="/sent/all">All</vaadin-side-nav-item>
              </vaadin-side-nav-item>
            </vaadin-side-nav>
            """.trimIndent(),
        ).unsafeCast<SideNavElement>(),
    )

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerSideNav()
        val host = newSideNav()

        assertEquals("vaadin-side-nav", host.tagName.lowercase())
        assertEquals("vaadin-side-nav", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun sideNavItemIsDefinedInTheCustomElementRegistry() {
        val ctor = registerSideNavItem()
        val item = attach(createSideNavItem())

        assertEquals("vaadin-side-nav-item", item.tagName.lowercase())
        assertEquals("vaadin-side-nav-item", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun isNotCollapsibleByDefault(): Promise<Unit> {
        val host = newSideNav()

        return nextRender(host).then {
            assertFalse(host.collapsible)
            assertFalse(host.collapsed)
        }
    }

    @Test
    fun collapsesFromTheToggleButton(): Promise<Unit> {
        val host = navFixture()

        return nextRender(host)
            .then {
                js("(button) => button.click()")(toggleButton(host))
                nextUpdate(host)
            }
            .then { assertTrue(host.collapsed) }
    }

    @Test
    fun expandsFromTheToggleButtonAgain(): Promise<Unit> {
        val host = navFixture()

        return nextRender(host)
            .then {
                js("(button) => button.click()")(toggleButton(host))
                nextUpdate(host)
            }
            .then {
                js("(button) => button.click()")(toggleButton(host))
                nextUpdate(host)
            }
            .then { assertFalse(host.collapsed) }
    }

    @Test
    fun reflectsCollapsedToTheAttribute(): Promise<Unit> {
        val host = newSideNav()

        return nextUpdate(host)
            .then {
                host.collapsed = true
                nextUpdate(host)
            }
            .then { assertTrue(host.hasAttribute("collapsed")) }
    }

    @Test
    fun firesCollapsedChanged(): Promise<Unit> {
        val host = newSideNav()
        var fired = 0
        on(host, "collapsed-changed") { fired++ }

        return nextRender(host)
            .then {
                host.collapsed = true
                nextUpdate(host)
            }
            .then { assertTrue(fired > 0, "expected a collapsed-changed event, got $fired") }
    }

    @Test
    fun rendersTheItems(): Promise<Unit> {
        val host = navFixture()

        return nextRender(host).then {
            val items = queryAll(host, "vaadin-side-nav-item")
            assertEquals(3, items.size)
        }
    }

    @Test
    fun reflectsExpandedOnAnItem(): Promise<Unit> {
        val item = attach(createSideNavItem())

        return nextUpdate(item)
            .then {
                item.expanded = true
                nextUpdate(item)
            }
            .then { assertTrue(item.hasAttribute("expanded")) }
    }

    @Test
    fun exposesThePathOfAnItem(): Promise<Unit> {
        val item = attach(createSideNavItem { path = "/inbox" })

        return nextRender(item).then { assertEquals("/inbox", item.path) }
    }

    @Test
    fun itemIsNotCurrentWithoutARouterMatch(): Promise<Unit> {
        val item = attach(createSideNavItem { path = "/inbox" })

        return nextRender(item).then {
            // `current` is read-only: it is set when the item matches the router location.
            assertFalse(item.current)
        }
    }

    @Test
    fun exposesPathAliasesAndRouterIgnore(): Promise<Unit> {
        val item = attach(createSideNavItem { path = "/inbox" })

        return nextRender(item).then {
            item.pathAliases = js("['/inbox/1', '/inbox/2']")
            item.routerIgnore = true
            assertEquals(2, item.pathAliases.size)
            assertTrue(item.routerIgnore)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createSideNavAppliesTheConfigureBlock() {
        val host = attach(createSideNav { collapsible = true; collapsed = true })

        assertTrue(host.collapsible)
        assertTrue(host.collapsed)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-side-nav", document.createElement(vaadin.VaadinTagName.sideNav).tagName.lowercase())
        assertEquals(
            "vaadin-side-nav-item",
            document.createElement(vaadin.VaadinTagName.sideNavItem).tagName.lowercase(),
        )
        assertEquals("collapsed", SideNavAttributes.collapsed)
        assertEquals("collapsible", SideNavAttributes.collapsible)
        assertEquals("no-auto-expand", SideNavAttributes.noAutoExpand)
        assertEquals("location", SideNavAttributes.location)
        assertEquals("path", SideNavItemAttributes.path)
        assertEquals("expanded", SideNavItemAttributes.expanded)
        assertEquals("current", SideNavItemAttributes.current)
        assertEquals("match-nested", SideNavItemAttributes.matchNested)
        assertEquals("router-ignore", SideNavItemAttributes.routerIgnore)
        assertEquals("target", SideNavItemAttributes.target)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(SideNavEvents.collapsedChanged)
    }

    @Test
    fun onNavigateCallbackIsAccepted() {
        val host = attach(createSideNav())

        host.onNavigate = { true }

        assertNotNull(host.onNavigate)
    }
}
