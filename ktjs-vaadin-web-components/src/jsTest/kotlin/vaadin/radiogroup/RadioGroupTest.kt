package vaadin.radiogroup

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
import vaadin.click
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/radio-group/test/radio-group.test.js` and
 * `packages/radio-group/test/radio-button.test.js`.
 *
 * The keyboard cases and the dev-mode warnings are not translated; the `value`, `name`, `disabled`
 * and `readonly` propagation cases are.
 */
class RadioGroupTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerRadioGroup()
        registerRadioButton()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newGroup(): RadioGroupElement = attach(createRadioGroup())

    private fun buttons(group: RadioGroupElement): Array<HTMLElement> =
        queryAll(group, "vaadin-radio-button")

    private fun groupFixture(): RadioGroupElement = attach(
        fixture(
            """
            <vaadin-radio-group>
              <vaadin-radio-button value="0" label="Zero"></vaadin-radio-button>
              <vaadin-radio-button value="1" label="One"></vaadin-radio-button>
              <vaadin-radio-button value="2" label="Two"></vaadin-radio-button>
            </vaadin-radio-group>
            """.trimIndent(),
        ).unsafeCast<RadioGroupElement>(),
    )

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerRadioGroup()
        val group = newGroup()

        assertEquals("vaadin-radio-group", group.tagName.lowercase())
        assertEquals("vaadin-radio-group", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun radioButtonIsDefinedInTheCustomElementRegistry() {
        val ctor = registerRadioButton()
        val button = attach(createRadioButton())

        assertEquals("vaadin-radio-button", button.tagName.lowercase())
        assertEquals("vaadin-radio-button", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun isEnabledAndWritableByDefault(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group).then {
            assertFalse(group.disabled)
            assertFalse(group.readonly)
        }
    }

    // value

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group).then { assertEquals("", group.value) }
    }

    @Test
    fun setsTheValueWhenAButtonIsChecked(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                click(buttons(group)[1])
                nextUpdate(group)
            }
            .then { assertEquals("1", group.value) }
    }

    @Test
    fun checksTheMatchingButtonWhenValueIsSet(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.value = "2"
                nextUpdate(group)
            }
            .then {
                assertEquals(true, js("(button) => button.checked")(buttons(group)[2]) as Boolean)
                assertEquals(false, js("(button) => button.checked")(buttons(group)[0]) as Boolean)
            }
    }

    @Test
    fun unchecksAllButtonsWhenValueIsCleared(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.value = "1"
                nextUpdate(group)
            }
            .then {
                group.value = ""
                nextUpdate(group)
            }
            .then {
                buttons(group).forEach { button ->
                    assertEquals(false, js("(button) => button.checked")(button) as Boolean)
                }
            }
    }

    @Test
    fun firesValueChangedWhenTheValueChanges(): Promise<Unit> {
        val group = groupFixture()
        var fired = 0
        on(group, "value-changed") { fired++ }

        return nextRender(group)
            .then {
                click(buttons(group)[0])
                nextUpdate(group)
            }
            .then { assertTrue(fired > 0, "expected a value-changed event, got $fired") }
    }

    // disabled, readonly and name propagation

    @Test
    fun propagatesDisabledToTheButtons(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.disabled = true
                nextUpdate(group)
            }
            .then {
                buttons(group).forEach { button ->
                    assertEquals(true, js("(button) => button.disabled")(button) as Boolean)
                }
            }
    }

    @Test
    fun propagatesTheNameToTheButtons(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.name = "choice"
                nextUpdate(group)
            }
            .then {
                buttons(group).forEach { button ->
                    assertEquals("choice", js("(button) => button.name")(button) as String)
                }
            }
    }

    @Test
    fun disablesUncheckedButtonsWhenReadonly(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.value = "1"
                nextUpdate(group)
            }
            .then {
                group.readonly = true
                nextUpdate(group)
            }
            .then {
                assertEquals(true, js("(button) => button.disabled")(buttons(group)[0]) as Boolean)
                assertEquals(false, js("(button) => button.disabled")(buttons(group)[1]) as Boolean)
            }
    }

    @Test
    fun togglesAriaInvalid(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.invalid = true
                nextUpdate(group)
            }
            .then { assertEquals("true", group.getAttribute("aria-invalid")) }
    }

    @Test
    fun isInvalidWhenRequiredAndEmpty(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.required = true
                nextUpdate(group)
            }
            .then {
                assertFalse(group.checkValidity())
                group.validate()
                assertTrue(group.invalid)
            }
    }

    // Kotlin-only coverage

    @Test
    fun createRadioGroupAndButtonApplyTheConfigureBlock() {
        val group = attach(createRadioGroup { label = "Choice" })
        val button = attach(createRadioButton { value = "0" })

        assertEquals("Choice", group.label)
        assertEquals("0", button.value)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-radio-group", document.createElement(vaadin.VaadinTagName.radioGroup).tagName.lowercase())
        assertEquals(
            "vaadin-radio-button",
            document.createElement(vaadin.VaadinTagName.radioButton).tagName.lowercase(),
        )
        assertEquals("value", RadioGroupAttributes.value)
        assertEquals("name", RadioGroupAttributes.name)
        assertEquals("readonly", RadioGroupAttributes.readonly)
        assertEquals("required", RadioGroupAttributes.required)
        assertEquals("value", RadioButtonAttributes.value)
        assertEquals("checked", RadioButtonAttributes.checked)
        assertEquals("label", RadioButtonAttributes.label)
        assertEquals("name", RadioButtonAttributes.name)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(RadioGroupEvents.valueChanged)
        assertNotNull(RadioGroupEvents.validated)
        assertNotNull(RadioButtonEvents.checkedChanged)
    }

    @Test
    fun validatesOnDemand(): Promise<Unit> {
        val group = groupFixture()
        var validated = 0
        on(group, "validated") { validated++ }

        return nextRender(group)
            .then {
                group.required = true
                nextUpdate(group)
            }
            .then {
                group.validate()
                assertTrue(validated > 0, "expected a validated event, got $validated")
            }
    }
}
