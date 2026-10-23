package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-button>` is an accessible and customizable button that allows users to perform actions.
 *
 * ```html
 * <vaadin-button>Press me</vaadin-button>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name | Description
 * ----------|-------------
 * `label`   | The label (text) inside the button.
 * `prefix`  | A slot for content before the label (e.g. an icon).
 * `suffix`  | A slot for content after the label (e.g. an icon).
 *
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|-------------
 * `active`       | Set when the button is pressed down, either with mouse, touch or the keyboard
 * `disabled`     | Set when the button is disabled
 * `focus-ring`   | Set when the button is focused using the keyboard
 * `focused`      | Set when the button is focused
 * `has-tooltip`  | Set when the button has a slotted tooltip
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                |
 * :----------------------------------|
 * | `--vaadin-button-background`     |
 * | `--vaadin-button-border-color`   |
 * | `--vaadin-button-border-radius`  |
 * | `--vaadin-button-border-width`   |
 * | `--vaadin-button-font-family`    |
 * | `--vaadin-button-font-size`      |
 * | `--vaadin-button-font-weight`    |
 * | `--vaadin-button-gap`            |
 * | `--vaadin-button-height`         |
 * | `--vaadin-button-label-wrap`     |
 * | `--vaadin-button-line-height`    |
 * | `--vaadin-button-margin`         |
 * | `--vaadin-button-padding`        |
 * | `--vaadin-button-text-color`     |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/button)
 */
external class ButtonElement : HTMLElement {
    /**
     * When disabled, the button is rendered as "dimmed".
     *
     * By default, disabled buttons are not focusable and don't react to hover.
     * As a result, they are hidden from assistive technologies, and it's not
     * possible to show a tooltip to explain why they are disabled. This can
     * be addressed by enabling the feature flag `accessibleDisabledButtons`,
     * which makes disabled buttons focusable and hoverable, while still
     * preventing them from being activated:
     *
     * ```js
     * // Set before any button is attached to the DOM.
     * window.Vaadin.featureFlags.accessibleDisabledButtons = true
     * ```
     */
    var disabled: Boolean
}

/**
 * Named exports of `@vaadin/button/vaadin-button.js`.
 *
 * Referencing the module registers the `<vaadin-button>` custom element.
 */
@JsModule("@vaadin/button/vaadin-button.js")
private external object ButtonModule {
    /** The registered custom element class. */
    val Button: dynamic
}

/**
 * Imports `@vaadin/button/vaadin-button.js`, registering the `<vaadin-button>` custom element,
 * and returns the element class.
 */
fun registerButton(): dynamic = ButtonModule.Button

/**
 * Creates a `<vaadin-button>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createButton(configure: ButtonElement.() -> Unit = {}): ButtonElement {
    registerButton()
    return document.createElement(VaadinTagName.button).apply(configure)
}
