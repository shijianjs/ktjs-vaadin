package vaadin.combobox

import kotlin.js.JsAny
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
import vaadin.nextUpdate
import vaadin.query
import web.html.HTMLElement

/**
 * Translated from `packages/combo-box/test/overlay-opening.test.js`.
 *
 * The upstream cases assert both `comboBox.opened` and `overlay.opened`; the overlay is a protected
 * member, so only the host state (plus the ARIA state of the input, see `AriaTest`) is asserted here.
 *
 * Dropped upstream cases: the ones that click the protected `_toggleElement`, the ones that open the
 * dropdown with `arrowDownKeyDown` / `arrowUpKeyDown`, and `should export overlay parts for styling`,
 * which reads the overlay shadow root.
 */
class OverlayOpeningTest {
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

    private fun newComboBox(): ComboBoxElement = attach(createComboBox())

    private fun withItems(): ComboBoxElement = newComboBox().apply { items = stringItems("foo", "bar", "baz") }

    // default (auto open)

    @Test
    fun isClosedByDefault() {
        assertFalse(withItems().opened)
    }

    @Test
    fun opensByEnteringAnInputValue(): Promise<Unit> {
        val comboBox = withItems()

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "foo")

            assertTrue(comboBox.opened)
        }
    }

    @Test
    fun opensByClearingTheInputValue(): Promise<Unit> {
        val comboBox = withItems()
        comboBox.value = "foo"

        return nextRender(comboBox).then {
            assertFalse(comboBox.opened)

            setComboBoxInput(comboBox, "")

            assertTrue(comboBox.opened)
        }
    }

    @Test
    fun opensOnFunctionCall(): Promise<Unit> {
        val comboBox = withItems()

        return nextRender(comboBox).then {
            comboBox.`open`()

            assertTrue(comboBox.opened)
        }
    }

    // auto open disabled

    @Test
    fun doesNotOpenOnInputWhenAutoOpenIsDisabled(): Promise<Unit> {
        val comboBox = withItems()
        comboBox.autoOpenDisabled = true

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "foo")

            assertFalse(comboBox.opened)
        }
    }

    @Test
    fun doesNotOpenOnClearingTheInputWhenAutoOpenIsDisabled(): Promise<Unit> {
        val comboBox = withItems()
        comboBox.autoOpenDisabled = true
        comboBox.value = "foo"

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "")

            assertFalse(comboBox.opened)
        }
    }

    // opening disallowed

    @Test
    fun doesNotOpenOnHelperTextClick(): Promise<Unit> {
        val comboBox = withItems()
        comboBox.helperText = "Helper Text"

        return nextUpdate(comboBox).then {
            vaadin.query(comboBox, "[slot=helper]")?.let { vaadin.click(it) }

            assertFalse(comboBox.opened)
        }
    }

    @Test
    fun doesNotOpenOnErrorMessageClick(): Promise<Unit> {
        val comboBox = withItems()
        comboBox.invalid = true
        comboBox.errorMessage = "Error message"

        return nextUpdate(comboBox).then {
            vaadin.query(comboBox, "[slot=error-message]")?.let { vaadin.click(it) }

            assertFalse(comboBox.opened)
        }
    }

    @Test
    fun doesNotOpenWhenDisabled(): Promise<Unit> {
        val comboBox = withItems()
        comboBox.disabled = true

        return nextRender(comboBox).then {
            comboBox.`open`()

            assertFalse(comboBox.opened)
        }
    }

    @Test
    fun doesNotOpenWhenReadonly(): Promise<Unit> {
        val comboBox = withItems()
        comboBox.readonly = true

        return nextRender(comboBox).then {
            comboBox.`open`()

            assertFalse(comboBox.opened)
        }
    }

    @Test
    fun keepsTheHostOpenedForEmptyItems(): Promise<Unit> {
        val comboBox = withItems()
        comboBox.items = null

        return nextRender(comboBox).then {
            comboBox.`open`()

            // The host stays opened while the dropdown itself stays closed, which the upstream case
            // asserts on the overlay element.
            assertTrue(comboBox.opened)
            assertEquals(0, comboBoxItems(comboBox).size)
        }
    }

    @Test
    fun keepsTheHostOpenedForAnEmptyFilteredItemsArray(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.filteredItems = emptyArray<JsAny>()

        return nextRender(comboBox).then {
            comboBox.`open`()

            assertTrue(comboBox.opened)
            assertEquals(0, comboBoxItems(comboBox).size)
        }
    }

    @Test
    fun exposesTheInputInTheShadowRoot() {
        val comboBox = withItems()

        assertNotNull(query(comboBox, "input"))
    }
}
