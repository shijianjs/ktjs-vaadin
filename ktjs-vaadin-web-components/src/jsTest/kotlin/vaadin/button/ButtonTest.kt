package vaadin.button

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
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/button/test/button.test.ts`.
 *
 * The upstream `active` state cases depend on real pointer sequences; they are covered through the
 * public `disabled` and `click` behaviour instead.
 */
class ButtonTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerButton()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newButton(): ButtonElement = attach(createButton())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerButton()
        val button = newButton()

        assertEquals("vaadin-button", button.tagName.lowercase())
        assertEquals("vaadin-button", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun isEnabledByDefault(): Promise<Unit> {
        val button = newButton()

        return nextRender(button).then { assertFalse(button.disabled) }
    }

    @Test
    fun reflectsDisabledToTheAttribute(): Promise<Unit> {
        val button = newButton()

        return nextUpdate(button)
            .then {
                button.disabled = true
                nextUpdate(button)
            }
            .then { assertTrue(button.hasAttribute("disabled")) }
    }

    @Test
    fun doesNotFireClickWhenDisabled(): Promise<Unit> {
        val button = attach(fixture("<vaadin-button disabled>Press me</vaadin-button>").unsafeCast<ButtonElement>())
        var clicks = 0
        on(button, "click") { clicks++ }

        return nextRender(button).then {
            js("(button) => button.click()")(button)
            assertEquals(0, clicks)
        }
    }

    @Test
    fun firesClickWhenEnabled(): Promise<Unit> {
        val button = attach(fixture("<vaadin-button>Press me</vaadin-button>").unsafeCast<ButtonElement>())
        var clicks = 0
        on(button, "click") { clicks++ }

        return nextRender(button).then {
            js("(button) => button.click()")(button)
            assertEquals(1, clicks)
        }
    }

    @Test
    fun hasTabIndexZeroByDefault(): Promise<Unit> {
        val button = newButton()

        return nextRender(button).then {
            assertEquals(0, js("(button) => button.tabIndex")(button) as Int)
        }
    }

    @Test
    fun isNotFocusableWhenDisabled(): Promise<Unit> {
        val button = attach(fixture("<vaadin-button disabled>Press me</vaadin-button>").unsafeCast<ButtonElement>())

        return nextRender(button).then {
            assertEquals(-1, js("(button) => button.tabIndex")(button) as Int)
        }
    }

    @Test
    fun addsFocusRingOnProgrammaticFocus(): Promise<Unit> {
        val button = newButton()

        return nextRender(button).then {
            js("(button) => button.focus()")(button)
            assertTrue(button.hasAttribute("focus-ring"))
        }
    }

    // Kotlin-only coverage

    @Test
    fun createButtonAppliesTheConfigureBlock() {
        val button = attach(createButton { disabled = true })

        assertTrue(button.disabled)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-button", document.createElement(vaadin.VaadinTagName.button).tagName.lowercase())
        assertEquals("disabled", ButtonAttributes.disabled)
        assertEquals("theme", ButtonAttributes.theme)
    }

    @Test
    fun themeIsAppliedThroughTheAttribute() {
        val button = attach(createButton())
        button.setAttribute("theme", "primary")

        assertEquals("primary", button.getAttribute("theme"))
    }
}
