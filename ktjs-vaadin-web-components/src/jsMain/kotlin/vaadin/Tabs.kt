package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-tabs>` is a Web Component for organizing and grouping content into sections.
 *
 * ```html
 * <vaadin-tabs selected="4">
 *   <vaadin-tab>Page 1</vaadin-tab>
 *   <vaadin-tab>Page 2</vaadin-tab>
 *   <vaadin-tab>Page 3</vaadin-tab>
 *   <vaadin-tab>Page 4</vaadin-tab>
 * </vaadin-tabs>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name         | Description
 * ------------------|--------------------------------------
 * `back-button`     | Button for moving the scroll back
 * `tabs`            | The tabs container
 * `forward-button`  | Button for moving the scroll forward
 *
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|--------------------------------------
 * `orientation`  | Tabs disposition, valid values are `horizontal` and `vertical`
 * `overflow`     | It's set to `start`, `end`, none or both.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property              |
 * :--------------------------------|
 * | `--vaadin-tabs-background`     |
 * | `--vaadin-tabs-border-color`   |
 * | `--vaadin-tabs-border-radius`  |
 * | `--vaadin-tabs-border-width`   |
 * | `--vaadin-tabs-font-size`      |
 * | `--vaadin-tabs-font-weight`    |
 * | `--vaadin-tabs-gap`            |
 * | `--vaadin-tabs-padding`        |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/tabs)
 */
external class TabsElement : HTMLElement {
    /**
     * If true, the user cannot interact with this element.
     * When the element is disabled, the selected item is
     * not updated when `selected` property is changed.
     */
    var disabled: Boolean

    /**
     * A read-only list of items from which a selection can be made.
     * It is populated from the elements passed to the light DOM,
     * and updated dynamically when adding or removing items.
     */
    val items: dynamic

    /**
     * Set tabs disposition. Possible values are `horizontal|vertical`
     */
    var orientation: String?

    /**
     * The index of the selected tab.
     */
    var selected: Double?
}

/**
 * Named exports of `@vaadin/tabs/vaadin-tabs.js`.
 *
 * Referencing the module registers the `<vaadin-tabs>` custom element.
 */
@JsModule("@vaadin/tabs/vaadin-tabs.js")
private external object TabsModule {
    /** The registered custom element class. */
    val Tabs: dynamic
}

/**
 * Imports `@vaadin/tabs/vaadin-tabs.js`, registering the `<vaadin-tabs>` custom element,
 * and returns the element class.
 */
fun registerTabs(): dynamic = TabsModule.Tabs

/**
 * Creates a `<vaadin-tabs>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createTabs(configure: TabsElement.() -> Unit = {}): TabsElement {
    registerTabs()
    return document.createElement(VaadinTagName.tabs).apply(configure)
}
