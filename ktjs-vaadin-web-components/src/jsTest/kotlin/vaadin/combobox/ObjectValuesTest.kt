package vaadin.combobox

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import web.html.HTMLElement

/**
 * Translated from `packages/combo-box/test/object-values.test.js`.
 *
 * Dropped upstream cases: the ones that override `toString` on a fixture item (`comboBox.items[0].toString = …`),
 * which a `JsAny` item cannot express; the `blur()` case, the `console.error` stub, and the overlay
 * `display` assertion.
 *
 * The fixture values `0` and `false` are asserted with a strict JS comparison: the wrapper types
 * `value` as `String`, but the component really stores the raw item value in that case.
 */
class ObjectValuesTest {
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

    private fun itemValues(): Array<JsAny> = jsonItems(
        "[{\"label\": \"foo\", \"custom\": \"bazs\", \"value\": \"bar\"}," +
            " {\"label\": \"baz\", \"custom\": \"bashcsdfsa\", \"value\": \"qux\"}," +
            " {\"label\": \"zero\", \"custom\": \"zero-custom\", \"value\": 0}," +
            " {\"label\": \"false\", \"custom\": \"false-custom\", \"value\": false}," +
            " {\"label\": \"empty string\", \"custom\": \"empty-string-custom\", \"value\": \"\"}," +
            " {\"label\": \"zero as a string\", \"custom\": \"zero-string-custom\", \"value\": \"0\"}," +
            " {\"label\": \"duplicate value 1\", \"value\": \"duplicate\"}," +
            " {\"label\": \"duplicate value 2\", \"value\": \"duplicate\"}," +
            " {\"label\": \"missing value 1\"}," +
            " {\"label\": \"missing value 2\"}," +
            " {\"label\": \"duplicate label\", \"value\": \"duplicate-label-1\"}," +
            " {\"label\": \"duplicate label\", \"value\": \"duplicate-label-2\"}]",
    )

    private fun opened(): Promise<ComboBoxElement> {
        val comboBox = newComboBox()
        comboBox.items = itemValues()
        comboBox.`open`()
        return nextRender(comboBox).then { comboBox }
    }

    private fun clickItem(comboBox: ComboBoxElement, index: Int) {
        comboBoxItems(comboBox)[index].let { vaadin.click(it) }
    }

    // label and value paths

    @Test
    fun changesTheValueWhenTheValuePathChanges(): Promise<Unit> = opened().then { comboBox ->
        clickItem(comboBox, 0)
        comboBox.itemValuePath = "custom"

        assertEquals("bazs", comboBox.value)
    }

    @Test
    fun usesTheDefaultLabelPropertyOnTheInput(): Promise<Unit> = opened().then { comboBox ->
        clickItem(comboBox, 0)

        assertEquals("foo", comboBoxInputValue(comboBox))
    }

    @Test
    fun usesTheDefaultLabelPropertyInTheDropdown(): Promise<Unit> = opened().then { comboBox ->
        assertTrue(comboBoxFirstItem(comboBox)?.textContent?.contains("foo") == true)
    }

    @Test
    fun usesTheProvidedLabelProperty(): Promise<Unit> = opened().then { comboBox ->
        comboBox.itemLabelPath = "custom"
        comboBox.value = "bar"

        assertEquals("bazs", comboBoxInputValue(comboBox))
    }

    @Test
    fun usesTheDefaultValueProperty(): Promise<Unit> = opened().then { comboBox ->
        clickItem(comboBox, 0)

        assertEquals("bar", comboBox.value)
    }

    @Test
    fun usesTheProvidedValueProperty(): Promise<Unit> = opened().then { comboBox ->
        comboBox.itemValuePath = "custom"
        clickItem(comboBox, 1)

        assertEquals("bashcsdfsa", comboBox.value)
    }

    @Test
    fun setsTheSelectedItemAndInputWhenOpen(): Promise<Unit> = opened().then { comboBox ->
        comboBox.value = "bar"

        assertSame(comboBox.items!![0], comboBox.selectedItem)
        assertEquals("foo", comboBoxInputValue(comboBox))
    }

    @Test
    fun setsTheSelectedItemAndInputWhenClosed(): Promise<Unit> = opened().then { comboBox ->
        comboBox.opened = false
        comboBox.value = "bar"

        assertSame(comboBox.items!![0], comboBox.selectedItem)
        assertEquals("foo", comboBoxInputValue(comboBox))
    }

    @Test
    fun setsTheValueOnSelection(): Promise<Unit> = opened().then { comboBox ->
        clickItem(comboBox, 0)

        assertEquals("foo", comboBoxInputValue(comboBox))
        assertEquals("bar", comboBox.value)
    }

    @Test
    fun setsTheValueEvenWhenItIsTheNumberZero(): Promise<Unit> = opened().then { comboBox ->
        clickItem(comboBox, 2)

        assertEquals("zero", comboBoxInputValue(comboBox))
        assertTrue(js("(comboBox) => comboBox.value === 0")(comboBox) as Boolean)
    }

    @Test
    fun setsTheValueEvenWhenItIsTheBooleanFalse(): Promise<Unit> = opened().then { comboBox ->
        clickItem(comboBox, 3)

        assertEquals("false", comboBoxInputValue(comboBox))
        assertTrue(js("(comboBox) => comboBox.value === false")(comboBox) as Boolean)
    }

    @Test
    fun setsTheValueEvenWhenItIsAnEmptyString(): Promise<Unit> = opened().then { comboBox ->
        clickItem(comboBox, 4)

        assertEquals("empty string", comboBoxInputValue(comboBox))
        assertEquals("", comboBox.value)
        assertTrue(comboBox.hasAttribute("has-value"))
    }

    @Test
    fun distinguishesTheNumberZeroFromTheStringZero(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = itemValues()
        comboBox.`open`()

        return nextRender(comboBox)
            .then {
                clickItem(comboBox, 2)
                assertEquals("zero", comboBoxInputValue(comboBox))
                assertTrue(js("(comboBox) => comboBox.value === 0")(comboBox) as Boolean)

                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                clickItem(comboBox, 5)

                assertEquals("zero as a string", comboBoxInputValue(comboBox))
                assertEquals("0", comboBox.value)
            }
    }

    @Test
    fun setsTheInputValueFromTheItemLabelWhenTheItemIsFound(): Promise<Unit> = opened().then { comboBox ->
        comboBox.value = "bar"

        assertEquals("foo", comboBoxInputValue(comboBox))
    }

    @Test
    fun selectsTheFirstOfDuplicateValues(): Promise<Unit> = opened().then { comboBox ->
        comboBox.value = "duplicate"

        assertSame(comboBox.items!![6], comboBox.selectedItem)
    }

    @Test
    fun selectsTheCorrectDuplicateValue(): Promise<Unit> = opened().then { comboBox ->
        var selectedItemChanged = 0
        vaadin.on(comboBox, "selected-item-changed") { selectedItemChanged++ }

        clickItem(comboBox, 7)

        assertSame(comboBox.items!![7], comboBox.selectedItem)
        assertEquals("duplicate", comboBox.value)
        assertEquals("duplicate value 2", comboBoxInputValue(comboBox))
        assertEquals(1, selectedItemChanged)
    }

    @Test
    fun selectsTheFirstOfDuplicateLabels(): Promise<Unit> = opened().then { comboBox ->
        comboBox.value = "duplicate-label-1"

        assertSame(comboBox.items!![10], comboBox.selectedItem)
    }

    @Test
    fun selectsTheSecondOfDuplicateLabels(): Promise<Unit> = opened().then { comboBox ->
        comboBox.value = "duplicate-label-2"

        assertSame(comboBox.items!![11], comboBox.selectedItem)
    }

    @Test
    fun selectsTheFirstOfDuplicateLabelsManually(): Promise<Unit> = opened().then { comboBox ->
        var selectedItemChanged = 0
        vaadin.on(comboBox, "selected-item-changed") { selectedItemChanged++ }

        clickItem(comboBox, 10)

        assertSame(comboBox.items!![10], comboBox.selectedItem)
        assertEquals("duplicate-label-1", comboBox.value)
        assertEquals("duplicate label", comboBoxInputValue(comboBox))
        assertEquals(1, selectedItemChanged)
    }

    @Test
    fun selectsTheSecondOfDuplicateLabelsManually(): Promise<Unit> = opened().then { comboBox ->
        var selectedItemChanged = 0
        vaadin.on(comboBox, "selected-item-changed") { selectedItemChanged++ }

        clickItem(comboBox, 11)

        assertSame(comboBox.items!![11], comboBox.selectedItem)
        assertEquals("duplicate-label-2", comboBox.value)
        assertEquals("duplicate label", comboBoxInputValue(comboBox))
        assertEquals(1, selectedItemChanged)
    }

    // custom values

    @Test
    fun clearsTheInputWhenTheItemIsNotFoundAndCustomValuesAreNotAllowed(): Promise<Unit> = opened().then { comboBox ->
        comboBox.allowCustomValue = false
        comboBox.value = "bar"

        comboBox.value = "not found"

        assertEquals("", comboBoxInputValue(comboBox))
    }

    @Test
    fun setsTheValueAsBindingValueWhenCustomValuesAreAllowed(): Promise<Unit> = opened().then { comboBox ->
        comboBox.allowCustomValue = true
        comboBox.value = "not found"

        assertEquals("not found", comboBoxInputValue(comboBox))
    }

    @Test
    fun setsTheInputValueUsingTheProvidedLabelPathForCustomValues(): Promise<Unit> = opened().then { comboBox ->
        comboBox.allowCustomValue = true
        comboBox.itemLabelPath = "custom"
        comboBox.value = "bar"

        assertEquals("bazs", comboBoxInputValue(comboBox))
    }
}
