package vaadin.checkboxgroup

import vaadin.queryAll
import kotlin.js.JsAny
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
import vaadin.click
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/checkbox-group/test/checkbox-group.test.js`.
 *
 * The keyboard cases (`Tab` / `Shift+Tab` through `sendKeys`) and the dev-mode warning cases are not
 * translated. `value` is typed as `Array<JsAny?>` because the manifest declares the array without an
 * item type; the tests build the array with `js(...)` the same way the upstream suite writes a plain
 * JS array.
 */
class CheckboxGroupTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerCheckboxGroup()
        vaadin.checkbox.registerCheckbox()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newGroup(): CheckboxGroupElement = attach(createCheckboxGroup())

    private fun checkboxes(group: CheckboxGroupElement): Array<HTMLElement> =
        queryAll(group, "vaadin-checkbox")

    private fun values(vararg value: String): Array<JsAny?> = js("(value) => value")(value.toList().toTypedArray())

    private fun groupFixture(): CheckboxGroupElement = attach(
        fixture(
            """
            <vaadin-checkbox-group>
              <vaadin-checkbox value="0" label="Zero"></vaadin-checkbox>
              <vaadin-checkbox value="1" label="One"></vaadin-checkbox>
              <vaadin-checkbox value="2" label="Two"></vaadin-checkbox>
            </vaadin-checkbox-group>
            """.trimIndent(),
        ).unsafeCast<CheckboxGroupElement>(),
    )

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerCheckboxGroup()
        val group = newGroup()

        assertEquals("vaadin-checkbox-group", group.tagName.lowercase())
        assertEquals("vaadin-checkbox-group", js("(ctor) => ctor.is")(ctor) as String)
    }

    // disabled and readonly

    @Test
    fun propagatesDisabledToTheCheckboxes(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.disabled = true
                nextUpdate(group)
            }
            .then {
                checkboxes(group).forEach { checkbox ->
                    assertEquals(true, js("(checkbox) => checkbox.disabled")(checkbox) as Boolean)
                }
            }
    }

    @Test
    fun propagatesReadonlyToTheCheckboxes(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.readonly = true
                nextUpdate(group)
            }
            .then {
                checkboxes(group).forEach { checkbox ->
                    assertEquals(true, js("(checkbox) => checkbox.readonly")(checkbox) as Boolean)
                }
            }
    }

    // value

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group).then { assertEquals(0, group.value.size) }
    }

    @Test
    fun collectsTheValueOfCheckedCheckboxes(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                click(checkboxes(group)[1])
                nextUpdate(group)
            }
            .then {
                assertEquals(1, group.value.size)
                assertEquals("1", group.value[0].toString())
            }
    }

    @Test
    fun checksTheMatchingCheckboxWhenValueIsSet(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.value = values("2")
                nextUpdate(group)
            }
            .then {
                assertEquals(true, js("(checkbox) => checkbox.checked")(checkboxes(group)[2]) as Boolean)
                assertEquals(false, js("(checkbox) => checkbox.checked")(checkboxes(group)[0]) as Boolean)
            }
    }

    @Test
    fun unchecksTheCheckboxWhenItsValueIsRemoved(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                group.value = values("1")
                nextUpdate(group)
            }
            .then {
                group.value = values()
                nextUpdate(group)
            }
            .then { assertEquals(false, js("(checkbox) => checkbox.checked")(checkboxes(group)[1]) as Boolean) }
    }

    @Test
    fun firesValueChangedWhenACheckboxIsChecked(): Promise<Unit> {
        val group = groupFixture()
        var fired = 0
        on(group, "value-changed") { fired++ }

        return nextRender(group)
            .then {
                click(checkboxes(group)[0])
                nextUpdate(group)
            }
            .then { assertTrue(fired > 0, "expected a value-changed event, got $fired") }
    }

    @Test
    fun togglesTheHasValueAttribute(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                assertFalse(group.hasAttribute("has-value"))
                click(checkboxes(group)[0])
                nextUpdate(group)
            }
            .then { assertTrue(group.hasAttribute("has-value")) }
    }

    @Test
    fun includesADynamicallyAddedCheckedCheckbox(): Promise<Unit> {
        val group = groupFixture()

        return nextRender(group)
            .then {
                val checkbox = document.createElement("vaadin-checkbox")
                checkbox.setAttribute("value", "3")
                checkbox.setAttribute("checked", "")
                group.appendChild(checkbox)
                nextUpdate(group)
            }
            .then { assertEquals("3", group.value.single().toString()) }
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
    fun createCheckboxGroupAppliesTheConfigureBlock() {
        val group = attach(createCheckboxGroup { label = "Choices" })

        assertEquals("Choices", group.label)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-checkbox-group",
            document.createElement(vaadin.VaadinTagName.checkboxGroup).tagName.lowercase(),
        )
        assertEquals("label", CheckboxGroupAttributes.label)
        assertEquals("value", CheckboxGroupAttributes.value)
        assertEquals("disabled", CheckboxGroupAttributes.disabled)
        assertEquals("readonly", CheckboxGroupAttributes.readonly)
        assertEquals("required", CheckboxGroupAttributes.required)
        assertEquals("theme", CheckboxGroupAttributes.theme)
    }

    @Test
    fun eventTypesAreExposed() {
        assertEquals(true, CheckboxGroupEvents.valueChanged != null)
        assertEquals(true, CheckboxGroupEvents.validated != null)
        assertEquals(true, CheckboxGroupEvents.invalidChanged != null)
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
