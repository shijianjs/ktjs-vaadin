package vaadin.combobox

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
import vaadin.nextRender
import vaadin.nextUpdate
import web.html.HTMLElement

/**
 * Translated from the public parts of `packages/combo-box/test/basic.test.js`.
 *
 * Dropped upstream cases: everything that reads the dropdown internals (`comboBox.$.overlay`,
 * `comboBox._scroller`, `_focusedIndex`), that drives the overlay through real key sequences
 * (`escKeyDown`, `arrowDownKeyDown`) or that asserts autoselect behaviour after opening and closing
 * the dropdown. The same internals make the `custom-value-set` cases of `events.test.js`
 * unreachable: committing a custom value requires the overlay close flow, which is not reproduced
 * through the public API. The items / value / selectedItem / pattern / autoselect defaults and the
 * "value set before attach" case are translated.
 */
class BasicsTest {
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

    private fun items(vararg values: String): Array<Any> = js("(values) => values")(arrayOf(*values)).unsafeCast<Array<Any>>()

    private fun inputValue(comboBox: ComboBoxElement): String =
        js("(cb) => cb.inputElement.value")(comboBox) as String

    private fun selectedItem(comboBox: ComboBoxElement): Any? = js("(cb) => cb.selectedItem")(comboBox)

    // items property

    @Test
    fun itemsAreUndefinedByDefault(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox).then {
            assertTrue(js("(cb) => cb.items === undefined")(comboBox) as Boolean)
        }
    }

    @Test
    fun supportsResettingItems(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.items = items("foo", "bar")
                comboBox.items = null
                assertTrue(js("(cb) => cb.items === undefined || cb.items === null")(comboBox) as Boolean)
            }
    }

    // value property

    @Test
    fun valueIsEmptyStringByDefault(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox).then { assertEquals("", comboBox.value) }
    }

    @Test
    fun valueCanBeSetBeforeItems(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.value = "foo"
                comboBox.items = items("foo", "bar")
                nextUpdate(comboBox)
            }
            .then {
                assertEquals("foo", selectedItem(comboBox))
                assertEquals("foo", inputValue(comboBox))
            }
    }

    @Test
    fun supportsObjectItemsSetBeforeValue(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.value = "1"
                comboBox.items = js("[{ label: 'foo', value: '1' }]").unsafeCast<Array<Any>>()
                nextUpdate(comboBox)
            }
            .then {
                assertEquals("1", js("(cb) => cb.selectedItem.value")(comboBox) as String)
                assertEquals("foo", inputValue(comboBox))
            }
    }

    // selectedItem property

    @Test
    fun selectedItemIsUndefinedByDefault(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.items = items("foo")
                nextUpdate(comboBox)
                assertTrue(js("(cb) => cb.selectedItem === undefined")(comboBox) as Boolean)
            }
    }

    @Test
    fun settingSelectedItemSetsValueAndInput(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.items = items("foo")
                comboBox.selectedItem = js("'foo'")
                nextUpdate(comboBox)
            }
            .then {
                assertEquals("foo", comboBox.value)
                assertEquals("foo", inputValue(comboBox))
            }
    }

    @Test
    fun resetsSelectedItemToNullWhenValueIsSetToNull(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.items = items("foo")
                comboBox.value = "foo"
                nextUpdate(comboBox)
            }
            .then {
                js("(cb) => { cb.value = null; }")(comboBox)
                nextUpdate(comboBox)
            }
            .then { assertTrue(js("(cb) => cb.selectedItem === null")(comboBox) as Boolean) }
    }

    // autoselect property

    @Test
    fun autoselectIsFalseByDefault(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox).then { assertFalse(comboBox.autoselect) }
    }

    // pattern property

    @Test
    fun patternValidationFailsForANonMatchingCustomValue(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.allowCustomValue = true
                comboBox.pattern = "[0-9]*"
                comboBox.value = "foo"
                nextUpdate(comboBox)
            }
            .then { assertFalse(comboBox.validate()) }
    }

    // value set before attach

    @Test
    fun setsTheInputValueWhenAddedToTheDom(): Promise<Unit> {
        registerComboBox()
        val comboBox = createComboBox()
        comboBox.items = items("a", "b")
        comboBox.value = "a"
        attach(comboBox)

        return nextRender(comboBox).then { assertEquals("a", inputValue(comboBox)) }
    }
}
