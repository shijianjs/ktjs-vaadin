package vaadin.combobox

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import web.html.HTMLElement

/**
 * Translated from `packages/combo-box/test/external-filtering.test.js`.
 *
 * The upstream suite commits the typed input with `enter(comboBox.inputElement)`; the same single
 * `Enter` keydown is dispatched here through [pressEnterInComboBox].
 *
 * Dropped upstream cases: every assertion on the focused dropdown item (`getFocusedItemIndex` reads
 * `_focusedIndex` through the scroller items) and the `blur()` case, which needs a real focus move.
 */
class ExternalFilteringTest {
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

    // basic

    @Test
    fun setsItemsToFilteredItems() {
        val comboBox = newComboBox()
        comboBox.filteredItems = stringItems("foo", "bar", "baz")

        comboBox.items = stringItems("foo")

        assertEquals(listOf("foo"), comboBox.filteredItems!!.map { it.unsafeCast<String>() })
    }

    @Test
    fun doesNotFilterItems(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.filteredItems = stringItems("foo", "bar", "baz")

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                setComboBoxInput(comboBox, "foo")

                assertEquals(listOf("foo", "bar", "baz"), comboBoxItemTexts(comboBox))
            }
    }

    @Test
    fun doesNotHideTheDropdownWhileLoading(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.filteredItems = stringItems("foo", "bar", "baz")

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "foo")
                comboBox.loading = true
                nextRender(comboBox)
            }
            .then { assertTrue(comboBox.opened) }
    }

    @Test
    fun refreshesItemsAfterReassignment(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.filteredItems = stringItems("foo", "bar", "baz")

        return nextRender(comboBox)
            .then {
                comboBox.opened = true
                nextRender(comboBox)
            }
            .then {
                comboBox.filteredItems = stringItems("foo")
                nextRender(comboBox)
            }
            .then { assertEquals(listOf("foo"), comboBoxItemTexts(comboBox)) }
    }

    @Test
    fun togglesTheLoadingAttributeOnTheHost() {
        val comboBox = newComboBox()

        comboBox.loading = true
        assertTrue(comboBox.hasAttribute("loading"))

        comboBox.loading = false
        assertFalse(comboBox.hasAttribute("loading"))
    }

    // requestContentUpdate

    @Test
    fun rerendersItemsAfterMutatingFilteredItems(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.filteredItems = stringItems("Item 0")

        return nextRender(comboBox)
            .then {
                comboBox.opened = true
                nextRender(comboBox)
            }
            .then {
                comboBox.filteredItems!![0] = "New Item".unsafeCast<JsAny>()
                comboBox.requestContentUpdate()
                nextRender(comboBox)
            }
            .then { assertEquals(listOf("New Item"), comboBoxItemTexts(comboBox)) }
    }

    @Test
    fun rerendersItemsAfterAddingMoreFilteredItems(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.filteredItems = stringItems("Item 0")

        return nextRender(comboBox)
            .then {
                comboBox.opened = true
                nextRender(comboBox)
            }
            .then {
                comboBox.filteredItems = stringItems("Item 0", "Item 1")
                comboBox.requestContentUpdate()
                nextRender(comboBox)
            }
            .then { assertEquals(listOf("Item 0", "Item 1"), comboBoxItemTexts(comboBox)) }
    }

    // filteredItems as an attribute

    @Test
    fun doesNotThrowWithFilteredItemsAndValueAsAttributes(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.setAttribute("filtered-items", "[\"a\", \"b\", \"c\"]")
        comboBox.setAttribute("value", "b")

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                assertEquals(3, comboBoxItems(comboBox).size)
                assertEquals("b", comboBox.value)
            }
    }

    // value is set after the filtered items

    @Test
    fun commitsTheFilteredValue(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.filteredItems = stringItems("foo", "bar")
                comboBox.value = "foo"
                setComboBoxInput(comboBox, "bar")
                pressEnterInComboBox(comboBox)
            }
            .then { assertEquals("bar", comboBox.value) }
    }

    @Test
    fun commitsTheEmptyValue(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.filteredItems = stringItems("foo", "bar")
                comboBox.value = "foo"
                setComboBoxInput(comboBox, "")
                pressEnterInComboBox(comboBox)
            }
            .then { assertEquals("", comboBox.value) }
    }

    // value is set before the filtered items

    @Test
    fun hasTheSelectedItemWhenTheValueIsSetBefore(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.value = "foo"

        return nextRender(comboBox)
            .then {
                comboBox.filteredItems = stringItems("foo", "bar")

                assertEquals("foo", comboBox.selectedItem.unsafeCast<String>())
                assertEquals("foo", comboBoxInputValue(comboBox))
            }
    }

    // value is set before the filtered items, auto open disabled

    @Test
    fun hasTheSelectedItemWithAutoOpenDisabled(): Promise<Unit> {
        val comboBox = newComboBox { autoOpenDisabled = true }
        comboBox.value = "bar"

        return nextRender(comboBox)
            .then {
                comboBox.filteredItems = stringItems("foo", "bar")

                assertEquals("bar", comboBox.selectedItem.unsafeCast<String>())
                assertEquals("bar", comboBoxInputValue(comboBox))
            }
    }

    @Test
    fun commitsTheFilteredValueWithAutoOpenDisabled(): Promise<Unit> {
        val comboBox = newComboBox { autoOpenDisabled = true }

        return nextRender(comboBox)
            .then {
                comboBox.filteredItems = stringItems("foo", "bar")
                setComboBoxInput(comboBox, "foo")
                pressEnterInComboBox(comboBox)
            }
            .then { assertEquals("foo", comboBox.value) }
    }

    @Test
    fun commitsAnEmptyValueWithAutoOpenDisabled(): Promise<Unit> {
        val comboBox = newComboBox { autoOpenDisabled = true }

        return nextRender(comboBox)
            .then {
                comboBox.filteredItems = stringItems("foo", "bar")
                setComboBoxInput(comboBox, "")
                pressEnterInComboBox(comboBox)
            }
            .then { assertEquals("", comboBox.value) }
    }

    @Test
    fun commitsACustomValueWhenCustomValuesAreAllowed(): Promise<Unit> {
        val comboBox = newComboBox { autoOpenDisabled = true }

        return nextRender(comboBox)
            .then {
                comboBox.allowCustomValue = true
                comboBox.filteredItems = stringItems("foo", "bar")
                setComboBoxInput(comboBox, "custom")
                pressEnterInComboBox(comboBox)
            }
            .then { assertEquals("custom", comboBox.value) }
    }

    // selectedItem is set before the filtered items

    @Test
    fun setsTheValueAndTheInputFromTheSelectedItem(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.selectedItem = json("{\"label\": \"Item 0\", \"value\": \"0\"}")
                comboBox.filteredItems = jsonItems(
                    "[{\"label\": \"Item 0\", \"value\": \"0\"}," +
                        " {\"label\": \"Item 1\", \"value\": \"1\"}," +
                        " {\"label\": \"Item 2\", \"value\": \"2\"}]",
                )
                nextRender(comboBox)
            }
            .then {
                assertEquals("0", comboBox.value)
                assertEquals("Item 0", comboBoxInputValue(comboBox))
            }
    }
}
