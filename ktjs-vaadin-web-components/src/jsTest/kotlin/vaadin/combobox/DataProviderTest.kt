package vaadin.combobox

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.timeout
import web.html.HTMLElement

/**
 * Translated from `packages/combo-box/test/data-provider.test.js`.
 *
 * Every case that requests a page through scrolling is dropped: the upstream suite drives the
 * virtualizer with the protected `_scrollIntoView()` / `_focusedIndex` and waits for the
 * `__virtualizer` to flush, none of which is reachable through the public API.
 *
 * Also dropped:
 * - `should not select placeholder items` and the placeholder assertions of `clearCache when open`
 *   that check `instanceof ComboBoxPlaceholder` on recycled items — the item element exposes `item`
 *   only through the protected scroller element type here.
 * - `lost focus before data is returned`: the upstream cases press Tab with `sendKeys` and then rely
 *   on the focus/blur ordering inside the component.
 * - the `console.warn` cases of the `value` suites: the check is about a developer warning that is
 *   emitted through the console, not about component state.
 */
class DataProviderTest {
    private val attached = mutableListOf<HTMLElement>()

    private val allItems: List<String> = List(SIZE) { "item $it" }

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

    private fun strings(values: List<String>): Array<JsAny?> = values.map { it.unsafeCast<JsAny>() }.toTypedArray()

    /** Records every page request of the synchronous string data provider used by most cases. */
    private class Recorder {
        val params = mutableListOf<ComboBoxDataProviderParams>()
        val filters: List<String> get() = params.map { it.filter }
        val pages: List<Double> get() = params.map { it.page }

        fun clear() = params.clear()
    }

    private fun stringProvider(recorder: Recorder): ComboBoxDataProvider = { params, callback ->
        recorder.params += params
        val filtered = allItems.filter { it.contains(params.filter) }
        val offset = (params.page * params.pageSize).toInt()
        val page = filtered.drop(offset).take(params.pageSize.toInt())
        callback(strings(page), filtered.size.toDouble())
    }

    private fun objectProvider(): ComboBoxDataProvider = { params, callback ->
        val offset = (params.page * params.pageSize).toInt()
        val count = minOf(offset + params.pageSize.toInt(), SIZE) - offset
        val items = List(count) { index ->
            json("{\"id\": $index, \"value\": \"value $index\", \"label\": \"label $index\"}")
        }
        callback(items.toTypedArray(), SIZE.toDouble())
    }

    // default

    @Test
    fun isNotInvokedWhenSet() {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = stringProvider(recorder)

        assertEquals(0, recorder.params.size)
    }

    @Test
    fun isInvokedOnOpen() {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = stringProvider(recorder)

        comboBox.opened = true

        assertEquals(1, recorder.params.size)
    }

    @Test
    fun isInvokedOnOpenWithSizeSet() {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.size = SIZE.toDouble()
        comboBox.dataProvider = stringProvider(recorder)

        comboBox.opened = true

        assertEquals(1, recorder.params.size)
    }

    @Test
    fun doesNotThrowWithALargeSize() {
        val comboBox = newComboBox()

        comboBox.size = 500000.0
    }

    @Test
    fun throwsIfSetAfterItems() {
        val comboBox = newComboBox()
        comboBox.items = stringItems("foo")
        val recorder = Recorder()

        assertFailsWith<Throwable> { comboBox.dataProvider = stringProvider(recorder) }

        assertEquals(0, recorder.params.size)
        assertTrue(js("(comboBox) => comboBox.dataProvider === undefined")(comboBox) as Boolean)
    }

    @Test
    fun throwsIfItemsAreSetAfter() {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())

        assertFailsWith<Throwable> { comboBox.items = stringItems("foo") }

        assertTrue(js("(comboBox) => comboBox.items === undefined")(comboBox) as Boolean)
    }

    @Test
    fun clearsTheValueOnClearButtonPressBeforeOpening(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())
        comboBox.selectedItem = "item 0".unsafeCast<JsAny>()
        comboBox.clearButtonVisible = true

        val clearButton = vaadin.shadowQuery(comboBox, "[part~=clear-button]")
        clearButton?.let { vaadin.click(it) }
        comboBox.opened = true

        return nextRender(comboBox).then {
            assertEquals("", comboBox.value)
            assertNull(comboBox.selectedItem)
        }
    }

    // when autoOpenDisabled

    @Test
    fun isInvokedOnOpenWhenAutoOpenIsDisabled() {
        val comboBox = newComboBox()
        comboBox.autoOpenDisabled = true
        focusInput(comboBox)
        val recorder = Recorder()
        comboBox.dataProvider = stringProvider(recorder)
        recorder.clear()

        comboBox.opened = true

        assertEquals(1, recorder.params.size)
    }

    @Test
    fun isInvokedWithTheFilterWhenFilteringWithAutoOpenDisabled() {
        val comboBox = newComboBox()
        comboBox.autoOpenDisabled = true
        focusInput(comboBox)
        val recorder = Recorder()
        comboBox.dataProvider = stringProvider(recorder)
        recorder.clear()

        setComboBoxInput(comboBox, "item 1")

        assertEquals("item 1", comboBox.filter)
        assertEquals(1, recorder.params.size)
        assertEquals("item 1", recorder.params[0].filter)
    }

    @Test
    fun isNotInvokedOnOpenAfterFilteringWithAutoOpenDisabled() {
        val comboBox = newComboBox()
        comboBox.autoOpenDisabled = true
        focusInput(comboBox)
        val recorder = Recorder()
        comboBox.dataProvider = stringProvider(recorder)
        setComboBoxInput(comboBox, "item 1")
        recorder.clear()

        comboBox.opened = true

        assertEquals(0, recorder.params.size)
    }

    // when open

    @Test
    fun isInvokedWhenSetWhileOpen(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true
        val recorder = Recorder()

        return nextRender(comboBox).then {
            comboBox.dataProvider = stringProvider(recorder)
            assertEquals(1, recorder.params.size)
        }
    }

    @Test
    fun receivesTheParamsArgument(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true
        val recorder = Recorder()

        return nextRender(comboBox).then {
            comboBox.dataProvider = stringProvider(recorder)
            assertEquals(1, recorder.params.size)
            assertEquals("", recorder.params[0].filter)
            assertEquals(0.0, recorder.params[0].page)
            assertEquals(50.0, recorder.params[0].pageSize)
        }
    }

    @Test
    fun doesNotRequestOnValueChange(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true
        val recorder = Recorder()

        return nextRender(comboBox).then {
            comboBox.dataProvider = stringProvider(recorder)
            recorder.clear()

            comboBox.value = "item 1"

            assertEquals(0, recorder.params.size)
        }
    }

    @Test
    fun populatesFilteredItems(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true

        return nextRender(comboBox).then {
            assertTrue(js("(comboBox) => comboBox.filteredItems === undefined")(comboBox) as Boolean)

            comboBox.dataProvider = stringProvider(Recorder())

            val filtered = comboBox.filteredItems
            assertEquals(SIZE.toDouble(), filtered?.size?.toDouble())
            assertEquals("item 0", filtered!![0].unsafeCast<String>())
            assertEquals("item 49", filtered[49].unsafeCast<String>())
        }
    }

    @Test
    fun togglesLoading(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true
        val observed = mutableListOf<Boolean>()

        return nextRender(comboBox).then {
            assertFalse(comboBox.loading)
            comboBox.dataProvider = { _, callback ->
                observed += comboBox.loading
                callback(emptyArray(), 0.0)
                observed += comboBox.loading
            }

            assertEquals(listOf(true, false), observed)
        }
    }

    @Test
    fun doesNotRequestTheLoadedPageAgain(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = stringProvider(recorder)

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.close()
                nextRender(comboBox)
            }
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then { assertEquals(1, recorder.params.size) }
    }

    // empty data set

    @Test
    fun doesNotRequestTheFirstPageOnOpenWhenTheDataSetIsEmpty(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = { params, callback ->
            recorder.params += params
            callback(emptyArray(), 0.0)
        }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.close()
                nextRender(comboBox)
            }
            .then {
                recorder.clear()
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then { assertEquals(0, recorder.params.size) }
    }

    @Test
    fun requestsTheFirstPageOnOpenAfterIncreasingTheSize(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = { params, callback ->
            recorder.params += params
            callback(emptyArray(), 0.0)
        }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.close()
                nextRender(comboBox)
            }
            .then {
                recorder.clear()
                comboBox.size = SIZE.toDouble()
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then { assertEquals(1, recorder.params.size) }
    }

    @Test
    fun requestsTheFirstPageOnOpenAfterClearingTheCache(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = { params, callback ->
            recorder.params += params
            callback(emptyArray(), 0.0)
        }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.close()
                nextRender(comboBox)
            }
            .then {
                recorder.clear()
                comboBox.clearCache()
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then { assertEquals(1, recorder.params.size) }
    }

    // selecting an item

    @Test
    fun doesNotCallTheProviderWhenSelectingAnItem(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = stringProvider(recorder)

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                recorder.clear()
                comboBoxItems(comboBox).firstOrNull()?.let { vaadin.click(it) }

                assertEquals("item 0", comboBox.selectedItem.unsafeCast<String>())
                assertEquals(0, recorder.params.size)
            }
    }

    @Test
    fun doesNotCallTheProviderWhenSelectingAFilteredItem(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = stringProvider(recorder)

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                setComboBoxInput(comboBox, "1")
                recorder.clear()
                comboBoxItems(comboBox).firstOrNull()?.let { vaadin.click(it) }

                assertEquals("item 1", comboBox.selectedItem.unsafeCast<String>())
                assertEquals(0, recorder.params.size)
            }
    }

    // async data provider

    @Test
    fun invokesTheAsyncProviderOnOpen(): Promise<Unit> {
        val comboBox = newComboBox()
        var calls = 0
        comboBox.dataProvider = { params, callback ->
            calls++
            timeout(0).then { stringProvider(Recorder())(params, callback) }
        }

        comboBox.opened = true

        return nextRender(comboBox).then { assertEquals(1, calls) }
    }

    // changing the data provider

    @Test
    fun hasCorrectItemsAfterSwitchingToAProviderReturningFewerItems(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = { _, callback -> callback(strings(listOf("foo", "bar")), 2.0) }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.close()
                nextRender(comboBox)
            }
            .then {
                comboBox.clearCache()
                comboBox.dataProvider = { _, callback -> callback(strings(listOf("baz")), 1.0) }
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                val filtered = comboBox.filteredItems
                assertEquals(1, filtered?.size)
                assertEquals("baz", filtered!![0].unsafeCast<String>())
                assertEquals(listOf("baz"), comboBoxItems(comboBox).map { it.textContent })
            }
    }

    // data provider set before attach

    @Test
    fun hasUndefinedSizeWhenTheProviderIsSetBeforeAttach(): Promise<Unit> {
        registerComboBox()
        val comboBox = createComboBox()
        comboBox.dataProvider = stringProvider(Recorder())
        attach(comboBox)

        return nextRender(comboBox).then {
            assertTrue(js("(comboBox) => comboBox.size === undefined")(comboBox) as Boolean)
            assertEquals(0, comboBox.filteredItems?.size)
        }
    }

    // pageSize

    @Test
    fun hasADefaultPageSize() {
        val comboBox = newComboBox()

        assertEquals(50.0, comboBox.pageSize)
    }

    @Test
    fun usesTheDefaultPageSizeInTheProvider(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true
        val recorder = Recorder()

        return nextRender(comboBox).then {
            comboBox.dataProvider = stringProvider(recorder)

            assertEquals(50.0, recorder.params[0].pageSize)
        }
    }

    @Test
    fun usesACustomPageSizeInTheProvider(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true
        comboBox.pageSize = 123.0
        val recorder = Recorder()

        return nextRender(comboBox).then {
            comboBox.dataProvider = stringProvider(recorder)

            assertEquals(123.0, recorder.params[0].pageSize)
        }
    }

    @Test
    fun throwsWhenThePageSizeIsSetToZero() {
        val comboBox = newComboBox()
        comboBox.pageSize = 123.0

        assertFailsWith<Throwable> { comboBox.pageSize = 0.0 }

        assertEquals(123.0, comboBox.pageSize)
    }

    @Test
    fun throwsWhenThePageSizeIsNotAPositiveInteger() {
        val comboBox = newComboBox()
        comboBox.pageSize = 123.0

        assertFailsWith<Throwable> { comboBox.pageSize = Double.NaN }
        assertFailsWith<Throwable> { comboBox.pageSize = 10.5 }
        assertFailsWith<Throwable> { comboBox.pageSize = -1.0 }
        assertFailsWith<Throwable> { js("(comboBox) => { comboBox.pageSize = undefined; }")(comboBox) }
        assertFailsWith<Throwable> { js("(comboBox) => { comboBox.pageSize = null; }")(comboBox) }
        assertFailsWith<Throwable> { js("(comboBox) => { comboBox.pageSize = '10'; }")(comboBox) }

        assertEquals(123.0, comboBox.pageSize)
    }

    // size

    @Test
    fun sizeIsUndefinedByDefault() {
        val comboBox = newComboBox()

        assertTrue(js("(comboBox) => comboBox.size === undefined")(comboBox) as Boolean)
    }

    @Test
    fun sizeIsUndefinedAfterSettingTheProvider() {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())

        assertTrue(js("(comboBox) => comboBox.size === undefined")(comboBox) as Boolean)
    }

    @Test
    fun sizeIsSetFromTheProviderCallback(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true

        return nextRender(comboBox).then {
            comboBox.dataProvider = stringProvider(Recorder())

            assertEquals(SIZE.toDouble(), comboBox.size)
        }
    }

    @Test
    fun sizeIsZeroAfterTheProviderReturnsZero(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true

        return nextRender(comboBox).then {
            comboBox.dataProvider = { _, callback -> callback(emptyArray(), 0.0) }

            assertEquals(0.0, comboBox.size)
        }
    }

    @Test
    fun keepsThePreviousSizeWhenTheProviderReturnsUndefined(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true

        return nextRender(comboBox)
            .then {
                comboBox.dataProvider = stringProvider(Recorder())
                assertEquals(SIZE.toDouble(), comboBox.size)

                comboBox.dataProvider = { _, callback -> callbackWithoutSize(callback) }
            }
            .then { assertEquals(SIZE.toDouble(), comboBox.size) }
    }

    @Test
    fun doesNotAddItemsExceedingTheReportedSize(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = { _, callback -> callback(strings(listOf("foo", "bar")), 1.0) }

        comboBox.opened = true

        return nextRender(comboBox).then {
            assertEquals(1, comboBox.filteredItems?.size)
            assertEquals("foo", comboBox.filteredItems!![0].unsafeCast<String>())
        }
    }

    @Test
    fun addsItemsWhenTheProviderReturnsUndefinedSize(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = { _, callback -> callbackWithoutSize(callback, "foo", "bar") }

        comboBox.opened = true

        return nextRender(comboBox).then {
            assertEquals(2, comboBox.filteredItems?.size)
        }
    }

    @Test
    fun removesExtraFilteredItemsWhenDecreasingTheSize(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = { _, callback -> callback(strings(listOf("foo", "bar")), 2.0) }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.size = 1.0

                assertEquals(1, comboBox.filteredItems?.size)
                assertEquals("foo", comboBox.filteredItems!![0].unsafeCast<String>())
            }
    }

    @Test
    fun doesNotResetTheSizeWhenTheCallbackHasNoSize(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.size = SIZE.toDouble()
        comboBox.dataProvider = { _, callback -> callbackWithoutSize(callback, *allItems.toTypedArray()) }

        comboBox.opened = true

        return nextRender(comboBox).then { assertEquals(SIZE.toDouble(), comboBox.size) }
    }

    // size set before attach

    @Test
    fun keepsTheSizeSetBeforeAttach(): Promise<Unit> {
        registerComboBox()
        val comboBox = createComboBox()
        comboBox.dataProvider = stringProvider(Recorder())
        comboBox.size = SIZE.toDouble()
        attach(comboBox)

        return nextRender(comboBox).then {
            assertEquals(SIZE.toDouble(), comboBox.size)
            assertEquals(SIZE, comboBox.filteredItems?.size)
        }
    }

    // value with string items

    @Test
    fun allowsSettingTheInitialValue() {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())

        comboBox.value = "foo"

        assertEquals("foo", comboBox.value)
    }

    @Test
    fun allowsSettingTheInitialValueWhenTheSizeIsPresent() {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())
        comboBox.size = SIZE.toDouble()

        comboBox.value = "foo"

        assertEquals("foo", comboBox.value)
    }

    @Test
    fun doesNotSetSelectedItemWhenTheMatchingItemIsNotLoaded() {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())

        comboBox.value = "item 0"

        assertTrue(js("(comboBox) => comboBox.selectedItem === undefined")(comboBox) as Boolean)
    }

    @Test
    fun setsSelectedItemMatchingValueWhenTheItemsLoad(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())
        comboBox.value = "item 0"

        comboBox.opened = true

        return nextRender(comboBox).then { assertEquals("item 0", comboBox.selectedItem.unsafeCast<String>()) }
    }

    @Test
    fun setsSelectedItemMatchingValueWhenTheItemsAreAlreadyLoaded(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())

        return nextRender(comboBox)
            .then {
                comboBox.opened = true
                nextRender(comboBox)
            }
            .then {
                comboBox.value = "item 0"

                assertEquals("item 0", comboBox.selectedItem.unsafeCast<String>())
            }
    }

    // value with object items

    @Test
    fun allowsSettingTheInitialValueForObjectItems(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.itemIdPath = "id"
        comboBox.dataProvider = objectProvider()

        comboBox.value = "foo"

        return nextRender(comboBox).then { assertEquals("foo", comboBox.value) }
    }

    @Test
    fun setsMatchingObjectSelectedItemWhenTheItemsLoad(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.itemIdPath = "id"
        comboBox.dataProvider = objectProvider()
        comboBox.value = "value 0"

        comboBox.opened = true

        return nextRender(comboBox).then {
            val selected = comboBox.selectedItem
            assertEquals("value 0", js("(item) => item.value")(selected) as String)
            assertEquals("label 0", js("(item) => item.label")(selected) as String)
        }
    }

    // selectedItem

    @Test
    fun allowsSettingTheInitialSelectedItem() {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())

        comboBox.selectedItem = "item 0".unsafeCast<JsAny>()

        assertEquals("item 0", comboBox.selectedItem.unsafeCast<String>())
    }

    @Test
    fun assignsTheValueFromSelectedItem() {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())

        comboBox.selectedItem = "item 0".unsafeCast<JsAny>()

        assertEquals("item 0", comboBox.value)
    }

    @Test
    fun allowsSettingTheInitialObjectSelectedItem() {
        val comboBox = newComboBox()
        comboBox.itemIdPath = "id"
        comboBox.dataProvider = objectProvider()
        val item = js("{ id: 0, value: 'value 0', label: 'label 0' }")

        comboBox.selectedItem = item

        assertSame(item, comboBox.selectedItem)
        assertEquals("value 0", comboBox.value)
    }

    // itemIdPath

    @Test
    fun itemIdPathHasNoDefaultValue() {
        val comboBox = newComboBox()

        assertTrue(js("(comboBox) => comboBox.itemIdPath === undefined")(comboBox) as Boolean)
    }

    // clearCache before opening

    @Test
    fun doesNotRequestTheFirstPageWhenClearingTheCacheBeforeOpening() {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.dataProvider = stringProvider(recorder)

        comboBox.clearCache()

        assertEquals(0, recorder.params.size)
    }

    @Test
    fun doesNotThrowWhenClearingTheCacheWithALargeSize() {
        val comboBox = newComboBox()
        comboBox.dataProvider = stringProvider(Recorder())
        comboBox.size = 500000.0

        comboBox.clearCache()
    }

    // clearCache while open

    @Test
    fun replacesFilteredItemsWithOneSharedPlaceholder(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true

        return nextRender(comboBox).then {
            comboBox.dataProvider = { _, _ -> }
            comboBox.size = SIZE.toDouble()
            comboBox.filteredItems = stringItems("item 0", "item 1")

            comboBox.clearCache()

            val filtered = comboBox.filteredItems
            assertEquals(SIZE, filtered?.size)
            assertSame(filtered!![0], filtered[1])
        }
    }

    @Test
    fun requestsTheFirstPageWhenClearingTheCacheWhileOpen(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true
        val recorder = Recorder()

        return nextRender(comboBox).then {
            comboBox.dataProvider = stringProvider(recorder)
            recorder.clear()

            comboBox.clearCache()

            assertEquals(1, recorder.params.size)
            assertEquals(0.0, recorder.params[0].page)
        }
    }

    @Test
    fun clearsOldPendingRequests(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.opened = true
        var pending: ComboBoxDataProviderCallback? = null

        return nextRender(comboBox).then {
            // Only the first callback is kept, so the call below goes through a request that the
            // cache clear has already dropped.
            comboBox.dataProvider = { _, callback -> if (pending == null) pending = callback }
            comboBox.clearCache()

            pending?.invoke(arrayOf(json("{}")), 1.0)

            assertEquals(0, comboBox.filteredItems?.size)
        }
    }

    // clearCache after closing

    @Test
    fun doesNotRequestTheFirstPageWhenClearingTheCacheAfterClosing(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.opened = true
        comboBox.dataProvider = stringProvider(recorder)

        return nextRender(comboBox)
            .then {
                comboBox.opened = false
                nextRender(comboBox)
            }
            .then {
                recorder.clear()
                comboBox.clearCache()

                assertEquals(0, recorder.params.size)
            }
    }

    @Test
    fun requestsTheFirstPageOnReopenAfterClearingTheCache(): Promise<Unit> {
        val comboBox = newComboBox()
        val recorder = Recorder()
        comboBox.opened = true
        comboBox.dataProvider = stringProvider(recorder)

        return nextRender(comboBox)
            .then {
                comboBox.opened = false
                nextRender(comboBox)
            }
            .then {
                recorder.clear()
                comboBox.clearCache()
                comboBox.opened = true
                nextRender(comboBox)
            }
            .then {
                assertEquals(1, recorder.params.size)
                assertEquals(0.0, recorder.params[0].page)
            }
    }

    private fun focusInput(comboBox: ComboBoxElement) {
        js("(comboBox) => comboBox.inputElement.focus()")(comboBox)
    }

    private companion object {
        const val SIZE = 200
    }
}
