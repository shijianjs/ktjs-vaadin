package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-breadcrumbs-item>` is a single item inside a `<vaadin-breadcrumbs>`.
 *
 * ```html
 * <vaadin-breadcrumbs-item path="/docs">Docs</vaadin-breadcrumbs-item>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name | Description
 * ----------|------------------------------------------------------------
 * `link`    | The interactive `<a>` rendered when `path` is set.
 * `nolink`  | The non-interactive `<span>` rendered when `path` is unset.
 * `label`   | Wraps the item's text content, inside `link` or `nolink`.
 *
 * The following state attributes are available for styling:
 *
 * Attribute    | Description
 * -------------|-------------
 * `current`    | Set by the parent `<vaadin-breadcrumbs>` on the last item when it has no `path`.
 * `disabled`   | Set when the item is disabled.
 * `focus-ring` | Set when the item is focused by the keyboard.
 * `focused`    | Set when the item is focused.
 * `has-prefix` | Set when the item has content in the prefix slot
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                          |
 * :--------------------------------------------|
 * | `--vaadin-breadcrumbs-item-border-radius`  |
 * | `--vaadin-breadcrumbs-item-gap`            |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/breadcrumbs)
 */
external class BreadcrumbsItemElement : HTMLElement {
    /**
     * When true, the item represents the current page. Set by the parent
     * `<vaadin-breadcrumbs>` on the last item when it has no `path`. The
     * item reflects this state by applying `aria-current="page"` to its
     * inner `[part="nolink"]` element.
     */
    val current: Boolean

    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * The path to navigate to. When set, the item renders as a link
     * (`<a part="link">`); when unset, it renders as a non-link
     * (`<span part="nolink">`).
     */
    var path: String?
}

/**
 * Named exports of `@vaadin/breadcrumbs/vaadin-breadcrumbs-item.js`.
 *
 * Referencing the module registers the `<vaadin-breadcrumbs-item>` custom element.
 */
@JsModule("@vaadin/breadcrumbs/vaadin-breadcrumbs-item.js")
private external object BreadcrumbsItemModule {
    /** The registered custom element class. */
    val BreadcrumbsItem: dynamic
}

/**
 * Imports `@vaadin/breadcrumbs/vaadin-breadcrumbs-item.js`, registering the `<vaadin-breadcrumbs-item>` custom element,
 * and returns the element class.
 */
fun registerBreadcrumbsItem(): dynamic = BreadcrumbsItemModule.BreadcrumbsItem

/**
 * Creates a `<vaadin-breadcrumbs-item>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createBreadcrumbsItem(configure: BreadcrumbsItemElement.() -> Unit = {}): BreadcrumbsItemElement {
    registerBreadcrumbsItem()
    return document.createElement(VaadinTagName.breadcrumbsItem).apply(configure)
}
