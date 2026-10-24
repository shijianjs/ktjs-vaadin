package vaadin.messageinput

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
import vaadin.typeInto
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/message-input/test/message-input.test.js`.
 *
 * The submit behaviour is translated through the public `value` property and the `submit` event;
 * the textarea is part of the light DOM (field pattern), so the typing helper works as-is.
 */
class MessageInputTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerMessageInput()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newInput(): MessageInputElement = attach(createMessageInput())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerMessageInput()
        val input = newInput()

        assertEquals("vaadin-message-input", input.tagName.lowercase())
        assertEquals("vaadin-message-input", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then { assertEquals("", input.value) }
    }

    @Test
    fun storesTheTypedValue(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                typeInto(input, "Hello")
                nextUpdate(input)
            }
            .then { assertEquals("Hello", input.value) }
    }

    @Test
    fun rendersTheSendButton(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            // The send button is a light-DOM `<vaadin-message-input-button>` slotted into the field.
            val button = js("(input) => input.querySelector('vaadin-message-input-button')")(input)
            assertNotNull(button, "expected the send button to be rendered")
        }
    }

    @Test
    fun firesSubmitFromTheSendButton(): Promise<Unit> {
        val input = newInput()
        var fired = 0
        on(input, "submit") { fired++ }

        return nextRender(input)
            .then {
                typeInto(input, "Hello")
                nextUpdate(input)
            }
            .then {
                val button = js("(input) => input.querySelector('vaadin-message-input-button')")(input) as HTMLElement
                js("(button) => button.click()")(button)
                nextUpdate(input)
            }
            .then { assertTrue(fired > 0, "expected a submit event, got $fired") }
    }

    @Test
    fun doesNotSubmitAnEmptyMessage(): Promise<Unit> {
        val input = newInput()
        var fired = 0
        on(input, "submit") { fired++ }

        return nextRender(input).then {
            val button = js("(input) => input.querySelector('vaadin-message-input-button')")(input) as HTMLElement
            js("(button) => button.click()")(button)
            nextUpdate(input)
            assertEquals(0, fired)
        }
    }

    @Test
    fun reflectsDisabled(): Promise<Unit> {
        val input = newInput()

        return nextUpdate(input)
            .then {
                input.disabled = true
                nextUpdate(input)
            }
            .then { assertTrue(input.hasAttribute("disabled")) }
    }

    @Test
    fun isEnabledByDefault(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then { assertFalse(input.disabled) }
    }

    // Kotlin-only coverage

    @Test
    fun createMessageInputAppliesTheConfigureBlock() {
        val input = attach(createMessageInput { value = "Hello" })

        assertEquals("Hello", input.value)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-message-input",
            document.createElement(vaadin.VaadinTagName.messageInput).tagName.lowercase(),
        )
        assertEquals("value", MessageInputAttributes.value)
        assertEquals("disabled", MessageInputAttributes.disabled)
        assertEquals("i18n", MessageInputAttributes.i18n)
        assertEquals("theme", MessageInputAttributes.theme)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(MessageInputEvents.submit)
    }
}
