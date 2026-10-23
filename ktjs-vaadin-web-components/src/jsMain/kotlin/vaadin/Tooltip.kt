package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-tooltip>` is a Web Component for creating tooltips.
 *
 * ```html
 * <button id="confirm">Confirm</button>
 * <vaadin-tooltip text="Click to save changes" for="confirm"></vaadin-tooltip>
 * ```
 *
 * ### Markdown Support
 *
 * The tooltip supports rendering Markdown content by setting the `markdown` property:
 *
 * ```html
 * <button id="info">Info</button>
 * <vaadin-tooltip
 *   text="**Important:** Click to view *detailed* information"
 *   markdown
 *   for="info">
 * </vaadin-tooltip>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name   | Description
 * ----------- | ---------------
 * `overlay`   | The overlay element
 * `content`   | The overlay content element
 *
 * The following state attributes are available for styling:
 *
 * Attribute        | Description
 * -----------------|----------------------------------------
 * `markdown`       | Reflects the `markdown` property value.
 * `position`       | Reflects the `position` property value.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                |
 * :----------------------------------|
 * | `--vaadin-tooltip-background`    |
 * | `--vaadin-tooltip-border-color`  |
 * | `--vaadin-tooltip-border-radius` |
 * | `--vaadin-tooltip-border-width`  |
 * | `--vaadin-tooltip-font-size`     |
 * | `--vaadin-tooltip-font-weight`   |
 * | `--vaadin-tooltip-line-height`   |
 * | `--vaadin-tooltip-max-width`     |
 * | `--vaadin-tooltip-offset-bottom` |
 * | `--vaadin-tooltip-offset-end`    |
 * | `--vaadin-tooltip-offset-start`  |
 * | `--vaadin-tooltip-offset-top`    |
 * | `--vaadin-tooltip-padding`       |
 * | `--vaadin-tooltip-shadow`        |
 * | `--vaadin-tooltip-text-color`    |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/tooltip)
 */
external class TooltipElement : HTMLElement {
    /**
     * Controls which ARIA attribute is used to link the target element(s)
     * with the tooltip content. Supported values:
     *
     * - `aria-describedby` - links the tooltip as a description.
     * - `aria-labelledby` - links the tooltip as an accessible name.
     * - `none` - does not add any ARIA linking attribute.
     *
     * Defaults to `aria-describedby`.
     */
    var ariaLinkMode: String?

    /**
     * Element used to link with the ARIA attribute controlled by the
     * `ariaLinkMode` property. Supports array of multiple elements.
     * When not set, defaults to `target`.
     */
    var ariaTarget: dynamic

    /**
     * Object with properties passed to `generator` and
     * `shouldShow` functions for generating tooltip text
     * or detecting whether to show the tooltip or not.
     */
    var context: dynamic

    /**
     * The delay in milliseconds before the tooltip
     * is opened on keyboard focus, when not in manual mode.
     */
    var focusDelay: Double?

    /**
     * The id of the element to be used as `target` value.
     * The element should be in the DOM by the time when
     * the attribute is set, otherwise a warning is shown.
     */
    var `for`: String?

    /**
     * Function used to generate the tooltip content.
     * When provided, it overrides the `text` property.
     * Use the `context` property to provide argument
     * that can be passed to the generator function.
     */
    var generator: dynamic

    /**
     * The delay in milliseconds before the tooltip
     * is closed on losing hover, when not in manual mode.
     * On blur, the tooltip is closed immediately.
     */
    var hideDelay: Double?

    /**
     * The delay in milliseconds before the tooltip
     * is opened on hover, when not in manual mode.
     */
    var hoverDelay: Double?

    /**
     * When true, the tooltip is controlled programmatically
     * instead of reacting to focus and mouse events.
     */
    var manual: Boolean

    /**
     * When enabled, the tooltip text is rendered as Markdown.
     *
     * **Note:** Using Markdown is discouraged if accessibility of the tooltip
     * content is essential, as semantics of the rendered HTML content
     * (headers, lists, ...) will not be conveyed to assistive technologies.
     */
    var markdown: Boolean

    /**
     * When true, the tooltip is opened.
     * In manual mode, this can be set programmatically.
     * In automatic mode, this is set automatically by internal logic.
     */
    var opened: Boolean

    /**
     * Position of the overlay with respect to the target.
     * Supported values: `top-start`, `top`, `top-end`,
     * `bottom-start`, `bottom`, `bottom-end`, `start-top`,
     * `start`, `start-bottom`, `end-top`, `end`, `end-bottom`.
     */
    var position: String?

    /**
     * Function used to detect whether to show the tooltip based on a condition,
     * called every time the tooltip is about to be shown on hover and focus.
     * The function takes two parameters: `target` and `context`, which contain
     * values of the corresponding tooltip properties at the time of calling.
     * The tooltip is only shown when the function invocation returns `true`.
     */
    var shouldShow: dynamic

    /**
     * Reference to the DOM element used both to trigger the overlay
     * by user interaction and to visually position it on the screen.
     *
     * Defaults to an element referenced with `for` attribute, in
     * which case it must be located in the same shadow scope.
     */
    var target: dynamic

    /**
     * String used as a tooltip content.
     */
    var text: String?
}

/**
 * Static members of the `<vaadin-tooltip>` class.
 */
private external interface TooltipStatics {
    /**
     * Sets the default focus delay to be used by all tooltip instances,
     * except for those that have focus delay configured using property.
     */
    fun setDefaultFocusDelay(focusDelay: Double)

    /**
     * Sets the default hide delay to be used by all tooltip instances,
     * except for those that have hide delay configured using property.
     */
    fun setDefaultHideDelay(hideDelay: Double)

    /**
     * Sets the default hover delay to be used by all tooltip instances,
     * except for those that have hover delay configured using property.
     */
    fun setDefaultHoverDelay(hoverDelay: Double)
}

/**
 * Named exports of `@vaadin/tooltip/vaadin-tooltip.js`.
 *
 * Referencing the module registers the `<vaadin-tooltip>` custom element.
 */
@JsModule("@vaadin/tooltip/vaadin-tooltip.js")
private external object TooltipModule {
    /** The registered custom element class. */
    val Tooltip: TooltipStatics
}

/**
 * Imports `@vaadin/tooltip/vaadin-tooltip.js`, registering the `<vaadin-tooltip>` custom element,
 * and returns the element class.
 */
fun registerTooltip(): dynamic = TooltipModule.Tooltip

/**
 * Static entry points of `<vaadin-tooltip>`.
 *
 * Calling these members imports the upstream package and registers the element.
 */
object Tooltip {
    /**
     * Sets the default focus delay to be used by all tooltip instances,
     * except for those that have focus delay configured using property.
     */
    fun setDefaultFocusDelay(focusDelay: Double) = TooltipModule.Tooltip.setDefaultFocusDelay(focusDelay)

    /**
     * Sets the default hide delay to be used by all tooltip instances,
     * except for those that have hide delay configured using property.
     */
    fun setDefaultHideDelay(hideDelay: Double) = TooltipModule.Tooltip.setDefaultHideDelay(hideDelay)

    /**
     * Sets the default hover delay to be used by all tooltip instances,
     * except for those that have hover delay configured using property.
     */
    fun setDefaultHoverDelay(hoverDelay: Double) = TooltipModule.Tooltip.setDefaultHoverDelay(hoverDelay)
}

/**
 * Creates a `<vaadin-tooltip>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createTooltip(configure: TooltipElement.() -> Unit = {}): TooltipElement {
    registerTooltip()
    return document.createElement(VaadinTagName.tooltip).apply(configure)
}
