package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-details>` is a Web Component which the creates an
 * expandable panel similar to `<details>` HTML element.
 *
 * ```html
 * <vaadin-details>
 *   <vaadin-details-summary slot="summary">Expandable Details</vaadin-details-summary>
 *   <div>
 *     Toggle using mouse, Enter and Space keys.
 *   </div>
 * </vaadin-details>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are exposed for styling:
 *
 * Part name        | Description
 * -----------------|----------------
 * `content`        | The wrapper for the collapsible details content.
 *
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|------------
 * `opened`       | Set when the collapsible content is expanded and visible
 * `disabled`     | Set when the element is disabled
 * `focus-ring`   | Set when the element is focused using the keyboard
 * `focused`      | Set when the element is focused
 * `has-tooltip`  | Set when the element has a slotted tooltip
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/details)
 */
external class DetailsElement : HTMLElement {
    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * If true, the collapsible content is visible.
     */
    var opened: Boolean

    /**
     * A text that is displayed in the summary, if no
     * element is assigned to the `summary` slot.
     */
    var summary: String?
}

/**
 * Named exports of `@vaadin/details/vaadin-details.js`.
 *
 * Referencing the module registers the `<vaadin-details>` custom element.
 */
@JsModule("@vaadin/details/vaadin-details.js")
private external object DetailsModule {
    /** The registered custom element class. */
    val Details: dynamic
}

/**
 * Imports `@vaadin/details/vaadin-details.js`, registering the `<vaadin-details>` custom element,
 * and returns the element class.
 */
fun registerDetails(): dynamic = DetailsModule.Details

/**
 * Creates a `<vaadin-details>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createDetails(configure: DetailsElement.() -> Unit = {}): DetailsElement {
    registerDetails()
    return document.createElement(VaadinTagName.details).apply(configure)
}
