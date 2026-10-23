package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-accordion>` is a Web Component implementing accordion widget:
 * a vertically stacked set of expandable panels. The component should be
 * used as a wrapper for two or more `<vaadin-accordion-panel>` components.
 *
 * Panel headings function as controls that enable users to open (expand)
 * or hide (collapse) their associated sections of content. The user can
 * toggle panels by mouse click, Enter and Space keys.
 *
 * Only one panel can be opened at a time, opening a new one forces
 * previous panel to close and hide its content.
 *
 * ```html
 * <vaadin-accordion>
 *   <vaadin-accordion-panel>
 *     <vaadin-accordion-heading slot="summary">Panel 1</vaadin-accordion-heading>
 *     <div>This panel is opened, so the text is visible by default.</div>
 *   </vaadin-accordion-panel>
 *   <vaadin-accordion-panel>
 *     <vaadin-accordion-heading slot="summary">Panel 2</vaadin-accordion-heading>
 *     <div>After opening this panel, the first one becomes closed.</div>
 *   </vaadin-accordion-panel>
 * </vaadin-accordion>
 * ```
 *
 * ### Styling
 *
 * Accordion does not have own stylable shadow parts or state attributes. Instead, apply styles to
 * the following components:
 *
 * - [`<vaadin-accordion-heading>`](#/elements/vaadin-accordion-heading)
 * - [`<vaadin-accordion-panel>`](#/elements/vaadin-accordion-panel)
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/accordion)
 */
external class AccordionElement : HTMLElement {
    /**
     * The ARIA heading level, used to set the `aria-level` attribute
     * on the `<vaadin-accordion-heading>` element of each panel.
     *
     * By default, no `aria-level` is set and the headings are announced by
     * screen readers using their default level. Set this property to expose
     * the headings at the level that matches the surrounding page structure.
     */
    var headingLevel: Double?

    /**
     * The list of `<vaadin-accordion-panel>` child elements.
     * It is populated from the elements passed to the light DOM,
     * and updated dynamically when adding or removing panels.
     */
    val items: dynamic

    /**
     * The index of currently opened panel. First panel is opened by
     * default. Only one panel can be opened at the same time.
     * Setting null or undefined closes all the accordion panels.
     */
    var opened: Double?
}

/**
 * Named exports of `@vaadin/accordion/vaadin-accordion.js`.
 *
 * Referencing the module registers the `<vaadin-accordion>` custom element.
 */
@JsModule("@vaadin/accordion/vaadin-accordion.js")
private external object AccordionModule {
    /** The registered custom element class. */
    val Accordion: dynamic
}

/**
 * Imports `@vaadin/accordion/vaadin-accordion.js`, registering the `<vaadin-accordion>` custom element,
 * and returns the element class.
 */
fun registerAccordion(): dynamic = AccordionModule.Accordion

/**
 * Creates a `<vaadin-accordion>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createAccordion(configure: AccordionElement.() -> Unit = {}): AccordionElement {
    registerAccordion()
    return document.createElement(VaadinTagName.accordion).apply(configure)
}
