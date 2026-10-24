package vaadin.combobox

import kotlin.js.Promise
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import web.html.HTMLElement

/**
 * Adapted from `packages/combo-box/test/data-provider-dynamic-size.test.js`.
 *
 * The upstream cases are all about the virtualizer: they call `_scrollIntoView`, flush
 * `__virtualizer`, and then inspect the viewport items and `_scroller._scrollHeight`. None of that is
 * reachable through the public API, so what is translated here is the size bookkeeping behind those
 * cases: the component keeps its `filteredItems` (loaded pages plus placeholders) in sync with the
 * size reported by the provider.
 *
 * Not translated at all: `undefined size` → `should restore the scroll position after size update`,
 * `should fetch the items after scrolling to the bottom with scrollbar`, and
 * `should not show the loading when exact size is suddenly reached` — all three need real scrolling.
 */
class DataProviderDynamicSizeTest {
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

    @Test
    fun trimsThePlaceholdersWhenTheProviderReportsASmallerSize(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.size = INITIAL_SIZE.toDouble()
        comboBox.dataProvider = { params, callback ->
            val items = Array(params.pageSize.toInt()) { index ->
                "Item ${(params.pageSize * params.page + index).toInt()}"
            }
            callback(items.map { it.unsafeCast<kotlin.js.JsAny?>() }.toTypedArray(), ACTUAL_SIZE.toDouble())
        }

        return nextRender(comboBox)
            .then {
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                // The first page reports the real size, so the placeholder run shrinks with it.
                assertEquals(ACTUAL_SIZE.toDouble(), comboBox.size)
                assertEquals(ACTUAL_SIZE, comboBox.filteredItems?.size)
            }
    }

    @Test
    fun keepsGrowingWhenTheProviderReportsAnUnknownSize(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox)
            .then {
                comboBox.dataProvider = { _, callback -> callbackWithoutSize(callback, "a", "b", "c") }
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then { assertEquals(3, comboBox.filteredItems?.size) }
    }

    @Test
    fun shrinksThePlaceholdersWhenTheSizeIsReduced(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.size = INITIAL_SIZE.toDouble()

        return nextRender(comboBox)
            .then {
                comboBox.dataProvider = { _, callback -> callbackWithoutSize(callback, "a", "b") }
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                comboBox.size = ACTUAL_SIZE.toDouble()

                assertEquals(ACTUAL_SIZE, comboBox.filteredItems?.size)
            }
    }

    private companion object {
        const val INITIAL_SIZE = 600
        const val ACTUAL_SIZE = 500
    }
}
