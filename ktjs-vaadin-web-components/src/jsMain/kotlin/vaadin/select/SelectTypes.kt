package vaadin.select

import web.html.HTMLElement

/** An item in the declarative `items` array of `<vaadin-select>`. */
external interface SelectItemData {
    /** The text rendered in the item. */
    var label: String?

    /** The value of the item. */
    var value: String?

    /** The tag name of the component used to render the item. */
    var component: String?

    /** Whether the item is disabled. */
    var disabled: Boolean?

    /** A CSS class name for the item. */
    var className: String?
}

/** Renders the content of the select overlay. */
typealias SelectRenderer = (HTMLElement, SelectElement) -> Unit
