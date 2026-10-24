package vaadin.combobox

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.on
import web.html.HTMLElement

/**
 * Translated from `packages/combo-box/test/selecting-items.test.js`.
 *
 * `outsideClick()` is replaced by [ComboBoxElement.close], which runs the same commit path.
 *
 * Dropped upstream cases: both `selection-changed` cases (the event is dispatched by the protected
 * `_scroller` and does not reach the host), `should stop click events from bubbling outside the
 * overlay` (it clicks the protected scroller itself), and the `_scroller.selectedItem` assertion of
 * `should select by using JS api`.
 */
class SelectingItemsTest {
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

    private fun newComboBox(configure: ComboBoxElement.() -> Unit = {}): ComboBoxElement =
        attach(createComboBox(configure))

    private fun openedComboBox(): Promise<ComboBoxElement> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        comboBox.`open`()
        return nextRender(comboBox).then { comboBox }
    }

    private fun clickItem(comboBox: ComboBoxElement, index: Int) {
        comboBoxItems(comboBox)[index].let { vaadin.click(it) }
    }

    // selecting items

    @Test
    fun selectsByUsingTheJsApi(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        comboBox.value = "foo"

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                assertEquals("foo", comboBox.selectedItem.unsafeCast<String>())
                assertEquals("foo", comboBoxInputValue(comboBox))
            }
    }

    @Test
    fun closesTheDropdownOnSelection(): Promise<Unit> = openedComboBox().then { comboBox ->
        clickItem(comboBox, 0)

        assertFalse(comboBox.opened)
    }

    @Test
    fun firesExactlyOneValueChangedEvent() {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        var valueChanged = 0
        on(comboBox, "value-changed") { valueChanged++ }

        comboBox.value = "foo"

        assertEquals(1, valueChanged)
    }

    @Test
    fun closesTheDropdownWhenReselectingTheCurrentValue(): Promise<Unit> = openedComboBox().then { comboBox ->
        comboBox.value = "foo"
        comboBox.`open`()

        nextRender(comboBox).then {
            clickItem(comboBox, 0)

            assertFalse(comboBox.opened)
        }
    }

    @Test
    fun doesNotFireAnEventWhenReselectingTheCurrentValue(): Promise<Unit> = openedComboBox().then { comboBox ->
        comboBox.value = "foo"
        var valueChanged = 0
        on(comboBox, "value-changed") { valueChanged++ }

        clickItem(comboBox, 0)

        assertEquals(0, valueChanged)
    }

    @Test
    fun doesNotThrowWhenTheValueWasUndefined(): Promise<Unit> {
        val comboBox = newComboBox { allowCustomValue = true }
        js("(comboBox) => { comboBox.value = undefined; }")(comboBox)

        return nextRender(comboBox).then {
            comboBox.`open`()
            comboBox.close()
        }
    }

    @Test
    fun allowsSettingSelectedItemBeforeAttaching(): Promise<Unit> {
        val original = newComboBox()
        val clone = original.cloneNode(true).unsafeCast<ComboBoxElement>()
        clone.items = stringItems("foo", "bar")
        clone.selectedItem = "foo".unsafeCast<JsAny>()
        attach(clone)

        return nextRender(clone).then { assertEquals("foo", clone.value) }
    }

    @Test
    fun allowsSettingSelectedItemAfterAttachingAndBeforeItems(): Promise<Unit> {
        val original = newComboBox()
        val clone = attach(original.cloneNode(true).unsafeCast<ComboBoxElement>())
        clone.selectedItem = "foo".unsafeCast<JsAny>()
        clone.items = stringItems("foo", "bar")

        return nextRender(clone).then { assertEquals("foo", clone.value) }
    }

    @Test
    fun allowsChangingTheValueInTheValueChangedListener(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                val items = comboBoxItems(comboBox)
                on(comboBox, "value-changed") {
                    if (comboBox.value == "foo") {
                        comboBox.value = "bar"
                    }
                }

                vaadin.click(items[0])

                comboBox.`open`()

                nextRender(comboBox).then {
                    assertEquals("bar", comboBox.value)
                    assertEquals("bar", comboBox.selectedItem.unsafeCast<String>())
                    assertFalse(js("(item) => item.selected")(items[0]) as Boolean)
                    assertTrue(js("(item) => item.selected")(items[1]) as Boolean)
                }
            }
    }

    @Test
    fun allowsClearingTheValueInTheValueChangedListener(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                val item = comboBoxFirstItem(comboBox)!!
                on(comboBox, "value-changed") {
                    if (comboBox.value.isNotEmpty()) {
                        comboBox.value = ""
                    }
                }

                vaadin.click(item)

                assertEquals("", comboBox.value)
                assertTrue(js("(comboBox) => !comboBox.selectedItem")(comboBox) as Boolean)
                assertFalse(js("(item) => item.selected")(item) as Boolean)
                assertFalse(comboBox.hasAttribute("has-value"))
            }
    }

    // `change` event

    @Test
    fun firesValueChangedAndSelectedItemChangedBeforeChange(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        var valueChanged = 0
        var selectedItemChanged = 0
        var order = mutableListOf<String>()
        on(comboBox, "value-changed") {
            valueChanged++
            order += "value"
        }
        on(comboBox, "selected-item-changed") {
            selectedItemChanged++
            order += "selected"
        }
        on(comboBox, "change") { order += "change" }

        return nextRender(comboBox).then {
            comboBox.`open`()
            comboBox.value = "foo"
            comboBox.close()

            assertEquals(1, valueChanged)
            assertEquals(1, selectedItemChanged)
            assertEquals("change", order.last())
        }
    }

    @Test
    fun firesExactlyOneChangeEvent(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        var change = 0
        on(comboBox, "change") { change++ }

        return nextRender(comboBox).then {
            comboBox.`open`()
            comboBox.value = "foo"
            comboBox.close()

            assertEquals(1, change)
        }
    }

    @Test
    fun doesNotFireChangeWhenSettingAValueWithoutCommitting(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        var change = 0
        on(comboBox, "change") { change++ }

        return nextRender(comboBox).then {
            comboBox.value = "foo"

            assertEquals(0, change)
        }
    }

    @Test
    fun firesChangeOnClear(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        comboBox.clearButtonVisible = true
        var change = 0
        on(comboBox, "change") { change++ }

        return nextRender(comboBox).then {
            comboBox.value = "foo"
            vaadin.shadowQuery(comboBox, "[part~=clear-button]")?.let { vaadin.click(it) }

            assertEquals(1, change)
        }
    }

    @Test
    fun doesNotFireChangeUntilClose(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        var change = 0
        on(comboBox, "change") { change++ }

        return nextRender(comboBox).then {
            comboBox.value = "foo"
            comboBox.`open`()
            comboBox.value = "bar"

            assertEquals(0, change)
        }
    }

    @Test
    fun doesNotFireChangeOnCancel(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        var change = 0
        on(comboBox, "change") { change++ }

        return nextRender(comboBox).then {
            comboBox.`open`()
            comboBox.value = "foo"
            comboBox.cancel()

            assertEquals(0, change)
        }
    }

    @Test
    fun doesNotFireChangeForChangesMadeWhileClosed(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        var change = 0
        on(comboBox, "change") { change++ }

        return nextRender(comboBox).then {
            comboBox.value = "foo"

            comboBox.`open`()
            comboBox.close()

            assertEquals(0, change)
        }
    }

    @Test
    fun stopsTheInputChangeEventFromBubbling(): Promise<Unit> {
        val comboBox = newComboBox { items = stringItems("foo", "bar") }
        var change = 0
        on(comboBox, "change") { change++ }

        return nextRender(comboBox).then {
            js(
                "(comboBox) => comboBox.inputElement.dispatchEvent(" +
                    "new Event('change', { bubbles: true, composed: true }))",
            )(comboBox)

            assertEquals(0, change)
        }
    }

    @Test
    fun firesChangeWhenSelectingAnItemViaClick(): Promise<Unit> = openedComboBox().then { comboBox ->
        var change = 0
        on(comboBox, "change") { change++ }

        clickItem(comboBox, 0)

        assertEquals(1, change)
    }

    // selecting a custom value

    @Test
    fun selectsTheExactMatchWhenClosing(): Promise<Unit> {
        val comboBox = newComboBox { allowCustomValue = true; items = stringItems("foo", "bar", "barbar") }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                setComboBoxInput(comboBox, "barbar")
                comboBox.close()

                assertEquals("barbar", comboBox.value)
            }
    }

    @Test
    fun selectsTheFirstOfMultipleMatchesWhenClosing(): Promise<Unit> {
        val comboBox = newComboBox { allowCustomValue = true; items = stringItems("foo", "bar", "barbar") }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                setComboBoxInput(comboBox, "BAR")
                comboBox.close()

                assertEquals("bar", comboBox.value)
            }
    }

    @Test
    fun clearsTheSelectionWhenTheInputIsCleared(): Promise<Unit> {
        val comboBox = newComboBox { allowCustomValue = true; items = stringItems("foo", "bar", "barbar") }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.value = "foo"
                setComboBoxInput(comboBox, "")
                comboBox.close()

                assertEquals("", comboBox.value)
                assertTrue(js("(comboBox) => comboBox.selectedItem === null")(comboBox) as Boolean)
                assertFalse(comboBox.hasAttribute("has-value"))
            }
    }

    @Test
    fun selectsACustomValue(): Promise<Unit> {
        val comboBox = newComboBox { allowCustomValue = true; items = stringItems("foo", "bar", "barbar") }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.value = "foobar"
                comboBox.close()

                assertEquals("foobar", comboBox.value)
                assertNull(comboBox.selectedItem)
                assertEquals("foobar", comboBoxInputValue(comboBox))
                assertTrue(comboBox.hasAttribute("has-value"))
            }
    }

    @Test
    fun clearsTheCustomValueOnClear(): Promise<Unit> {
        val comboBox = newComboBox { allowCustomValue = true; items = stringItems("foo", "bar", "barbar") }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.value = "foobar"
                setComboBoxInput(comboBox, "")
                comboBox.close()

                assertEquals("", comboBox.value)
                assertTrue(js("(comboBox) => comboBox.selectedItem === null")(comboBox) as Boolean)
            }
    }

    @Test
    fun allowsChangingToAnExistingItemWhenACustomValueIsSet(): Promise<Unit> {
        val comboBox = newComboBox { allowCustomValue = true; items = stringItems("foo", "bar", "barbar") }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.value = "foobar"
                comboBox.close()

                comboBox.value = "foo"

                assertEquals("foo", comboBox.value)
                assertEquals("foo", comboBox.selectedItem.unsafeCast<String>())
            }
    }
}
