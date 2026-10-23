package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-grid-tree-toggle>` is a helper element for the `<vaadin-grid>`
 * that provides toggle and level display functionality for the item tree.
 *
 * #### Example:
 * ```html
 * <vaadin-grid-column id="column"></vaadin-grid-column>
 * ```
 * ```js
 * const column = document.querySelector('#column');
 * column.renderer = (root, column, model) => {
 *   let treeToggle = root.firstElementChild;
 *   if (!treeToggle) {
 *     treeToggle = document.createElement('vaadin-grid-tree-toggle');
 *     treeToggle.addEventListener('expanded-changed', () => { ... });
 *     root.appendChild(treeToggle);
 *   }
 *   treeToggle.leaf = !model.item.hasChildren;
 *   treeToggle.level = level;
 *   treeToggle.expanded = expanded;
 *   treeToggle.textContent = model.item.name;
 * };
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name | Description
 * ---|---
 * `toggle` | The tree toggle icon
 *
 * The following state attributes are available for styling:
 *
 * Attribute  | Description
 * -----------|-------------------------------------
 * `expanded` | When present, the toggle is expanded
 * `leaf`     | When present, the toggle is not expandable, i. e., the current item is a leaf
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                          |
 * :--------------------------------------------|
 * | `--vaadin-grid-tree-toggle-level-offset`   |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/grid)
 */
external class GridTreeToggleElement : HTMLElement {
    /**
     * Sublevel toggle state.
     */
    var expanded: Boolean

    /**
     * Hides the toggle icon and disables toggling a tree sublevel.
     */
    var leaf: Boolean

    /**
     * Current level of the tree represented with a horizontal offset
     * of the toggle button.
     */
    var level: Double?
}

/**
 * Named exports of `@vaadin/grid/vaadin-grid-tree-toggle.js`.
 *
 * Referencing the module registers the `<vaadin-grid-tree-toggle>` custom element.
 */
@JsModule("@vaadin/grid/vaadin-grid-tree-toggle.js")
private external object GridTreeToggleModule {
    /** The registered custom element class. */
    val GridTreeToggle: dynamic
}

/**
 * Imports `@vaadin/grid/vaadin-grid-tree-toggle.js`, registering the `<vaadin-grid-tree-toggle>` custom element,
 * and returns the element class.
 */
fun registerGridTreeToggle(): dynamic = GridTreeToggleModule.GridTreeToggle

/**
 * Creates a `<vaadin-grid-tree-toggle>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createGridTreeToggle(configure: GridTreeToggleElement.() -> Unit = {}): GridTreeToggleElement {
    registerGridTreeToggle()
    return document.createElement(VaadinTagName.gridTreeToggle).apply(configure)
}
