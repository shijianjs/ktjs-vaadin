package vaadin.grid

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import vaadin.nextRender
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/grid/test/basic.test.js`.
 *
 * The upstream suite drives the grid through its test helpers (`flushGrid`, `getPhysicalItems`,
 * `getFirstVisibleItem`, ...), which poke the private observables and the virtualizer. Those helpers
 * are re-implemented here in one place with `js(...)`; everything else uses the typed API
 * (`size`, `items`, `dataProvider`, `scrollToIndex`, `allRowsVisible`).
 *
 * Dropped upstream cases: `should update first and last column parts for newly created rows` (needs
 * the virtualizer to allocate new rows after a resize), `reorder should keep focused row` (requires
 * the page to have focus) and `should have a visible header after row reorder` (it resolves a cell
 * from a point with the protected `_cellFromPoint`).
 */
class BasicTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerGrid()
        registerGridColumn()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newGrid(height: String = "300px"): GridElement {
        val grid = attach(createGrid())
        grid.style.setProperty("width", "200px")
        grid.style.setProperty("height", height)
        grid.size = 1000.0
        val column = document.createElement(vaadin.VaadinTagName.gridColumn)
        grid.appendChild(column)
        column.unsafeCast<GridColumnElement>().renderer = { root, _, model ->
            js("(root, index) => { root.textContent = index; }")(root, model.index)
        }
        grid.dataProvider = infiniteDataProvider()
        return grid
    }

    private fun newGridWithRenderer(counter: GridRenderCounter): GridElement {
        val grid = newGrid()
        val column = grid.firstElementChild.unsafeCast<GridColumnElement>()
        column.renderer = { root, _, model ->
            counter.calls++
            if (model.index == 0.0) counter.firstCellCalls++
            js("(root, index) => { root.textContent = index; }")(root, model.index)
        }
        return grid
    }

    private class GridRenderCounter {
        var calls = 0
        var firstCellCalls = 0
    }

    // region basic features

    @Test
    fun allRowsVisibleIsFalseByDefault(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid).then { assertFalse(grid.allRowsVisible) }
    }

    @Test
    fun notifiesTheSizeProperty(): Promise<Unit> {
        val grid = newGrid()
        var fired = 0
        vaadin.on(grid, "size-changed") { fired++ }

        return nextRender(grid)
            .then {
                grid.size = 10.0
                nextFrame()
            }
            .then { assertEquals(1, fired) }
    }

    @Test
    fun physicalItemsHaveTheSameHeight(): Promise<Unit> {
        val grid = newGrid()
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then {
                val items = physicalItems(grid)
                assertTrue(items.isNotEmpty())
                val rowHeight = items[0].offsetHeight
                assertTrue(rowHeight > 0, "expected a positive row height, got $rowHeight")
                items.forEach { item ->
                    assertTrue(
                        kotlin.math.abs(item.offsetHeight - rowHeight) <= 1.0,
                        "expected every row to be $rowHeight tall, got ${item.offsetHeight}",
                    )
                }
            }
    }

    @Test
    fun showsOneRowPerItem(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.size = 10.0
                flushGrid(grid)
                nextFrame()
            }
            .then {
                val rows = js(
                    "(grid) => grid.shadowRoot.querySelectorAll('tbody tr:not([hidden]):not(#emptystaterow)').length",
                )(grid) as Int
                assertEquals(10, rows)
            }
    }

    @Test
    fun scrollsToTheRequestedIndex(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.size = 100.0
                grid.scrollToIndex(30.0)
                flushGrid(grid)
                nextFrame()
            }
            .then {
                assertEquals(30.0, firstVisibleIndex(grid))

                grid.scrollToIndex(0.0)
                flushGrid(grid)
                nextFrame()
            }
            .then { assertEquals(0.0, firstVisibleIndex(grid)) }
    }

    @Test
    fun scrollsToTheTop(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.scrollToIndex(99.0)
                flushGrid(grid)
                nextFrame()
            }
            .then {
                scrollGrid(grid, 0.0)
                flushGrid(grid)
                nextFrame()
            }
            .then { assertEquals(0.0, tableScrollTop(grid)) }
    }

    @Test
    fun scrollsToAGivenScrollTop(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.scrollToIndex(99.0)
                flushGrid(grid)
                nextFrame()
            }
            .then {
                scrollGrid(grid, 500.0)
                flushGrid(grid)
                nextFrame()
            }
            .then { assertEquals(500.0, tableScrollTop(grid)) }
    }

    @Test
    fun doesNotScrollWhenTheSizeChanges(): Promise<Unit> {
        val grid = newGrid()
        var previousTop = -1.0

        return nextRender(grid)
            .then {
                grid.scrollToIndex(99.0)
                flushGrid(grid)
                nextFrame()
            }
            .then {
                previousTop = tableScrollTop(grid)
                assertTrue(previousTop > 0.0, "expected the grid to be scrolled, got $previousTop")

                grid.size += 1
                nextFrame()
            }
            .then { assertEquals(previousTop, tableScrollTop(grid)) }
    }

    @Test
    fun restoresTheScrollPositionWhenMovedWithinTheDom(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.scrollToIndex(99.0)
                flushGrid(grid)
                nextFrame()
            }
            .then {
                val top = tableScrollTop(grid)
                val wrapper = attach(document.createElement("div"))
                wrapper.appendChild(grid)
                nextFrame()

                assertEquals(top, tableScrollTop(grid))
            }
    }

    @Test
    fun resetsTheItems(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.size = 100.0
                nextFrame()
            }
            .then {
                assertEquals("0", bodyCellContent(grid, 0.0, 0.0)?.textContent)

                grid.size = 0.0
                nextFrame()
            }
            .then {
                grid.size = 100.0
                nextFrame()
            }
            .then { assertEquals("0", bodyCellContent(grid, 0.0, 0.0)?.textContent) }
    }

    @Test
    fun keepsThePhysicalRowsInOrderAfterScrolling(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.size = 1000.0
                listOf(1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144).fold(Promise.resolve(Unit)) { previous, steps ->
                    previous.then {
                        scrollGrid(grid, 5000.0 + physicalAverage(grid) * steps)
                        flushGrid(grid)
                        flushGrid(grid)

                        val indexes = physicalItems(grid).map { js("(row) => row.index")(it) as Double }
                        indexes.zipWithNext { previousIndex, index ->
                            assertEquals(previousIndex + 1, index)
                        }
                        nextFrame()
                    }
                }
            }
    }

    @Test
    fun doesNotThrowAfterSettingTheSizeToZero(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.size = 1000.0
                grid.scrollToIndex(grid.size - 1)
                flushGrid(grid)
                nextFrame()
            }
            .then {
                grid.size = 0.0
                flushGrid(grid)
                nextFrame()
            }
    }

    @Test
    fun hasTheFlexGrowAndShrinkStyles(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid).then {
            val style = vaadin.computedStyle(grid, "flexShrink")
            assertEquals(1.0, style.toDouble())
            assertEquals(1.0, vaadin.computedStyle(grid, "flexGrow").toDouble())
            assertEquals("auto", vaadin.computedStyle(grid, "flexBasis"))
        }
    }

    @Test
    fun marksTheLastBodyRow(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.scrollToIndex(grid.size - 1)
                flushGrid(grid)
                nextFrame()
            }
            .then {
                val texts = js(
                    """(grid) => Array.from(grid.shadowRoot.querySelectorAll('[part~="last-row"] slot'))
                         .map((slot) => slot.assignedNodes()[0].textContent)""",
                )(grid).unsafeCast<Array<String>>()
                assertEquals(1, texts.size)
                assertEquals("999", texts[0])
            }
    }

    @Test
    fun marksTheLastBodyRowAfterAResize(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.size = 2.0
                flushGrid(grid)
                nextFrame()
            }
            .then {
                val text = js(
                    "(grid) => grid.shadowRoot.querySelector('[part~=\"last-row\"] slot').assignedNodes()[0].textContent",
                )(grid) as String
                assertEquals("1", text)
            }
    }

    @Test
    fun hasASingleLastBodyRowAfterAResize(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.size = 2.0
                flushGrid(grid)
                grid.size = 3.0
                flushGrid(grid)
                nextFrame()
            }
            .then {
                val rows = js("(grid) => grid.shadowRoot.querySelectorAll('[part~=\"last-row\"]').length")(grid) as Int
                assertEquals(1, rows)
            }
    }

    @Test
    fun hasNoLastBodyRowWhenTheSizeExceedsTheViewport(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.size = 2.0
                flushGrid(grid)
                grid.size = 1000.0
                flushGrid(grid)
                nextFrame()
            }
            .then {
                val rows = js("(grid) => grid.shadowRoot.querySelectorAll('[part~=\"last-row\"]').length")(grid) as Int
                assertEquals(0, rows)
            }
    }

    // region renderer call counts

    private fun counterGrid(): GridElement {
        val counter = GridRenderCounter()
        val grid = newGridWithRenderer(counter)
        grid.dataProvider = infiniteDataProvider()
        flushGrid(grid)
        return grid
    }

    @Test
    fun rendersTheFirstCellAtMostTwiceDuringInitialization(): Promise<Unit> {
        val counter = GridRenderCounter()
        val grid = newGridWithRenderer(counter)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then { assertTrue(counter.firstCellCalls <= 2, "got ${counter.firstCellCalls} renders") }
    }

    @Test
    fun rerendersTheFirstCellWhenTheLastRowEntersTheViewport(): Promise<Unit> {
        val counter = GridRenderCounter()
        val grid = newGridWithRenderer(counter)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then {
                counter.firstCellCalls = 0
                grid.size = 1.0
                nextFrame()
            }
            .then { assertEquals(1, counter.firstCellCalls) }
    }

    @Test
    fun rerendersTheFirstCellWhenTheLastRowLeavesTheViewport(): Promise<Unit> {
        val counter = GridRenderCounter()
        val grid = newGridWithRenderer(counter)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then {
                grid.size = 1.0
                nextFrame()
            }
            .then {
                counter.firstCellCalls = 0
                grid.size = 1000.0
                nextFrame()
            }
            .then { assertEquals(2, counter.firstCellCalls) }
    }

    @Test
    fun doesNotRerenderTheFirstCellWhenTheLastRowChangesOutsideTheViewport(): Promise<Unit> {
        val counter = GridRenderCounter()
        val grid = newGridWithRenderer(counter)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then {
                counter.firstCellCalls = 0
                grid.size = 100.0
                grid.size = 200.0
                nextFrame()
            }
            .then { assertEquals(0, counter.firstCellCalls) }
    }

    @Test
    fun rerendersTheFirstCellWhenTheLastRowChangesInsideTheViewport(): Promise<Unit> {
        val counter = GridRenderCounter()
        val grid = newGridWithRenderer(counter)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then {
                counter.firstCellCalls = 0
                grid.size = 2.0
                grid.size = 3.0
                nextFrame()
            }
            .then { assertEquals(1, counter.firstCellCalls) }
    }

    @Test
    fun rerendersTheFirstCellOnAResizeFromZero(): Promise<Unit> {
        val counter = GridRenderCounter()
        val grid = newGridWithRenderer(counter)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then {
                counter.firstCellCalls = 0
                grid.size = 0.0
                grid.size = 1.0
                nextFrame()
            }
            .then { assertEquals(2, counter.firstCellCalls) }
    }

    // endregion

    // endregion

    // region flex child

    private fun flexLayout(direction: String): HTMLElement = attach(
        vaadin.fixture(
            "<div style=\"display: flex; width: 300px; height: 300px; flex-direction: $direction;\">" +
                "<div style=\"width: 100px; height: 100px; flex-shrink: 0;\">Layout sibling</div>" +
                "<vaadin-grid size=\"1000\"><vaadin-grid-column></vaadin-grid-column></vaadin-grid></div>",
        ),
    )

    private fun gridIn(layout: HTMLElement): GridElement = js("(layout) => layout.querySelector('vaadin-grid')")(layout)
        .unsafeCast<GridElement>()

    @Test
    fun hasFourHundredPixelHeightByDefault(): Promise<Unit> {
        val layout = flexLayout("row")
        val grid = gridIn(layout)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then { assertEquals(400.0, grid.getBoundingClientRect().height, 1.0) }
    }

    @Test
    fun stretchesTheWidthInARowLayout(): Promise<Unit> {
        val layout = flexLayout("row")
        val grid = gridIn(layout)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then { assertEquals(200.0, grid.getBoundingClientRect().width, 1.0) }
    }

    @Test
    fun doesNotStretchTheHeightInARowLayout(): Promise<Unit> {
        val layout = flexLayout("row")
        val grid = gridIn(layout)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then { assertEquals(400.0, grid.getBoundingClientRect().height, 1.0) }
    }

    @Test
    fun stretchesTheWidthInAColumnLayout(): Promise<Unit> {
        val layout = flexLayout("column")
        val grid = gridIn(layout)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then { assertEquals(300.0, grid.getBoundingClientRect().width, 1.0) }
    }

    @Test
    fun stretchesTheHeightInAColumnLayout(): Promise<Unit> {
        val layout = flexLayout("column")
        val grid = gridIn(layout)
        grid.dataProvider = infiniteDataProvider()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                nextFrame()
            }
            .then { assertEquals(200.0, grid.getBoundingClientRect().height, 1.0) }
    }

    // endregion

    // region empty state

    private fun emptyStateGrid(): GridElement {
        val grid = attach(createGrid())
        val column = document.createElement(vaadin.VaadinTagName.gridColumn)
        column.setAttribute("path", "name")
        grid.appendChild(column)
        val emptyState = document.createElement("div")
        emptyState.setAttribute("slot", "empty-state")
        emptyState.textContent = "No items"
        grid.appendChild(emptyState)
        return grid
    }

    private fun emptyState(grid: GridElement): HTMLElement =
        assertNotNull(vaadin.query(grid, "[slot=\"empty-state\"]"))

    private fun emptyStateVisible(grid: GridElement): Boolean = emptyState(grid).offsetHeight > 0

    private fun itemsBodyVisible(grid: GridElement): Boolean =
        (js("(grid) => grid.${'$'}.items.offsetHeight")(grid) as Int) > 0

    @Test
    fun showsTheEmptyState(): Promise<Unit> {
        val grid = emptyStateGrid()

        return nextRender(grid)
            .then { nextFrame() }
            .then {
                assertTrue(emptyStateVisible(grid))
                assertFalse(itemsBodyVisible(grid))
            }
    }

    @Test
    fun hidesTheEmptyStateWhenTheGridHasItems(): Promise<Unit> {
        val grid = emptyStateGrid()

        return nextRender(grid)
            .then {
                grid.items = js("[{ name: 'foo' }]").unsafeCast<Array<JsAny>>()
                nextFrame()
            }
            .then {
                assertFalse(emptyStateVisible(grid))
                assertTrue(itemsBodyVisible(grid))
            }
    }

    @Test
    fun hidesTheEmptyStateWhenItsContentIsNotDefined(): Promise<Unit> {
        val grid = emptyStateGrid()

        return nextRender(grid)
            .then {
                grid.removeChild(emptyState(grid))
                nextFrame()
            }
            .then { assertTrue(itemsBodyVisible(grid)) }
    }

    @Test
    fun doesNotDispatchCellActivateOnEmptyStateClick(): Promise<Unit> {
        val grid = emptyStateGrid()
        var activated = 0
        vaadin.on(grid, "cell-activate") { activated++ }

        return nextRender(grid)
            .then {
                nextFrame()
                emptyState(grid).click()
                nextFrame()
            }
            .then { assertEquals(0, activated) }
    }

    // endregion
}
