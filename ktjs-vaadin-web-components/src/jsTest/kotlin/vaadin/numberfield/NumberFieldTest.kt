package vaadin.numberfield

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
import vaadin.shadowQuery
import vaadin.typeInto
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/number-field/test/number-field.test.js` and the public part of
 * `packages/number-field/test/number-field-validation.test.js`.
 *
 * The step-button cases click the `increase-button` / `decrease-button` parts of the shadow root,
 * which is what the upstream suite does through the same parts.
 */
class NumberFieldTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerNumberField()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newField(): NumberFieldElement = attach(createNumberField())

    private fun clickPart(field: NumberFieldElement, part: String) {
        val button = assertNotNull(shadowQuery(field, "[part~=\"$part\"]"))
        js("(button) => button.click()")(button)
    }

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerNumberField()
        val field = newField()

        assertEquals("vaadin-number-field", field.tagName.lowercase())
        assertEquals("vaadin-number-field", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then { assertEquals("", field.value) }
    }

    @Test
    fun keepsTheValueAsAString(): Promise<Unit> {
        val field = newField()

        return nextRender(field)
            .then {
                typeInto(field, "12.5")
                nextUpdate(field)
            }
            .then { assertEquals("12.5", field.value) }
    }

    @Test
    fun appliesMinMaxAndStep(): Promise<Unit> {
        val field = attach(createNumberField { min = 0.0; max = 10.0; step = 2.0 })

        return nextRender(field).then {
            assertEquals(0.0, field.min)
            assertEquals(10.0, field.max)
            assertEquals(2.0, field.step)
        }
    }

    @Test
    fun increasesTheValueWithTheStepButton(): Promise<Unit> {
        val field = attach(createNumberField { value = "2"; step = 1.0; stepButtonsVisible = true })

        return nextRender(field)
            .then {
                clickPart(field, "increase-button")
                nextUpdate(field)
            }
            .then { assertEquals("3", field.value) }
    }

    @Test
    fun decreasesTheValueWithTheStepButton(): Promise<Unit> {
        val field = attach(createNumberField { value = "2"; step = 1.0; stepButtonsVisible = true })

        return nextRender(field)
            .then {
                clickPart(field, "decrease-button")
                nextUpdate(field)
            }
            .then { assertEquals("1", field.value) }
    }

    @Test
    fun hidesTheStepButtonsByDefault(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then { assertFalse(field.stepButtonsVisible) }
    }

    @Test
    fun rejectsANonNumericValue(): Promise<Unit> {
        val field = newField()

        return nextRender(field)
            .then {
                typeInto(field, "abc")
                js("(field) => field.querySelector('input').dispatchEvent(new FocusEvent('blur'))")(field)
                nextUpdate(field)
            }
            .then {
                // A number input rejects the text itself, so the field keeps an empty value. The
                // `unparsable-change` event of the upstream suite needs real typing (`badInput`),
                // which the synthetic events cannot produce.
                assertEquals("", field.value)
                assertTrue(field.checkValidity())
            }
    }

    @Test
    fun isInvalidWhenRequiredAndEmpty(): Promise<Unit> {
        val field = attach(createNumberField { required = true })

        return nextRender(field).then {
            assertFalse(field.checkValidity())
            field.validate()
            assertTrue(field.invalid)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createNumberFieldAppliesTheConfigureBlock() {
        val field = attach(createNumberField { value = "42" })

        assertEquals("42", field.value)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-number-field", document.createElement(vaadin.VaadinTagName.numberField).tagName.lowercase())
        assertEquals("min", NumberFieldAttributes.min)
        assertEquals("max", NumberFieldAttributes.max)
        assertEquals("step", NumberFieldAttributes.step)
        assertEquals("step-buttons-visible", NumberFieldAttributes.stepButtonsVisible)
        assertEquals("value", NumberFieldAttributes.value)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(NumberFieldEvents.valueChanged)
        assertNotNull(NumberFieldEvents.unparsableChange)
        assertNotNull(NumberFieldEvents.validated)
    }
}
