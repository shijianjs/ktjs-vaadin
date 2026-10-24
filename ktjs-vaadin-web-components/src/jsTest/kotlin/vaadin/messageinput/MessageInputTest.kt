package vaadin.messageinput

import vaadin.query
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
import vaadin.computedStyle
import vaadin.detachAll
import vaadin.fireMouse
import vaadin.keyDown
import vaadin.nextFrame
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import vaadin.typeInto
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/message-input/test/message-input.test.js`.
 *
 * The `click` cases of the upstream suite drive a real mouse through
 * `@vaadin/test-runner-commands` (`sendMouse`), which needs a browser input pipeline that the Kotlin
 * test DSL does not provide; they are skipped. Everything else 鈥?submit, i18n, disabled, focus and
 * the content slots 鈥?is translated.
 */
class MessageInputTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerMessageInput()
        vaadin.textarea.registerTextArea()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newInput(): MessageInputElement = attach(createMessageInput())

    private fun textArea(input: MessageInputElement): HTMLElement =
        assertNotNull(query(input, "vaadin-text-area"))

    private fun inputElement(input: MessageInputElement): HTMLElement =
        assertNotNull(
            js("(input) => input.querySelector('vaadin-text-area').querySelector('textarea')")(input) as HTMLElement?,
        )

    private fun button(input: MessageInputElement): HTMLElement =
        assertNotNull(query(input, "vaadin-message-input-button"))

    private fun setTextAreaValue(input: MessageInputElement, value: String) {
        js("(input, value) => { input.querySelector('vaadin-text-area').value = value; }")(input, value)
    }

    /** Replaces the send button with a custom `<button slot="button">`, like upstream does. */
    private fun replaceWithCustomButton(input: MessageInputElement, innerHtml: String = ""): HTMLElement {
        val custom = document.createElement("button")
        custom.setAttribute("slot", "button")
        if (innerHtml.isNotEmpty()) {
            js("(button, html) => { button.innerHTML = html; }")(custom, innerHtml)
        }
        js("(input, custom) => { input.querySelector('vaadin-message-input-button').replaceWith(custom); }")(input, custom)
        return custom
    }

    // custom element definition

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerMessageInput()
        val input = newInput()

        assertEquals("vaadin-message-input", input.tagName.lowercase())
        assertEquals("vaadin-message-input", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAValidStaticIsGetter() {
        val ctor = registerMessageInput()

        assertEquals("vaadin-message-input", js("(ctor) => ctor.is")(ctor) as String)
    }

    // hidden

    @Test
    fun isHiddenWithTheHiddenAttribute(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            input.setAttribute("hidden", "")
            assertEquals("none", computedStyle(input, "display"))
        }
    }

    // submit functionality

    @Test
    fun firesSubmitOnButtonClick(): Promise<Unit> {
        val input = newInput()
        var submitted = 0
        on(input, "submit") { submitted++ }

        return nextRender(input).then {
            input.value = "foo"
            js("(button) => button.click()")(button(input))
            assertEquals(1, submitted)
        }
    }

    @Test
    fun firesSubmitOnEnterKeydown(): Promise<Unit> {
        val input = newInput()
        var submitted = 0
        on(input, "submit") { submitted++ }

        return nextRender(input).then {
            setTextAreaValue(input, "foo")
            keyDown(inputElement(input), "Enter")
            assertEquals(1, submitted)
        }
    }

    @Test
    fun doesNotFireSubmitOnShiftEnterKeydown(): Promise<Unit> {
        val input = newInput()
        var submitted = 0
        on(input, "submit") { submitted++ }

        return nextRender(input).then {
            setTextAreaValue(input, "foo")
            keyDown(inputElement(input), "Enter", shiftKey = true)
            assertEquals(0, submitted)
        }
    }

    @Test
    fun doesNotFireSubmitWhenTheValueIsEmpty(): Promise<Unit> {
        val input = newInput()
        var submitted = 0
        on(input, "submit") { submitted++ }

        return nextRender(input).then {
            js("(button) => button.click()")(button(input))
            assertEquals(0, submitted)
        }
    }

    @Test
    fun emptiesTheInputAfterSubmit(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            input.value = "foo"
            assertEquals("foo", input.value)
            js("(button) => button.click()")(button(input))
            assertEquals("", input.value)
        }
    }

    @Test
    fun includesTheFilledValueInTheSubmitEvent(): Promise<Unit> {
        val input = newInput()
        var value: String? = null
        on(input, "submit") { event ->
            value = js("(event) => event.detail.value")(event) as String
        }

        return nextRender(input).then {
            setTextAreaValue(input, "foo")
            js("(button) => button.click()")(button(input))
            assertEquals("foo", value)
        }
    }

    @Test
    fun focusesTheInputAfterSubmit(): Promise<Unit> {
        val input = newInput()
        var focused = 0
        on(textArea(input), "focus") { focused++ }

        return nextRender(input).then {
            setTextAreaValue(input, "foo")
            js("(button) => button.click()")(button(input))
            assertTrue(focused > 0, "expected the text area to be focused, got $focused")
        }
    }

    @Test
    fun firesSubmitOnCustomButtonClick(): Promise<Unit> {
        val input = newInput()
        var submitted = 0
        on(input, "submit") { submitted++ }

        return nextRender(input)
            .then {
                val custom = replaceWithCustomButton(input)
                nextFrame().then { custom }
            }
            .then { custom ->
                input.value = "foo"
                js("(custom) => custom.click()")(custom)
                assertEquals(1, submitted)
            }
    }

    // i18n

    @Test
    fun translatesTheButtonText(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                input.i18n = js("({ send: 'L盲het盲' })")
                nextUpdate(input)
            }
            .then { assertEquals("L盲het盲", js("(button) => button.innerText")(button(input)) as String) }
    }

    @Test
    fun translatesThePlaceholder(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                input.i18n = js("({ message: 'Viesti' })")
                nextUpdate(input)
            }
            .then {
                assertEquals(
                    "Viesti",
                    js("(input) => input.querySelector('vaadin-text-area').placeholder")(input) as String,
                )
            }
    }

    @Test
    fun translatesTheAriaLabel(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                input.i18n = js("({ message: 'Viesti' })")
                nextFrame()
            }
            .then {
                assertEquals(
                    "Viesti",
                    js(
                        "(input) => input.querySelector('vaadin-text-area').querySelector('textarea').getAttribute('aria-label')",
                    )(input) as String,
                )
            }
    }

    @Test
    fun supportsAPartialI18nBundle(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                input.i18n = js("({ send: 'L盲het盲' })")
                nextUpdate(input)
            }
            .then {
                assertEquals("L盲het盲", js("(button) => button.innerText")(button(input)) as String)
                assertEquals(
                    "Message",
                    js("(input) => input.querySelector('vaadin-text-area').placeholder")(input) as String,
                )
            }
    }

    @Test
    fun setsAriaLabelOnACustomButtonWithOnlyHiddenText(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                val custom = replaceWithCustomButton(input, "<span aria-hidden=\"true\">鈫?/span>")
                input.i18n = js("({ send: 'L盲het盲' })")
                nextFrame().then { custom }
            }
            .then { custom -> assertEquals("L盲het盲", custom.getAttribute("aria-label")) }
    }

    @Test
    fun setsAriaLabelOnACustomButtonWithOnlyCommentNodes(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                val custom = replaceWithCustomButton(
                    input,
                    "<!-- lit marker --><svg aria-hidden=\"true\"><path d=\"M0 0h1v1H0z\"></path></svg>",
                )
                input.i18n = js("({ send: 'L盲het盲' })")
                nextFrame().then { custom }
            }
            .then { custom -> assertEquals("L盲het盲", custom.getAttribute("aria-label")) }
    }

    @Test
    fun setsAriaLabelOnACustomButtonWithOnlyNamedSlotText(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                val custom = replaceWithCustomButton(input, "<span slot=\"tooltip\">Send prompt</span>")
                input.i18n = js("({ send: 'L盲het盲' })")
                nextFrame().then { custom }
            }
            .then { custom -> assertEquals("L盲het盲", custom.getAttribute("aria-label")) }
    }

    @Test
    fun doesNotOverrideAnAriaLabelSetAfterSlotting(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                val custom = replaceWithCustomButton(
                    input,
                    "<svg aria-hidden=\"true\"><path d=\"M0 0h1v1H0z\"></path></svg>",
                )
                nextFrame().then { custom }
            }
            .then { custom ->
                custom.setAttribute("aria-label", "Send prompt")
                input.value = "foo"
                nextFrame().then { custom }
            }
            .then { custom -> assertEquals("Send prompt", custom.getAttribute("aria-label")) }
    }

    @Test
    fun removesAriaLabelWhenTheCustomButtonGetsText(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                val custom = replaceWithCustomButton(
                    input,
                    "<svg aria-hidden=\"true\"><path d=\"M0 0h1v1H0z\"></path></svg>",
                )
                nextFrame().then { custom }
            }
            .then { custom ->
                assertTrue(custom.hasAttribute("aria-label"))
                js("(custom) => custom.append('Publish')")(custom)
                input.value = "foo"
                nextFrame().then { custom }
            }
            .then { custom -> assertFalse(custom.hasAttribute("aria-label")) }
    }

    @Test
    fun doesNotUpdateAriaLabelOnARemovedCustomButton(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                val custom = replaceWithCustomButton(
                    input,
                    "<svg aria-hidden=\"true\"><path d=\"M0 0h1v1H0z\"></path></svg>",
                )
                nextFrame().then { custom }
            }
            .then { custom ->
                custom.remove()
                nextFrame().then { custom }
            }
            .then { custom ->
                input.i18n = js("({ send: 'L盲het盲' })")
                nextFrame().then { custom }
            }
            .then { custom -> assertEquals("Send", custom.getAttribute("aria-label")) }
    }

    // disabled

    @Test
    fun isEnabledByDefault(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then { assertFalse(input.disabled) }
    }

    @Test
    fun reflectsDisabledToTheAttribute(): Promise<Unit> {
        val input = newInput()

        return nextUpdate(input)
            .then {
                input.disabled = true
                nextUpdate(input)
            }
            .then { assertTrue(input.hasAttribute("disabled")) }
    }

    @Test
    fun disablesTheSubmitButtonWithAnEmptyValue(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            assertEquals(true, js("(button) => button.disabled")(button(input)) as Boolean)
        }
    }

    @Test
    fun enablesTheSubmitButtonWithANonEmptyValue(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                input.value = "foo"
                nextUpdate(input)
            }
            .then { assertEquals(false, js("(button) => button.disabled")(button(input)) as Boolean) }
    }

    @Test
    fun doesNotEnableTheSubmitButtonWhenTheValueIsNull(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                js("(input) => { input.value = null; }")(input)
                nextUpdate(input)
            }
            .then { assertEquals(true, js("(button) => button.disabled")(button(input)) as Boolean) }
    }

    @Test
    fun doesNotEnableTheSubmitButtonWhenDisabled(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                input.disabled = true
                input.value = "foo"
                nextUpdate(input)
            }
            .then { assertEquals(true, js("(button) => button.disabled")(button(input)) as Boolean) }
    }

    @Test
    fun reEnablesTheSubmitButtonWhenEnabledAgain(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                input.disabled = true
                input.value = "foo"
                nextUpdate(input)
            }
            .then {
                input.disabled = false
                nextUpdate(input)
            }
            .then { assertEquals(false, js("(button) => button.disabled")(button(input)) as Boolean) }
    }

    @Test
    fun togglesTheDisabledStateOfACustomButton(): Promise<Unit> {
        val input = newInput()

        return nextRender(input)
            .then {
                val custom = replaceWithCustomButton(input)
                nextFrame().then { custom }
            }
            .then { custom ->
                assertEquals(true, js("(custom) => custom.disabled")(custom) as Boolean)
                input.value = "foo"
                nextUpdate(input)
                assertEquals(false, js("(custom) => custom.disabled")(custom) as Boolean)
            }
    }

    // focus

    @Test
    fun focusesTheTextAreaFromThePublicMethod(): Promise<Unit> {
        val input = newInput()
        var focused = 0
        on(textArea(input), "focus") { focused++ }

        return nextRender(input).then {
            input.focus()
            assertTrue(focused > 0, "expected the text area to be focused, got $focused")
        }
    }

    @Test
    fun doesNotThrowOnFocusWhenDetached(): Unit {
        val element = document.createElement(vaadin.VaadinTagName.messageInput).unsafeCast<MessageInputElement>()

        element.focus()
    }

    @Test
    fun togglesTheFocusedAttributeOnTextAreaFocusAndBlur(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            js("(input) => input.querySelector('vaadin-text-area').focus()")(input)
            assertTrue(input.hasAttribute("focused"))
            js("(input) => input.querySelector('vaadin-text-area').blur()")(input)
            nextUpdate(input)
            assertFalse(input.hasAttribute("focused"))
        }
    }

    @Test
    fun doesNotSetFocusedWhenTheSendButtonIsFocused(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            input.value = "Hello"
            js("(button) => button.focus()")(button(input))
            assertFalse(input.hasAttribute("focused"))
        }
    }

    @Test
    fun setsFocusRingOnTextAreaFocusAfterTab(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            keyDown(document.body!!.unsafeCast<HTMLElement>(), "Tab")
            js("(input) => input.querySelector('vaadin-text-area').focus()")(input)
            assertTrue(input.hasAttribute("focus-ring"))
        }
    }

    @Test
    fun doesNotSetFocusRingOnTextAreaFocusAfterMouseDown(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            keyDown(document.body!!.unsafeCast<HTMLElement>(), "Tab")
            fireMouse(document.body!!.unsafeCast<HTMLElement>(), "mousedown")
            js("(input) => input.querySelector('vaadin-text-area').focus()")(input)
            assertFalse(input.hasAttribute("focus-ring"))
        }
    }

    @Test
    fun setsFocusRingOnProgrammaticFocus(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            fireMouse(document.body!!.unsafeCast<HTMLElement>(), "mousedown")
            input.focus()
            assertTrue(input.hasAttribute("focus-ring"))
        }
    }

    @Test
    fun doesNotSetFocusRingForFocusVisibleFalse(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            fireMouse(document.body!!.unsafeCast<HTMLElement>(), "mousedown")
            js("(input) => input.focus({ focusVisible: false })")(input)
            assertFalse(input.hasAttribute("focus-ring"))
        }
    }

    @Test
    fun doesNotSetFocusRingWhileDisabled(): Promise<Unit> {
        val input = newInput()

        return nextRender(input).then {
            input.disabled = true
            input.focus()
            assertFalse(input.hasAttribute("focus-ring"))
        }
    }

    @Test
    fun doesNotSetFocusRingWhenDetached(): Unit {
        val element = document.createElement(vaadin.VaadinTagName.messageInput).unsafeCast<MessageInputElement>()

        element.focus()

        assertFalse(element.hasAttribute("focus-ring"))
    }

    // content slots

    private fun slotLifecycle(slot: String): Promise<Unit> {
        val input = newInput()
        val attribute = "has-$slot"

        return nextRender(input).then {
            assertFalse(input.hasAttribute(attribute), "expected no $attribute before adding content")
            val content = document.createElement("div")
            content.setAttribute("slot", slot)
            input.appendChild(content)
            nextFrame()
        }
            .then {
                assertTrue(input.hasAttribute(attribute), "expected $attribute after adding content")
                js("(input, slot) => input.querySelector('[slot=\"' + slot + '\"]').remove()")(input, slot)
                nextFrame()
            }
            .then { assertFalse(input.hasAttribute(attribute), "expected no $attribute after removing content") }
    }

    @Test
    fun updatesTheHeaderSlotAttribute() = slotLifecycle("header")

    @Test
    fun updatesThePrefixSlotAttribute() = slotLifecycle("prefix")

    @Test
    fun updatesTheFooterSlotAttribute() = slotLifecycle("footer")

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
}
