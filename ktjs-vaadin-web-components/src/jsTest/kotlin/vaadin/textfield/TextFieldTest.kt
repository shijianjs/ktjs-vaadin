package vaadin.textfield

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
import vaadin.click
import vaadin.commitInput
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import vaadin.typeInto
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/text-field/test/text-field.test.js` and the public part of
 * `packages/text-field/test/text-field-validation.test.js`.
 *
 * The `input` element lives in the light DOM (field pattern), so the typing helpers of
 * `TestSupport` write to `querySelector('input')` like the upstream `setInputValue` does.
 */
class TextFieldTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerTextField()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newTextField(): TextFieldElement = attach(createTextField())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerTextField()
        val field = newTextField()

        assertEquals("vaadin-text-field", field.tagName.lowercase())
        assertEquals("vaadin-text-field", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val field = newTextField()

        return nextRender(field).then { assertEquals("", field.value) }
    }

    @Test
    fun rendersTheSlottedInput(): Promise<Unit> {
        val field = newTextField()

        return nextRender(field).then {
            assertNotNull(js("(field) => field.querySelector('input')")(field) as HTMLElement?)
        }
    }

    // value

    @Test
    fun setsTheValueFromUserInput(): Promise<Unit> {
        val field = newTextField()

        return nextRender(field)
            .then {
                typeInto(field, "Hello")
                nextUpdate(field)
            }
            .then { assertEquals("Hello", field.value) }
    }

    @Test
    fun firesValueChangedOnUserInput(): Promise<Unit> {
        val field = newTextField()
        var fired = 0
        on(field, "value-changed") { fired++ }

        return nextRender(field)
            .then {
                typeInto(field, "Hello")
                nextUpdate(field)
            }
            .then { assertTrue(fired > 0, "expected a value-changed event, got $fired") }
    }

    @Test
    fun firesInputAndChange(): Promise<Unit> {
        val field = newTextField()
        var inputs = 0
        var changes = 0
        on(field, "input") { inputs++ }
        on(field, "change") { changes++ }

        return nextRender(field)
            .then {
                typeInto(field, "Hello")
                commitInput(field)
                nextUpdate(field)
            }
            .then {
                assertTrue(inputs > 0, "expected an input event, got $inputs")
                assertTrue(changes > 0, "expected a change event, got $changes")
            }
    }

    @Test
    fun clearsTheValueWithClear(): Promise<Unit> {
        val field = attach(createTextField { value = "Hello" })

        return nextRender(field).then {
            field.clear()
            assertEquals("", field.value)
        }
    }

    @Test
    fun valueRoundTripsThroughTheProperty(): Promise<Unit> {
        val field = newTextField()

        return nextUpdate(field)
            .then {
                field.value = "Hello"
                nextUpdate(field)
            }
            .then { assertEquals("Hello", field.value) }
    }

    // label and placeholder

    @Test
    fun appliesLabelAndPlaceholder(): Promise<Unit> {
        val field = newTextField()

        return nextUpdate(field)
            .then {
                field.label = "Name"
                field.placeholder = "Your name"
                nextUpdate(field)
            }
            .then {
                assertEquals("Name", field.label)
                assertEquals("Your name", field.placeholder)
                // The label is slotted into the `label` part instead of being reflected.
                val label = js("(field) => field.querySelector('[slot=label]').textContent")(field) as String
                assertEquals(true, label.contains("Name"), "expected the label slot to render the label")
            }
    }

    // clear button

    @Test
    fun hidesTheClearButtonByDefault(): Promise<Unit> {
        val field = attach(createTextField { value = "Hello" })

        return nextRender(field).then { assertFalse(field.clearButtonVisible) }
    }

    @Test
    fun showsTheClearButtonWhenEnabled(): Promise<Unit> {
        val field = attach(createTextField { value = "Hello"; clearButtonVisible = true })

        return nextRender(field).then {
            val button = js("(field) => field.shadowRoot.querySelector('[part~=clear-button]')")(field) as HTMLElement?
            assertNotNull(button)
        }
    }

    // readonly and disabled

    @Test
    fun reflectsReadonlyAndDisabled(): Promise<Unit> {
        val field = newTextField()

        return nextUpdate(field)
            .then {
                field.readonly = true
                field.disabled = true
                nextUpdate(field)
            }
            .then {
                assertTrue(field.hasAttribute("readonly"))
                assertTrue(field.hasAttribute("disabled"))
            }
    }

    // validation

    @Test
    fun isInvalidWhenRequiredAndEmpty(): Promise<Unit> {
        val field = attach(createTextField { required = true })

        return nextRender(field).then {
            assertFalse(field.checkValidity())
            field.validate()
            assertTrue(field.invalid)
        }
    }

    @Test
    fun becomesValidWhenFilled(): Promise<Unit> {
        val field = attach(createTextField { required = true })

        return nextRender(field)
            .then {
                typeInto(field, "Hello")
                nextUpdate(field)
            }
            .then { assertTrue(field.checkValidity()) }
    }

    @Test
    fun validatesThePattern(): Promise<Unit> {
        val field = attach(createTextField { pattern = "[0-9]+" })

        return nextRender(field)
            .then {
                typeInto(field, "abc")
                nextUpdate(field)
            }
            .then { assertFalse(field.checkValidity()) }
    }

    @Test
    fun forwardsTheLengthConstraintsToTheInput(): Promise<Unit> {
        val field = attach(createTextField { minlength = 3.0; maxlength = 8.0 })

        return nextRender(field).then {
            // `tooShort` / `tooLong` only apply to user-typed values, so the wrapper guarantees the
            // constraints reach the slotted `<input>`; the browser enforces them on real typing.
            val input = js("(field) => field.querySelector('input')")(field) as HTMLElement
            assertEquals("3", input.getAttribute("minlength"))
            assertEquals("8", input.getAttribute("maxlength"))
        }
    }

    @Test
    fun reportsTheErrorMessage(): Promise<Unit> {
        val field = attach(createTextField { required = true; errorMessage = "Required" })

        return nextRender(field).then {
            field.validate()
            assertEquals("Required", field.errorMessage)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createTextFieldAppliesTheConfigureBlock() {
        val field = attach(createTextField { value = "Hello" })

        assertEquals("Hello", field.value)
    }

    @Test
    fun markupFixtureIsUpgraded() {
        val field = attach(fixture("<vaadin-text-field value=\"Hello\"></vaadin-text-field>").unsafeCast<TextFieldElement>())

        assertEquals("Hello", field.value)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-text-field", document.createElement(vaadin.VaadinTagName.textField).tagName.lowercase())
        assertEquals("value", TextFieldAttributes.value)
        assertEquals("label", TextFieldAttributes.label)
        assertEquals("placeholder", TextFieldAttributes.placeholder)
        assertEquals("pattern", TextFieldAttributes.pattern)
        assertEquals("minlength", TextFieldAttributes.minlength)
        assertEquals("maxlength", TextFieldAttributes.maxlength)
        assertEquals("clear-button-visible", TextFieldAttributes.clearButtonVisible)
        assertEquals("theme", TextFieldAttributes.theme)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(TextFieldEvents.valueChanged)
        assertNotNull(TextFieldEvents.input)
        assertNotNull(TextFieldEvents.change)
        assertNotNull(TextFieldEvents.validated)
        assertNotNull(TextFieldEvents.invalidChanged)
    }

    @Test
    fun clickOnTheHostDoesNotChangeTheValue(): Promise<Unit> {
        val field = attach(createTextField { value = "Hello" })

        return nextRender(field).then {
            click(field)
            assertEquals("Hello", field.value)
        }
    }
}
