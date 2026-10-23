package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-grid-selection-column>` is a helper element for the `<vaadin-grid>`
 * that provides default renderers and functionality for item selection.
 *
 * #### Example:
 * ```html
 * <vaadin-grid>
 *  <vaadin-grid-selection-column frozen auto-select></vaadin-grid-selection-column>
 *
 *  <vaadin-grid-column>
 *    ...
 * ```
 *
 * By default the selection column displays `<vaadin-checkbox>` elements in the
 * column cells. The checkboxes in the body rows toggle selection of the corresponding row items.
 *
 * When the grid data is provided as an array of [`items`](#/elements/vaadin-grid#property-items),
 * the column header gets an additional checkbox that can be used for toggling
 * selection for all the items at once.
 *
 * __The default content can also be overridden__
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/grid)
 */
external class GridSelectionColumnElement : HTMLElement {
    /**
     * When true, the active item gets automatically selected.
     */
    var autoSelect: Boolean

    /**
     * Override `autoWidth` to enable auto-width
     */
    var autoWidth: Boolean

    /**
     * When true, rows can be selected by dragging over the selection column.
     */
    var dragSelect: Boolean

    /**
     * Flex grow ratio for the cell widths. When set to 0, cell width is fixed.
     */
    var flexGrow: Double?

    /**
     * Custom part name for the footer cell.
     */
    var footerPartName: String?

    /**
     * Custom function for rendering the footer content.
     * Receives two arguments:
     *
     * - `root` The footer cell content DOM element. Append your content to it.
     * - `column` The `<vaadin-grid-column>` element.
     */
    var footerRenderer: dynamic

    /**
     * When true, the column is frozen. When a column inside of a column group is frozen,
     * all of the sibling columns inside the group will get frozen also.
     */
    var frozen: Boolean

    /**
     * When true, the column is frozen to end of grid.
     *
     * When a column inside of a column group is frozen to end, all of the sibling columns
     * inside the group will get frozen to end also.
     *
     * Column can not be set as `frozen` and `frozenToEnd` at the same time.
     */
    var frozenToEnd: Boolean

    /**
     * Text content to display in the header cell of the column.
     */
    var header: String?

    /**
     * Custom part name for the header cell.
     */
    var headerPartName: String?

    /**
     * Custom function for rendering the header content.
     * Receives two arguments:
     *
     * - `root` The header cell content DOM element. Append your content to it.
     * - `column` The `<vaadin-grid-column>` element.
     */
    var headerRenderer: dynamic

    /**
     * Path to an item sub-property whose value gets displayed in the column body cells.
     * The property name is also shown in the column header if an explicit header or renderer isn't defined.
     */
    var path: String?

    /**
     * Custom function for rendering the cell content.
     * Receives three arguments:
     *
     * - `root` The cell content DOM element. Append your content to it.
     * - `column` The `<vaadin-grid-column>` element.
     * - `model` The object with the properties related with
     *   the rendered item, contains:
     *   - `model.index` The index of the item.
     *   - `model.item` The item.
     *   - `model.expanded` Sublevel toggle state.
     *   - `model.level` Level of the tree represented with a horizontal offset of the toggle button.
     *   - `model.selected` Selected state.
     *   - `model.detailsOpened` Details opened state.
     *   - `model.hasChildren` Whether the item has children.
     */
    var renderer: dynamic

    /**
     * When set to true, the column is user-resizable.
     */
    var resizable: Boolean

    /**
     * When true, the cells for this column will be rendered with the `role` attribute
     * set as `rowheader`, instead of the `gridcell` role value used by default.
     *
     * When a column is set as row header, its cells will be announced by screen readers
     * while navigating to help user identify the current row as uniquely as possible.
     */
    var rowHeader: Boolean

    /**
     * When true, all the items are selected.
     */
    var selectAll: Boolean

    /**
     * Aligns the columns cell content horizontally.
     */
    var textAlign: String?

    /**
     * Width of the cells for this column.
     */
    var width: String?
}

/**
 * Named exports of `@vaadin/grid/vaadin-grid-selection-column.js`.
 *
 * Referencing the module registers the `<vaadin-grid-selection-column>` custom element.
 */
@JsModule("@vaadin/grid/vaadin-grid-selection-column.js")
private external object GridSelectionColumnModule {
    /** The registered custom element class. */
    val GridSelectionColumn: dynamic
}

/**
 * Imports `@vaadin/grid/vaadin-grid-selection-column.js`, registering the `<vaadin-grid-selection-column>` custom element,
 * and returns the element class.
 */
fun registerGridSelectionColumn(): dynamic = GridSelectionColumnModule.GridSelectionColumn

/**
 * Creates a `<vaadin-grid-selection-column>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createGridSelectionColumn(configure: GridSelectionColumnElement.() -> Unit = {}): GridSelectionColumnElement {
    registerGridSelectionColumn()
    return document.createElement(VaadinTagName.gridSelectionColumn).apply(configure)
}
