package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-tab>` is a Web Component providing an accessible and customizable tab.
 *
 * ```html
 * <vaadin-tab>Tab 1</vaadin-tab>
 * ```
 *
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|---------------------------------
 * `disabled`     | Set when the element is disabled
 * `focused`      | Set when the element is focused
 * `focus-ring`   | Set when the element is keyboard focused
 * `selected`     | Set when the tab is selected
 * `active`       | Set when mousedown or enter/spacebar pressed
 * `orientation`  | Set to `horizontal` or `vertical` depending on the direction of items
 * `has-tooltip`  | Set when the tab has a slotted tooltip
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property            |
 * :------------------------------|
 * | `--vaadin-tab-background`    |
 * | `--vaadin-tab-border-color`  |
 * | `--vaadin-tab-border-radius` |
 * | `--vaadin-tab-border-width`  |
 * | `--vaadin-tab-font-size`     |
 * | `--vaadin-tab-font-weight`   |
 * | `--vaadin-tab-gap`           |
 * | `--vaadin-tab-line-height`   |
 * | `--vaadin-tab-padding`       |
 * | `--vaadin-tab-text-color`    |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/tabs)
 */
external class TabElement : HTMLElement {
    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * If true, the item is in selected state.
     */
    var selected: Boolean

    /**
     * Submittable string value. The default value is the trimmed text content of the element.
     */
    var value: String?
}

/**
 * Named exports of `@vaadin/tabs/vaadin-tab.js`.
 *
 * Referencing the module registers the `<vaadin-tab>` custom element.
 */
@JsModule("@vaadin/tabs/vaadin-tab.js")
private external object TabModule {
    /** The registered custom element class. */
    val Tab: dynamic
}

/**
 * Imports `@vaadin/tabs/vaadin-tab.js`, registering the `<vaadin-tab>` custom element,
 * and returns the element class.
 */
fun registerTab(): dynamic = TabModule.Tab

/**
 * Creates a `<vaadin-tab>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createTab(configure: TabElement.() -> Unit = {}): TabElement {
    registerTab()
    return document.createElement(VaadinTagName.tab).apply(configure)
}
