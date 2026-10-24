package vaadin.grid

import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import web.html.HTMLElement

/**
 * Counterparts of `packages/grid/test/helpers.js`.
 *
 * The upstream helpers flush the private observables and the virtualizer, or read the physical rows
 * out of the shadow DOM. There is no typed API for any of it, so the bodies stay in `js(...)`, but
 * every suite in this package shares these definitions instead of copying them.
 */

/** Mirrors `flushGrid(grid)` from the upstream helpers. */
internal fun flushGrid(grid: GridElement) {
    js(
        """(grid) => {
             grid.performUpdate && grid.performUpdate();
             grid._observer.flush();
             [grid.__clearCacheDebouncer, grid.__updateColumnTreeDebouncer, grid._debounceScrolling,
              grid._debouncerHiddenChanged, grid._debouncerApplyCachedData,
              grid.__debounceUpdateFrozenColumn, grid.__renderHeaderFooterDebouncer]
               .forEach((debouncer) => debouncer && debouncer.flush());
             grid.__virtualizer.flush();
           }""",
    )(grid)
}

/** Mirrors `getPhysicalItems(grid)`: the rendered rows sorted by their item index. */
internal fun physicalItems(grid: GridElement): Array<HTMLElement> = js(
    """(grid) => Array.from(grid.shadowRoot.querySelector('tbody#items').children)
         .filter((item) => !item.hidden)
         .sort((a, b) => a.index - b.index)""",
)(grid).unsafeCast<Array<HTMLElement>>()

/** Mirrors `getPhysicalAverage(grid)`: the average rendered row height. */
internal fun physicalAverage(grid: GridElement): Double = js(
    """(grid) => {
         const items = Array.from(grid.shadowRoot.querySelector('tbody#items').children)
           .filter((item) => !item.hidden);
         return items.map((el) => el.offsetHeight).reduce((sum, value) => sum + value, 0) / items.length;
       }""",
)(grid) as Double

/** Mirrors `getVisibleItems(grid)`, reduced to the item indexes. */
internal fun visibleItemIndexes(grid: GridElement): Array<Double> = js(
    """(grid) => {
         const table = grid.shadowRoot.querySelector('table');
         const tableRect = table.getBoundingClientRect();
         const headerHeight = grid.shadowRoot.querySelector('thead').offsetHeight;
         const footerHeight = grid.shadowRoot.querySelector('tfoot').offsetHeight;
         const items = Array.from(grid.shadowRoot.querySelector('tbody#items').children)
           .filter((item) => !item.hidden)
           .sort((a, b) => a.index - b.index);
         return items
           .filter((item) => {
             const rect = item.getBoundingClientRect();
             const offset = parseInt(getComputedStyle(item.firstElementChild).borderTopWidth);
             return (
               rect.top + rect.height - offset > tableRect.top + headerHeight &&
               rect.top + offset < tableRect.top + tableRect.height - footerHeight
             );
           })
           .map((item) => item.index);
       }""",
)(grid).unsafeCast<Array<Double>>()

/** Mirrors `getFirstVisibleItem(grid)`, returning the item index (or `-1`). */
internal fun firstVisibleIndex(grid: GridElement): Double = visibleItemIndexes(grid).firstOrNull() ?: -1.0

/** Mirrors `getBodyCellContent(grid, row, col)`. */
internal fun bodyCellContent(grid: GridElement, row: Double, col: Double): HTMLElement? = cellContent(
    grid,
    "items",
    row,
    col,
)

/** Mirrors `getHeaderCellContent(grid, row, col)`. */
internal fun headerCellContent(grid: GridElement, row: Double, col: Double): HTMLElement? = cellContent(
    grid,
    "header",
    row,
    col,
)

private fun cellContent(grid: GridElement, section: String, row: Double, col: Double): HTMLElement? = js(
    """(grid, section, row, col) => {
         const rows = Array.from(grid.${'$'}[section].querySelectorAll('[part~="row"]'));
         const cells = Array.from(rows[row].querySelectorAll('[part~="cell"]'));
         const slot = cells[col].querySelector('slot');
         return slot ? slot.assignedNodes()[0] : null;
       }""",
)(grid, section, row, col) as HTMLElement?

/** Mirrors `grid.shadowRoot.querySelector('table').scrollTop`. */
internal fun tableScrollTop(grid: GridElement): Double =
    js("(grid) => grid.shadowRoot.querySelector('table').scrollTop")(grid) as Double

/** Mirrors `scrollGrid(grid, left, top)`. */
internal fun scrollGrid(grid: GridElement, top: Double, left: Double = 0.0) {
    js("(grid, left, top) => grid.shadowRoot.querySelector('table').scroll(left, top)")(grid, left, top)
}

/** Mirrors `scrollToEnd(grid)`. */
internal fun scrollToEnd(grid: GridElement) {
    grid.scrollToIndex(grid.size - 1)
    flushGrid(grid)
}

/** Mirrors `infiniteDataProvider` from the upstream helpers. */
internal fun infiniteDataProvider(): GridDataProvider = { params, callback ->
    // The items are pushed instead of returned from a mapper: the Kotlin/JS compiler re-prints the
    // js() body and a returned object literal ends up as a block, i.e. `undefined`.
    js(
        """(params, callback) => {
             const items = [];
             for (let i = 0; i < params.pageSize; i++) {
               items.push({ value: 'foo' + (params.page * params.pageSize + i) });
             }
             callback(items);
           }""",
    )(params, callback)
}

/** Mirrors `grid._filters`. */
internal fun gridFilters(grid: GridElement): Array<HTMLElement> =
    js("(grid) => grid._filters")(grid).unsafeCast<Array<HTMLElement>>()

/** Mirrors `flushFilters(grid)`. */
internal fun flushFilters(grid: GridElement) {
    js(
        """(grid) => Array.from(grid.querySelectorAll('vaadin-grid-filter'))
             .forEach((filter) => filter._debouncerFilterChanged && filter._debouncerFilterChanged.flush())""",
    )(grid)
}

/** Reads a property the typed wrapper does not expose. */
internal fun <T> jsProperty(target: JsAny, name: String): T = js("(target, name) => target[name]")(target, name)
    .unsafeCast<T>()
