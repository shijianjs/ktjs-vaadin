package vaadin.integerfield

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
import vaadin.typeInto
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/integer-field/test/integer-field.test.js` and the public part of
 * `packages/integer-field/test/integer-field-validation.test.js`.
 *
 * `IntegerFieldElement` extends `NumberFieldElement`; the suite adds the integer-specific parsing
 * and the `step` default of 1.
 */
class IntegerFieldTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerIntegerField()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newField(): IntegerFieldElement = attach(createIntegerField())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerIntegerField()
        val field = newField()

        assertEquals("vaadin-integer-field", field.tagName.lowercase())
        assertEquals("vaadin-integer-field", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun stepsByOne(): Promise<Unit> {
        val field = attach(createIntegerField { value = "2"; stepButtonsVisible = true })

        return nextRender(field)
            .then {
                val button = js("(field) => field.shadowRoot.querySelector('[part~=\"increase-button\"]')")(
                    field,
                ) as HTMLElement
                js("(button) => button.click()")(button)
                nextUpdate(field)
            }
            .then { assertEquals("3", field.value) }
    }

    @Test
    fun acceptsAnInteger(): Promise<Unit> {
        val field = newField()

        return nextRender(field)
            .then {
                typeInto(field, "42")
                nextUpdate(field)
            }
            .then {
                assertEquals("42", field.value)
                assertTrue(field.checkValidity())
            }
    }

    @Test
    fun rejectsADecimalValue(): Promise<Unit> {
        val field = newField()

        return nextRender(field)
            .then {
                typeInto(field, "4.2")
                nextUpdate(field)
            }
            .then { assertEquals("", field.value, "the decimal input is rejected") }
    }

    @Test
    fun rejectsAValueBelowMin(): Promise<Unit> {
        val field = attach(createIntegerField { min = 10.0 })

        return nextRender(field)
            .then {
                typeInto(field, "5")
                nextUpdate(field)
            }
            .then { assertFalse(field.checkValidity()) }
    }

    @Test
    fun rejectsAValueAboveMax(): Promise<Unit> {
        val field = attach(createIntegerField { max = 10.0 })

        return nextRender(field)
            .then {
                typeInto(field, "15")
                nextUpdate(field)
            }
            .then { assertFalse(field.checkValidity()) }
    }

    @Test
    fun isInvalidWhenRequiredAndEmpty(): Promise<Unit> {
        val field = attach(createIntegerField { required = true })

        return nextRender(field).then {
            assertFalse(field.checkValidity())
            field.validate()
            assertTrue(field.invalid)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createIntegerFieldAppliesTheConfigureBlock() {
        val field = attach(createIntegerField { value = "7" })

        assertEquals("7", field.value)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-integer-field",
            document.createElement(vaadin.VaadinTagName.integerField).tagName.lowercase(),
        )
        assertEquals("step", IntegerFieldAttributes.step)
        assertEquals("min", IntegerFieldAttributes.min)
        assertEquals("max", IntegerFieldAttributes.max)
        assertEquals("value", IntegerFieldAttributes.value)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(IntegerFieldEvents.valueChanged)
        assertNotNull(IntegerFieldEvents.unparsableChange)
    }
}
