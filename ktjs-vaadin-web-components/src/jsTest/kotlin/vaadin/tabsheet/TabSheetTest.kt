package vaadin.tabsheet

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
import vaadin.nextFrame
import vaadin.nextRender
import vaadin.nextResize
import vaadin.nextUpdate
import vaadin.on
import vaadin.timeout
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/tabsheet/test/tabsheet.test.js`.
 *
 * The upstream suite flushes the private `_observer` of the tabs element to make a mutation visible
 * synchronously; the Kotlin tests await the next frame instead (same observable behaviour, no access
 * to private state). The `loading` height cases depend on measured heights and are translated as-is
 * where the browser layout allows it.
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

    private fun tabSheetFixture(html: String = defaultMarkup): TabSheetElement =
        attach(fixture(html).unsafeCast<TabSheetElement>())

    private fun tabsOf(host: TabSheetElement): HTMLElement =
        assertNotNull(js("(host) => host.querySelector('vaadin-tabs')")(host) as HTMLElement?)

    private fun tabItems(host: TabSheetElement): Array<HTMLElement> =
        js("(host) => Array.from(host.querySelector('vaadin-tabs').children)")(host) as Array<HTMLElement>

    private fun panels(host: TabSheetElement): Array<HTMLElement> =
        js("(host) => Array.from(host.querySelectorAll('[tab]'))")(host) as Array<HTMLElement>

    private fun isHidden(element: HTMLElement): Boolean = js("(element) => element.hidden")(element) as Boolean

    private val defaultMarkup = """
        <vaadin-tabsheet>
          <div slot="prefix">Prefix</div>
          <div slot="suffix">Suffix</div>

          <vaadin-tabs slot="tabs">
            <vaadin-tab id="tab-1">Tab 1</vaadin-tab>
            <vaadin-tab id="tab-2">Tab 2</vaadin-tab>
            <vaadin-tab id="tab-3">Tab 3</vaadin-tab>
          </vaadin-tabs>

          <p tab="tab-1">Panel 1</p>
          <p tab="tab-2">Panel 2</p>
          <p tab="tab-3">Panel 3</p>
        </vaadin-tabsheet>
    """.trimIndent()

    // custom element definition

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerTabSheet()
        val host = newTabSheet()

        assertEquals("vaadin-tabsheet", host.tagName.lowercase())
        assertEquals("vaadin-tabsheet", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAValidStaticIsGetter() {
        val ctor = registerTabSheet()

        assertEquals("vaadin-tabsheet", js("(ctor) => ctor.is")(ctor) as String)
    }

    // items

    @Test
    fun setsItemsToTheArrayOfChildTabs(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host).then {
            val items = assertNotNull(host.items)
            assertEquals(3, items.size)
        }
    }

    @Test
    fun updatesItemsWhenTheTabsItemsChange(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                js("(host) => host.querySelector('vaadin-tabs').removeChild(host.querySelector('vaadin-tabs').children[2])")(
                    host,
                )
                nextFrame()
            }
            .then { assertEquals(2, assertNotNull(host.items).size) }
    }

    @Test
    fun doesNotUpdateItemsWhenADetachedTabsItemsChange(): Promise<Unit> {
        val host = tabSheetFixture()
        val tabs = tabsOf(host)

        return nextRender(host)
            .then {
                tabs.remove()
                nextFrame()
            }
            .then {
                js("(tabs) => tabs.removeChild(tabs.children[2])")(tabs)
                nextFrame()
            }
            .then {
                // Detaching the tabs empties `items` instead of following the detached children.
                assertTrue((host.items?.size ?: 0) != 2, "detached tabs must not update items")
            }
    }

    @Test
    fun emptiesItemsWhenTheTabsIsDetached(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                tabsOf(host).remove()
                nextFrame()
            }
            .then { assertEquals(0, assertNotNull(host.items).size) }
    }

    // selected

    @Test
    fun selectsTheFirstTabByDefault(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host).then {
            assertEquals(0.0, host.selected)
            assertTrue(js("(tab) => tab.selected")(tabItems(host)[0]) as Boolean)
        }
    }

    @Test
    fun updatesSelectedWhenAnotherTabIsClicked(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                js("(tab) => tab.click()")(tabItems(host)[1])
                nextFrame()
            }
            .then {
                assertTrue(js("(tab) => tab.selected")(tabItems(host)[1]) as Boolean)
                assertEquals(1.0, host.selected)
            }
    }

    @Test
    fun doesNotUpdateSelectedOnASelectionOnADetachedTabs(): Promise<Unit> {
        val host = tabSheetFixture()
        val tabs = tabsOf(host)

        return nextRender(host)
            .then {
                tabs.remove()
                nextFrame()
            }
            .then {
                js("(tabs) => { tabs.selected = 1; }")(tabs)
                js("(tabs) => tabs.children[1].click()")(tabs)
                nextFrame()
            }
            .then { assertTrue(host.selected != 1.0, "detached tabs must not change selected") }
    }

    @Test
    fun closesThePreviouslySelectedTab(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                js("(tab) => tab.click()")(tabItems(host)[1])
                nextFrame()
            }
            .then {
                assertTrue(js("(tab) => tab.selected")(tabItems(host)[1]) as Boolean)
                assertFalse(js("(tab) => tab.selected")(tabItems(host)[0]) as Boolean)
            }
    }

    @Test
    fun doesNotChangeSelectedWhenTheTabWasRemoved(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                val tabs = tabsOf(host)
                js("(tabs) => tabs.removeChild(tabs.children[1])")(tabs)
                nextFrame()
            }
            .then {
                // The removed tab is no longer part of the tab sheet.
                assertEquals(0.0, host.selected)
            }
    }

    @Test
    fun firesSelectedChangedWhenSelectedChanges(): Promise<Unit> {
        val host = tabSheetFixture()
        var fired = 0
        on(host, "selected-changed") { fired++ }

        return nextRender(host)
            .then {
                js("(tab) => tab.click()")(tabItems(host)[1])
                nextFrame()
            }
            .then { assertEquals(1, fired) }
    }

    // syncing properties

    @Test
    fun propagatesTheSelectedValueToTabs(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                assertEquals(host.selected, js("(tabs) => tabs.selected")(tabsOf(host)) as Double)
                host.selected = 1.0
                nextFrame()
            }
            .then {
                assertEquals(host.selected, js("(tabs) => tabs.selected")(tabsOf(host)) as Double)
            }
    }

    @Test
    fun doesNotPropagateTheValueToADetachedTabs(): Promise<Unit> {
        val host = tabSheetFixture()
        val tabs = tabsOf(host)

        return nextRender(host)
            .then {
                tabs.remove()
                nextFrame()
            }
            .then {
                host.selected = 1.0
                nextFrame()
            }
            .then { assertTrue(js("(tabs) => tabs.selected")(tabs) as Double != host.selected) }
    }

    // panels

    @Test
    fun onlyShowsTheFirstPanelByDefault(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host).then {
            val list = panels(host)
            assertFalse(isHidden(list[0]))
            assertTrue(isHidden(list[1]))
            assertTrue(isHidden(list[2]))
        }
    }

    @Test
    fun showsAnotherPanelOnTabChange(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                host.selected = 1.0
                nextFrame()
            }
            .then {
                val list = panels(host)
                assertTrue(isHidden(list[0]))
                assertFalse(isHidden(list[1]))
            }
    }

    @Test
    fun doesNotShowAPanelWhenNoPanelMatches(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                host.selected = 3.0
                nextFrame()
            }
            .then {
                val list = panels(host)
                assertTrue(isHidden(list[0]))
                assertTrue(isHidden(list[1]))
                assertTrue(isHidden(list[2]))
            }
    }

    @Test
    fun overridesTheDisplayStyleWhenAPanelIsHidden(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                host.selected = 1.0
                nextFrame()
            }
            .then {
                val panel = panels(host)[2]
                panel.style.display = "contents"
                assertEquals("none", computedStyle(panel, "display"))
            }
    }

    @Test
    fun bindsADynamicallyAddedTabAndPanel(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                val tab = document.createElement("vaadin-tab")
                tab.setAttribute("id", "tab-4")
                val panel = document.createElement("div")
                panel.setAttribute("tab", "tab-4")
                tabsOf(host).appendChild(tab)
                host.appendChild(panel)
                nextFrame().then { panel }
            }
            .then { panel ->
                assertEquals("tabpanel", panel.getAttribute("role"))
                assertTrue(isHidden(panel))
                assertEquals("tab-4", panel.getAttribute("aria-labelledby"))
                val panelId = assertNotNull(panel.getAttribute("id"))
                assertEquals(panelId, js("(tab) => tab.getAttribute('aria-controls')")(tabItems(host)[3]) as String)
            }
    }

    @Test
    fun doesNotOverwriteTheIdOfADynamicallyAddedPanel(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                val tab = document.createElement("vaadin-tab")
                tab.setAttribute("id", "tab-4")
                val panel = document.createElement("div")
                panel.setAttribute("tab", "tab-4")
                panel.setAttribute("id", "custom-id")
                tabsOf(host).appendChild(tab)
                host.appendChild(panel)
                nextFrame().then { panel }
            }
            .then { panel -> assertEquals("custom-id", panel.getAttribute("id")) }
    }

    @Test
    fun resetsTheHiddenStateWhenAPanelIsRemoved(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                val div = document.createElement("div")
                host.appendChild(div)
                nextFrame().then { div }
            }
            .then { div ->
                assertTrue(isHidden(div))
                div.setAttribute("slot", "prefix")
                nextFrame().then { div }
            }
            .then { div -> assertFalse(isHidden(div)) }
    }

    @Test
    fun doesNotRemoveAnExplicitHiddenState(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                val div = document.createElement("div")
                js("(div) => { div.hidden = true; }")(div)
                host.appendChild(div)
                nextFrame().then { div }
            }
            .then { div ->
                div.setAttribute("slot", "prefix")
                nextFrame().then { div }
            }
            .then { div -> assertTrue(isHidden(div)) }
    }

    // loading

    @Test
    fun isNotInLoadingStateInitially(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host).then { assertFalse(host.hasAttribute("loading")) }
    }

    @Test
    fun entersLoadingStateWhenOpeningATabWithoutAPanel(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                addTabWithoutPanel(host, "new-tab")
                nextFrame()
            }
            .then {
                js("(tab) => tab.click()")(tabItems(host).last())
                nextFrame()
            }
            .then {
                assertTrue(host.hasAttribute("loading"))
                assertEquals("", host.getAttribute("loading"))
            }
    }

    @Test
    fun exitsLoadingStateWhenTheMissingPanelIsAdded(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                addTabWithoutPanel(host, "new-tab")
                nextFrame()
            }
            .then {
                js("(tab) => tab.click()")(tabItems(host).last())
                val panel = document.createElement("div")
                panel.setAttribute("tab", "new-tab")
                host.appendChild(panel)
                nextFrame()
            }
            .then { assertFalse(host.hasAttribute("loading")) }
    }

    @Test
    fun preservesTheContentHeightWhileLoading(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                addTabWithoutPanel(host, "new-tab")
                nextFrame()
            }
            .then {
                val content = assertNotNull(vaadin.shadowQuery(host, "[part~=content]"))
                val height = js("(element) => element.offsetHeight")(content) as Int
                js("(tab) => tab.click()")(tabItems(host).last())
                nextFrame().then { content to height }
            }
            .then { (content, height) ->
                assertTrue(
                    (js("(element) => element.offsetHeight")(content) as Int) >= height,
                    "the content should keep its height while loading",
                )
            }
    }

    @Test
    fun releasesTheContentHeightWhenLeavingTheLoadingState(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                addTabWithoutPanel(host, "new-tab")
                nextFrame()
            }
            .then {
                js("(tab) => tab.click()")(tabItems(host).last())
                nextFrame()
            }
            .then {
                val content = assertNotNull(vaadin.shadowQuery(host, "[part~=content]"))
                assertTrue((js("(element) => element.style.minHeight")(content) as String).isNotEmpty())
                val panel = document.createElement("div")
                panel.setAttribute("tab", "new-tab")
                host.appendChild(panel)
                nextFrame().then { content }
            }
            .then { content -> assertEquals("", js("(element) => element.style.minHeight")(content) as String) }
    }

    @Test
    fun hasNoHeightInLoadingStateWithoutPanels(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                val height = js("(element) => element.offsetHeight")(host) as Int
                panels(host).forEach { it.remove() }
                nextFrame().then { height }
            }
            .then { height ->
                assertTrue(
                    (js("(element) => element.offsetHeight")(host) as Int) < height,
                    "an empty tab sheet should not keep the content height",
                )
            }
    }

    @Test
    fun hasNoHeightInLoadingStateWithTooManyVisiblePanels(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                val height = js("(element) => element.offsetHeight")(host) as Int
                panels(host).forEach { it.remove() }
                nextFrame().then { height }
            }
            .then { height ->
                val first = document.createElement("p")
                first.setAttribute("tab", "new-tab-1")
                val second = document.createElement("p")
                second.setAttribute("tab", "new-tab-2")
                host.appendChild(first)
                host.appendChild(second)
                nextFrame().then { height }
            }
            .then { height ->
                assertTrue((js("(element) => element.offsetHeight")(host) as Int) < height)
            }
    }

    // overflow attribute

    private fun overflowingTabSheet(): Promise<TabSheetElement> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                js("(host) => { host.style.maxHeight = (host.offsetHeight - 10) + 'px'; }")(host)
                nextResize(host)
            }
            .then { nextFrame() }
            .then { nextFrame() }
            .then { host }
    }

    @Test
    fun setsOverflowToBottomAtTheBeginning(): Promise<Unit> = overflowingTabSheet().then { host ->
        assertEquals("bottom", host.getAttribute("overflow"))
    }

    @Test
    fun setsOverflowToTopBottomInTheMiddle(): Promise<Unit> = overflowingTabSheet()
        .then { host ->
            js("(host) => { host.shadowRoot.querySelector('[part~=content]').scrollTop = 1; }")(host)
            nextFrame().then { host }
        }
        .then { host -> assertEquals("top bottom", host.getAttribute("overflow")) }

    @Test
    fun setsOverflowToTopAtTheEnd(): Promise<Unit> = overflowingTabSheet()
        .then { host ->
            js(
                "(host) => { const content = host.shadowRoot.querySelector('[part~=content]');" +
                    " content.scrollTop = content.scrollHeight - content.clientHeight; }",
            )(host)
            nextFrame().then { host }
        }
        .then { host -> assertEquals("top", host.getAttribute("overflow")) }

    // theme propagation

    @Test
    fun propagatesTheThemeToTheSlottedTabs(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                host.setAttribute("theme", "foo")
                nextFrame()
            }
            .then { assertEquals("foo", tabsOf(host).getAttribute("theme")) }
    }

    @Test
    fun removesTheThemeFromTheSlottedTabs(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                host.setAttribute("theme", "foo")
                nextFrame()
            }
            .then {
                host.removeAttribute("theme")
                nextFrame()
            }
            .then { assertFalse(tabsOf(host).hasAttribute("theme")) }
    }

    @Test
    fun propagatesTheThemeToNewlyAddedTabs(): Promise<Unit> {
        val host = tabSheetFixture()

        return nextRender(host)
            .then {
                host.setAttribute("theme", "foo")
                nextFrame()
            }
            .then {
                tabsOf(host).remove()
                val newTabs = document.createElement("vaadin-tabs")
                newTabs.setAttribute("slot", "tabs")
                host.appendChild(newTabs)
                nextFrame().then { newTabs }
            }
            .then { newTabs -> assertEquals("foo", newTabs.getAttribute("theme")) }
    }

    // lazy tabs

    private fun lazyTabSheet(): TabSheetElement {
        val host = tabSheetFixture(
            """
            <vaadin-tabsheet theme="foo">
              <div tab="tab-1">Panel 1</div>
              <div tab="tab-2">Panel 2</div>
              <div tab="tab-3">Panel 3</div>
            </vaadin-tabsheet>
            """.trimIndent(),
        )
        val tabs = document.createElement("vaadin-tabs")
        tabs.setAttribute("slot", "tabs")
        js(
            "(tabs) => { tabs.innerHTML = '<vaadin-tab id=\"tab-1\">Tab 1</vaadin-tab>" +
                "<vaadin-tab id=\"tab-2\">Tab 2</vaadin-tab><vaadin-tab id=\"tab-3\">Tab 3</vaadin-tab>'; }",
        )(tabs)
        host.appendChild(tabs)
        return host
    }

    @Test
    fun lazyTabsSetsItems(): Promise<Unit> {
        val host = lazyTabSheet()

        return nextFrame().then { assertEquals(3, assertNotNull(host.items).size) }
    }

    @Test
    fun lazyTabsPropagatesTheTheme(): Promise<Unit> {
        val host = lazyTabSheet()

        return nextFrame().then { assertEquals("foo", tabsOf(host).getAttribute("theme")) }
    }

    @Test
    fun lazyTabsShowsOnlyTheFirstPanel(): Promise<Unit> {
        val host = lazyTabSheet()

        return nextFrame().then {
            val list = panels(host)
            assertFalse(isHidden(list[0]))
            assertTrue(isHidden(list[1]))
            assertTrue(isHidden(list[2]))
        }
    }

    // full height content

    @Test
    fun doesNotOverflowWhenThePanelHasFullHeight(): Promise<Unit> {
        val host = tabSheetFixture(
            """
            <vaadin-tabsheet style="height: 300px;">
              <vaadin-tabs slot="tabs">
                <vaadin-tab id="tab-1">Tab 1</vaadin-tab>
              </vaadin-tabs>
              <div tab="tab-1" style="height: 100%;">Panel 1</div>
            </vaadin-tabsheet>
            """.trimIndent(),
        )

        return nextFrame().then {
            val fits = js(
                "(host) => { const panel = host.querySelector('[tab=\"tab-1\"]');" +
                    " return panel.getBoundingClientRect().bottom <= host.getBoundingClientRect().bottom + 1; }",
            )(host) as Boolean
            assertTrue(fits, "the panel should not exceed the tab sheet")
        }
    }

    // without nodes in the default slot

    @Test
    fun doesNotThrowWhenATabIdIsSetWithoutPanels(): Promise<Unit> {
        val host = tabSheetFixture(
            "<vaadin-tabsheet style=\"height: 300px;\"><vaadin-tabs slot=\"tabs\">" +
                "<vaadin-tab id=\"tab-1\">Tab 1</vaadin-tab></vaadin-tabs></vaadin-tabsheet>",
        )

        return timeout(0).then {
            js("(host) => { host.querySelector('vaadin-tab').id = 'tab-1'; }")(host)
            timeout(0)
        }
    }

    // tabs without id

    private fun tabSheetWithoutIds(): TabSheetElement = tabSheetFixture(
        """
        <vaadin-tabsheet>
          <vaadin-tabs slot="tabs">
            <vaadin-tab>Tab 1</vaadin-tab>
            <vaadin-tab>Tab 2</vaadin-tab>
          </vaadin-tabs>

          <div tab="tab-1">Panel 1</div>
          <div tab="tab-2">Panel 2</div>
        </vaadin-tabsheet>
        """.trimIndent(),
    )

    @Test
    fun isInLoadingStateUntilAnIdIsSet(): Promise<Unit> {
        val host = tabSheetWithoutIds()

        return nextFrame().then { assertTrue(host.hasAttribute("loading")) }
    }

    @Test
    fun leavesLoadingStateAfterSettingAnId(): Promise<Unit> {
        val host = tabSheetWithoutIds()

        return nextFrame()
            .then {
                js("(host) => { host.querySelector('vaadin-tab').id = 'tab-1'; }")(host)
                nextFrame()
            }
            .then { assertFalse(host.hasAttribute("loading")) }
    }

    @Test
    fun restoresLoadingStateAfterRemovingTheId(): Promise<Unit> {
        val host = tabSheetWithoutIds()

        return nextFrame()
            .then {
                js("(host) => { host.querySelector('vaadin-tab').id = 'tab-1'; }")(host)
                nextFrame()
            }
            .then {
                js("(host) => { host.querySelector('vaadin-tab').id = ''; }")(host)
                nextFrame()
            }
            .then { assertTrue(host.hasAttribute("loading")) }
    }

    @Test
    fun hidesAllPanelsUntilAnIdIsSet(): Promise<Unit> {
        val host = tabSheetWithoutIds()

        return nextFrame().then {
            val list = panels(host)
            assertTrue(isHidden(list[0]))
            assertTrue(isHidden(list[1]))
        }
    }

    @Test
    fun showsTheMatchingPanelAfterSettingTheIdOfTheSelectedTab(): Promise<Unit> {
        val host = tabSheetWithoutIds()

        return nextFrame()
            .then {
                js("(host) => { host.querySelector('vaadin-tab').id = 'tab-1'; }")(host)
                nextFrame()
            }
            .then {
                val list = panels(host)
                assertFalse(isHidden(list[0]))
                assertTrue(isHidden(list[1]))
            }
    }

    @Test
    fun doesNotShowThePanelAfterSettingTheIdOfANonSelectedTab(): Promise<Unit> {
        val host = tabSheetWithoutIds()

        return nextFrame()
            .then {
                js("(host) => { host.querySelectorAll('vaadin-tab')[1].id = 'tab-2'; }")(host)
                nextFrame()
            }
            .then {
                val list = panels(host)
                assertTrue(isHidden(list[0]))
                assertTrue(isHidden(list[1]))
            }
    }

    @Test
    fun doesNotShowThePanelAfterSettingTheIdOfADetachedTab(): Promise<Unit> {
        val host = tabSheetWithoutIds()

        return nextFrame()
            .then {
                // Keep a reference: after moving the tab out, a query would find another tab.
                val tab = js("(host) => host.querySelector('vaadin-tab')")(host) as HTMLElement
                js("(host, tab) => { host.parentElement.appendChild(tab); }")(host, tab)
                nextFrame().then { tab }
            }
            .then { tab ->
                js("(tab) => { tab.id = 'tab-1'; }")(tab)
                nextFrame()
            }
            .then {
                val list = panels(host)
                assertTrue(isHidden(list[0]))
                assertTrue(isHidden(list[1]))
            }
    }

    @Test
    fun linksTheTabWithThePanelRegardlessOfSelection(): Promise<Unit> {
        val host = tabSheetWithoutIds()

        return nextFrame()
            .then {
                js("(host) => { host.querySelectorAll('vaadin-tab')[0].id = 'tab-1'; }")(host)
                nextFrame()
            }
            .then {
                val list = panels(host)
                assertEquals(
                    list[0].getAttribute("id"),
                    js("(host) => host.querySelectorAll('vaadin-tab')[0].getAttribute('aria-controls')")(host) as String,
                )
                assertEquals("tab-1", list[0].getAttribute("aria-labelledby"))
                js("(host) => { host.querySelectorAll('vaadin-tab')[1].id = 'tab-2'; }")(host)
                nextFrame().then { list }
            }
            .then { list ->
                assertEquals(
                    list[1].getAttribute("id"),
                    js("(host) => host.querySelectorAll('vaadin-tab')[1].getAttribute('aria-controls')")(host) as String,
                )
                assertEquals("tab-2", list[1].getAttribute("aria-labelledby"))
            }
    }

    // helpers and Kotlin-only coverage

    private fun addTabWithoutPanel(host: TabSheetElement, id: String) {
        val tab = document.createElement("vaadin-tab")
        tab.setAttribute("id", id)
        tabsOf(host).appendChild(tab)
    }

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
}
