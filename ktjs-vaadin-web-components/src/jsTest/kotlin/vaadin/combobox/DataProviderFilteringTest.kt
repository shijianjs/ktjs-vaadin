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
import vaadin.timeout
import web.html.HTMLElement

/**
 * Translated from `packages/combo-box/test/data-provider-filtering.test.js`.
 *
 * The upstream "dropdown behaviour" group asserts `comboBox.$.overlay.opened`, which is a protected
 * member; the dropdown state that is observable through the wrapper is the `opened` property of the
 * host plus the `vaadin-combo-box-dropdown-opened` event, so the cases below assert those. The
 * `should close when there are no items after filtering` and `should not toggle between opened and
 * closed when clearing cache while loading` cases are dropped for the same reason — they assert on
 * the overlay element only.
 */
class DataProviderFilteringTest {
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

    private class Recorder {
        val params = mutableListOf<ComboBoxDataProviderParams>()
        fun clear() = params.clear()
    }

    /** The async provider of the upstream "basic" group: answers on the next macrotask. */
    private fun asyncProvider(recorder: Recorder): ComboBoxDataProvider = { params, callback ->
        recorder.params += params
        val items = listOf("Item 1", "Item 2", "Item 3").filter { it.contains(params.filter) }
        timeout(0).then {
            callback(items.map { it.unsafeCast<JsAny>() }.toTypedArray(), items.size.toDouble())
        }
    }

    private fun syncProvider(recorder: Recorder): ComboBoxDataProvider = { params, callback ->
        recorder.params += params
        val items = listOf("Item 1", "Item 2", "Item 3").filter { it.contains(params.filter) }
        callback(items.map { it.unsafeCast<JsAny>() }.toTypedArray(), items.size.toDouble())
    }

    // basic

    @Test
    fun requestsWithAnEmptyFilterWhenOpened(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = asyncProvider(recorder)

        comboBox.opened = true

        return nextRender(comboBox)
            .then { timeout(0) }
            .then { assertEquals("", recorder.params.last().filter) }
    }

    @Test
    fun requestsWithTheEnteredFilterOnFilterChange(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = asyncProvider(recorder)
        comboBox.opened = true

        return nextRender(comboBox)
            .then { timeout(0) }
            .then {
                recorder.clear()
                setComboBoxInput(comboBox, "Item")

                assertEquals("Item", recorder.params.last().filter)
            }
    }

    @Test
    fun requestsWithAnEmptyFilterOnValueChange(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = asyncProvider(recorder)
        comboBox.opened = true

        return nextRender(comboBox)
            .then { timeout(0) }
            .then {
                setComboBoxInput(comboBox, "Item")
                timeout(0)
            }
            .then {
                recorder.clear()
                comboBox.value = "Item 2"

                assertEquals("", recorder.params.last().filter)
            }
    }

    @Test
    fun requestsWithAnEmptyFilterAfterPartialFilterCancelAndReopen(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = asyncProvider(recorder)
        comboBox.opened = true

        return nextRender(comboBox)
            .then { timeout(0) }
            .then {
                setComboBoxInput(comboBox, "It")
                timeout(0)
            }
            .then {
                recorder.clear()
                comboBox.cancel()
                comboBox.opened = true
                timeout(0)
            }
            .then { assertEquals("", recorder.params.last().filter) }
    }

    @Test
    fun clearsFilteredItemsOnFilterChange(): Promise<Unit> {
        val comboBox = newComboBox()
        // The async provider answers on the next macrotask, so the filtered items are still cleared
        // when the input change returns.
        comboBox.dataProvider = asyncProvider(Recorder())
        comboBox.opened = true

        return nextRender(comboBox)
            .then {
                setComboBoxInput(comboBox, "Item")

                assertEquals(0, comboBox.filteredItems?.size)
            }
    }

    @Test
    fun updatesFilteredItemsOnFilterResponse(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = asyncProvider(Recorder())
        comboBox.opened = true

        return nextRender(comboBox)
            .then { timeout(0) }
            .then {
                setComboBoxInput(comboBox, "Item 1")
                timeout(0)
            }
            .then {
                assertEquals(
                    listOf("Item 1"),
                    comboBox.filteredItems!!.map { it.unsafeCast<String>() },
                )
            }
    }

    @Test
    fun clearsTheFilterOnValueClear(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = asyncProvider(Recorder())
        comboBox.opened = true

        return nextRender(comboBox)
            .then { timeout(0) }
            .then {
                setComboBoxInput(comboBox, "Item")
                timeout(0)
            }
            .then {
                comboBox.value = "Item 1"
                comboBox.value = ""

                assertEquals("", comboBox.filter)
            }
    }

    @Test
    fun clearsTheFilterOnClose(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = asyncProvider(Recorder())
        comboBox.opened = true

        return nextRender(comboBox)
            .then { timeout(0) }
            .then {
                setComboBoxInput(comboBox, "Item")
                timeout(0)
            }
            .then {
                comboBox.opened = false

                assertEquals("", comboBox.filter)
            }
    }

    // dropdown behaviour

    @Test
    fun doesNotToggleTheDropdownWhenFiltering(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = syncProvider(Recorder())
        comboBox.opened = true
        var dropdownOpened = 0
        vaadin.on(comboBox, "vaadin-combo-box-dropdown-opened") { dropdownOpened++ }

        return nextRender(comboBox).then {
            comboBox.filter = "Item"

            assertEquals(0, dropdownOpened)
        }
    }

    @Test
    fun doesNotToggleTheDropdownWhenSettingAValue(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = syncProvider(Recorder())
        comboBox.opened = true
        var dropdownOpened = 0
        vaadin.on(comboBox, "vaadin-combo-box-dropdown-opened") { dropdownOpened++ }

        return nextRender(comboBox).then {
            comboBox.filter = "Item"
            comboBox.value = "Item 1"

            assertEquals(0, dropdownOpened)
        }
    }

    // opening on input

    @Test
    fun opensTheHostWithoutMatchingItemsWithASyncProvider(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()

        return nextRender(comboBox).then {
            comboBox.dataProvider = syncProvider(recorder)
            setComboBoxInput(comboBox, "no match")

            // Only the host state is reachable through the wrapper: the empty result set keeps the
            // dropdown itself closed, which the upstream case asserts on the overlay.
            assertTrue(comboBox.opened)
        }
    }

    @Test
    fun opensWhileLoadingWithAnAsyncProvider(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = asyncProvider(recorder)

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "Item")

            assertEquals(1, recorder.params.size)
            assertTrue(comboBox.loading)
            assertTrue(comboBox.opened)
        }
    }
}
