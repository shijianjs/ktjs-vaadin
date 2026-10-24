package vaadin.menubar

/** A top-level item in the declarative `items` array of `<vaadin-menu-bar>`. */
external interface MenuBarItem {
    /** The text rendered in the menu button. */
    var text: String?

    /** Tooltip text shown for the button. */
    var tooltip: String?

    /** Position of the tooltip relative to the button. */
    var tooltipPosition: String?

    /** An element or a tag name used to render the button content. */
    var component: dynamic

    /** Whether the button is disabled. */
    var disabled: Boolean?

    /** A theme name or an array of theme names for the button. */
    var theme: dynamic

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
