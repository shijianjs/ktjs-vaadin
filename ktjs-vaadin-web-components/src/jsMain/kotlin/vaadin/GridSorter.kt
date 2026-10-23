package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-grid-sorter>` is a helper element for the `<vaadin-grid>` that provides out-of-the-box UI controls,
 * visual feedback, and handlers for sorting the grid data.
 *
 * #### Example:
 * ```html
 * <vaadin-grid-column id="column"></vaadin-grid-column>
 * ```
 * ```js
 * const column = document.querySelector('#column');
 * column.renderer = (root, column, model) => {
 *   let sorter = root.firstElementChild;
 *   if (!sorter) {
 *     sorter = document.createElement('vaadin-grid-sorter');
 *     root.appendChild(sorter);
 *   }
 *   sorter.path = 'name.first';
 * };
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name | Description
 * ----------------|----------------
 * `content` | The slotted content wrapper
 * `indicators` | The internal sorter indicators.
 * `order` | The internal sorter order
 *
 * The following state attributes are available for styling:
 *
 * Attribute    | Description
 * -------------|---------------------------
 * `direction`  | Sort direction of a sorter
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/grid)
 */
external class GridSorterElement : HTMLElement {
    /**
     * How to sort the data.
     * Possible values are `asc` to use an ascending algorithm, `desc` to sort the data in
     * descending direction, or `null` for not sorting the data.
     */
    var direction: String?

    /**
     * JS Path of the property in the item used for sorting the data.
     */
    var path: String?
}

/**
 * Named exports of `@vaadin/grid/vaadin-grid-sorter.js`.
 *
 * Referencing the module registers the `<vaadin-grid-sorter>` custom element.
 */
@JsModule("@vaadin/grid/vaadin-grid-sorter.js")
private external object GridSorterModule {
    /** The registered custom element class. */
    val GridSorter: dynamic
}

/**
 * Imports `@vaadin/grid/vaadin-grid-sorter.js`, registering the `<vaadin-grid-sorter>` custom element,
 * and returns the element class.
 */
fun registerGridSorter(): dynamic = GridSorterModule.GridSorter

/**
 * Creates a `<vaadin-grid-sorter>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createGridSorter(configure: GridSorterElement.() -> Unit = {}): GridSorterElement {
    registerGridSorter()
    return document.createElement(VaadinTagName.gridSorter).apply(configure)
}
