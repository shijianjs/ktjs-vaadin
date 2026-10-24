package vaadin.inputcontainer

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/** Translated from `packages/input-container/test/input-container.test.js`. */
class InputContainerTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun fixture(): Pair<InputContainerElement, HTMLElement> {
        val container = attach(createInputContainer())
        val input = document.createElement("input")
        container.appendChild(input)
        return container to input
    }

    private fun dispatchPointerDown(element: HTMLElement): web.events.Event {
        val event = js("() => new PointerEvent('pointerdown', { bubbles: true, cancelable: true })")()
        element.dispatchEvent(event)
        return event as web.events.Event
    }

    @Test
    fun readonlyReflectsToTheAttribute(): Promise<Unit> {
        val (container, _) = fixture()

        return nextUpdate(container)
            .then {
                container.readonly = true
                nextUpdate(container)
            }
            .then {
                assertTrue(container.hasAttribute("readonly"))
                container.readonly = false
                nextUpdate(container)
            }
            .then { assertFalse(container.hasAttribute("readonly")) }
    }

    @Test
    fun disabledReflectsToTheAttribute(): Promise<Unit> {
        val (container, _) = fixture()

        return nextUpdate(container)
            .then {
                container.disabled = true
                nextUpdate(container)
            }
            .then {
                assertTrue(container.hasAttribute("disabled"))
                container.disabled = false
                nextUpdate(container)
            }
            .then { assertFalse(container.hasAttribute("disabled")) }
    }

    @Test
    fun invalidReflectsToTheAttribute(): Promise<Unit> {
        val (container, _) = fixture()

        return nextUpdate(container)
            .then {
                container.invalid = true
                nextUpdate(container)
            }
            .then {
                assertTrue(container.hasAttribute("invalid"))
                container.invalid = false
                nextUpdate(container)
            }
            .then { assertFalse(container.hasAttribute("invalid")) }
    }

    @Test
    fun pointerDownOnTheContainerIsCancelled() {
        val (container, _) = fixture()
        assertTrue(dispatchPointerDown(container).defaultPrevented)
    }

    @Test
    fun pointerDownOnTheInputIsNotCancelled() {
        val (_, input) = fixture()
        assertFalse(dispatchPointerDown(input).defaultPrevented)
    }

    @Test
    fun clickingTheContainerFocusesTheInput() {
        val (container, input) = fixture()
        container.click()
        assertTrue(document.activeElement == input)
    }

    @Test
    fun clickingTheInputDoesNotRefocusIt() {
        val (_, input) = fixture()
        js(
            """(element) => {
                element.focus = () => {
                    element.dataset.focusCalls = String(Number(element.dataset.focusCalls || 0) + 1);
                };
            }""",
        )(input)
        input.click()
        assertTrue(js("(element) => Number(element.dataset.focusCalls || 0)")(input) == 0)
    }

    @Test
    fun heightCustomPropertyIsNotSetByDefault() {
        val (container, _) = fixture()
        assertTrue(js("(element) => getComputedStyle(element).getPropertyValue('--vaadin-input-field-height')")(container) == "")
    }

    @Test
    fun heightCustomPropertyIsInheritedByTheContainer(): Promise<Unit> {
        val (container, _) = fixture()

        return nextUpdate(container).then {
            container.style.setProperty("--vaadin-input-field-height", "55px")
            val value = js("(element) => getComputedStyle(element).getPropertyValue('--vaadin-input-field-height')")(container)
            assertTrue(value == "55px", "expected 55px, got " + value)
        }
    }
}
