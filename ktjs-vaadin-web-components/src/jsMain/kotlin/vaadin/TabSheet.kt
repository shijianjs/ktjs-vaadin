package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-tabsheet>` is a Web Component for organizing and grouping content
 * into scrollable panels. The panels can be switched between by using tabs.
 *
 * ```html
 * <vaadin-tabsheet>
 *   <div slot="prefix">Prefix</div>
 *   <div slot="suffix">Suffix</div>
 *
 *   <vaadin-tabs slot="tabs">
 *     <vaadin-tab id="tab-1">Tab 1</vaadin-tab>
 *     <vaadin-tab id="tab-2">Tab 2</vaadin-tab>
 *     <vaadin-tab id="tab-3">Tab 3</vaadin-tab>
 *   </vaadin-tabs>
 *
 *   <div tab="tab-1">Panel 1</div>
 *   <div tab="tab-2">Panel 2</div>
 *   <div tab="tab-3">Panel 3</div>
 * </vaadin-tabsheet>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are exposed for styling:
 *
 * Part name | Description
 * --------- | ---------------
 * `tabs-container`    | The container for the slotted prefix, tabs and suffix
 * `content`    | The container for the slotted panels
 *
 * The following state attributes are available for styling:
 *
 * Attribute         | Description
 * ------------------|-------------
 * `loading` | Set when a tab without associated content is selected
 * `overflow`   | Set to `top`, `bottom`, `start`, `end`, all of them, or none.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                  |
 * :------------------------------------|
 * | `--vaadin-tabsheet-border-color`   |
 * | `--vaadin-tabsheet-border-radius`  |
 * | `--vaadin-tabsheet-border-width`   |
 * | `--vaadin-tabsheet-gap`            |
 * | `--vaadin-tabsheet-padding`        |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/tabsheet)
 */
external class TabSheetElement : HTMLElement {
    /**
     * A read-only list of `<vaadin-tab>`s from which a selection can be made.
     * It is populated from the elements passed inside the slotted
     * `<vaadin-tabs>`, and updated dynamically when adding or removing items.
     */
    val items: dynamic

    /**
     * The index of the selected tab.
     */
    var selected: Double?
}

/**
 * Named exports of `@vaadin/tabsheet/vaadin-tabsheet.js`.
 *
 * Referencing the module registers the `<vaadin-tabsheet>` custom element.
 */
@JsModule("@vaadin/tabsheet/vaadin-tabsheet.js")
private external object TabSheetModule {
    /** The registered custom element class. */
    val TabSheet: dynamic
}

/**
 * Imports `@vaadin/tabsheet/vaadin-tabsheet.js`, registering the `<vaadin-tabsheet>` custom element,
 * and returns the element class.
 */
fun registerTabSheet(): dynamic = TabSheetModule.TabSheet

/**
 * Creates a `<vaadin-tabsheet>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createTabSheet(configure: TabSheetElement.() -> Unit = {}): TabSheetElement {
    registerTabSheet()
    return document.createElement(VaadinTagName.tabsheet).apply(configure)
}
