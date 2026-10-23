package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * The details summary element.
 *
 * ### Styling
 *
 * The following shadow DOM parts are exposed for styling:
 *
 * Part name  | Description
 * -----------|-------------------
 * `toggle`   | The icon element
 * `content`  | The content wrapper
 *
 * The following state attributes are available for styling:
 *
 * Attribute    | Description
 * -------------| -----------
 * `active`     | Set when the element is pressed down, either with mouse, touch or the keyboard.
 * `opened`     | Set when the element is expanded and related collapsible content is visible.
 * `disabled`   | Set when the element is disabled.
 * `focus-ring` | Set when the element is focused using the keyboard.
 * `focused`    | Set when the element is focused.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                        |
 * :------------------------------------------|
 * | `--vaadin-details-summary-background`    |
 * | `--vaadin-details-summary-border-color`  |
 * | `--vaadin-details-summary-border-radius` |
 * | `--vaadin-details-summary-border-width`  |
 * | `--vaadin-details-summary-font-size`     |
 * | `--vaadin-details-summary-font-weight`   |
 * | `--vaadin-details-summary-gap`           |
 * | `--vaadin-details-summary-height`        |
 * | `--vaadin-details-summary-padding`       |
 * | `--vaadin-details-summary-text-color`    |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/details)
 */
external class DetailsSummaryElement : HTMLElement {
    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * When true, the element is opened.
     */
    var opened: Boolean
}

/**
 * Named exports of `@vaadin/details/vaadin-details-summary.js`.
 *
 * Referencing the module registers the `<vaadin-details-summary>` custom element.
 */
@JsModule("@vaadin/details/vaadin-details-summary.js")
private external object DetailsSummaryModule {
    /** The registered custom element class. */
    val DetailsSummary: dynamic
}

/**
 * Imports `@vaadin/details/vaadin-details-summary.js`, registering the `<vaadin-details-summary>` custom element,
 * and returns the element class.
 */
fun registerDetailsSummary(): dynamic = DetailsSummaryModule.DetailsSummary

/**
 * Creates a `<vaadin-details-summary>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createDetailsSummary(configure: DetailsSummaryElement.() -> Unit = {}): DetailsSummaryElement {
    registerDetailsSummary()
    return document.createElement(VaadinTagName.detailsSummary).apply(configure)
}
