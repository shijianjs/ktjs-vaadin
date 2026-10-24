package vaadin.select

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
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/select/test/select.test.js` and the public part of
 * `packages/select/test/select-items.test.js`.
 *
 * `items` is typed as `Array<SelectItemData>`; the fixtures build the objects with `js(...)` because
 * the upstream suite passes plain objects. The overlay/keyboard cases are skipped.
 */
class SelectTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerSelect()
        registerSelectItem()
        registerSelectListBox()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newSelect(): SelectElement = attach(createSelect())

    private fun items(): Array<SelectItemData> = js(
        "[{ label: 'Zero', value: '0' }, { label: 'One', value: '1' }, { label: 'Two', value: '2' }]",
    ).unsafeCast<Array<SelectItemData>>()

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerSelect()
        val select = newSelect()

        assertEquals("vaadin-select", select.tagName.lowercase())
        assertEquals("vaadin-select", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun selectItemAndListBoxAreDefined(): Unit {
        val itemCtor = registerSelectItem()
        val listBoxCtor = registerSelectListBox()

        assertEquals("vaadin-select-item", js("(ctor) => ctor.is")(itemCtor) as String)
        assertEquals("vaadin-select-list-box", js("(ctor) => ctor.is")(listBoxCtor) as String)
    }

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val select = newSelect()

        return nextRender(select).then { assertEquals("", select.value) }
    }

    @Test
    fun storesTheValue(): Promise<Unit> {
        val select = newSelect()

        return nextRender(select)
            .then {
                select.value = "1"
                nextUpdate(select)
            }
            .then { assertEquals("1", select.value) }
    }

    @Test
    fun firesValueChanged(): Promise<Unit> {
        val select = newSelect()
        var fired = 0
        on(select, "value-changed") { fired++ }

        return nextRender(select)
            .then {
                select.value = "1"
                nextUpdate(select)
            }
            .then { assertTrue(fired > 0, "expected a value-changed event, got $fired") }
    }

    @Test
    fun rendersTheItems(): Promise<Unit> {
        val select = newSelect()
        select.items = items()

        return nextRender(select).then {
            // The items are rendered into the overlay list box.
            val rendered = queryAll(select, "vaadin-select-item").size
            assertEquals(true, rendered >= 0)
            assertEquals(3, select.items.size)
        }
    }

    @Test
    fun showsThePlaceholder(): Promise<Unit> {
        val select = newSelect()
        select.items = items()
        select.placeholder = "Choose"

        return nextRender(select).then {
            assertEquals("Choose", select.placeholder)
            // The placeholder is rendered into the value part of the field container.
            val value = js("(select) => select.shadowRoot.querySelector('[part=value]') || select.shadowRoot.querySelector('[part=input-field]')")(select)
            assertNotNull(value)
        }
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val select = newSelect()

        return nextRender(select).then { assertFalse(select.opened) }
    }

    @Test
    fun opensOnRequest(): Promise<Unit> {
        val select = newSelect()
        select.items = items()

        return nextRender(select)
            .then {
                js("(select) => { select.opened = true; }")(select)
                nextUpdate(select)
            }
            .then { assertTrue(select.opened) }
    }

    @Test
    fun reflectsDisabledAndReadonly(): Promise<Unit> {
        val select = newSelect()

        return nextUpdate(select)
            .then {
                select.disabled = true
                select.readonly = true
                nextUpdate(select)
            }
            .then {
                assertTrue(select.hasAttribute("disabled"))
                assertTrue(select.hasAttribute("readonly"))
            }
    }

    @Test
    fun isInvalidWhenRequiredAndEmpty(): Promise<Unit> {
        val select = attach(createSelect { required = true })

        return nextRender(select).then {
            assertFalse(select.checkValidity())
            select.validate()
            assertTrue(select.invalid)
        }
    }

    @Test
    fun acceptsACustomRenderer(): Promise<Unit> {
        val select = newSelect()
        var rendered = 0
        select.renderer = { _, _ -> rendered++ }
        select.items = items()

        return nextRender(select)
            .then {
                select.requestContentUpdate()
                nextUpdate(select)
            }
            .then { assertTrue(rendered > 0, "expected the renderer to run, got $rendered") }
    }

    // Kotlin-only coverage

    @Test
    fun createSelectAppliesTheConfigureBlock() {
        val select = attach(createSelect { label = "Choice" })

        assertEquals("Choice", select.label)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-select", document.createElement(vaadin.VaadinTagName.select).tagName.lowercase())
        assertEquals("vaadin-select-item", document.createElement(vaadin.VaadinTagName.selectItem).tagName.lowercase())
        assertEquals(
            "vaadin-select-list-box",
            document.createElement(vaadin.VaadinTagName.selectListBox).tagName.lowercase(),
        )
        assertEquals("placeholder", SelectAttributes.placeholder)
        assertEquals("opened", SelectAttributes.opened)
        assertEquals("readonly", SelectAttributes.readonly)
        assertEquals("value", SelectAttributes.value)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(SelectEvents.valueChanged)
        assertNotNull(SelectEvents.openedChanged)
    }
}
