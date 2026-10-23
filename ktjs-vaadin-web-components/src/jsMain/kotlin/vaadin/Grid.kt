package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-grid>` is a free, high quality data grid / data table Web Component. The content of the
 * the grid can be populated by using renderer callback function.
 *
 * ### Quick Start
 *
 * Start with an assigning an array to the [`items`](#/elements/vaadin-grid#property-items) property to visualize your data.
 *
 * Use the [`<vaadin-grid-column>`](#/elements/vaadin-grid-column) element to configure the grid columns. Set `path` and `header`
 * shorthand properties for the columns to define what gets rendered in the cells of the column.
 *
 * #### Example:
 * ```html
 * <vaadin-grid>
 *   <vaadin-grid-column path="name.first" header="First name"></vaadin-grid-column>
 *   <vaadin-grid-column path="name.last" header="Last name"></vaadin-grid-column>
 *   <vaadin-grid-column path="email"></vaadin-grid-column>
 * </vaadin-grid>
 * ```
 *
 * For custom content `vaadin-grid-column` element provides you with three types of `renderer` callback functions: `headerRenderer`,
 * `renderer` and `footerRenderer`.
 *
 * Each of those renderer functions provides `root`, `column`, `model` arguments when applicable.
 * Generate DOM content, append it to the `root` element and control the state
 * of the host element by accessing `column`. Before generating new content,
 * users are able to check if there is already content in `root` for reusing it.
 *
 * Renderers are called on initialization of new column cells and each time the
 * related row model is updated. DOM generated during the renderer call can be reused
 * in the next renderer call and will be provided with the `root` argument.
 * On first call it will be empty.
 *
 * #### Example:
 * ```html
 * <vaadin-grid>
 *   <vaadin-grid-column></vaadin-grid-column>
 *   <vaadin-grid-column></vaadin-grid-column>
 *   <vaadin-grid-column></vaadin-grid-column>
 * </vaadin-grid>
 * ```
 * ```js
 * const grid = document.querySelector('vaadin-grid');
 * grid.items = [{'name': 'John', 'surname': 'Lennon', 'role': 'singer'},
 *               {'name': 'Ringo', 'surname': 'Starr', 'role': 'drums'}];
 *
 * const columns = grid.querySelectorAll('vaadin-grid-column');
 *
 * columns[0].headerRenderer = function(root) {
 *   root.textContent = 'Name';
 * };
 * columns[0].renderer = function(root, column, model) {
 *   root.textContent = model.item.name;
 * };
 *
 * columns[1].headerRenderer = function(root) {
 *   root.textContent = 'Surname';
 * };
 * columns[1].renderer = function(root, column, model) {
 *   root.textContent = model.item.surname;
 * };
 *
 * columns[2].headerRenderer = function(root) {
 *   root.textContent = 'Role';
 * };
 * columns[2].renderer = function(root, column, model) {
 *   root.textContent = model.item.role;
 * };
 * ```
 *
 * The following properties are available in the `model` argument:
 *
 * Property name | Type | Description
 * --------------|------|------------
 * `index`| Number | The index of the item.
 * `item` | String or Object | The item.
 * `level` | Number | Number of the item's tree sublevel, starts from 0.
 * `expanded` | Boolean | True if the item's tree sublevel is expanded.
 * `selected` | Boolean | True if the item is selected.
 * `detailsOpened` | Boolean | True if the item's row details are open.
 * `hasChildren` | Boolean | True if the item has children
 *
 * The following helper elements can be used for further customization:
 * - [`<vaadin-grid-column-group>`](#/elements/vaadin-grid-column-group)
 * - [`<vaadin-grid-filter>`](#/elements/vaadin-grid-filter)
 * - [`<vaadin-grid-sorter>`](#/elements/vaadin-grid-sorter)
 * - [`<vaadin-grid-selection-column>`](#/elements/vaadin-grid-selection-column)
 * - [`<vaadin-grid-tree-toggle>`](#/elements/vaadin-grid-tree-toggle)
 *
 * __Note that the helper elements must be explicitly imported.__
 * If you want to import everything at once you can use the `all-imports.js` entrypoint.
 *
 * ### Lazy Loading with Function Data Provider
 *
 * In addition to assigning an array to the items property, you can alternatively
 * provide the `<vaadin-grid>` data through the
 * [`dataProvider`](#/elements/vaadin-grid#property-dataProvider) function property.
 * The `<vaadin-grid>` calls this function lazily, only when it needs more data
 * to be displayed.
 *
 * See the [`dataProvider`](#/elements/vaadin-grid#property-dataProvider) property
 * documentation for the detailed data provider arguments description.
 *
 * __Note that expanding the tree grid's item will trigger a call to the `dataProvider`.__
 *
 * __Also, note that when using function data providers, the total number of items
 * needs to be set manually. The total number of items can be returned
 * in the second argument of the data provider callback:__
 *
 * ```javascript
 * grid.dataProvider = ({page, pageSize}, callback) => {
 *   // page: the requested page index
 *   // pageSize: number of items on one page
 *   const url = `https://api.example/data?page=${page}&per_page=${pageSize}`;
 *
 *   fetch(url)
 *     .then((res) => res.json())
 *     .then(({ employees, totalSize }) => {
 *       callback(employees, totalSize);
 *     });
 * };
 * ```
 *
 * __Alternatively, you can use the `size` property to set the total number of items:__
 *
 * ```javascript
 * grid.size = 200; // The total number of items
 * grid.dataProvider = ({page, pageSize}, callback) => {
 *   const url = `https://api.example/data?page=${page}&per_page=${pageSize}`;
 *
 *   fetch(url)
 *     .then((res) => res.json())
 *     .then((resJson) => callback(resJson.employees));
 * };
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name                  | Description
 * ---------------------------|----------------
 * `row`                      | Row in the internal table
 * `body-row`                 | Body row in the internal table
 * `header-row`               | Header row in the internal table
 * `footer-row`               | Footer row in the internal table
 * `collapsed-row`            | Collapsed row
 * `expanded-row`             | Expanded row
 * `selected-row`             | Selected row
 * `nonselectable-row`        | Row that the user may not select or deselect
 * `details-opened-row`       | Row with details open
 * `odd-row`                  | Odd row
 * `even-row`                 | Even row
 * `first-row`                | The first body row
 * `first-header-row`         | The first header row
 * `first-footer-row`         | The first footer row
 * `last-row`                 | The last body row
 * `last-header-row`          | The last header row
 * `last-footer-row`          | The last footer row
 * `dragstart-row`            | Set on the row for one frame when drag is starting.
 * `dragover-above-row`       | Set on the row when the a row is dragged over above
 * `dragover-below-row`       | Set on the row when the a row is dragged over below
 * `dragover-on-top-row`      | Set on the row when the a row is dragged over on top
 * `drag-disabled-row`        | Set to a row that isn't available for dragging
 * `drop-disabled-row`        | Set to a row that can't be dropped on top of
 * `cell`                     | Cell in the internal table
 * `header-cell`              | Header cell in the internal table
 * `body-cell`                | Body cell in the internal table
 * `footer-cell`              | Footer cell in the internal table
 * `details-cell`             | Row details cell in the internal table
 * `focused-cell`             | Focused cell in the internal table
 * `odd-row-cell`             | Cell in an odd row
 * `even-row-cell`            | Cell in an even row
 * `first-row-cell`           | Cell in the first body row
 * `last-row-cell`            | Cell in the last body row
 * `first-header-row-cell`    | Cell in the first header row
 * `first-footer-row-cell`    | Cell in the first footer row
 * `last-header-row-cell`     | Cell in the last header row
 * `last-footer-row-cell`     | Cell in the last footer row
 * `loading-row-cell`         | Cell in a row that is waiting for data from data provider
 * `selected-row-cell`        | Cell in a selected row
 * `nonselectable-row-cell`   | Cell in a row that the user may not select or deselect
 * `collapsed-row-cell`       | Cell in a collapsed row
 * `expanded-row-cell`        | Cell in an expanded row
 * `details-opened-row-cell`  | Cell in an row with details open
 * `dragstart-row-cell`       | Cell in the ghost image row, but not in a source row
 * `drag-source-row-cell`     | Cell in a source row, but not in the ghost image
 * `dragover-above-row-cell`  | Cell in a row that has another row dragged over above
 * `dragover-below-row-cell`  | Cell in a row that has another row dragged over below
 * `dragover-on-top-row-cell` | Cell in a row that has another row dragged over on top
 * `drag-disabled-row-cell`   | Cell in a row that isn't available for dragging
 * `drop-disabled-row-cell`   | Cell in a row that can't be dropped on top of
 * `frozen-cell`              | Frozen cell in the internal table
 * `frozen-to-end-cell`       | Frozen to end cell in the internal table
 * `last-frozen-cell`         | Last frozen cell
 * `first-frozen-to-end-cell` | First cell frozen to end
 * `first-column-cell`        | First visible cell on a row
 * `last-column-cell`         | Last visible cell on a row
 * `reorder-allowed-cell`     | Cell in a column where another column can be reordered
 * `reorder-dragging-cell`    | Cell in a column currently being reordered
 * `resize-handle`            | Handle for resizing the columns
 * `empty-state`              | The container for the content to be displayed when there are no body rows to show
 * `reorder-ghost`            | Ghost element of the header cell being dragged
 *
 * The following state attributes are available for styling:
 *
 * Attribute             | Description                                                                                       | Part name
 * ----------------------|---------------------------------------------------------------------------------------------------|-----------
 * `loading`             | Set when the grid is loading data from data provider                                              | :host
 * `interacting`         | Keyboard navigation in interaction mode                                                           | :host
 * `navigating`          | Keyboard navigation in navigation mode                                                            | :host
 * `overflow`            | Set when rows are overflowing the grid viewport. Possible values: `top`, `bottom`, `start`, `end` | :host
 * `reordering`          | Set when the grid's columns are being reordered                                                   | :host
 * `dragover`            | Set when the grid (not a specific row) is dragged over                                            | :host
 * `dragging-rows`       | Set when grid rows are dragged                                                                    | :host
 * `reorder-status`      | Reflects the status of a cell while columns are being reordered                                   | cell
 * `frozen`              | Frozen cell                                                                                       | cell
 * `frozen-to-end`       | Cell frozen to end                                                                                | cell
 * `last-frozen`         | Last frozen cell                                                                                  | cell
 * `first-frozen-to-end` | First cell frozen to end                                                                          | cell
 * `first-column`        | First visible cell on a row                                                                       | cell
 * `last-column`         | Last visible cell on a row                                                                        | cell
 * `selected`            | Selected row                                                                                      | row
 * `expanded`            | Expanded row                                                                                      | row
 * `details-opened`      | Row with details open                                                                             | row
 * `loading`             | Row that is waiting for data from data provider                                                   | row
 * `odd`                 | Odd row                                                                                           | row
 * `first`               | The first body row                                                                                | row
 * `last`                | The last body row                                                                                 | row
 * `dragstart`           | Set for one frame when starting to drag a row. The value is a number when dragging multiple rows  | row
 * `dragover`            | Set when the row is dragged over                                                                  | row
 * `drag-disabled`       | Set to a row that isn't available for dragging                                                    | row
 * `drop-disabled`       | Set to a row that can't be dropped on top of                                                      | row
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                |
 * :--------------------------------------------------|
 * | `--vaadin-grid-background`                       |
 * | `--vaadin-grid-border-color`                     |
 * | `--vaadin-grid-border-radius`                    |
 * | `--vaadin-grid-border-width`                     |
 * | `--vaadin-grid-cell-background-color`            |
 * | `--vaadin-grid-cell-padding`                     |
 * | `--vaadin-grid-cell-text-overflow`               |
 * | `--vaadin-grid-column-border-width`              |
 * | `--vaadin-grid-column-resize-handle-color`       |
 * | `--vaadin-grid-header-font-size`                 |
 * | `--vaadin-grid-header-font-weight`               |
 * | `--vaadin-grid-header-text-color`                |
 * | `--vaadin-grid-row-background-color`             |
 * | `--vaadin-grid-row-border-width`                 |
 * | `--vaadin-grid-row-highlight-background-color`   |
 * | `--vaadin-grid-row-hover-background-color`       |
 * | `--vaadin-grid-row-odd-background-color`         |
 * | `--vaadin-grid-row-selected-background-color`    |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/grid)
 */
external class GridElement : HTMLElement {
    /**
     * String used to label the grid to screen reader users.
     */
    var accessibleName: String?

    /**
     * The item user has last interacted with. Turns to `null` after user deactivates
     * the item by re-interacting with the currently active item.
     */
    var activeItem: dynamic

    /**
     * If true, the grid's height is defined by its rows.
     *
     * Effectively, this disables the grid's virtual scrolling so that all the rows are rendered in the DOM at once.
     * If the grid has a large number of items, using the feature is discouraged to avoid performance issues.
     */
    var allRowsVisible: Boolean

    /**
     * A function that allows generating CSS `part` names for grid cells in Shadow DOM based
     * on their row and column, for styling from outside using the `::part()` selector.
     *
     * The return value should be the generated part name as a string, or multiple part names
     * separated by whitespace characters.
     *
     * Receives two arguments:
     * - `column` The `<vaadin-grid-column>` element (`undefined` for details-cell).
     * - `model` The object with the properties related with
     *   the rendered item, contains:
     *   - `model.index` The index of the item.
     *   - `model.item` The item.
     *   - `model.expanded` Sublevel toggle state.
     *   - `model.level` Level of the tree represented with a horizontal offset of the toggle button.
     *   - `model.selected` Selected state.
     */
    var cellPartNameGenerator: dynamic

    /**
     * Allows you to choose between modes for rendering columns in the grid:
     *
     * "eager" (default): All columns are rendered upfront, regardless of their visibility within the viewport.
     * This mode should generally be preferred, as it avoids the limitations imposed by the "lazy" mode.
     * Use this mode unless the grid has a large number of columns and performance outweighs the limitations
     * in priority.
     *
     * "lazy": Optimizes the rendering of cells when there are multiple columns in the grid by virtualizing
     * horizontal scrolling. In this mode, body cells are rendered only when their corresponding columns are
     * inside the visible viewport.
     *
     * Using "lazy" rendering should be used only if you're dealing with a large number of columns and performance
     * is your highest priority. For most use cases, the default "eager" mode is recommended due to the
     * limitations imposed by the "lazy" mode.
     *
     * When using the "lazy" mode, keep the following limitations in mind:
     *
     * - Row Height: When only a number of columns are visible at once, the height of a row can only be that of
     * the highest cell currently visible on that row. Make sure each cell on a single row has the same height
     * as all other cells on that row. If row cells have different heights, users may experience jumpiness when
     * scrolling the grid horizontally as lazily rendered cells with different heights are scrolled into view.
     *
     * - Auto-width Columns: For the columns that are initially outside the visible viewport but still use auto-width,
     * only the header content is taken into account when calculating the column width because the body cells
     * of the columns outside the viewport are not initially rendered.
     *
     * - Screen Reader Compatibility: Screen readers may not be able to associate the focused cells with the correct
     * headers when only a subset of the body cells on a row is rendered.
     *
     * - Keyboard Navigation: Tabbing through focusable elements inside the grid body may not work as expected because
     * some of the columns that would include focusable elements in the body cells may be outside the visible viewport
     * and thus not rendered.
     */
    var columnRendering: String?

    /**
     * Set to true to allow column reordering.
     */
    var columnReorderingAllowed: Boolean

    /**
     * Function that provides items lazily. Receives arguments `params`, `callback`
     *
     * `params.page` Requested page index
     *
     * `params.pageSize` Current page size
     *
     * `params.filters` Currently applied filters
     *
     * `params.sortOrders` Currently applied sorting orders
     *
     * `params.parentItem` When tree is used, and sublevel items
     * are requested, reference to parent item of the requested sublevel.
     * Otherwise `undefined`.
     *
     * `callback(items, size)` Callback function with arguments:
     *   - `items` Current page of items
     *   - `size` Total number of items. When tree sublevel items
     *     are requested, total number of items in the requested sublevel.
     *     Optional when tree is not used, required for tree.
     */
    var dataProvider: dynamic

    /**
     * An array containing references to items with open row details.
     */
    var detailsOpenedItems: dynamic

    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * A function that filters dragging of specific grid rows. The return value should be false
     * if dragging of the row should be disabled.
     *
     * Receives one argument:
     * - `model` The object with the properties related with
     *   the rendered item, contains:
     *   - `model.index` The index of the item.
     *   - `model.item` The item.
     *   - `model.expanded` Sublevel toggle state.
     *   - `model.level` Level of the tree represented with a horizontal offset of the toggle button.
     *   - `model.selected` Selected state.
     */
    var dragFilter: dynamic

    /**
     * A function that filters dropping on specific grid rows. The return value should be false
     * if dropping on the row should be disabled.
     *
     * Receives one argument:
     * - `model` The object with the properties related with
     *   the rendered item, contains:
     *   - `model.index` The index of the item.
     *   - `model.item` The item.
     *   - `model.expanded` Sublevel toggle state.
     *   - `model.level` Level of the tree represented with a horizontal offset of the toggle button.
     *   - `model.selected` Selected state.
     */
    var dropFilter: dynamic

    /**
     * Defines the locations within the Grid row where an element can be dropped.
     *
     * Possible values are:
     * - `between`: The drop event can happen between Grid rows.
     * - `on-top`: The drop event can happen on top of Grid rows.
     * - `on-top-or-between`: The drop event can happen either on top of or between Grid rows.
     * - `on-grid`: The drop event will not happen on any specific row, it will show the drop target outline around the whole grid.
     */
    var dropMode: String?

    /**
     * An array that contains the expanded items.
     */
    var expandedItems: dynamic

    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following JSON structure and default values:
     *
     * ```js
     * {
     *   // Accessible name (aria-label) for the select all checkbox in the
     *   // selection column header cell.
     *   selectAll: 'Select All',
     *   // Screen-reader-only label announced for the selection column header cell
     *   // when the Select All checkbox is hidden (data provider or conditional selection).
     *   selectAllUnavailable: 'Select All unavailable',
     *   // Accessible name (aria-label) for the select row checkbox in each
     *   // selection column body cell. The `{rowHeader}` placeholder is replaced with the
     *   // row header cell text content or row index if there is no row header column.
     *   selectRow: 'Select Row {rowHeader}',
     *   // Accessible name (aria-label) for the grid sorters. The `{column}`
     *   // placeholder is replaced with the sorter text content.
     *   sorter: 'Sort by {column}',
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * A function to check whether a specific item in the grid may be
     * selected or deselected by the user. Used by the selection column to
     * conditionally enable to disable checkboxes for individual items. This
     * function does not prevent programmatic selection/deselection of
     * items. Changing the function does not modify the currently selected
     * items.
     *
     * Configuring this function hides the select all checkbox of the grid
     * selection column, which means users can not select or deselect all
     * items anymore, nor do they get feedback on whether all items are
     * selected or not.
     *
     * Receives an item instance and should return a boolean indicating
     * whether users may change the selection state of that item.
     */
    var isItemSelectable: dynamic

    /**
     * Path to an item sub-property that indicates whether the item has child items.
     */
    var itemHasChildrenPath: String?

    /**
     * Path to an item sub-property that identifies the item.
     */
    var itemIdPath: String?

    /**
     * An array containing the items which will be passed to renderer functions.
     */
    var items: dynamic

    /**
     * `true` while data is being requested from the data provider.
     */
    val loading: Boolean

    /**
     * When `true`, all `<vaadin-grid-sorter>` are applied for sorting.
     */
    var multiSort: Boolean

    /**
     * When `true`, Shift-clicking an unsorted column's sorter adds it to the multi-sort.
     * Shift + Space does the same action via keyboard. This property has precedence over the
     * `multiSort` property. If `multiSortOnShiftClick` is true, the multiSort property is effectively ignored.
     */
    var multiSortOnShiftClick: Boolean

    /**
     * Controls how columns are added to the sort order when using multi-sort.
     * The sort order is visually indicated by numbers in grid sorters placed in column headers.
     *
     * By default, whenever an unsorted column is sorted, or the sort-direction of a column is
     * changed, that column gets sort priority 1, thus affecting the priority for all the other
     * sorted columns. This is identical to using `multi-sort-priority="prepend"`.
     *
     * Using this property allows to change this behavior so that sorting an unsorted column
     * would add it to the "end" of the sort, and changing column's sort direction would retain
     * it's previous priority. To set this, use `multi-sort-priority="append"`.
     */
    var multiSortPriority: String?

    /**
     * Number of items fetched at a time from the dataprovider.
     */
    var pageSize: Double?

    /**
     * Custom function for rendering the content of the row details.
     * Receives three arguments:
     *
     * - `root` The row details content DOM element. Append your content to it.
     * - `grid` The `<vaadin-grid>` element.
     * - `model` The object with the properties related with
     *   the rendered item, contains:
     *   - `model.index` The index of the item.
     *   - `model.item` The item.
     *   - `model.level` The number of the item's tree sublevel, starts from 0.
     *   - `model.expanded` True if the item's tree sublevel is expanded.
     *   - `model.selected` True if the item is selected.
     */
    var rowDetailsRenderer: dynamic

    /**
     * Marks the grid's rows to be available for dragging.
     */
    var rowsDraggable: Boolean

    /**
     * An array that contains the selected items.
     */
    var selectedItems: dynamic

    /**
     * The number of root-level items in the grid.
     */
    var size: Double?

    /**
     * Clears the cached pages and reloads data from dataprovider when needed.
     */
    fun clearCache()

    /**
     * Close the details row of a given item.
     */
    fun closeItemDetails(item: dynamic)

    /**
     * Collapses the given item tree.
     */
    fun collapseItem(item: dynamic)

    /**
     * Deselects the given item if it is already selected.
     * @param item The item object
     */
    fun deselectItem(item: dynamic)

    /**
     * Expands the given item tree.
     */
    fun expandItem(item: dynamic)

    /**
     * Runs the `dragFilter` and `dropFilter` hooks for the visible cells.
     * If the filter depends on varying conditions, you may need to
     * call this function manually in order to update the draggability when
     * the conditions change.
     */
    fun filterDragAndDrop()

    /**
     * Runs the `cellPartNameGenerator` for the visible cells.
     * If the generator depends on varying conditions, you need to
     * call this function manually in order to update the styles when
     * the conditions change.
     */
    fun generateCellPartNames()

    /**
     * Returns an object with context information about the event target:
     * - `item`: the data object corresponding to the targeted row (not specified when targeting header or footer)
     * - `column`: the column element corresponding to the targeted cell (not specified when targeting row details)
     * - `section`: whether the event targeted the body, header, footer or details of the grid
     *
     * These additional properties are included when `item` is specified:
     * - `index`: the index of the item
     * - `selected`: the selected state of the item
     * - `detailsOpened`: whether the row details are open for the item
     * - `expanded`: the expanded state of the tree toggle
     * - `level`: the tree hierarchy level
     * - `hasChildren`: whether the item has children
     *
     * The returned object is populated only when a grid cell, header, footer or row details is found in `event.composedPath()`.
     * This means mostly mouse and keyboard events. If such a grid part is not found in the path, an empty object is returned.
     * This may be the case eg. if the event is fired on the `<vaadin-grid>` element and not any deeper in the DOM, or if
     * the event targets the empty part of the grid body.
     */
    fun getEventContext(event: web.events.Event): dynamic

    /**
     * Returns a value that identifies the item. Uses `itemIdPath` if available.
     * Can be customized by overriding.
     */
    fun getItemId(item: dynamic): dynamic

    /**
     * Open the details row of a given item.
     */
    fun openItemDetails(item: dynamic)

    /**
     * Updates the `width` of all columns which have `autoWidth` set to `true`.
     */
    fun recalculateColumnWidths()

    /**
     * Requests an update for the content of cells.
     *
     * While performing the update, the following renderers are invoked:
     * - `Grid.rowDetailsRenderer`
     * - `GridColumn.renderer`
     * - `GridColumn.headerRenderer`
     * - `GridColumn.footerRenderer`
     *
     * It is not guaranteed that the update happens immediately (synchronously) after it is requested.
     */
    fun requestContentUpdate()

    /**
     * Scrolls horizontally so that the column becomes visible in the viewport.
     *
     * The column can be specified either by its index (among visible columns
     * in visual order) or by the column element itself.
     * @param indexOrColumn Column element or column index
     */
    fun scrollToColumn(indexOrColumn: dynamic)

    /**
     * Scroll to a specific row index in the virtual list. Note that the row index is
     * not always the same for any particular item. For example, sorting or filtering
     * items can affect the row index related to an item.
     *
     * The `indexes` parameter can be either a single number or multiple numbers.
     * The grid will first try to scroll to the item at the first index on the top level.
     * In case the item at the first index is expanded, the grid will then try scroll to the
     * item at the second index within the children of the expanded first item, and so on.
     * Each given index points to a child of the item at the previous index.
     *
     * Using `Infinity` as an index will point to the last item on the level.
     * @param indexes Row indexes to scroll to
     */
    fun scrollToIndex(vararg indexes: Double)

    /**
     * Selects the given item.
     * @param item The item object
     */
    fun selectItem(item: dynamic)
}

/**
 * Static members of the `<vaadin-grid>` class.
 */
private external interface GridStatics {
    /**
     * Sets the default multi-sort priority to use for all grid instances.
     * This method should be called before creating any grid instances.
     * Changing this setting does not affect the default for existing grids.
     */
    fun setDefaultMultiSortPriority(priority: String)
}

/**
 * Named exports of `@vaadin/grid/vaadin-grid.js`.
 *
 * Referencing the module registers the `<vaadin-grid>` custom element.
 */
@JsModule("@vaadin/grid/vaadin-grid.js")
private external object GridModule {
    /** The registered custom element class. */
    val Grid: GridStatics
}

/**
 * Imports `@vaadin/grid/vaadin-grid.js`, registering the `<vaadin-grid>` custom element,
 * and returns the element class.
 */
fun registerGrid(): dynamic = GridModule.Grid

/**
 * Static entry points of `<vaadin-grid>`.
 *
 * Calling these members imports the upstream package and registers the element.
 */
object Grid {
    /**
     * Sets the default multi-sort priority to use for all grid instances.
     * This method should be called before creating any grid instances.
     * Changing this setting does not affect the default for existing grids.
     */
    fun setDefaultMultiSortPriority(priority: String) = GridModule.Grid.setDefaultMultiSortPriority(priority)
}

/**
 * Creates a `<vaadin-grid>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createGrid(configure: GridElement.() -> Unit = {}): GridElement {
    registerGrid()
    return document.createElement(VaadinTagName.grid).apply(configure)
}
