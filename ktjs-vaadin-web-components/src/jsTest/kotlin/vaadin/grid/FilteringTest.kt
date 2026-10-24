package vaadin.grid

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import vaadin.nextRender
import vaadin.typeInto
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/grid/test/filtering.test.js`.
 *
 * The upstream `filter` group wraps `<vaadin-grid-filter>` in a Lit element that only rebinds
 * `value`; the cases below use the filter element directly, which is what the wrapper exercised.
 * The grid-level groups read the private `_filters` list, the array provider and the data provider
 * root cache through the shared helpers in `GridSupport.kt`.
 *
 * Dropped upstream case: `should not overflow filter text field` — it compares the offset widths of
 * the filter host and its text field, which needs the upstream stylesheet.
 */
class FilteringTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerGrid()
        registerGridColumn()
        registerGridFilter()
        registerGridFilterColumn()
        registerGridSorter()
        vaadin.textfield.registerTextField()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    // region the `filter` group

    // `_filterChanged` bails out while `path` is undefined, so every fixture sets one; upstream does
    // the same with `path="foo"` in the Lit wrapper.
    private fun newFilter(): GridFilterElement = attach(createGridFilter { path = "foo" })

    @Test
    fun firesFilterChangedOnAValueChange(): Promise<Unit> {
        val filter = newFilter()
        var fired = 0

        return nextRender(filter)
            .then {
                vaadin.on(filter, "filter-changed") { fired++ }
                filter.value = "foo"
                vaadin.timeout(250)
            }
            .then { assertEquals(1, fired) }
    }

    @Test
    fun firesFilterChangedOnAFieldInputEvent(): Promise<Unit> {
        val filter = newFilter()
        var fired = 0

        return nextRender(filter)
            .then {
                vaadin.on(filter, "filter-changed") { fired++ }
                typeInto(filter, "foo")
                vaadin.timeout(250)
            }
            .then { assertEquals(1, fired) }
    }

    @Test
    fun firesFilterChangedOnAFieldInputEventWithAnEmptyString(): Promise<Unit> {
        val filter = newFilter()
        var fired = 0

        return nextRender(filter)
            .then {
                vaadin.on(filter, "filter-changed") { fired++ }
                typeInto(filter, "foo")
                vaadin.timeout(250)
            }
            .then {
                fired = 0
                typeInto(filter, "")
                vaadin.timeout(250)
            }
            .then { assertEquals(1, fired) }
    }

    @Test
    fun firesFilterChangedOnAPathChange(): Promise<Unit> {
        val filter = newFilter()
        var fired = 0

        return nextRender(filter)
            .then {
                filter.value = "foo"
                vaadin.timeout(250)
            }
            .then {
                vaadin.on(filter, "filter-changed") { fired++ }
                filter.path = "bar"
                vaadin.timeout(250)
            }
            .then { assertEquals(1, fired) }
    }

    @Test
    fun updatesTheValueOnAnInputEvent(): Promise<Unit> {
        val filter = newFilter()

        return nextRender(filter)
            .then {
                typeInto(filter, "foo")
                vaadin.timeout(250)
            }
            .then { assertEquals("foo", filter.value) }
    }

    @Test
    fun focusesTheInputWhenFocusIsCalled(): Promise<Unit> {
        val filter = newFilter()

        return nextRender(filter).then {
            filter.focus()
            val active = js(
                "(filter) => filter.querySelector('vaadin-text-field').matches(':focus-within')",
            )(filter)
            assertTrue(active as Boolean, "expected the filter input to be the active element")
        }
    }

    @Test
    fun hidesTheFilterWithTheHiddenAttribute(): Promise<Unit> {
        val filter = newFilter()

        return nextRender(filter)
            .then {
                js("(filter) => { filter.hidden = true; }")(filter)
                nextFrame()
            }
            .then { assertEquals("none", vaadin.computedStyle(filter, "display")) }
    }

    // endregion

    // region the `filtering` group

    private fun filtersFixture(): GridElement {
        val grid = attach(createGrid())
        val first = document.createElement(vaadin.VaadinTagName.gridColumn)
        first.setAttribute("path", "first")
        val last = document.createElement(vaadin.VaadinTagName.gridColumn)
        last.setAttribute("path", "last")
        grid.appendChild(first)
        grid.appendChild(last)
        grid.appendChild(document.createElement(vaadin.VaadinTagName.gridFilterColumn))

        first.unsafeCast<GridColumnElement>().headerRenderer = { root, _ ->
            js(
                "(root) => { if (!root.firstChild) root.innerHTML = " +
                    "'<vaadin-grid-filter path=\"first\" value=\"bar\"></vaadin-grid-filter>" +
                    "<vaadin-grid-sorter path=\"first\"></vaadin-grid-sorter>'; }",
            )(root)
        }
        last.unsafeCast<GridColumnElement>().headerRenderer = { root, _ ->
            js(
                "(root) => { if (!root.firstChild) root.innerHTML = " +
                    "'<vaadin-grid-filter path=\"last\" value=\"qux\"></vaadin-grid-filter>'; }",
            )(root)
        }
        return grid
    }

    private fun filteredGrid(): Promise<GridElement> {
        val grid = filtersFixture()

        return nextRender(grid)
            .then {
                flushGrid(grid)
                flushFilters(grid)
                nextFrame()
            }
            .then { grid }
    }

    private fun filter(index: Int, grid: GridElement): GridFilterElement =
        gridFilters(grid)[index].unsafeCast<GridFilterElement>()

    @Test
    fun collectsTheFilters(): Promise<Unit> = filteredGrid().then { grid ->
        val filters = gridFilters(grid)
        assertEquals(2, filters.size)
        assertEquals("first", jsProperty<String>(filters[0], "path"))
        assertEquals("last", jsProperty<String>(filters[1], "path"))
    }

    @Test
    fun rendersTheDefaultInputs(): Promise<Unit> = filteredGrid().then { grid ->
        gridFilters(grid).forEach { filter ->
            val field = assertNotNull(vaadin.query(filter, "vaadin-text-field"))
            assertTrue(field.clientHeight > 0, "expected the filter text field to be rendered")
        }
    }

    @Test
    fun dropsTheReferenceToARemovedColumnFilter(): Promise<Unit> = filteredGrid().then { grid ->
        val removed = gridFilters(grid)[0]
        grid.removeChild(grid.firstElementChild.unsafeCast<web.dom.Node>())
        flushGrid(grid)

        nextFrame().then {
            assertTrue(gridFilters(grid).none { it === removed }, "expected the removed filter to be gone")
        }
    }

    @Test
    fun keepsTheReferencesToTheRemainingFilters(): Promise<Unit> = filteredGrid().then { grid ->
        assertEquals(2, gridFilters(grid).size)

        val second = js("(grid) => grid.firstElementChild.nextElementSibling")(grid).unsafeCast<HTMLElement>()
        grid.removeChild(second.unsafeCast<web.dom.Node>())
        flushGrid(grid)

        nextFrame().then {
            val filters = gridFilters(grid)
            assertEquals(1, filters.size)
            assertEquals("first", jsProperty<String>(filters[0], "path"))
        }
    }

    @Test
    fun passesTheFiltersToTheDataProvider(): Promise<Unit> {
        val grid = filtersFixture()
        var checked = false

        return nextRender(grid)
            .then {
                flushGrid(grid)
                flushFilters(grid)
                nextFrame()
            }
            .then {
                grid.size = 10.0
                grid.dataProvider = { params, _ ->
                    val filters = js("(params) => params.filters")(params).unsafeCast<Array<JsAny>>()
                    assertEquals(2, filters.size)
                    assertEquals("first", js("(f) => f.path")(filters[0]) as String)
                    assertEquals("bar", js("(f) => f.value")(filters[0]) as String)
                    assertEquals("last", js("(f) => f.path")(filters[1]) as String)
                    assertEquals("qux", js("(f) => f.value")(filters[1]) as String)
                    checked = true
                }
                nextFrame()
            }
            .then { assertTrue(checked, "expected the data provider to be called with the filters") }
    }

    @Test
    fun clearsTheCacheWhenTheFiltersChange(): Promise<Unit> = filteredGrid().then { grid ->
        js(
            """(grid) => {
                 grid.items = ['foo', 'bar'];
                 grid.__clearCacheCalls = 0;
                 const original = grid.clearCache.bind(grid);
                 grid.clearCache = () => { grid.__clearCacheCalls++; original(); };
               }""",
        )(grid)

        filter(0, grid).value = ""
        flushFilters(grid)

        nextFrame().then { assertEquals(1.0, jsProperty<Double>(grid, "__clearCacheCalls")) }
    }

    @Test
    fun displaysAllTheFilteredItems(): Promise<Unit> = filteredGrid().then { grid ->
        filter(0, grid).value = ""
        filter(1, grid).value = ""
        flushFilters(grid)

        val items = js(
            """() => {
                 const result = [];
                 for (let i = 0; i < 1000; i++) result.push({ first: i, last: i });
                 return result;
               }""",
        )().unsafeCast<Array<JsAny>>()
        grid.items = items
        scrollToEnd(grid)

        filter(0, grid).value = "99"
        flushFilters(grid)
        grid.allRowsVisible = true
        flushGrid(grid)

        nextFrame().then {
            val rows = js("(grid) => grid.${'$'}.items.querySelectorAll('tr:not([hidden])').length")(grid) as Int
            assertEquals(19, rows)
        }
    }

    // endregion

    // region the `filter-column` group

    private fun filterColumnCell(grid: GridElement): HTMLElement = assertNotNull(headerCellContent(grid, 0.0, 2.0))

    private fun internalFilter(grid: GridElement): GridFilterElement =
        assertNotNull(vaadin.query(filterColumnCell(grid), "vaadin-grid-filter")).unsafeCast<GridFilterElement>()

    @Test
    fun propagatesThePathToTheInternalFilter(): Promise<Unit> = filteredGrid().then { grid ->
        val filterColumn = assertNotNull(vaadin.query(grid, "vaadin-grid-filter-column"))
            .unsafeCast<GridFilterColumnElement>()

        filterColumn.path = "last"

        nextFrame().then { assertEquals("last", internalFilter(grid).path) }
    }

    @Test
    fun usesTheHeaderAsTheFilterLabel(): Promise<Unit> = filteredGrid().then { grid ->
        val filterColumn = assertNotNull(vaadin.query(grid, "vaadin-grid-filter-column"))
            .unsafeCast<GridFilterColumnElement>()
        val textField = assertNotNull(internalFilter(grid).firstElementChild)

        filterColumn.header = "Last column"

        nextFrame().then { assertEquals("Last column", js("(field) => field.label")(textField) as String) }
    }

    @Test
    fun generatesTheFilterLabelFromThePath(): Promise<Unit> = filteredGrid().then { grid ->
        val filterColumn = assertNotNull(vaadin.query(grid, "vaadin-grid-filter-column"))
            .unsafeCast<GridFilterColumnElement>()
        val textField = assertNotNull(internalFilter(grid).firstElementChild)

        filterColumn.path = "last"

        nextFrame().then { assertEquals("Last", js("(field) => field.label")(textField) as String) }
    }

    @Test
    fun appliesTheInputValueToTheFilter(): Promise<Unit> = filteredGrid().then { grid ->
        val filter = internalFilter(grid)

        typeInto(filter, "foo")
        flushFilters(grid)

        nextFrame().then { assertEquals("foo", filter.value) }
    }

    @Test
    fun ignoresACustomHeaderRenderer(): Promise<Unit> = filteredGrid().then { grid ->
        val filterColumn = assertNotNull(vaadin.query(grid, "vaadin-grid-filter-column"))
            .unsafeCast<GridFilterColumnElement>()
        val cellContent = filterColumnCell(grid)
        val filter = assertNotNull(vaadin.query(cellContent, "vaadin-grid-filter"))

        filterColumn.headerRenderer = { root, _ -> js("(root) => { root.textContent = 'header'; }")(root) }
        flushGrid(grid)

        nextFrame().then { assertEquals(filter, cellContent.firstElementChild) }
    }

    // endregion

    // region the `array data provider` group

    private fun arrayGrid(): Promise<GridElement> {
        lateinit var result: GridElement

        return filteredGrid()
            .then { grid ->
                filter(0, grid).value = ""
                filter(1, grid).value = ""
                flushFilters(grid)

                grid.items = js(
                    "[{ first: 'foo', last: 'bar' }, { first: 'foo', last: 'baz' }, { first: 'bar', last: 'bar' }]",
                ).unsafeCast<Array<JsAny>>()
                flushGrid(grid)
                result = grid
                nextFrame()
            }
            .then { result }
    }

    @Test
    fun doesNotThrowOnANullFilter(): Promise<Unit> = arrayGrid().then { grid ->
        grid.items = js("['foo', 'bar']").unsafeCast<Array<JsAny>>()
        js("(filter) => { filter.value = null; }")(filter(0, grid))

        flushFilters(grid)
        nextFrame()
    }

    @Test
    fun filtersAutomatically(): Promise<Unit> = arrayGrid().then { grid ->
        assertEquals("foo", bodyCellContent(grid, 0.0, 0.0)?.textContent)
        assertEquals("foo", bodyCellContent(grid, 1.0, 0.0)?.textContent)
        assertEquals("bar", bodyCellContent(grid, 2.0, 0.0)?.textContent)

        filter(0, grid).value = "r"
        flushFilters(grid)

        nextFrame().then { assertEquals("bar", bodyCellContent(grid, 0.0, 0.0)?.textContent) }
    }

    @Test
    fun updatesTheFilteringWhenAColumnIsRemoved(): Promise<Unit> = arrayGrid().then { grid ->
        filter(0, grid).value = "bar"
        flushFilters(grid)

        grid.removeChild(grid.firstElementChild.unsafeCast<web.dom.Node>())
        flushGrid(grid)

        nextFrame().then { assertEquals(3, visibleItemIndexes(grid).size) }
    }

    @Test
    fun invokesTheDataProviderOnceWhenRemovingAColumnWithAFilter(): Promise<Unit> = arrayGrid().then { grid ->
        js(
            """(grid) => {
                 grid.__providerCalls = 0;
                 const provider = grid.dataProvider;
                 grid._arrayDataProvider = provider;
                 grid.dataProvider = function (params, callback) {
                   grid.__providerCalls++;
                   return provider(params, callback);
                 };
               }""",
        )(grid)

        grid.removeChild(grid.firstElementChild.unsafeCast<web.dom.Node>())
        flushGrid(grid)

        nextFrame().then { assertEquals(1.0, jsProperty<Double>(grid, "__providerCalls")) }
    }

    @Test
    fun doesNotFilterBeforeTheGridIsReattached(): Promise<Unit> = arrayGrid().then { grid ->
        filter(0, grid).value = "bar"
        flushFilters(grid)

        val parent = assertNotNull(grid.parentElement)
        parent.removeChild(grid)
        grid.removeChild(grid.firstElementChild.unsafeCast<web.dom.Node>())
        flushGrid(grid)

        assertEquals(
            1,
            js("(grid) => Object.keys(grid._dataProviderController.rootCache.items).length")(grid) as Int,
        )

        parent.appendChild(grid)

        nextFrame().then {
            assertEquals(
                3,
                js("(grid) => Object.keys(grid._dataProviderController.rootCache.items).length")(grid) as Int,
            )
        }
    }

    @Test
    fun sortsTheFilteredItems(): Promise<Unit> = arrayGrid()
        .then { grid ->
            filter(1, grid).value = "r"
            flushFilters(grid)
            nextFrame().then { grid }
        }
        .then { grid ->
            val sorter = assertNotNull(vaadin.query(grid, "vaadin-grid-sorter")).unsafeCast<GridSorterElement>()
            sorter.direction = "asc"
            flushGrid(grid)
            nextFrame().then { grid }
        }
        .then { grid ->
            assertEquals(2.0, grid.size)
            assertEquals("bar", bodyCellContent(grid, 0.0, 0.0)?.textContent)
            assertEquals("foo", bodyCellContent(grid, 1.0, 0.0)?.textContent)
        }

    // endregion

    // region lazy init

    @Test
    fun doesNotFilterWithoutData(): Promise<Unit> {
        val grid = filtersFixture()

        return nextRender(grid)
            .then {
                grid.size = 100.0
                grid.dataProvider = { _, _ -> }
                flushFilters(grid)
                nextFrame()
            }
    }

    // endregion
}
