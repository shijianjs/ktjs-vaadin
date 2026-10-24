package vaadin.combobox

import kotlin.js.JsAny
import kotlin.js.Promise
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
import web.html.HTMLElement

/**
 * Translated from `packages/combo-box/test/internal-filtering.test.js`.
 *
 * The upstream suite closes the dropdown with `outsideClick()`; [ComboBoxElement.close] runs the same
 * commit path, so it is used here.
 *
 * Dropped upstream cases: the ones that read `_focusedIndex` (`getFocusedItemIndex`) or the scroller
 * (`_scroller.selectedItem`, `_scrollIntoView`), the overlay-geometry assertions
 * (`$.overlay.opened`), and the placeholder-item case that constructs `ComboBoxPlaceholder`
 * instances directly.
 */
class InternalFilteringTest {
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

    private fun newComboBox(configure: ComboBoxElement.() -> Unit = {}): ComboBoxElement = attach(createComboBox(configure))

    private fun stringComboBox(): ComboBoxElement = newComboBox().apply { items = stringItems("foo", "bar", "baz") }

    // value is set before the items

    @Test
    fun hasTheSelectedItemWhenTheValueIsSetBefore(): Promise<Unit> {
        val comboBox = newComboBox { value = "foo" }

        return nextRender(comboBox)
            .then {
                comboBox.items = stringItems("foo", "bar")

                assertEquals("foo", comboBox.selectedItem.unsafeCast<String>())
                assertEquals("foo", comboBoxInputValue(comboBox))
            }
    }

    // setting the input field value

    @Test
    fun filtersItemsWhenTheFilterChanges(): Promise<Unit> {
        val comboBox = stringComboBox()
        comboBox.value = "foo"

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                setComboBoxInput(comboBox, "foo ")

                assertEquals(0, comboBox.filteredItems?.size)
            }
    }

    @Test
    fun doesNotChangeTheValueOnInput(): Promise<Unit> {
        val comboBox = stringComboBox()
        comboBox.value = "foo"

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "bar")

                assertEquals("foo", comboBox.value)
            }
    }

    @Test
    fun setsTheValueWhenClosing(): Promise<Unit> {
        val comboBox = stringComboBox()
        comboBox.value = "foo"

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "bar")
                comboBox.close()

                assertEquals("bar", comboBox.value)
            }
    }

    @Test
    fun setsTheValueCaseInsensitivelyWhenClosing(): Promise<Unit> {
        val comboBox = stringComboBox()
        comboBox.value = "foo"

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "BAR")
                comboBox.close()

                assertEquals("bar", comboBox.value)
            }
    }

    @Test
    fun revertsTheInputWhenCancelled(): Promise<Unit> {
        val comboBox = stringComboBox()
        comboBox.value = "foo"

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "b")
                comboBox.cancel()

                assertEquals("foo", comboBoxInputValue(comboBox))
            }
    }

    @Test
    fun revertsTheInputWhenClosingWithTheSameValue(): Promise<Unit> {
        val comboBox = stringComboBox()
        comboBox.value = "foo"

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "FOO")
                comboBox.close()

                assertEquals("foo", comboBoxInputValue(comboBox))
            }
    }

    // filtering items

    @Test
    fun filtersItemsUsingContains(): Promise<Unit> {
        val comboBox = stringComboBox()

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "a")

                assertEquals(listOf("bar", "baz"), comboBox.filteredItems!!.map { it.unsafeCast<String>() })
            }
    }

    @Test
    fun filtersOutAllItemsWithAnInvalidFilter(): Promise<Unit> {
        val comboBox = stringComboBox()

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "qux")

                assertEquals(0, comboBox.filteredItems?.size)
            }
    }

    @Test
    fun resetsTheFilterAfterClosing(): Promise<Unit> {
        val comboBox = stringComboBox()

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "foo")
                comboBox.close()

                assertEquals("", comboBox.filter)
            }
    }

    @Test
    fun filtersBooleanItems(): Promise<Unit> {
        val comboBox = newComboBox { items = booleanItems() }

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "t")

                val filtered = comboBox.filteredItems
                assertEquals(1, filtered?.size)
                assertTrue(js("(value) => value === true")(filtered!![0]) as Boolean)
            }
    }

    @Test
    fun ignoresCase(): Promise<Unit> {
        val comboBox = stringComboBox()

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "FOO")

                assertEquals(listOf("foo"), comboBox.filteredItems!!.map { it.unsafeCast<String>() })
            }
    }

    @Test
    fun doesNotFilterWithANullFilter(): Promise<Unit> {
        val comboBox = stringComboBox()

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "")

                assertEquals(3, comboBox.filteredItems?.size)
            }
    }

    @Test
    fun doesNotThrowWhenItemsAreNotSet(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.items = null
                setComboBoxInput(comboBox, "foo")

                assertNull(comboBox.filteredItems)
            }
    }

    @Test
    fun resetsTheFilterOnValueChange(): Promise<Unit> {
        val comboBox = stringComboBox()

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "bar")
                assertEquals("bar", comboBox.filter)

                comboBox.value = "foo"
                assertEquals("", comboBox.filter)
            }
    }

    @Test
    fun resetsTheFilterOnSelectedItemChange(): Promise<Unit> {
        val comboBox = stringComboBox()

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "bar")
                assertEquals("bar", comboBox.filter)

                comboBox.selectedItem = "foo".unsafeCast<JsAny>()
                assertEquals("", comboBox.filter)
            }
    }

    // setting items while opened

    @Test
    fun displaysAllItemsWhenFilteredItemsAreAssignedWhileOpened(): Promise<Unit> {
        val comboBox = newComboBox { items = emptyArray<JsAny>() }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                comboBox.filteredItems = makeItems(10)
                nextRender(comboBox)
            }
            .then { assertEquals(10, comboBoxItems(comboBox).size) }
    }

    private fun booleanItems(): Array<JsAny> =
        js("(values) => values")(arrayOf(true, false)).unsafeCast<Array<JsAny>>()
}
