package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-grid-tree-column>` is a helper element for the `<vaadin-grid>`
 * that provides default renderer and functionality for toggling tree/hierarchical items.
 *
 * #### Example:
 * ```html
 * <vaadin-grid>
 *  <vaadin-grid-tree-column path="name.first"></vaadin-grid-tree-column>
 *
 *  <vaadin-grid-column>
 *    ...
 * ```
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/grid)
 */
external class GridTreeColumnElement : HTMLElement {
    /**
     * Automatically sets the width of the column based on the column contents when this is set to `true`.
     *
     * For performance reasons the column width is calculated automatically only once when the grid items
     * are rendered for the first time and the calculation only considers the rows which are currently
     * rendered in DOM (a bit more than what is currently visible). If the grid is scrolled, or the cell
     * content changes, the column width might not match the contents anymore.
     *
     * Hidden columns are ignored in the calculation and their widths are not automatically updated when
     * you show a column that was initially hidden.
     *
     * You can manually trigger the auto sizing behavior again by calling `grid.recalculateColumnWidths()`.
     *
     * The column width may still grow larger when `flexGrow` is not 0.
     */
    var autoWidth: Boolean

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
     * JS Path of the property in the item used as text content for the tree toggle.
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
     * Aligns the columns cell content horizontally.
     */
    var textAlign: String?

    /**
     * Width of the cells for this column.
     * Setting the property to `null` or `undefined` restores the default width.
     *
     * Please note that using the `em` length unit is discouraged as
     * it might lead to misalignment issues if the header, body, and footer
     * cells have different font sizes. Instead, use `rem` if you need
     * a length unit relative to the font size.
     */
    var width: String?
}

/**
 * Named exports of `@vaadin/grid/vaadin-grid-tree-column.js`.
 *
 * Referencing the module registers the `<vaadin-grid-tree-column>` custom element.
 */
@JsModule("@vaadin/grid/vaadin-grid-tree-column.js")
private external object GridTreeColumnModule {
    /** The registered custom element class. */
    val GridTreeColumn: dynamic
}

/**
 * Imports `@vaadin/grid/vaadin-grid-tree-column.js`, registering the `<vaadin-grid-tree-column>` custom element,
 * and returns the element class.
 */
fun registerGridTreeColumn(): dynamic = GridTreeColumnModule.GridTreeColumn

/**
 * Creates a `<vaadin-grid-tree-column>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createGridTreeColumn(configure: GridTreeColumnElement.() -> Unit = {}): GridTreeColumnElement {
    registerGridTreeColumn()
    return document.createElement(VaadinTagName.gridTreeColumn).apply(configure)
}
