package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-breadcrumbs>` is a Web Component that displays the user's location
 * within a hierarchy as a trail of links from the root to the current page.
 *
 * ```html
 * <vaadin-breadcrumbs>
 *   <vaadin-breadcrumbs-item path="/">Home</vaadin-breadcrumbs-item>
 *   <vaadin-breadcrumbs-item path="/docs">Docs</vaadin-breadcrumbs-item>
 *   <vaadin-breadcrumbs-item>Current page</vaadin-breadcrumbs-item>
 * </vaadin-breadcrumbs>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name          | Description
 * -------------------|------------
 * `list`             | The element with `role="list"` wrapping all items.
 * `overflow`         | The element wrapping the overflow button.
 * `overflow-button`  | The button that reveals collapsed items.
 * `overlay`          | The outer panel of the overflow overlay.
 * `overlay-content`  | The inner wrapper of the overflow overlay.
 *
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|------------
 * `has-overflow` | Set when one or more items are collapsed into the overflow overlay.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                          |
 * :--------------------------------------------|
 * | `--vaadin-breadcrumbs-font-size`           |
 * | `--vaadin-breadcrumbs-font-weight`         |
 * | `--vaadin-breadcrumbs-gap`                 |
 * | `--vaadin-breadcrumbs-line-height`         |
 * | `--vaadin-breadcrumbs-link-color`          |
 * | `--vaadin-breadcrumbs-overflow-icon`       |
 * | `--vaadin-breadcrumbs-separator-icon`      |
 * | `--vaadin-breadcrumbs-text-color`          |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/breadcrumbs)
 */
external class BreadcrumbsElement : HTMLElement {
    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following JSON structure and default values:
     *
     * ```
     * {
     *   // Accessible label of the overflow button revealing collapsed items.
     *   moreItems: 'More items'
     * }
     * ```
     */
    var i18n: dynamic
}

/**
 * Named exports of `@vaadin/breadcrumbs/vaadin-breadcrumbs.js`.
 *
 * Referencing the module registers the `<vaadin-breadcrumbs>` custom element.
 */
@JsModule("@vaadin/breadcrumbs/vaadin-breadcrumbs.js")
private external object BreadcrumbsModule {
    /** The registered custom element class. */
    val Breadcrumbs: dynamic
}

/**
 * Imports `@vaadin/breadcrumbs/vaadin-breadcrumbs.js`, registering the `<vaadin-breadcrumbs>` custom element,
 * and returns the element class.
 */
fun registerBreadcrumbs(): dynamic = BreadcrumbsModule.Breadcrumbs

/**
 * Creates a `<vaadin-breadcrumbs>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createBreadcrumbs(configure: BreadcrumbsElement.() -> Unit = {}): BreadcrumbsElement {
    registerBreadcrumbs()
    return document.createElement(VaadinTagName.breadcrumbs).apply(configure)
}
