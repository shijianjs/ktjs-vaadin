package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-item>` is a Web Component providing layout for items in tabs and menus.
 *
 * ```html
 * <vaadin-item>Item content</vaadin-item>
 * ```
 *
 * ### Selectable
 *
 * `<vaadin-item>` has the `selected` property and the corresponding state attribute.
 * Currently, the component sets the `selected` to false, when `disabled` property is set to true.
 * But other than that, the `<vaadin-item>` does not switch selection by itself.
 * In general, it is the wrapper component, like `<vaadin-list-box>`, which should update
 * the `selected` property on the items, e. g. on mousedown or when Enter / Spacebar is pressed.
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name    | Description
 * -------------|----------------
 * `checkmark`  | The graphical checkmark shown for a selected item
 * `content`    | The element that wraps the slot
 *
 * The following state attributes are available for styling:
 *
 * Attribute    | Description
 * -------------|-------------
 * `active`     | Set when the item is pressed down, either with mouse, touch or the keyboard.
 * `disabled`   | Set when the item is disabled.
 * `focus-ring` | Set when the item is focused using the keyboard.
 * `focused`    | Set when the item is focused.
 * `selected`   | Set when the item is selected
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                  |
 * :------------------------------------|
 * | `--vaadin-item-border-radius`      |
 * | `--vaadin-item-checkmark-color`    |
 * | `--vaadin-item-checkmark-display`  |
 * | `--vaadin-item-gap`                |
 * | `--vaadin-item-height`             |
 * | `--vaadin-item-padding`            |
 * | `--vaadin-item-text-align`         |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/item)
 */
external class ItemElement : HTMLElement {
    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * If true, the item is in selected state.
     */
    var selected: Boolean

    /**
     * Submittable string value. The default value is the trimmed text content of the element.
     */
    var value: String?
}

/**
 * Named exports of `@vaadin/item/vaadin-item.js`.
 *
 * Referencing the module registers the `<vaadin-item>` custom element.
 */
@JsModule("@vaadin/item/vaadin-item.js")
private external object ItemModule {
    /** The registered custom element class. */
    val Item: dynamic
}

/**
 * Imports `@vaadin/item/vaadin-item.js`, registering the `<vaadin-item>` custom element,
 * and returns the element class.
 */
fun registerItem(): dynamic = ItemModule.Item

/**
 * Creates a `<vaadin-item>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createItem(configure: ItemElement.() -> Unit = {}): ItemElement {
    registerItem()
    return document.createElement(VaadinTagName.item).apply(configure)
}
