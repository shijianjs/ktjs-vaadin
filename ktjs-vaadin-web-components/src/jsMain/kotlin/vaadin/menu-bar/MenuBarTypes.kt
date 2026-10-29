package vaadin.menubar

import js.reflect.unsafeCast
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

/** A top-level item in the declarative `items` array of `<vaadin-menu-bar>`. */
external interface MenuBarItem {
    /** The text rendered in the menu button. */
    var text: String?

    /** Tooltip text shown for the button. */
    var tooltip: String?

    /** Position of the tooltip relative to the button. */
    var tooltipPosition: String?

    /** An element or a tag name used to render the button content. */
    var component: HTMLElementOrString?

    /** Whether the button is disabled. */
    var disabled: Boolean?

    /** A theme name or an array of theme names for the button. */
    var theme: StringArrayOrString?

    /** A CSS class name for the button. */
    var className: String?

    /** The sub menu items of this button. */
    var children: Array<SubMenuItem>?
}

/** An item in a sub menu of `<vaadin-menu-bar>`. */
external interface SubMenuItem : MenuBarItem {
    /** Whether the item is checked. */
    var checked: Boolean?
}

/**
 * Localisation texts of `<vaadin-menu-bar>`, declared as `MenuBarI18n` in the upstream `.d.ts`.
 * The manifest only records the `i18n` property as `object`, so the interface is maintained here.
 */
external interface MenuBarI18n {
    /** Accessible label of the overflow button, shown when items no longer fit. */
    var moreOptions: String?
}
