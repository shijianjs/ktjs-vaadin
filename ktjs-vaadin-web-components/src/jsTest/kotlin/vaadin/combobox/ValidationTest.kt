package vaadin.combobox

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
import web.html.HTMLElement

/**
 * Translated from the public parts of `packages/combo-box/test/validation.test.js`.
 *
 * Dropped upstream cases: the blur / outside-click / Enter ordering cases need real focus and key
 * sequences, and the "document losing focus" case stubs `document.hasFocus`. The validity defaults,
 * the required constraint and the `validated` event are translated.
 */
class ValidationTest {
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

    @Test
    fun passesValidationByDefault(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox).then {
            assertTrue(comboBox.checkValidity())
            assertTrue(comboBox.validate())
            assertFalse(comboBox.invalid)
        }
    }

    @Test
    fun failsValidationWhenRequiredAndEmpty(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.required = true
                nextUpdate(comboBox)
            }
            .then {
                assertFalse(comboBox.checkValidity())
                assertFalse(comboBox.validate())
                assertTrue(comboBox.invalid)
            }
    }

    @Test
    fun passesValidationWhenRequiredWithAValue(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.items = js("['foo', 'bar', 'baz']").unsafeCast<Array<Any>>()
                comboBox.required = true
                comboBox.value = "foo"
                nextUpdate(comboBox)
            }
            .then {
                assertTrue(comboBox.checkValidity())
                assertTrue(comboBox.validate())
                assertFalse(comboBox.invalid)
            }
    }

    @Test
    fun firesValidatedEventOnSuccess(): Promise<Unit> {
        val comboBox = newComboBox()
        val values = mutableListOf<Boolean>()

        return nextRender(comboBox)
            .then {
                on(comboBox, "validated") { event -> values += js("(e) => e.detail.valid")(event) as Boolean }
                comboBox.validate()
                assertEquals(1, values.size)
                assertEquals(true, values[0])
            }
    }

    @Test
    fun firesValidatedEventOnFailure(): Promise<Unit> {
        val comboBox = newComboBox()
        val values = mutableListOf<Boolean>()

        return nextRender(comboBox)
            .then {
                on(comboBox, "validated") { event -> values += js("(e) => e.detail.valid")(event) as Boolean }
                comboBox.required = true
                nextUpdate(comboBox)
            }
            .then {
                comboBox.validate()
                assertEquals(1, values.size)
                assertEquals(false, values[0])
            }
    }

    @Test
    fun doesNotValidateOnAttach(): Promise<Unit> {
        registerComboBox()
        val comboBox = createComboBox()
        var validated = 0
        on(comboBox, "validated") { validated++ }
        attach(comboBox)

        return nextRender(comboBox).then { assertEquals(0, validated) }
    }

    @Test
    fun doesNotValidateOnAttachWithAnInitialValue(): Promise<Unit> {
        registerComboBox()
        val comboBox = createComboBox()
        comboBox.allowCustomValue = true
        comboBox.value = "foo"
        var validated = 0
        on(comboBox, "validated") { validated++ }
        attach(comboBox)

        return nextRender(comboBox).then { assertEquals(0, validated) }
    }
}
