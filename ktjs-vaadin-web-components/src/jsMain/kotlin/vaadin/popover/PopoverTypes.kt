package vaadin.popover

/**
 * Renders the popover content into [root].
 *
 * Deprecated upstream: add content elements as children of the popover instead.
 */
typealias PopoverRenderer = (root: web.html.HTMLElement, popover: PopoverElement) -> Unit
