package vaadin.formlayout

import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import web.dom.document
import web.html.HTMLInputElement
import web.html.HTMLElement

/**
 * Translated from `packages/form-layout/test/form-item.test.js`.
 *
 * The upstream "warnings" and "input width" suites are skipped: one asserts console output, the
 * other measures pixel widths. Label wiring, `aria-labelledby` and the required/invalid state
 * synchronization are covered.
 */
class FormItemTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun label(text: String = "Label"): HTMLElement =
        document.createElement("label").also {
            it.setAttribute("slot", "label")
            it.textContent = text
        }

    private fun input(): HTMLInputElement = document.createElement("input").unsafeCast<HTMLInputElement>()

    private fun fixture(): Triple<FormItemElement, HTMLElement, HTMLInputElement> {
        val item = attach(createFormItem())
        val label = label()
        val input = input()
        item.appendChild(label)
        item.appendChild(input)
        return Triple(item, label, input)
    }

    private fun countCalls(element: HTMLElement, method: String): Int =
        js(
            """(element, method) => Number(element.dataset[method + 'Calls'] || 0)""",
        )(element, method) as Int

    private fun spyOn(element: HTMLElement, method: String) {
        js(
            """(element, method) => {
                const original = element[method];
                element.dataset[method + 'Calls'] = '0';
                element[method] = (...args) => {
                    element.dataset[method + 'Calls'] = String(Number(element.dataset[method + 'Calls']) + 1);
                    return original.apply(element, args);
                };
            }""",
        )(element, method)
    }

    @Test
    fun labelsGetUniqueIds(): Promise<Unit> {
        val first = fixture().second
        val second = fixture().second

        return nextFrame().then {
            val firstId = first.getAttribute("id")!!
            val secondId = second.getAttribute("id")!!
            assertTrue(firstId != secondId)
            assertTrue(Regex("^label-vaadin-form-item-\\d+$").matches(firstId), "unexpected id $firstId")
            assertTrue(Regex("^label-vaadin-form-item-\\d+$").matches(secondId), "unexpected id $secondId")
        }
    }

    @Test
    fun clickingTheLabelFocusesTheInput(): Promise<Unit> {
        val (_, label, input) = fixture()

        return nextFrame()
            .then {
                spyOn(input, "focus")
                label.click()
                countCalls(input, "focus")
            }
            .then { assertEquals(1, it) }
    }

    @Test
    fun clickingTheLabelClicksTheInput(): Promise<Unit> {
        val (_, label, input) = fixture()

        return nextFrame()
            .then {
                spyOn(input, "click")
                label.click()
                countCalls(input, "click")
            }
            .then { assertEquals(1, it) }
    }

    @Test
    fun replacingTheLabelMovesTheIdToTheNewLabel(): Promise<Unit> {
        val (item, oldLabel, input) = fixture()

        return nextFrame()
            .then {
                val newLabel = label()
                item.replaceChild(newLabel, oldLabel)
                nextFrame().then { newLabel to input }
            }
            .then { (newLabel, input) ->
                assertEquals(input.getAttribute("aria-labelledby"), newLabel.getAttribute("id"))
            }
    }

    @Test
    fun appendingASecondLabelDoesNotStealTheId(): Promise<Unit> {
        val (item, label, _) = fixture()

        return nextFrame()
            .then {
                val labelId = label.getAttribute("id")
                val newLabel = label("Other")
                item.appendChild(newLabel)
                nextFrame().then { Triple(label, labelId, newLabel) }
            }
            .then { (label, labelId, newLabel) ->
                assertEquals(labelId, label.getAttribute("id"))
                assertNull(newLabel.getAttribute("id"))
            }
    }

    @Test
    fun insertingALabelBeforeTheCurrentOneMovesTheId(): Promise<Unit> {
        val (item, label, input) = fixture()

        return nextFrame()
            .then {
                val newLabel = label("Other")
                item.insertBefore(newLabel, label)
                nextFrame().then { newLabel to input }
            }
            .then { (newLabel, input) ->
                assertEquals(input.getAttribute("aria-labelledby"), newLabel.getAttribute("id"))
            }
    }

    @Test
    fun customLabelIdIsPreservedAndLinked(): Promise<Unit> {
        val (item, oldLabel, input) = fixture()

        return nextFrame()
            .then {
                val newLabel = label()
                newLabel.setAttribute("id", "custom-label-id")
                item.replaceChild(newLabel, oldLabel)
                nextFrame().then { newLabel to input }
            }
            .then { (newLabel, input) ->
                assertEquals("custom-label-id", newLabel.getAttribute("id"))
                assertEquals("custom-label-id", input.getAttribute("aria-labelledby"))
            }
    }

    @Test
    fun labelIdSurvivesBeingReplacedBack(): Promise<Unit> {
        val (item, oldLabel, _) = fixture()
        val newLabel = label()
        newLabel.setAttribute("id", "custom-label-id")

        return nextFrame()
            .then {
                item.replaceChild(newLabel, oldLabel)
                nextFrame().then { item.replaceChild(oldLabel, newLabel) }
            }
            .then { nextFrame() }
            .then { assertEquals("custom-label-id", newLabel.getAttribute("id")) }
    }

    @Test
    fun existingAriaLabelledbyIsExtendedWithTheLabelId(): Promise<Unit> {
        val item = attach(createFormItem())
        val label = label()
        val input = input()
        input.setAttribute("aria-labelledby", "custom-id")
        item.appendChild(label)
        item.appendChild(input)

        return nextFrame().then {
            assertEquals("custom-id ${label.getAttribute("id")}", input.getAttribute("aria-labelledby"))
        }
    }

    @Test
    fun replacingTheInputMovesTheLabelLink(): Promise<Unit> {
        val item = attach(createFormItem())
        val label = label()
        val input = input()
        input.setAttribute("aria-labelledby", "custom-id")
        item.appendChild(label)
        item.appendChild(input)

        return nextFrame()
            .then {
                val newInput = input()
                item.replaceChild(newInput, input)
                nextFrame().then { Triple(label, input, newInput) }
            }
            .then { (label, oldInput, newInput) ->
                assertEquals("custom-id", oldInput.getAttribute("aria-labelledby"))
                assertEquals(label.getAttribute("id"), newInput.getAttribute("aria-labelledby"))
            }
    }

    @Test
    fun appendingAnInputDoesNotLinkIt(): Promise<Unit> {
        val item = attach(createFormItem())
        val label = label()
        val input = input()
        input.setAttribute("aria-labelledby", "custom-id")
        item.appendChild(label)
        item.appendChild(input)

        return nextFrame()
            .then {
                val other = input()
                item.appendChild(other)
                nextFrame().then { Triple(label, input, other) }
            }
            .then { (label, input, other) ->
                assertEquals("custom-id ${label.getAttribute("id")}", input.getAttribute("aria-labelledby"))
                assertFalse(other.hasAttribute("aria-labelledby"))
            }
    }

    @Test
    fun removingTheLabelUnlinksIt(): Promise<Unit> {
        val item = attach(createFormItem())
        val label = label()
        val input = input()
        input.setAttribute("aria-labelledby", "custom-id")
        item.appendChild(label)
        item.appendChild(input)

        return nextFrame()
            .then {
                label.remove()
                nextFrame()
            }
            .then { assertEquals("custom-id", input.getAttribute("aria-labelledby")) }
    }

    @Test
    fun labelAddedLaterIsLinked(): Promise<Unit> {
        val item = attach(createFormItem())
        val input = input()
        input.setAttribute("aria-labelledby", "custom-id")
        item.appendChild(input)

        return nextFrame()
            .then {
                assertEquals("custom-id", input.getAttribute("aria-labelledby"))
                val label = label()
                item.appendChild(label)
                nextFrame().then { label }
            }
            .then { label ->
                assertEquals("custom-id ${label.getAttribute("id")}", input.getAttribute("aria-labelledby"))
            }
    }

    @Test
    fun requiredInputSetsRequiredAttribute(): Promise<Unit> {
        val (item, _, input) = fixture()
        input.required = true

        return nextFrame().then { assertTrue(item.hasAttribute("required")) }
    }

    @Test
    fun invalidIsNotSetInitially(): Promise<Unit> {
        val (item, _, input) = fixture()
        input.required = true

        return nextFrame().then { assertFalse(item.hasAttribute("invalid")) }
    }

    @Test
    fun clearingRequiredRemovesTheRequiredAttribute(): Promise<Unit> {
        val (item, _, input) = fixture()
        input.required = true

        return nextFrame()
            .then {
                input.required = false
                nextFrame()
            }
            .then { assertFalse(item.hasAttribute("required")) }
    }

    @Test
    fun removingTheInputRemovesTheRequiredAttribute(): Promise<Unit> {
        val (item, _, input) = fixture()
        input.required = true

        return nextFrame()
            .then {
                input.remove()
                nextFrame()
            }
            .then { assertFalse(item.hasAttribute("required")) }
    }

    @Test
    fun changeEventOnEmptyRequiredInputMarksInvalid(): Promise<Unit> {
        val (item, _, input) = fixture()
        input.required = true

        return nextFrame()
            .then {
                input.dispatchEvent(js("() => new Event('change')")())
                item.hasAttribute("invalid")
            }
            .then { assertTrue(it) }
    }

    @Test
    fun changeEventOnFilledRequiredInputIsValid(): Promise<Unit> {
        val (item, _, input) = fixture()
        input.required = true

        return nextFrame()
            .then {
                js("(element) => { element.value = 'foo'; }")(input)
                input.dispatchEvent(js("() => new Event('change')")())
                item.hasAttribute("invalid")
            }
            .then { assertFalse(it) }
    }

    @Test
    fun blurEventOnEmptyRequiredInputMarksInvalid(): Promise<Unit> {
        val (item, _, input) = fixture()
        input.required = true

        return nextFrame()
            .then {
                input.dispatchEvent(js("() => new Event('blur')")())
                item.hasAttribute("invalid")
            }
            .then { assertTrue(it) }
    }

    @Test
    fun invalidIsClearedWhenRequiredIsTurnedOff(): Promise<Unit> {
        val (item, _, input) = fixture()
        input.required = true

        return nextFrame()
            .then {
                item.setAttribute("invalid", "")
                input.required = false
                nextFrame()
            }
            .then { assertFalse(item.hasAttribute("invalid")) }
    }

    @Test
    fun invalidIsClearedWhenTheInputIsRemoved(): Promise<Unit> {
        val (item, _, input) = fixture()
        input.required = true

        return nextFrame()
            .then {
                item.setAttribute("invalid", "")
                input.remove()
                nextFrame()
            }
            .then { assertFalse(item.hasAttribute("invalid")) }
    }

    @Test
    fun requiredFieldSynchronizesRequiredAndInvalid(): Promise<Unit> {
        val item = attach(createFormItem())
        val field = vaadin.textfield.createTextField()
        item.appendChild(field)

        return nextFrame()
            .then {
                field.required = true
                nextFrame()
            }
            .then {
                assertTrue(item.hasAttribute("required"))
                assertFalse(item.hasAttribute("invalid"))
                field.required = false
                nextFrame()
            }
            .then {
                assertFalse(item.hasAttribute("required"))
                assertFalse(item.hasAttribute("invalid"))
            }
    }
}
