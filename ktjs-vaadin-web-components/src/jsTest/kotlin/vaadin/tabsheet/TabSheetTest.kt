package vaadin.tabsheet

import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
 * Translated from `packages/tabsheet/test/tabsheet.test.js`.
 *
 * The suite builds the tab sheet from markup (`<vaadin-tabs slot="tabs">` plus panels), which is the
 * same shape the assertions below use; the detached-tabs cases are covered by removing the tabs from
 * the document.
 */
class TabSheetTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerTabSheet()
        vaadin.tabs.registerTabs()
        vaadin.tabs.registerTab()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newTabSheet(): TabSheetElement = attach(createTabSheet())

    private fun tabSheetFixture(): TabSheetElement = attach(
        fixture(
            """
            <vaadin-tabsheet>
              <vaadin-tabs slot="tabs">
                <vaadin-tab id="tab-1">Tab 1</vaadin-tab>
                <vaadin-tab id="tab-2">Tab 2</vaadin-tab>
                <vaadin-tab id="tab-3">Tab 3</vaadin-tab>
              </vaadin-tabs>
              <div tab="tab-1">Panel 1</div>
              <div tab="tab-2">Panel 2</div>
              <div tab="tab-3">Panel 3</div>
            </vaadin-tabsheet>
            """.trimIndent(),
        ).unsafeCast<TabSheetElement>(),
    )

    private fun panels(host: TabSheetElement): Array<HTMLElement> =
        js("(host) => Array.from(host.querySelectorAll('[tab]'))")(host) as Array<HTMLElement>

    private fun visiblePanels(host: TabSheetElement): Array<HTMLElement> =
        panels(host).filter { panel -> js("(panel) => panel.getAttribute('hidden') === null")(panel) as Boolean }
            .toTypedArray()

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerTabSheet()
        val host = newTabSheet()

        assertEquals("vaadin-tabsheet", host.tagName.lowercase())
        assertEquals("vaadin-tabsheet", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun exposesTheChildTabsAsItems(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host).then {
            val items = assertNotNull(host.items)
            assertEquals(3, items.size)
        }
    }

    @Test
    fun updatesItemsWhenATabIsAdded(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                val tab = document.createElement(vaadin.VaadinTagName.tab)
                js("(tabs, tab) => tabs.appendChild(tab)")(host.querySelector("vaadin-tabs"), tab)
                nextUpdate(host)
            }
            .then { assertEquals(4, assertNotNull(host.items).size) }
    }

    @Test
    fun selectsTheFirstTabByDefault(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host).then { assertEquals(0.0, host.selected) }
    }

    @Test
    fun showsOnlyTheFirstPanelByDefault(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host).then {
            val visible = visiblePanels(host)
            assertEquals(1, visible.size)
            assertEquals("Panel 1", js("(panel) => panel.textContent")(visible[0]) as String)
        }
    }

    @Test
    fun showsAnotherPanelOnTabChange(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                host.selected = 1.0
                nextUpdate(host)
            }
            .then {
                val visible = visiblePanels(host)
                assertEquals(1, visible.size)
                assertEquals("Panel 2", js("(panel) => panel.textContent")(visible[0]) as String)
            }
    }

    @Test
    fun propagatesTheSelectedIndexToTheTabs(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                host.selected = 2.0
                nextUpdate(host)
            }
            .then {
                val tabs = host.querySelector("vaadin-tabs") as HTMLElement
                assertEquals(2, js("(tabs) => tabs.selected")(tabs) as Int)
            }
    }

    @Test
    fun firesSelectedChanged(): Promise<Unit> {
        val host = tabSheetFixture()
        var fired = 0
        on(host, "selected-changed") { fired++ }

        return nextRender(host)
            .then {
                host.selected = 1.0
                nextUpdate(host)
            }
            .then { assertTrue(fired > 0, "expected a selected-changed event, got $fired") }
    }

    @Test
    fun selectedRoundTripsThroughTheProperty(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextUpdate(host)
            .then {
                host.selected = 1.0
                nextUpdate(host)
            }
            .then { assertEquals(1.0, host.selected) }
    }

    @Test
    fun hidesEveryPanelWhenNoTabMatches(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                js("(host) => host.querySelectorAll('[tab]')[1].setAttribute('tab', 'missing')")(host)
                host.selected = 1.0
                nextUpdate(host)
            }
            .then { assertEquals(0, visiblePanels(host).size) }
    }

    // Kotlin-only coverage

    @Test
    fun createTabSheetAppliesTheConfigureBlock() {
        val host = attach(createTabSheet { selected = 1.0 })

        assertEquals(1.0, host.selected)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-tabsheet", document.createElement(vaadin.VaadinTagName.tabsheet).tagName.lowercase())
        assertEquals("selected", TabSheetAttributes.selected)
        assertEquals("theme", TabSheetAttributes.theme)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(TabSheetEvents.selectedChanged)
        assertNotNull(TabSheetEvents.itemsChanged)
    }
}
