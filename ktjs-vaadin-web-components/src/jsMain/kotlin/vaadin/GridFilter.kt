package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-grid-filter>` is a helper element for the `<vaadin-grid>` that provides out-of-the-box UI controls,
 * and handlers for filtering the grid data.
 *
 * #### Example:
 * ```html
 * <vaadin-grid-column id="column"></vaadin-grid-column>
 * ```
 * ```js
 * const column = document.querySelector('#column');
 * column.headerRenderer = (root, column) => {
 *   let filter = root.firstElementChild;
 *   if (!filter) {
 *     filter = document.createElement('vaadin-grid-filter');
 *     root.appendChild(filter);
 *   }
 *   filter.path = 'name.first';
 * };
 * column.renderer = (root, column, model) => {
 *   root.textContent = model.item.name.first;
 * };
 * ```
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/grid)
 */
external class GridFilterElement : HTMLElement {
    /**
     * JS Path of the property in the item used for filtering the data.
     */
    var path: String?

    /**
     * Current filter value.
     */
    var value: String?

    /**
     * Moves focus to the filter field.
     */
    fun focus()
}

/**
 * Named exports of `@vaadin/grid/vaadin-grid-filter.js`.
 *
 * Referencing the module registers the `<vaadin-grid-filter>` custom element.
 */
@JsModule("@vaadin/grid/vaadin-grid-filter.js")
private external object GridFilterModule {
    /** The registered custom element class. */
    val GridFilter: dynamic
}

/**
 * Imports `@vaadin/grid/vaadin-grid-filter.js`, registering the `<vaadin-grid-filter>` custom element,
 * and returns the element class.
 */
fun registerGridFilter(): dynamic = GridFilterModule.GridFilter

/**
 * Creates a `<vaadin-grid-filter>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createGridFilter(configure: GridFilterElement.() -> Unit = {}): GridFilterElement {
    registerGridFilter()
    return document.createElement(VaadinTagName.gridFilter).apply(configure)
}
