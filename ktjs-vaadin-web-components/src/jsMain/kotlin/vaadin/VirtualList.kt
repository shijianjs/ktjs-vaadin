package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-virtual-list>` is a Web Component for displaying a virtual/infinite list of items.
 *
 * ```html
 * <vaadin-virtual-list></vaadin-virtual-list>
 * ```
 *
 * ```js
 * const list = document.querySelector('vaadin-virtual-list');
 * list.items = items; // An array of data items
 * list.renderer = (root, list, {item, index}) => {
 *   root.textContent = `#${index}: ${item.name}`
 * }
 * ```
 *
 * ### Styling
 *
 * The following state attributes are available for styling:
 *
 * Attribute        | Description
 * -----------------|--------------------------------------------
 * `overflow`       | Set to `top`, `bottom`, both, or none.
 *
 * ### Built-in Theme Variants
 *
 * `<vaadin-virtual-list>` supports the following theme variants:
 *
 * Theme variant                            | Description
 * -----------------------------------------|---------------
 * `theme="overflow-indicators"`            | Shows visual indicators at the top and bottom when the content is scrolled
 * `theme="overflow-indicator-top"`         | Shows the visual indicator at the top when the content is scrolled
 * `theme="overflow-indicator-top-bottom"`  | Shows the visual indicator at the bottom when the content is scrolled
 *
 * ### Custom CSS Properties
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                |
 * :--------------------------------------------------|
 * `--vaadin-virtual-list-overflow-indicator-color`   |
 * `--vaadin-virtual-list-overflow-indicator-height`  |
 * `--vaadin-virtual-list-padding-block`              |
 * `--vaadin-virtual-list-padding-inline`             |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/virtual-list)
 */
external class VirtualListElement : HTMLElement {
    /**
     * Gets the index of the first visible item in the viewport.
     */
    val firstVisibleIndex: dynamic

    /**
     * A function that generates accessible names for virtual list items.
     * The function gets the item as an argument and the
     * return value should be a string representing that item. The
     * result gets applied to the corresponding virtual list child element
     * as an `aria-label` attribute.
     */
    var itemAccessibleNameGenerator: dynamic

    /**
     * An array containing items determining how many instances to render.
     */
    var items: dynamic

    /**
     * Gets the index of the last visible item in the viewport.
     */
    val lastVisibleIndex: dynamic

    /**
     * Custom function for rendering the content of every item.
     * Receives three arguments:
     *
     * - `root` The render target element representing one item at a time.
     * - `virtualList` The reference to the `<vaadin-virtual-list>` element.
     * - `model` The object with the properties related with the rendered
     *   item, contains:
     *   - `model.index` The index of the rendered item.
     *   - `model.item` The item.
     */
    var renderer: dynamic

    /**
     * Requests an update for the content of the rows.
     * While performing the update, it invokes the renderer passed in the `renderer` property for each visible row.
     *
     * It is not guaranteed that the update happens immediately (synchronously) after it is requested.
     */
    fun requestContentUpdate()

    /**
     * Scroll to a specific index in the virtual list.
     * @param index Index to scroll to
     */
    fun scrollToIndex(index: Double)
}

/**
 * Named exports of `@vaadin/virtual-list/vaadin-virtual-list.js`.
 *
 * Referencing the module registers the `<vaadin-virtual-list>` custom element.
 */
@JsModule("@vaadin/virtual-list/vaadin-virtual-list.js")
private external object VirtualListModule {
    /** The registered custom element class. */
    val VirtualList: dynamic
}

/**
 * Imports `@vaadin/virtual-list/vaadin-virtual-list.js`, registering the `<vaadin-virtual-list>` custom element,
 * and returns the element class.
 */
fun registerVirtualList(): dynamic = VirtualListModule.VirtualList

/**
 * Creates a `<vaadin-virtual-list>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createVirtualList(configure: VirtualListElement.() -> Unit = {}): VirtualListElement {
    registerVirtualList()
    return document.createElement(VaadinTagName.virtualList).apply(configure)
}
