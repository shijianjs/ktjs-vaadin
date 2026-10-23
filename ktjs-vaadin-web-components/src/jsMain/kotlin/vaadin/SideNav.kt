package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-side-nav>` is a Web Component for navigation menus.
 *
 * ```html
 * <vaadin-side-nav>
 *   <vaadin-side-nav-item>Item 1</vaadin-side-nav-item>
 *   <vaadin-side-nav-item>Item 2</vaadin-side-nav-item>
 *   <vaadin-side-nav-item>Item 3</vaadin-side-nav-item>
 *   <vaadin-side-nav-item>Item 4</vaadin-side-nav-item>
 * </vaadin-side-nav>
 * ```
 *
 * ### Customization
 *
 * You can configure the component by using `slot` names.
 *
 * Slot name | Description
 * ----------|-------------
 * `label`   | The label (text) inside the side nav.
 *
 * #### Example
 *
 * ```html
 * <vaadin-side-nav>
 *   <span slot="label">Main menu</span>
 *   <vaadin-side-nav-item>Item</vaadin-side-nav-item>
 * </vaadin-side-nav>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name       | Description
 * ----------------|----------------
 * `label`         | The label element
 * `children`      | The element that wraps child items
 * `toggle-button` | The toggle button
 *
 * The following state attributes are available for styling:
 *
 * Attribute    | Description
 * -------------|-------------
 * `collapsed`  | Set when the element is collapsed.
 * `focus-ring` | Set when the label is focused using the keyboard.
 * `focused`    | Set when the label is focused.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                       |
 * :-----------------------------------------|
 * | `--vaadin-side-nav-child-indent`        |
 * | `--vaadin-side-nav-items-gap`           |
 * | `--vaadin-side-nav-label-color`         |
 * | `--vaadin-side-nav-label-font-size`     |
 * | `--vaadin-side-nav-label-font-weight`   |
 * | `--vaadin-side-nav-label-line-height`   |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/side-nav)
 */
external class SideNavElement : HTMLElement {
    /**
     * Whether the side nav is collapsed. When collapsed, the items are hidden.
     */
    var collapsed: Boolean

    /**
     * Whether the side nav is collapsible. When enabled, the toggle icon is shown.
     */
    var collapsible: Boolean

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
     * A change to this property triggers an update of the highlighted item in the side navigation. While it typically
     * corresponds to the browser's URL, the specific value assigned to the property is irrelevant. The component has
     * its own internal logic for determining which item is highlighted.
     *
     * The main use case for this property is when the side navigation is used with a client-side router. In this case,
     * the component needs to be informed about route changes so it can update the highlighted item.
     */
    var location: dynamic

    /**
     * Whether to expand parent items of the nested matching item after initial
     * rendering or navigation. By default, all the parent items are expanded.
     * Set to true to disable this behavior.
     */
    var noAutoExpand: Boolean

    /**
     * Callback function for router integration.
     *
     * When a side nav item link is clicked, this function is called and the default click action is cancelled.
     * This delegates the responsibility of navigation to the function's logic.
     *
     * The click event action is not cancelled in the following cases:
     * - The click event has a modifier (e.g. `metaKey`, `shiftKey`)
     * - The click event is on an external link
     * - The click event is on a link with `target="_blank"`
     * - The function explicitly returns `false`
     *
     * The function receives an object with the properties of the clicked side-nav item:
     * - `path`: The path of the navigation item.
     * - `target`: The target of the navigation item.
     * - `current`: A boolean indicating whether the navigation item is currently selected.
     * - `expanded`: A boolean indicating whether the navigation item is expanded.
     * - `pathAliases`: An array of path aliases for the navigation item.
     * - `originalEvent`: The original DOM event that triggered the navigation.
     *
     * Also see the `location` property for updating the highlighted navigation item on route change.
     */
    var onNavigate: dynamic
}

/**
 * Named exports of `@vaadin/side-nav/vaadin-side-nav.js`.
 *
 * Referencing the module registers the `<vaadin-side-nav>` custom element.
 */
@JsModule("@vaadin/side-nav/vaadin-side-nav.js")
private external object SideNavModule {
    /** The registered custom element class. */
    val SideNav: dynamic
}

/**
 * Imports `@vaadin/side-nav/vaadin-side-nav.js`, registering the `<vaadin-side-nav>` custom element,
 * and returns the element class.
 */
fun registerSideNav(): dynamic = SideNavModule.SideNav

/**
 * Creates a `<vaadin-side-nav>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createSideNav(configure: SideNavElement.() -> Unit = {}): SideNavElement {
    registerSideNav()
    return document.createElement(VaadinTagName.sideNav).apply(configure)
}
