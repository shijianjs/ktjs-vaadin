package vaadin.customfield

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLInputElement
import web.html.HTMLElement

/**
 * Translated from `packages/custom-field/test/custom-field.test.js`.
 *
 * The upstream "incorrect parser" case asserts a `console.warn` call; it is skipped because the
 * warning is an implementation detail and stubbing console output in Kotlin/JS would test the
 * test harness rather than the wrapper.
 */
class CustomFieldTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun input(type: String = "text"): HTMLInputElement =
        document.createElement("input").unsafeCast<HTMLInputElement>().also { it.setAttribute("type", type) }

    private fun fixture(): CustomFieldElement {
        val field = attach(createCustomField())
        field.appendChild(input())
        field.appendChild(input("number"))
        return field
    }

    private fun fireChange(element: HTMLElement) {
        element.dispatchEvent(js("() => new Event('change', { bubbles: true })")())
    }

    private fun focusIn(element: HTMLElement) {
        element.dispatchEvent(js("() => new FocusEvent('focusin', { bubbles: true, composed: true })")())
    }

    private fun focusOut(element: HTMLElement, relatedTarget: HTMLElement? = null) {
        element.dispatchEvent(
            js("(target) => new FocusEvent('focusout', { bubbles: true, composed: true, relatedTarget: target })")(relatedTarget),
        )
    }

    @Test
    fun inputsAreReadOnly(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field).then {
            assertTrue(field.inputs!!.size > 0)
            js("(element) => { element.inputs = []; }")(field)
            assertTrue(field.inputs!!.size > 0)
        }
    }

    @Test
    fun internalInputsAreDefined(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field).then {
            val inputs = field.inputs!!
            assertEquals(2, inputs.size)
            inputs.forEach { assertEquals("input", it.localName) }
        }
    }

    @Test
    fun focusMovesToTheFirstInput(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field)
            .then {
                field.focus()
                document.activeElement
            }
            .then { active -> assertEquals(field.inputs!![0], active) }
    }

    @Test
    fun slottedInputsAreIgnored(): Promise<Unit> {
        val field = attach(createCustomField())
        val wrapper = document.createElement("div")
        val input = input()
        input.setAttribute("slot", "input")
        val textarea = document.createElement("textarea")
        textarea.setAttribute("slot", "textarea")
        wrapper.appendChild(input)
        wrapper.appendChild(textarea)
        field.appendChild(wrapper)

        return nextUpdate(field).then { assertTrue(field.inputs!!.isEmpty()) }
    }

    @Test
    fun valueIsTabSeparated(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field).then { assertEquals("\t", field.value) }
    }

    @Test
    fun changeEventsUpdateTheValue(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field)
            .then {
                field.inputs!!.forEach {
                    js("(element) => { element.value = '1'; }")(it)
                    fireChange(it)
                }
                nextUpdate(field)
            }
            .then { assertEquals("1\t1", field.value) }
    }

    @Test
    fun settingTheValueUpdatesTheInputs(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field)
            .then {
                field.value = "1\t1"
                nextUpdate(field)
            }
            .then {
                field.inputs!!.forEach { assertEquals("1", js("(element) => element.value")(it) as String) }
            }
    }

    @Test
    fun hasValueAttributeIsSetWhenInputsHaveValues(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field)
            .then {
                field.inputs!!.forEach {
                    js("(element) => { element.value = '1'; }")(it)
                    fireChange(it)
                }
                nextUpdate(field)
            }
            .then { assertTrue(field.hasAttribute("has-value")) }
    }

    @Test
    fun settingValueToNullClearsTheInputs(): Promise<Unit> {
        val field = fixture()

        // The manifest types `value` as `string`, while the implementation also accepts `null`;
        // the assignment goes through `js()` so the wrapper stays faithful to the manifest.
        return nextUpdate(field)
            .then {
                field.value = "1\t1"
                nextUpdate(field)
            }
            .then {
                js("(element) => { element.value = null; }")(field)
                nextUpdate(field)
            }
            .then {
                field.inputs!!.forEach { assertEquals("", js("(element) => element.value")(it) as String) }
            }
    }

    @Test
    fun removingInputsUpdatesTheValue(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field)
            .then {
                field.value = "1\t1"
                nextUpdate(field)
            }
            .then {
                field.removeChild(field.inputs!![0])
                nextUpdate(field)
            }
            .then {
                assertEquals("1", field.value)
                field.removeChild(field.inputs!![0])
                nextUpdate(field)
            }
            .then { assertEquals("", field.value) }
    }

    @Test
    fun valueSetWithTheAttributeIsNotReset(): Promise<Unit> {
        val field = attach(createCustomField())
        field.setAttribute("value", "01\t25")
        field.appendChild(input("number"))
        field.appendChild(input("number"))

        return nextUpdate(field).then { assertEquals("01\t25", field.value) }
    }

    @Test
    fun valueSetWithTheAttributeIsAppliedToTheInputs(): Promise<Unit> {
        val field = attach(createCustomField())
        field.setAttribute("value", "01\t25")
        field.appendChild(input("number"))
        field.appendChild(input("number"))

        return nextUpdate(field).then {
            assertEquals("01", js("(element) => element.value")(field.inputs!![0]) as String)
            assertEquals("25", js("(element) => element.value")(field.inputs!![1]) as String)
        }
    }

    @Test
    fun requiredTogglesAriaRequired(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field)
            .then {
                field.required = true
                nextUpdate(field)
            }
            .then {
                assertEquals("true", field.getAttribute("aria-required"))
                field.required = false
                nextUpdate(field)
            }
            .then { assertFalse(field.hasAttribute("aria-required")) }
    }

    @Test
    fun focusInSetsTheFocusedAttribute(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field)
            .then {
                focusIn(field.inputs!![0])
                nextUpdate(field)
            }
            .then { assertTrue(field.hasAttribute("focused")) }
    }

    @Test
    fun focusOutRemovesTheFocusedAttribute(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field)
            .then {
                focusIn(field.inputs!![0])
                focusOut(field.inputs!![0])
                nextUpdate(field)
            }
            .then { assertFalse(field.hasAttribute("focused")) }
    }

    @Test
    fun movingFocusBetweenInputsKeepsTheFocusedAttribute(): Promise<Unit> {
        val field = fixture()

        return nextUpdate(field)
            .then {
                focusIn(field.inputs!![0])
                focusOut(field.inputs!![0], field.inputs!![1])
                nextUpdate(field)
            }
            .then { assertTrue(field.hasAttribute("focused")) }
    }

    @Test
    fun customParserIsUsed(): Promise<Unit> {
        val field = fixture()
        field.parseValue = { value ->
            val parsed = value.split(" ").map { js("(text) => parseInt(text) + 1")(it) }
            Array<kotlin.js.JsAny?>(parsed.size) { parsed[it] }
        }

        return nextUpdate(field)
            .then {
                field.value = "1 1"
                nextUpdate(field)
            }
            .then {
                field.inputs!!.forEach { assertEquals("2", js("(element) => element.value")(it) as String) }
            }
    }

    @Test
    fun customFormatterIsUsed(): Promise<Unit> {
        val field = fixture()
        field.formatValue = { values ->
            values
                .map { js("(value) => parseInt(value) + 1 || ''")(it) }
                .joinToString(" ")
        }

        return nextUpdate(field)
            .then {
                field.inputs!!.forEach {
                    js("(element) => { element.value = '1'; }")(it)
                    fireChange(it)
                }
                nextUpdate(field)
            }
            .then { assertEquals("2 2", field.value) }
    }
}
