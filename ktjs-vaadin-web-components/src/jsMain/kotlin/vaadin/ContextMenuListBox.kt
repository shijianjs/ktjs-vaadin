package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-context-menu-list-box>` is a Web Component for wrapping `<vaadin-context-menu>` items.
 *
 * ```html
 * <vaadin-context-menu>
 *   <vaadin-context-menu-list-box slot="overlay">
 *     <vaadin-context-menu-item>Edit</vaadin-context-menu-item>
 *     <vaadin-context-menu-item>Delete</vaadin-context-menu-item>
 *   </vaadin-context-menu-list-box>
 * </vaadin-context-menu>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name         | Description
 * ------------------|------------------------
 * `items`           | The items container
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/context-menu)
 */
external class ContextMenuListBoxElement : HTMLElement {
    /**
     * If true, the user cannot interact with this element.
     * When the element is disabled, the selected item is
     * not updated when `selected` property is changed.
     */
    var disabled: Boolean

    /**
     * A read-only list of items from which a selection can be made.
     * It is populated from the elements passed to the light DOM,
     * and updated dynamically when adding or removing items.
     */
    val items: dynamic

    /**
     * Define how items are disposed in the dom.
     * Possible values are: `horizontal|vertical`.
     * It also changes navigation keys from left/right to up/down.
     */
    val orientation: String?

    /**
     * The index of the item selected in the items array.
     * Note: Not updated when used in `multiple` selection mode.
     */
    var selected: Double?
}

/**
 * Named exports of `@vaadin/context-menu/vaadin-context-menu-list-box.js`.
 *
 * Referencing the module registers the `<vaadin-context-menu-list-box>` custom element.
 */
@JsModule("@vaadin/context-menu/vaadin-context-menu-list-box.js")
private external object ContextMenuListBoxModule {
    /** The registered custom element class. */
    val ContextMenuListBox: dynamic
}

/**
 * Imports `@vaadin/context-menu/vaadin-context-menu-list-box.js`, registering the `<vaadin-context-menu-list-box>` custom element,
 * and returns the element class.
 */
fun registerContextMenuListBox(): dynamic = ContextMenuListBoxModule.ContextMenuListBox

/**
 * Creates a `<vaadin-context-menu-list-box>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createContextMenuListBox(configure: ContextMenuListBoxElement.() -> Unit = {}): ContextMenuListBoxElement {
    registerContextMenuListBox()
    return document.createElement(VaadinTagName.contextMenuListBox).apply(configure)
}
