package vaadin.textarea

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
 * Translated from `packages/text-area/test/text-area.test.js`.
 *
 * `minRows` / `maxRows` drive the rendered `rows` attribute of the textarea, which is what the
 * upstream suite asserts as well.
 */
class TextAreaTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerTextArea()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newTextArea(): TextAreaElement = attach(createTextArea())

    private fun textareaOf(element: HTMLElement): HTMLElement =
        assertNotNull(js("(element) => element.querySelector('textarea')")(element) as HTMLElement?)

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerTextArea()
        val area = newTextArea()

        assertEquals("vaadin-text-area", area.tagName.lowercase())
        assertEquals("vaadin-text-area", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun rendersASlottedTextarea(): Promise<Unit> {
        val area = newTextArea()

        return nextRender(area).then { assertNotNull(textareaOf(area)) }
    }

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val area = newTextArea()

        return nextRender(area).then { assertEquals("", area.value) }
    }

    @Test
    fun keepsLineBreaksInTheValue(): Promise<Unit> {
        val area = newTextArea()

        return nextRender(area)
            .then {
                typeInto(area, "first\nsecond")
                nextUpdate(area)
            }
            .then { assertEquals("first\nsecond", area.value) }
    }

    @Test
    fun firesValueChangedOnUserInput(): Promise<Unit> {
        val area = newTextArea()
        var fired = 0
        on(area, "value-changed") { fired++ }

        return nextRender(area)
            .then {
                typeInto(area, "Hello")
                nextUpdate(area)
            }
            .then { assertTrue(fired > 0, "expected a value-changed event, got $fired") }
    }

    @Test
    fun appliesMinRows(): Promise<Unit> {
        val area = attach(createTextArea { minRows = 3.0 })

        return nextRender(area).then { assertEquals("3", textareaOf(area).getAttribute("rows")) }
    }

    @Test
    fun appliesMaxRows(): Promise<Unit> {
        val area = attach(createTextArea { maxRows = 6.0 })

        return nextRender(area).then {
            assertEquals(6.0, area.maxRows)
        }
    }

    @Test
    fun scrollsToTheStartAndEnd(): Promise<Unit> {
        val area = attach(createTextArea { value = "first\nsecond\nthird" })

        return nextRender(area)
            .then {
                area.scrollToEnd()
                nextUpdate(area)
            }
            .then {
                assertTrue((js("(element) => element.querySelector('textarea').scrollTop")(area) as Int) >= 0)
                area.scrollToStart()
                nextUpdate(area)
            }
            .then {
                assertEquals(0, js("(element) => element.querySelector('textarea').scrollTop")(area) as Int)
            }
    }

    @Test
    fun isInvalidWhenRequiredAndEmpty(): Promise<Unit> {
        val area = attach(createTextArea { required = true })

        return nextRender(area).then {
            assertFalse(area.checkValidity())
            area.validate()
            assertTrue(area.invalid)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createTextAreaAppliesTheConfigureBlock() {
        val area = attach(createTextArea { value = "Hello" })

        assertEquals("Hello", area.value)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-text-area", document.createElement(vaadin.VaadinTagName.textArea).tagName.lowercase())
        assertEquals("min-rows", TextAreaAttributes.minRows)
        assertEquals("max-rows", TextAreaAttributes.maxRows)
        assertEquals("value", TextAreaAttributes.value)
        assertEquals("placeholder", TextAreaAttributes.placeholder)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(TextAreaEvents.valueChanged)
        assertNotNull(TextAreaEvents.input)
        assertNotNull(TextAreaEvents.validated)
    }
}
