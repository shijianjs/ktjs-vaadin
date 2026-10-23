package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-horizontal-layout>` provides a simple way to horizontally align your HTML elements.
 *
 * ```html
 * <vaadin-horizontal-layout>
 *   <div>Item 1</div>
 *   <div>Item 2</div>
 * </vaadin-horizontal-layout>
 * ```
 *
 * ### Built-in Theme Variations
 *
 * `<vaadin-horizontal-layout>` supports the following theme variations:
 *
 * Theme variation    | Description
 * -------------------|---------------
 * `theme="margin"`   | Applies the default amount of CSS margin for the host element
 * `theme="padding"`  | Applies the default amount of CSS padding for the host element
 * `theme="spacing"`  | Applies the default amount of CSS margin between items
 * `theme="wrap"`     | Items wrap to the next row when they exceed the layout width
 *
 * ### Component's slots
 *
 * The following slots are available to be set:
 *
 * Slot name          | Description
 * -------------------|---------------
 * no name            | Default slot
 * `middle`           | Slot for the content placed in the middle
 * `end`              | Slot for the content placed at the end
 *
 * ### Custom CSS Properties
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                  | Description
 * -------------------------------------|-------------
 * `--vaadin-horizontal-layout-margin`  | The default CSS margin applied when using `theme="margin"`
 * `--vaadin-horizontal-layout-padding` | The default CSS padding applied when using `theme="padding"`
 * `--vaadin-horizontal-layout-gap`     | The default CSS gap applied when using `theme="spacing"`
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/horizontal-layout)
 */
external class HorizontalLayoutElement : HTMLElement {
}

/**
 * Named exports of `@vaadin/horizontal-layout/vaadin-horizontal-layout.js`.
 *
 * Referencing the module registers the `<vaadin-horizontal-layout>` custom element.
 */
@JsModule("@vaadin/horizontal-layout/vaadin-horizontal-layout.js")
private external object HorizontalLayoutModule {
    /** The registered custom element class. */
    val HorizontalLayout: dynamic
}

/**
 * Imports `@vaadin/horizontal-layout/vaadin-horizontal-layout.js`, registering the `<vaadin-horizontal-layout>` custom element,
 * and returns the element class.
 */
fun registerHorizontalLayout(): dynamic = HorizontalLayoutModule.HorizontalLayout

/**
 * Creates a `<vaadin-horizontal-layout>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createHorizontalLayout(configure: HorizontalLayoutElement.() -> Unit = {}): HorizontalLayoutElement {
    registerHorizontalLayout()
    return document.createElement(VaadinTagName.horizontalLayout).apply(configure)
}
