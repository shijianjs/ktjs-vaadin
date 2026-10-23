package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-vertical-layout>` provides a simple way to vertically align your HTML elements.
 *
 * ```html
 * <vaadin-vertical-layout>
 *   <div>Item 1</div>
 *   <div>Item 2</div>
 * </vaadin-vertical-layout>
 * ```
 *
 * ### Built-in Theme Variations
 *
 * `<vaadin-vertical-layout>` supports the following theme variations:
 *
 * Theme variation    | Description
 * -------------------|---------------
 * `theme="margin"`   | Applies the default amount of CSS margin for the host element
 * `theme="padding"`  | Applies the default amount of CSS padding for the host element
 * `theme="spacing"`  | Applies the default amount of CSS margin between items
 * `theme="wrap"`     | Items wrap to the next row when they exceed the layout width
 *
 * ### Custom CSS Properties
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                | Description
 * -----------------------------------|-------------
 * `--vaadin-vertical-layout-margin`  | The default CSS margin applied when using `theme="margin"`
 * `--vaadin-vertical-layout-padding` | The default CSS padding applied when using `theme="padding"`
 * `--vaadin-vertical-layout-gap`     | The default CSS gap applied when using `theme="spacing"`
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/vertical-layout)
 */
external class VerticalLayoutElement : HTMLElement {
}

/**
 * Named exports of `@vaadin/vertical-layout/vaadin-vertical-layout.js`.
 *
 * Referencing the module registers the `<vaadin-vertical-layout>` custom element.
 */
@JsModule("@vaadin/vertical-layout/vaadin-vertical-layout.js")
private external object VerticalLayoutModule {
    /** The registered custom element class. */
    val VerticalLayout: dynamic
}

/**
 * Imports `@vaadin/vertical-layout/vaadin-vertical-layout.js`, registering the `<vaadin-vertical-layout>` custom element,
 * and returns the element class.
 */
fun registerVerticalLayout(): dynamic = VerticalLayoutModule.VerticalLayout

/**
 * Creates a `<vaadin-vertical-layout>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createVerticalLayout(configure: VerticalLayoutElement.() -> Unit = {}): VerticalLayoutElement {
    registerVerticalLayout()
    return document.createElement(VaadinTagName.verticalLayout).apply(configure)
}
