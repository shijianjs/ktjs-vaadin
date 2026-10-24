package vaadin.emailfield

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
import vaadin.typeInto
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/email-field/test/email-field.test.js`.
 *
 * The element extends `TextFieldElement`; the suite only asserts the `email` input type and the
 * email-specific validation, so the shared text-field behaviour is covered by `TextFieldTest`.
 */
class EmailFieldTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerEmailField()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newField(): EmailFieldElement = attach(createEmailField())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerEmailField()
        val field = newField()

        assertEquals("vaadin-email-field", field.tagName.lowercase())
        assertEquals("vaadin-email-field", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun usesTheEmailInputType(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then {
            val input = assertNotNull(js("(field) => field.querySelector('input')")(field) as HTMLElement?)
            assertEquals("email", input.getAttribute("type"))
        }
    }

    @Test
    fun acceptsAValidAddress(): Promise<Unit> {
        val field = newField()

        return nextRender(field)
            .then {
                typeInto(field, "foo@example.com")
                nextUpdate(field)
            }
            .then { assertTrue(field.checkValidity()) }
    }

    @Test
    fun rejectsAnInvalidAddress(): Promise<Unit> {
        val field = newField()

        return nextRender(field)
            .then {
                typeInto(field, "foo")
                nextUpdate(field)
            }
            .then { assertFalse(field.checkValidity()) }
    }

    @Test
    fun requiresAValueWhenRequired(): Promise<Unit> {
        val field = attach(createEmailField { required = true })

        return nextRender(field).then {
            assertFalse(field.checkValidity())
            field.validate()
            assertTrue(field.invalid)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createEmailFieldAppliesTheConfigureBlock() {
        val field = attach(createEmailField { label = "Email" })

        assertEquals("Email", field.label)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-email-field", document.createElement(vaadin.VaadinTagName.emailField).tagName.lowercase())
        assertEquals("value", EmailFieldAttributes.value)
        assertEquals("label", EmailFieldAttributes.label)
        assertEquals("pattern", EmailFieldAttributes.pattern)
        assertEquals("inputmode", EmailFieldAttributes.inputmode)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(EmailFieldEvents.valueChanged)
        assertNotNull(EmailFieldEvents.input)
        assertNotNull(EmailFieldEvents.validated)
    }
}
