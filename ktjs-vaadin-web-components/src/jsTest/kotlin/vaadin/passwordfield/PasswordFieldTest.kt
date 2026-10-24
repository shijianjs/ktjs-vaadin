package vaadin.passwordfield

import vaadin.query
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
import vaadin.shadowQuery
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/password-field/test/password-field.test.js`.
 *
 * The reveal-button cases are translated through the `passwordVisible` and `revealButtonHidden`
 * properties; the click sequence on the reveal button uses the part of the shadow root.
 */
class PasswordFieldTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerPasswordField()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newField(): PasswordFieldElement = attach(createPasswordField())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerPasswordField()
        val field = newField()

        assertEquals("vaadin-password-field", field.tagName.lowercase())
        assertEquals("vaadin-password-field", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun usesThePasswordInputType(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then {
            val input = assertNotNull(query(field, "input"))
            assertEquals("password", input.getAttribute("type"))
        }
    }

    @Test
    fun revealsThePasswordFromTheRevealButton(): Promise<Unit> {
        val field = newField()

        return nextRender(field)
            .then {
                // The clickable node is the `vaadin-password-field-button` slotted into the
                // `reveal` slot; the shadow part is only its wrapper.
                val button = assertNotNull(
                    query(field, "vaadin-password-field-button"),
                )
                js("(button) => button.click()")(button)
                nextUpdate(field)
            }
            .then {
                val input = assertNotNull(query(field, "input"))
                assertTrue(field.passwordVisible)
                assertEquals("text", input.getAttribute("type"))
            }
    }

    @Test
    fun hidesThePasswordAgain(): Promise<Unit> {
        val field = newField()

        return nextRender(field)
            .then {
                val button = assertNotNull(
                    query(field, "vaadin-password-field-button"),
                )
                js("(button) => button.click()")(button)
                nextUpdate(field)
            }
            .then {
                val button = assertNotNull(
                    query(field, "vaadin-password-field-button"),
                )
                js("(button) => button.click()")(button)
                nextUpdate(field)
            }
            .then {
                val input = assertNotNull(query(field, "input"))
                assertFalse(field.passwordVisible)
                assertEquals("password", input.getAttribute("type"))
            }
    }

    @Test
    fun showsTheRevealButtonByDefault(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then {
            assertFalse(field.revealButtonHidden)
            assertNotNull(shadowQuery(field, "[part~=\"reveal-button\"]"))
        }
    }

    @Test
    fun hidesTheRevealButtonWhenConfigured(): Promise<Unit> {
        val field = attach(createPasswordField { revealButtonHidden = true })

        return nextRender(field).then {
            assertTrue(field.revealButtonHidden)
        }
    }

    @Test
    fun togglesTheVisibilityFromTheRevealButton(): Promise<Unit> {
        val field = newField()

        return nextRender(field)
            .then {
                val button = assertNotNull(
                    query(field, "vaadin-password-field-button"),
                )
                js("(button) => button.click()")(button)
                nextUpdate(field)
            }
            .then { assertTrue(field.passwordVisible) }
    }

    // Kotlin-only coverage

    @Test
    fun createPasswordFieldAppliesTheConfigureBlock() {
        val field = attach(createPasswordField { label = "Password" })

        assertEquals("Password", field.label)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-password-field",
            document.createElement(vaadin.VaadinTagName.passwordField).tagName.lowercase(),
        )
        assertEquals("password-visible", PasswordFieldAttributes.passwordVisible)
        assertEquals("reveal-button-hidden", PasswordFieldAttributes.revealButtonHidden)
        assertEquals("value", PasswordFieldAttributes.value)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(PasswordFieldEvents.valueChanged)
        assertNotNull(PasswordFieldEvents.validated)
    }
}
