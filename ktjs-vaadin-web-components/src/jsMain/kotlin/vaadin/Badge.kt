package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-badge>` is a Web Component for displaying badges.
 *
 * ```html
 * <vaadin-badge>New</vaadin-badge>
 * ```
 *
 * ### Slots
 *
 * Name     | Description
 * ---------|-------------
 * (none)   | Default slot for the badge text content
 * `icon`   | Slot for an icon to place before the text
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name  | Description
 * -----------|-------------
 * `icon`     | The container for the icon slot
 * `number`   | The container for the number value
 * `content`  | The container for the default slot
 *
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|-------------
 * `has-icon`     | Set when the badge has content in the icon slot
 * `has-content`  | Set when the badge has content in the default slot
 * `has-number`   | Set when the badge has a number value
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property              |
 * :--------------------------------|
 * `--vaadin-badge-background`      |
 * `--vaadin-badge-border-color`    |
 * `--vaadin-badge-border-radius`   |
 * `--vaadin-badge-border-width`    |
 * `--vaadin-badge-font-size`       |
 * `--vaadin-badge-font-weight`     |
 * `--vaadin-badge-font-family`     |
 * `--vaadin-badge-gap`             |
 * `--vaadin-badge-line-height`     |
 * `--vaadin-badge-padding`         |
 * `--vaadin-badge-text-color`      |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/badge)
 */
external class BadgeElement : HTMLElement {
    /**
     * The number to display in the badge.
     */
    var number: Double?
}

/**
 * Named exports of `@vaadin/badge/vaadin-badge.js`.
 *
 * Referencing the module registers the `<vaadin-badge>` custom element.
 */
@JsModule("@vaadin/badge/vaadin-badge.js")
private external object BadgeModule {
    /** The registered custom element class. */
    val Badge: dynamic
}

/**
 * Imports `@vaadin/badge/vaadin-badge.js`, registering the `<vaadin-badge>` custom element,
 * and returns the element class.
 */
fun registerBadge(): dynamic = BadgeModule.Badge

/**
 * Creates a `<vaadin-badge>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createBadge(configure: BadgeElement.() -> Unit = {}): BadgeElement {
    registerBadge()
    return document.createElement(VaadinTagName.badge).apply(configure)
}
