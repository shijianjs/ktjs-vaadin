package vaadin.virtuallist

import web.html.HTMLElement

/** The model passed to a [VirtualListRenderer]. */
external interface VirtualListItemModel {
    /** The index of the item in the list. */
    var index: Double

    /** The item data. */
    var item: kotlin.js.JsAny?
}

/** Renders the content of a virtual list item. */
typealias VirtualListRenderer = (HTMLElement, VirtualListElement, VirtualListItemModel) -> Unit
