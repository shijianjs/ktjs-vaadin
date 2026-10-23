package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-select-item>` is a Web Component for creating `<vaadin-select>` items.
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
 * Custom CSS property                |
 * :----------------------------------|
 * | `--vaadin-item-border-radius`    |
 * | `--vaadin-item-checkmark-color`  |
 * | `--vaadin-item-gap`              |
 * | `--vaadin-item-height`           |
 * | `--vaadin-item-padding`          |
 * | `--vaadin-item-text-align`       |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/select)
 */
external class SelectItemElement : HTMLElement {
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
 * Named exports of `@vaadin/select/vaadin-select-item.js`.
 *
 * Referencing the module registers the `<vaadin-select-item>` custom element.
 */
@JsModule("@vaadin/select/vaadin-select-item.js")
private external object SelectItemModule {
    /** The registered custom element class. */
    val SelectItem: dynamic
}

/**
 * Imports `@vaadin/select/vaadin-select-item.js`, registering the `<vaadin-select-item>` custom element,
 * and returns the element class.
 */
fun registerSelectItem(): dynamic = SelectItemModule.SelectItem

/**
 * Creates a `<vaadin-select-item>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createSelectItem(configure: SelectItemElement.() -> Unit = {}): SelectItemElement {
    registerSelectItem()
    return document.createElement(VaadinTagName.selectItem).apply(configure)
}
