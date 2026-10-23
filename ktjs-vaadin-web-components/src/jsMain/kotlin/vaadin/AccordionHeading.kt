package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * The accordion heading element.
 *
 * `vaadin-accordion-heading` is the element for the headings in the accordion.
 * As recommended by the WAI ARIA Best Practices, each heading needs to wrap a
 * `<button>`. This element puts that button in the Shadow DOM, as it is more
 * convenient to use and doesn't make styling of the heading more problematic.
 *
 * The WAI ARIA Best Practices also recommend setting `aria-level` depending
 * on what level the headings are. It is hard to determine the level of a heading
 * algorithmically, and setting it is not strictly required to have an accessible
 * accordion. To keep things easier to use, this element does not set `aria-level`
 * attribute but leaves that to the developer creating an accordion.
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
 * `opened`     | Set when the collapsible content is expanded and visible.
 * `disabled`   | Set when the element is disabled.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                          |
 * :--------------------------------------------|
 * | `--vaadin-accordion-heading-background`    |
 * | `--vaadin-accordion-heading-border-color`  |
 * | `--vaadin-accordion-heading-border-radius` |
 * | `--vaadin-accordion-heading-border-width`  |
 * | `--vaadin-accordion-heading-font-size`     |
 * | `--vaadin-accordion-heading-font-weight`   |
 * | `--vaadin-accordion-heading-gap`           |
 * | `--vaadin-accordion-heading-height`        |
 * | `--vaadin-accordion-heading-padding`       |
 * | `--vaadin-accordion-heading-text-color`    |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/accordion)
 */
external class AccordionHeadingElement : HTMLElement {
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
 * Named exports of `@vaadin/accordion/vaadin-accordion-heading.js`.
 *
 * Referencing the module registers the `<vaadin-accordion-heading>` custom element.
 */
@JsModule("@vaadin/accordion/vaadin-accordion-heading.js")
private external object AccordionHeadingModule {
    /** The registered custom element class. */
    val AccordionHeading: dynamic
}

/**
 * Imports `@vaadin/accordion/vaadin-accordion-heading.js`, registering the `<vaadin-accordion-heading>` custom element,
 * and returns the element class.
 */
fun registerAccordionHeading(): dynamic = AccordionHeadingModule.AccordionHeading

/**
 * Creates a `<vaadin-accordion-heading>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createAccordionHeading(configure: AccordionHeadingElement.() -> Unit = {}): AccordionHeadingElement {
    registerAccordionHeading()
    return document.createElement(VaadinTagName.accordionHeading).apply(configure)
}
