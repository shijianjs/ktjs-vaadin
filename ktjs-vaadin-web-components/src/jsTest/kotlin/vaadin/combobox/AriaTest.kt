package vaadin.combobox

import kotlin.js.Promise
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
import vaadin.query
import web.html.HTMLElement

/**
 * Translated from the public parts of `packages/combo-box/test/aria.test.js`.
 *
 * The upstream suite opens the dropdown with `arrowDownKeyDown` and closes it with `escKeyDown`; here
 * the `opened` property is used, which toggles the same ARIA state.
 *
 * Dropped upstream cases: everything about `aria-activedescendant` (`should set aria-activedescendant
 * on the input element depending on the focused item` and its "opened on input" variant) — it mirrors
 * the internal `_focusedIndex`, which only moves through real key sequences.
 */
class AriaTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerComboBox()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newComboBox(): ComboBoxElement = attach(createComboBox { items = stringItems("foo", "bar", "baz") })

    private fun input(comboBox: ComboBoxElement): HTMLElement = assertNotNull(query(comboBox, "input"))

    @Test
    fun togglesAriaExpandedOnOpen(): Promise<Unit> {
        val comboBox = newComboBox()
        val input = input(comboBox)

        return nextRender(comboBox)
            .then {
                comboBox.opened = true
                nextRender(comboBox)
            }
            .then {
                assertEquals("true", input.getAttribute("aria-expanded"))

                comboBox.close()
                nextRender(comboBox)
            }
            .then { assertEquals("false", input.getAttribute("aria-expanded")) }
    }

    @Test
    fun togglesAriaControlsOnOpen(): Promise<Unit> {
        val comboBox = newComboBox()
        val input = input(comboBox)

        return nextRender(comboBox)
            .then {
                comboBox.opened = true
                nextRender(comboBox)
            }
            .then {
                assertTrue(input.hasAttribute("aria-controls"))

                comboBox.close()
                nextRender(comboBox)
            }
            .then { assertFalse(input.hasAttribute("aria-controls")) }
    }

    @Test
    fun setsAriaSelectedOnTheItemElements(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.opened = true
                nextRender(comboBox)
            }
            .then {
                comboBox.value = "foo"
                val items = comboBoxItems(comboBox)

                assertEquals("true", items[0].getAttribute("aria-selected"))
                assertEquals("false", items[1].getAttribute("aria-selected"))

                comboBox.value = "bar"

                assertEquals("false", items[0].getAttribute("aria-selected"))
                assertEquals("true", items[1].getAttribute("aria-selected"))
            }
    }
}
