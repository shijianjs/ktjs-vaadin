package vaadin.contextmenu

import js.reflect.unsafeCast
import web.events.Event
import web.html.HTMLElement

/**
 * Union of an element and a tag name.
 *
 * Kotlin has no union types, so the members are converted explicitly with the [HTMLElementOrString]
 * factory functions.
 */
external interface HTMLElementOrString

/** Wraps an element as a [HTMLElementOrString]. */
fun HTMLElementOrString(value: HTMLElement): HTMLElementOrString = unsafeCast(value)

/** Wraps a tag name as a [HTMLElementOrString]. */
fun HTMLElementOrString(value: String): HTMLElementOrString = unsafeCast(value)

/** Union of a list of theme names and a single theme name. */
external interface StringArrayOrString

/** Wraps a list of theme names as a [StringArrayOrString]. */
fun StringArrayOrString(value: Array<String>): StringArrayOrString = unsafeCast(value)

/** Wraps a single theme name as a [StringArrayOrString]. */
fun StringArrayOrString(value: String): StringArrayOrString = unsafeCast(value)

/** Additional detail about the element a context menu was opened for. */
external interface ContextMenuRendererContextDetail {
    /** The event that triggered opening the context menu. */
    var sourceEvent: Event
}

/** Context passed to a [ContextMenuRenderer]. */
external interface ContextMenuRendererContext {
    /** The element the context menu was opened for. */
    var target: HTMLElement

    /** Extra detail, when the menu was opened from an event. */
    var detail: ContextMenuRendererContextDetail?
}

/** Renders the content of a context menu. */
typealias ContextMenuRenderer = (HTMLElement, ContextMenuElement, ContextMenuRendererContext) -> Unit

/**
 * An item in the declarative `items` array of `<vaadin-context-menu>`.
 *
 * Items can be nested through [children]; a parent item with children opens a sub menu.
 */
external interface ContextMenuItemData {
    /** The text rendered in the item. */
    var text: String?

    /** Tooltip text shown for the item. */
    var tooltip: String?

    /** Position of the tooltip relative to the item. */
    var tooltipPosition: String?

    /** An element or a tag name used to render the item content. */
    var component: HTMLElementOrString?

    /** Whether the item is disabled. */
    var disabled: Boolean?

    /** Whether the item is checked. */
    var checked: Boolean?

    /** Whether the sub menu stays open after the item is selected. */
    var keepOpen: Boolean?

    /** A theme name or an array of theme names for the item. */
    var theme: StringArrayOrString?

    /** A CSS class name for the item. */
    var className: String?

    /** Sub menu items. */
    var children: Array<ContextMenuItemData>?
}
