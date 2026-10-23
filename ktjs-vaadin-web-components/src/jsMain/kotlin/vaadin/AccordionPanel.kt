package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * The accordion panel element.
 *
 * ### Styling
 *
 * The following shadow DOM parts are exposed for styling:
 *
 * Part name        | Description
 * -----------------|----------------
 * `content`        | The wrapper for the collapsible panel content.
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
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/accordion)
 */
external class AccordionPanelElement : HTMLElement {
    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * If true, the collapsible content is visible.
     */
    var opened: Boolean

    /**
     * A text that is displayed in the heading, if no
     * element is assigned to the `summary` slot.
     */
    var summary: String?
}

/**
 * Named exports of `@vaadin/accordion/vaadin-accordion-panel.js`.
 *
 * Referencing the module registers the `<vaadin-accordion-panel>` custom element.
 */
@JsModule("@vaadin/accordion/vaadin-accordion-panel.js")
private external object AccordionPanelModule {
    /** The registered custom element class. */
    val AccordionPanel: dynamic
}

/**
 * Imports `@vaadin/accordion/vaadin-accordion-panel.js`, registering the `<vaadin-accordion-panel>` custom element,
 * and returns the element class.
 */
fun registerAccordionPanel(): dynamic = AccordionPanelModule.AccordionPanel

/**
 * Creates a `<vaadin-accordion-panel>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createAccordionPanel(configure: AccordionPanelElement.() -> Unit = {}): AccordionPanelElement {
    registerAccordionPanel()
    return document.createElement(VaadinTagName.accordionPanel).apply(configure)
}
