package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-list-box>` is a Web Component for creating menus.
 *
 * ```html
 * <vaadin-list-box selected="2">
 *   <vaadin-item>Item 1</vaadin-item>
 *   <vaadin-item>Item 2</vaadin-item>
 *   <vaadin-item>Item 3</vaadin-item>
 *   <vaadin-item>Item 4</vaadin-item>
 * </vaadin-list-box>
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
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|---------------------------------
 * `has-tooltip`  | Set when the element has a slotted tooltip
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/list-box)
 */
external class ListBoxElement : HTMLElement {
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
     * Specifies that multiple options can be selected at once.
     */
    var multiple: Boolean

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

    /**
     * Array of indexes of the items selected in the items array
     * Note: Not updated when used in single selection mode.
     */
    var selectedValues: dynamic
}

/**
 * Named exports of `@vaadin/list-box/vaadin-list-box.js`.
 *
 * Referencing the module registers the `<vaadin-list-box>` custom element.
 */
@JsModule("@vaadin/list-box/vaadin-list-box.js")
private external object ListBoxModule {
    /** The registered custom element class. */
    val ListBox: dynamic
}

/**
 * Imports `@vaadin/list-box/vaadin-list-box.js`, registering the `<vaadin-list-box>` custom element,
 * and returns the element class.
 */
fun registerListBox(): dynamic = ListBoxModule.ListBox

/**
 * Creates a `<vaadin-list-box>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createListBox(configure: ListBoxElement.() -> Unit = {}): ListBoxElement {
    registerListBox()
    return document.createElement(VaadinTagName.listBox).apply(configure)
}
