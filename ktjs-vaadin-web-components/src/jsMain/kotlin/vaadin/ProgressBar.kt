package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-progress-bar>` is a Web Component for progress bars.
 *
 * ```html
 * <vaadin-progress-bar min="0" max="1" value="0.5">
 * </vaadin-progress-bar>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name  | Description
 * -----------|----------------
 * `bar`      | Progress-bar's background
 * `value`    | Progress-bar's foreground
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                          |
 * :--------------------------------------------|
 * | `--vaadin-progress-bar-animation-duration` |
 * | `--vaadin-progress-bar-background`         |
 * | `--vaadin-progress-bar-border-color`       |
 * | `--vaadin-progress-bar-border-radius`      |
 * | `--vaadin-progress-bar-border-width`       |
 * | `--vaadin-progress-bar-height`             |
 * | `--vaadin-progress-bar-padding`            |
 * | `--vaadin-progress-bar-value-background`   |
 * | `--vaadin-progress-value`                  |
 *
 * The following state attributes are available for styling:
 *
 * Attribute       | Description
 * ----------------|-------------------------------------
 * `indeterminate` | Set to an indeterminate progress bar
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/progress-bar)
 */
external class ProgressBarElement : HTMLElement {
    /**
     * Indeterminate state of the progress bar.
     * This property takes precedence over other state properties (min, max, value).
     */
    var indeterminate: Boolean

    /**
     * Maximum bound of the progress bar.
     */
    var max: Double?

    /**
     * Minimum bound of the progress bar.
     */
    var min: Double?

    /**
     * Current progress value.
     */
    var value: Double?
}

/**
 * Named exports of `@vaadin/progress-bar/vaadin-progress-bar.js`.
 *
 * Referencing the module registers the `<vaadin-progress-bar>` custom element.
 */
@JsModule("@vaadin/progress-bar/vaadin-progress-bar.js")
private external object ProgressBarModule {
    /** The registered custom element class. */
    val ProgressBar: dynamic
}

/**
 * Imports `@vaadin/progress-bar/vaadin-progress-bar.js`, registering the `<vaadin-progress-bar>` custom element,
 * and returns the element class.
 */
fun registerProgressBar(): dynamic = ProgressBarModule.ProgressBar

/**
 * Creates a `<vaadin-progress-bar>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createProgressBar(configure: ProgressBarElement.() -> Unit = {}): ProgressBarElement {
    registerProgressBar()
    return document.createElement(VaadinTagName.progressBar).apply(configure)
}
