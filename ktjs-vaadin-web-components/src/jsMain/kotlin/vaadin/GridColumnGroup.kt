package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * A `<vaadin-grid-column-group>` is used to make groups of columns in `<vaadin-grid>` and
 * to configure additional headers and footers.
 *
 * Groups can be nested to create complex header and footer configurations.
 *
 * #### Example:
 * ```html
 * <vaadin-grid-column-group resizable id="columnGroup">
 *   <vaadin-grid-column id="column1"></vaadin-grid-column>
 *   <vaadin-grid-column id="column2"></vaadin-grid-column>
 * </vaadin-grid-column-group>
 * ```
 *
 * ```js
 * const columnGroup = document.querySelector('#columnGroup');
 * columnGroup.headerRenderer = (root, columnGroup) => {
 *   root.textContent = 'header';
 * }
 *
 * const column1 = document.querySelector('#column1');
 * column1.headerRenderer = (root, column) => { ... };
 * column1.renderer = (root, column, model) => { ... };
 *
 * const column2 = document.querySelector('#column2');
 * column2.headerRenderer = (root, column) => { ... };
 * column2.renderer = (root, column, model) => { ... };
 * ```
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/grid)
 */
external class GridColumnGroupElement : HTMLElement {
    /**
     * Flex grow ratio for the column group as the sum of the ratios of its child columns.
     */
    val flexGrow: Double?

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
     * Width of the column group as the sum of the widths of its child columns.
     */
    val width: String?
}

/**
 * Named exports of `@vaadin/grid/vaadin-grid-column-group.js`.
 *
 * Referencing the module registers the `<vaadin-grid-column-group>` custom element.
 */
@JsModule("@vaadin/grid/vaadin-grid-column-group.js")
private external object GridColumnGroupModule {
    /** The registered custom element class. */
    val GridColumnGroup: dynamic
}

/**
 * Imports `@vaadin/grid/vaadin-grid-column-group.js`, registering the `<vaadin-grid-column-group>` custom element,
 * and returns the element class.
 */
fun registerGridColumnGroup(): dynamic = GridColumnGroupModule.GridColumnGroup

/**
 * Creates a `<vaadin-grid-column-group>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createGridColumnGroup(configure: GridColumnGroupElement.() -> Unit = {}): GridColumnGroupElement {
    registerGridColumnGroup()
    return document.createElement(VaadinTagName.gridColumnGroup).apply(configure)
}
