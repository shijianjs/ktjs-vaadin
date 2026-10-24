package vaadin.combobox

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
 * Translated from `packages/combo-box/test/combo-box.test.js` and the public part of
 * `packages/combo-box/test/combo-box-filtering.test.js`.
 *
 * The filtering and keyboard cases need real typing sequences; the value/items API, the open state,
 * the custom-value flag and the attribute contract are translated.
 */
class ComboBoxTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerComboBox()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newComboBox(): ComboBoxElement = attach(createComboBox())

    private fun items(): Array<Any> = js(
        "[{ label: 'Zero', value: '0' }, { label: 'One', value: '1' }]",
    ).unsafeCast<Array<Any>>()

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerComboBox()
        val comboBox = newComboBox()

        assertEquals("vaadin-combo-box", comboBox.tagName.lowercase())
        assertEquals("vaadin-combo-box", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox).then { assertEquals("", comboBox.value) }
    }

    @Test
    fun storesTheValue(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = items()

        return nextRender(comboBox)
            .then {
                comboBox.value = "1"
                nextUpdate(comboBox)
            }
            .then { assertEquals("1", comboBox.value) }
    }

    @Test
    fun firesValueChanged(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = items()
        var fired = 0
        on(comboBox, "value-changed") { fired++ }

        return nextRender(comboBox)
            .then {
                comboBox.value = "0"
                nextUpdate(comboBox)
            }
            .then { assertTrue(fired > 0, "expected a value-changed event, got $fired") }
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox).then { assertFalse(comboBox.opened) }
    }

    @Test
    fun opensThroughTheProperty(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = items()

        return nextRender(comboBox)
            .then {
                comboBox.opened = true
                nextUpdate(comboBox)
            }
            .then { assertTrue(comboBox.opened) }
    }

    @Test
    fun appliesTheCustomValueFlag(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextUpdate(comboBox)
            .then {
                comboBox.allowCustomValue = true
                nextUpdate(comboBox)
            }
            .then { assertTrue(comboBox.allowCustomValue) }
    }

    @Test
    fun appliesTheFilter(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextUpdate(comboBox)
            .then {
                comboBox.filter = "startsWith"
                nextUpdate(comboBox)
            }
            .then { assertEquals("startsWith", comboBox.filter) }
    }

    @Test
    fun reflectsDisabledAndReadonly(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextUpdate(comboBox)
            .then {
                comboBox.disabled = true
                comboBox.readonly = true
                nextUpdate(comboBox)
            }
            .then {
                assertTrue(comboBox.hasAttribute("disabled"))
                assertTrue(comboBox.hasAttribute("readonly"))
            }
    }

    @Test
    fun acceptsADataProvider(): Promise<Unit> {
        val comboBox = newComboBox()
        var calls = 0
        comboBox.dataProvider = { params, callback ->
            calls++
            js("(callback) => callback([{ label: 'Zero', value: '0' }], 1)")(callback)
        }

        return nextRender(comboBox)
            .then {
                comboBox.opened = true
                nextUpdate(comboBox)
            }
            .then { assertTrue(calls > 0, "expected the data provider to be called, got $calls") }
    }

    @Test
    fun isInvalidWhenRequiredAndEmpty(): Promise<Unit> {
        val comboBox = attach(createComboBox { required = true })

        return nextRender(comboBox).then {
            assertFalse(comboBox.checkValidity())
            comboBox.validate()
            assertTrue(comboBox.invalid)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createComboBoxAppliesTheConfigureBlock() {
        val comboBox = attach(createComboBox { label = "Choice" })

        assertEquals("Choice", comboBox.label)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-combo-box", document.createElement(vaadin.VaadinTagName.comboBox).tagName.lowercase())
        assertEquals("allow-custom-value", ComboBoxAttributes.allowCustomValue)
        assertEquals("auto-open-disabled", ComboBoxAttributes.autoOpenDisabled)
        assertEquals("opened", ComboBoxAttributes.opened)
        assertEquals("page-size", ComboBoxAttributes.pageSize)
        assertEquals("filter", ComboBoxAttributes.filter)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(ComboBoxEvents.valueChanged)
        assertNotNull(ComboBoxEvents.openedChanged)
        assertNotNull(ComboBoxEvents.customValueSet)
    }
}
