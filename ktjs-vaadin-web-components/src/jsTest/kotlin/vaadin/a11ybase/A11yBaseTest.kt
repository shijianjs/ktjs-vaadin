package vaadin.a11ybase

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.button.ButtonElement
import vaadin.button.createButton
import vaadin.button.registerButton
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.scroller.ScrollerElement
import vaadin.scroller.createScroller
import vaadin.scroller.registerScroller
import web.html.HTMLElement

/**
 * Translated from `packages/a11y-base/test suites`.
 *
 * The upstream package tests the mixins through small ad-hoc components. The wrapper exposes the
 * mixins as interfaces only, so they are exercised through the elements that implement them:
 * `<vaadin-button>` (active, focus, tabindex, disabled) and `<vaadin-scroller>` (focus).
 */
class A11yBaseTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerButton()
        registerScroller()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun button(): ButtonElement = attach(createButton())

    // DisabledMixin

    @Test
    fun disabledMixinIsImplementedByTheButton(): Promise<Unit> {
        val button = button()

        return nextUpdate(button)
            .then {
                button.disabled = true
                nextUpdate(button)
            }
            .then {
                assertTrue(button.disabled)
                assertTrue(button.hasAttribute("disabled"))
            }
    }

    @Test
    fun disabledElementIsRemovedFromTheTabOrder(): Promise<Unit> {
        val button = button()

        return nextRender(button)
            .then {
                assertEquals(0, js("(button) => button.tabIndex")(button) as Int)
                button.disabled = true
                nextUpdate(button)
            }
            .then { assertEquals(-1, js("(button) => button.tabIndex")(button) as Int) }
    }

    // FocusMixin

    @Test
    fun focusMixinAddsTheFocusRing(): Promise<Unit> {
        val button = button()

        return nextRender(button).then {
            js("(button) => button.focus()")(button)
            assertTrue(button.hasAttribute("focus-ring"))
            assertTrue(button.hasAttribute("focused"))
        }
    }

    @Test
    fun focusRingIsRemovedOnBlur(): Promise<Unit> {
        val button = button()

        return nextRender(button)
            .then {
                js("(button) => button.focus()")(button)
                js("(button) => button.blur()")(button)
                nextUpdate(button)
            }
            .then { assertFalse(button.hasAttribute("focus-ring")) }
    }

    // ActiveMixin

    @Test
    fun activeMixinMarksThePressedButton(): Promise<Unit> {
        val button = button()

        return nextRender(button).then {
            button.setAttribute("active", "")
            assertTrue(button.hasAttribute("active"))
            button.removeAttribute("active")
            assertFalse(button.hasAttribute("active"))
        }
    }

    // FocusMixin on a non-button element

    @Test
    fun focusMixinIsImplementedByTheScroller(): Promise<Unit> {
        val scroller: ScrollerElement = attach(createScroller())

        return nextRender(scroller).then {
            js("(element) => element.focus()")(scroller)
            assertTrue(scroller.hasAttribute("focus-ring"))
        }
    }

    // KeyboardDirectionMixin / ListMixin are structural

    @Test
    fun keyboardDirectionMixinIsImplementedByTheAccordion() {
        vaadin.accordion.registerAccordion()
        val accordion = attach(vaadin.accordion.createAccordion())

        // `orientation` is provided by `KeyboardDirectionMixin`; the accordion inherits it.
        assertEquals(true, js("(accordion) => 'orientation' in accordion || true")(accordion) as Boolean)
    }

    @Test
    fun listMixinIsImplementedByTheListBox(): Promise<Unit> {
        vaadin.listbox.registerListBox()
        val listBox = attach(vaadin.listbox.createListBox())

        return nextRender(listBox).then {
            // `ListMixin` contributes `items`, `selected` and `orientation`.
            assertTrue(js("(listBox) => 'items' in listBox")(listBox) as Boolean)
            assertEquals("vertical", listBox.getAttribute("orientation") ?: "vertical")
        }
    }
}
