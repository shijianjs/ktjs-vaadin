package vaadin.listbox

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
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/list-box/test/list-box.test.js` and the public part of
 * `packages/list-box/test/multi-select-list-mixin.test.js`.
 *
 * The keyboard cases are skipped (they need `sendKeys`); selection, multiple mode, disabled
 * propagation and the attribute API are translated.
 */
class ListBoxTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerListBox()
        vaadin.item.registerItem()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newListBox(): ListBoxElement = attach(createListBox())

    private fun items(host: ListBoxElement): Array<HTMLElement> =
        js("(host) => Array.from(host.querySelectorAll('vaadin-item'))")(host) as Array<HTMLElement>

    private fun listBoxFixture(): ListBoxElement = attach(
        fixture(
            """
            <vaadin-list-box>
              <vaadin-item value="0">Zero</vaadin-item>
              <vaadin-item value="1">One</vaadin-item>
              <vaadin-item value="2">Two</vaadin-item>
            </vaadin-list-box>
            """.trimIndent(),
        ).unsafeCast<ListBoxElement>(),
    )

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerListBox()
        val host = newListBox()

        assertEquals("vaadin-list-box", host.tagName.lowercase())
        assertEquals("vaadin-list-box", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun selectsNoItemByDefault(): Promise<Unit> {
        val host = listBoxFixture()

        return nextRender(host).then {
            assertEquals(null, js("(host) => host.selected == null ? null : host.selected")(host))
        }
    }

    @Test
    fun selectsAnItemByIndex(): Promise<Unit> {
        val host = listBoxFixture()

        return nextRender(host)
            .then {
                js("(host) => { host.selected = 1; }")(host)
                nextUpdate(host)
            }
            .then {
                assertEquals(true, js("(item) => item.selected")(items(host)[1]) as Boolean)
                assertEquals(false, js("(item) => item.selected")(items(host)[0]) as Boolean)
            }
    }

    @Test
    fun firesSelectedChanged(): Promise<Unit> {
        val host = listBoxFixture()
        var fired = 0
        on(host, "selected-changed") { fired++ }

        return nextRender(host)
            .then {
                js("(host) => { host.selected = 2; }")(host)
                nextUpdate(host)
            }
            .then { assertTrue(fired > 0, "expected a selected-changed event, got $fired") }
    }

    @Test
    fun reflectsOrientation(): Promise<Unit> {
        val host = newListBox()

        return nextUpdate(host)
            .then {
                host.setAttribute("orientation", "horizontal")
                nextUpdate(host)
            }
            .then { assertEquals("horizontal", host.getAttribute("orientation")) }
    }

    @Test
    fun propagatesDisabledToTheItems(): Promise<Unit> {
        val host = listBoxFixture()

        return nextRender(host)
            .then {
                host.disabled = true
                nextUpdate(host)
            }
            .then {
                assertTrue(js("(host) => host.disabled")(host) as Boolean)
                assertTrue(host.hasAttribute("disabled"))
            }
    }

    @Test
    fun isNotMultipleByDefault(): Promise<Unit> {
        val host = newListBox()

        return nextRender(host).then {
            assertFalse(js("(host) => host.multiple")(host) as Boolean)
        }
    }

    @Test
    fun supportsMultipleSelection(): Promise<Unit> {
        val host = listBoxFixture()

        return nextRender(host)
            .then {
                js("(host) => { host.multiple = true; }")(host)
                js("(host) => { host.selectedValues = [0, 2]; }")(host)
                nextUpdate(host)
            }
            .then {
                assertEquals(true, js("(item) => item.selected")(items(host)[0]) as Boolean)
                assertEquals(true, js("(item) => item.selected")(items(host)[2]) as Boolean)
                assertEquals(false, js("(item) => item.selected")(items(host)[1]) as Boolean)
            }
    }

    @Test
    fun firesSelectedValuesChanged(): Promise<Unit> {
        val host = listBoxFixture()
        var fired = 0
        on(host, "selected-values-changed") { fired++ }

        return nextRender(host)
            .then {
                js("(host) => { host.multiple = true; host.selectedValues = [1]; }")(host)
                nextUpdate(host)
            }
            .then { assertTrue(fired > 0, "expected a selected-values-changed event, got $fired") }
    }

    // Kotlin-only coverage

    @Test
    fun createListBoxAppliesTheConfigureBlock() {
        val host = attach(createListBox { multiple = true })

        assertTrue(js("(host) => host.multiple")(host) as Boolean)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-list-box", document.createElement(vaadin.VaadinTagName.listBox).tagName.lowercase())
        assertEquals("multiple", ListBoxAttributes.multiple)
        assertEquals("selected", ListBoxAttributes.selected)
        assertEquals("selected-values", ListBoxAttributes.selectedValues)
        assertEquals("orientation", ListBoxAttributes.orientation)
        assertEquals("disabled", ListBoxAttributes.disabled)
    }

    @Test
    fun eventTypesAreExposed() {
        assertEquals(true, ListBoxEvents.selectedChanged != null)
        assertEquals(true, ListBoxEvents.selectedValuesChanged != null)
        assertEquals(true, ListBoxEvents.itemsChanged != null)
    }
}
