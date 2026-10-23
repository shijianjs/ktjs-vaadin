package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-scroller>` provides a simple way to enable scrolling when its content is overflowing.
 *
 * ```html
 * <vaadin-scroller>
 *   <div>Content</div>
 * </vaadin-scroller>
 * ```
 *
 * ### Styling
 *
 * The following state attributes are available for styling:
 *
 * Attribute    | Description
 * -------------| -----------
 * `focus-ring` | Set when the element is focused using the keyboard.
 * `focused`    | Set when the element is focused.
 * `overflow`   | Set to `top`, `bottom`, `start`, `end`, all of them, or none.
 *
 * ### Built-in Theme Variants
 *
 * `<vaadin-scroller>` supports the following theme variants:
 *
 * Theme variant                            | Description
 * -----------------------------------------|---------------
 * `theme="overflow-indicators"`            | Shows visual indicators at the top and bottom when the content is scrolled
 * `theme="overflow-indicator-top"`         | Shows the visual indicator at the top when the content is scrolled
 * `theme="overflow-indicator-top-bottom"`  | Shows the visual indicator at the bottom when the content is scrolled
 *
 * ### Custom CSS Properties
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                            |
 * :----------------------------------------------|
 * `--vaadin-scroller-overflow-indicator-color`   |
 * `--vaadin-scroller-overflow-indicator-height`  |
 * `--vaadin-scroller-padding-block`              |
 * `--vaadin-scroller-padding-inline`             |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/scroller)
 */
external class ScrollerElement : HTMLElement {
    /**
     * This property indicates the scroll direction. Supported values are `vertical`, `horizontal`, `none`.
     * When `scrollDirection` is undefined scrollbars will be shown in both directions.
     */
    var scrollDirection: String?
}

/**
 * Named exports of `@vaadin/scroller/vaadin-scroller.js`.
 *
 * Referencing the module registers the `<vaadin-scroller>` custom element.
 */
@JsModule("@vaadin/scroller/vaadin-scroller.js")
private external object ScrollerModule {
    /** The registered custom element class. */
    val Scroller: dynamic
}

/**
 * Imports `@vaadin/scroller/vaadin-scroller.js`, registering the `<vaadin-scroller>` custom element,
 * and returns the element class.
 */
fun registerScroller(): dynamic = ScrollerModule.Scroller

/**
 * Creates a `<vaadin-scroller>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createScroller(configure: ScrollerElement.() -> Unit = {}): ScrollerElement {
    registerScroller()
    return document.createElement(VaadinTagName.scroller).apply(configure)
}
