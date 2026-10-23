package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-select-list-box>` is a Web Component for wrapping `<vaadin-select>` items.
 *
 * ```html
 * <vaadin-select>
 *   <vaadin-select-list-box slot="overlay">
 *     <vaadin-select-item value="foo">Foo</vaadin-select-item>
 *     <vaadin-select-item value="bar">Bar</vaadin-select-item>
 *   </vaadin-select-list-box>
 * </vaadin-select>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name         | Description
 * ------------------|------------------------
 * `items`           | The items container
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/select)
 */
external class SelectListBoxElement : HTMLElement {
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
     * Define how items are disposed in the dom.
     * Possible values are: `horizontal|vertical`.
     * It also changes navigation keys from left/right to up/down.
     */
    val orientation: String?

    /**
     * The index of the item selected in the items array.
     * Note: Not updated when used in `multiple` selection mode.
     */
    var selected: Double?
}

/**
 * Named exports of `@vaadin/select/vaadin-select-list-box.js`.
 *
 * Referencing the module registers the `<vaadin-select-list-box>` custom element.
 */
@JsModule("@vaadin/select/vaadin-select-list-box.js")
private external object SelectListBoxModule {
    /** The registered custom element class. */
    val SelectListBox: dynamic
}

/**
 * Imports `@vaadin/select/vaadin-select-list-box.js`, registering the `<vaadin-select-list-box>` custom element,
 * and returns the element class.
 */
fun registerSelectListBox(): dynamic = SelectListBoxModule.SelectListBox

/**
 * Creates a `<vaadin-select-list-box>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createSelectListBox(configure: SelectListBoxElement.() -> Unit = {}): SelectListBoxElement {
    registerSelectListBox()
    return document.createElement(VaadinTagName.selectListBox).apply(configure)
}
