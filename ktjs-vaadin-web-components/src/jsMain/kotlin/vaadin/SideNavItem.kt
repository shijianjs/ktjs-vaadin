package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * A navigation item to be used within `<vaadin-side-nav>`. Represents a navigation target.
 * Not intended to be used separately.
 *
 * ```html
 * <vaadin-side-nav-item>
 *   Item 1
 *   <vaadin-side-nav-item path="/path1" slot="children">
 *     Child item 1
 *   </vaadin-side-nav-item>
 *   <vaadin-side-nav-item path="/path2" slot="children">
 *     Child item 2
 *   </vaadin-side-nav-item>
 * </vaadin-side-nav-item>
 * ```
 *
 * ### Customization
 *
 * You can configure the item by using `slot` names.
 *
 * Slot name | Description
 * ----------|-------------
 * `prefix`  | A slot for content before the label (e.g. an icon).
 * `suffix`  | A slot for content after the label (e.g. an icon).
 *
 * #### Example
 *
 * ```html
 * <vaadin-side-nav-item>
 *   <vaadin-icon icon="vaadin:chart" slot="prefix"></vaadin-icon>
 *   Item
 *   <vaadin-badge slot="suffix">Suffix</vaadin-badge>
 * </vaadin-side-nav-item>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name       | Description
 * ----------------|----------------
 * `content`       | The element that wraps link and toggle button
 * `children`      | The element that wraps child items
 * `link`          | The clickable anchor used for navigation
 * `toggle-button` | The toggle button
 *
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|-------------
 * `disabled`     | Set when the element is disabled.
 * `expanded`     | Set when the element is expanded.
 * `has-children` | Set when the element has child items.
 * `has-tooltip`  | Set when the element has a slotted tooltip.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                       |
 * :-----------------------------------------|
 * | `--vaadin-side-nav-item-background`     |
 * | `--vaadin-side-nav-item-border-color`   |
 * | `--vaadin-side-nav-item-border-radius`  |
 * | `--vaadin-side-nav-item-border-width`   |
 * | `--vaadin-side-nav-item-font-size`      |
 * | `--vaadin-side-nav-item-font-weight`    |
 * | `--vaadin-side-nav-item-gap`            |
 * | `--vaadin-side-nav-item-line-height`    |
 * | `--vaadin-side-nav-item-padding`        |
 * | `--vaadin-side-nav-item-text-color`     |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/side-nav)
 */
external class SideNavItemElement : HTMLElement {
    /**
     * Whether the item's path matches the current browser URL.
     *
     * A match occurs when both share the same base origin (like https://example.com),
     * the same path (like /path/to/page), and the browser URL contains at least
     * all the query parameters with the same values from the item's path.
     *
     * See [`matchNested`](#/elements/vaadin-side-nav-item#property-matchNested) for how to change the path matching behavior.
     *
     * The state is updated when the item is added to the DOM or when the browser
     * navigates to a new page.
     */
    val current: Boolean

    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * Whether to show the child items or not
     */
    var expanded: Boolean

    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following structure and default values:
     * ```js
     * {
     *   toggle: 'Toggle child items'
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * Whether to also match nested paths / routes. `false` by default.
     *
     * When enabled, an item with the path `/path` is considered current when
     * the browser URL is `/path`, `/path/child`, `/path/child/grandchild`,
     * etc.
     *
     * Note that this only affects matching of the URLs path, not the base
     * origin or query parameters.
     */
    var matchNested: Boolean

    /**
     * The path to navigate to
     */
    var path: String?

    /**
     * The list of alternative paths matching this item
     */
    var pathAliases: dynamic

    /**
     * Whether to exclude the item from client-side routing. When enabled,
     * this causes the item to behave like a regular anchor, causing a full
     * page reload. This only works with supported routers, such as the one
     * provided in Vaadin apps, or when using the side nav `onNavigate` hook.
     */
    var routerIgnore: Boolean

    /**
     * The target of the link. Works only when `path` is set.
     */
    var target: String?
}

/**
 * Named exports of `@vaadin/side-nav/vaadin-side-nav-item.js`.
 *
 * Referencing the module registers the `<vaadin-side-nav-item>` custom element.
 */
@JsModule("@vaadin/side-nav/vaadin-side-nav-item.js")
private external object SideNavItemModule {
    /** The registered custom element class. */
    val SideNavItem: dynamic
}

/**
 * Imports `@vaadin/side-nav/vaadin-side-nav-item.js`, registering the `<vaadin-side-nav-item>` custom element,
 * and returns the element class.
 */
fun registerSideNavItem(): dynamic = SideNavItemModule.SideNavItem

/**
 * Creates a `<vaadin-side-nav-item>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createSideNavItem(configure: SideNavItemElement.() -> Unit = {}): SideNavItemElement {
    registerSideNavItem()
    return document.createElement(VaadinTagName.sideNavItem).apply(configure)
}
