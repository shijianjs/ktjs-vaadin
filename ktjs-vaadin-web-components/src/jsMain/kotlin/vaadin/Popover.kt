package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-popover>` is a Web Component for creating overlays
 * that are positioned next to specified DOM element (target).
 *
 * Unlike `<vaadin-tooltip>`, the popover supports rich content
 * that can be provided by using `renderer` function.
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name        | Description
 * -----------------|-------------------------------------------
 * `backdrop`       | Backdrop of the overlay
 * `overlay`        | The overlay container
 * `content`        | The overlay content
 * `arrow`          | Optional arrow pointing to the target when using `theme="arrow"`
 *
 * The following state attributes are available for styling:
 *
 * Attribute        | Description
 * -----------------|----------------------------------------
 * `position`       | Reflects the `position` property value.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                      |
 * :----------------------------------------|
 * |`--vaadin-overlay-backdrop-background`  |
 * |`--vaadin-popover-arrow-border-radius`  |
 * |`--vaadin-popover-arrow-inset`          |
 * |`--vaadin-popover-arrow-size`           |
 * |`--vaadin-popover-background`           |
 * |`--vaadin-popover-border-color`         |
 * |`--vaadin-popover-border-radius`        |
 * |`--vaadin-popover-border-width`         |
 * |`--vaadin-popover-offset-bottom`        |
 * |`--vaadin-popover-offset-end`           |
 * |`--vaadin-popover-offset-start`         |
 * |`--vaadin-popover-offset-top`           |
 * |`--vaadin-popover-padding`              |
 * |`--vaadin-popover-text-color`           |
 * |`--vaadin-popover-shadow`               |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/popover)
 */
external class PopoverElement : HTMLElement {
    /**
     * String used to label the popover to screen reader users.
     */
    var accessibleName: String?

    /**
     * Id of the element used as label of the popover to screen reader users.
     */
    var accessibleNameRef: String?

    /**
     * The delay in milliseconds before the popover is opened
     * on focus when the corresponding trigger is used.
     *
     * When not specified, the global default (500ms) is used.
     */
    var focusDelay: Double?

    /**
     * The id of the element to be used as `target` value.
     * The element should be in the DOM by the time when
     * the attribute is set, otherwise a warning is shown.
     */
    var `for`: String?

    /**
     * Set the height of the popover.
     * If a unitless number is provided, pixels are assumed.
     */
    var height: String?

    /**
     * The delay in milliseconds before the popover is closed
     * on losing hover, when the corresponding trigger is used.
     * On blur, the popover is closed immediately.
     *
     * When not specified, the global default (500ms) is used.
     */
    var hideDelay: Double?

    /**
     * The delay in milliseconds before the popover is opened
     * on hover when the corresponding trigger is used.
     *
     * When not specified, the global default (500ms) is used.
     */
    var hoverDelay: Double?

    /**
     * When true, the popover prevents interacting with background elements
     * by setting `pointer-events` style on the document body to `none`.
     * This also enables trapping focus inside the popover.
     */
    var modal: Boolean

    /**
     * Set to true to disable closing popover on Escape press.
     */
    var noCloseOnEsc: Boolean

    /**
     * Set to true to disable closing popover on outside click.
     */
    var noCloseOnOutsideClick: Boolean

    /**
     * When true, pressing Tab on the target or a sibling element does not move
     * focus into the popover's content (including any nested popovers), and
     * Shift+Tab does not move focus into the popover's last focusable. Focus
     * still moves out of the popover on Tab / Shift+Tab if it was placed
     * inside programmatically.
     *
     * Has no effect on modal popovers, which use their own focus trap.
     */
    var noTabFocus: Boolean

    /**
     * True if the popover is visible and available for interaction.
     */
    var opened: Boolean

    /**
     * The `role` attribute value to be set on the popover.
     */
    var overlayRole: String?

    /**
     * Position of the overlay with respect to the target.
     * Supported values: `top-start`, `top`, `top-end`,
     * `bottom-start`, `bottom`, `bottom-end`, `start-top`,
     * `start`, `start-bottom`, `end-top`, `end`, `end-bottom`.
     */
    var position: String?

    /**
     * Custom function for rendering the content of the popover.
     * Receives two arguments:
     *
     * - `root` The root container DOM element. Append your content to it.
     * - `popover` The reference to the `vaadin-popover` element.
     */
    var renderer: dynamic

    /**
     * Reference to the DOM element used both to trigger the overlay
     * by user interaction and to visually position it on the screen.
     *
     * Defaults to an element referenced with `for` attribute, in
     * which case it must be located in the same shadow scope.
     */
    var target: dynamic

    /**
     * Popover trigger mode, used to configure how the popover is opened or closed.
     * Could be set to multiple by providing an array, e.g. `trigger = ['hover', 'focus']`.
     *
     * Supported values:
     * - `click` (default) - opens and closes on target click.
     * - `hover` - opens on target mouseenter, closes on target mouseleave. Moving mouse
     * to the popover content keeps the popover opened.
     * - `focus` - opens on target focus, closes on target blur. Moving focus to the
     * popover content keeps the popover opened.
     *
     * In addition to the behavior specified by `trigger`, the popover can be closed by:
     * - pressing Escape key (unless `noCloseOnEsc` property is true)
     * - outside click (unless `noCloseOnOutsideClick` property is true)
     *
     * When setting `trigger` property to `null`, `undefined` or empty array, the popover
     * can be only opened programmatically by changing `opened` property. Note, closing
     * on Escape press or outside click is still allowed unless explicitly disabled.
     */
    var trigger: dynamic

    /**
     * Set the width of the popover.
     * If a unitless number is provided, pixels are assumed.
     */
    var width: String?

    /**
     * When true, the popover has a backdrop (modality curtain) on top of the
     * underlying page content, covering the whole viewport.
     */
    var withBackdrop: Boolean

    /**
     * Requests an update for the content of the popover.
     * While performing the update, it invokes the renderer passed in the `renderer` property.
     *
     * It is not guaranteed that the update happens immediately (synchronously) after it is requested.
     */
    fun requestContentUpdate()
}

/**
 * Static members of the `<vaadin-popover>` class.
 */
private external interface PopoverStatics {
    /**
     * Sets the default focus delay to be used by all popover instances,
     * except for those that have focus delay configured using property.
     */
    fun setDefaultFocusDelay(focusDelay: Double)

    /**
     * Sets the default hide delay to be used by all popover instances,
     * except for those that have hide delay configured using property.
     */
    fun setDefaultHideDelay(hideDelay: Double)

    /**
     * Sets the default hover delay to be used by all popover instances,
     * except for those that have hover delay configured using property.
     */
    fun setDefaultHoverDelay(hoverDelay: Double)
}

/**
 * Named exports of `@vaadin/popover/vaadin-popover.js`.
 *
 * Referencing the module registers the `<vaadin-popover>` custom element.
 */
@JsModule("@vaadin/popover/vaadin-popover.js")
private external object PopoverModule {
    /** The registered custom element class. */
    val Popover: PopoverStatics
}

/**
 * Imports `@vaadin/popover/vaadin-popover.js`, registering the `<vaadin-popover>` custom element,
 * and returns the element class.
 */
fun registerPopover(): dynamic = PopoverModule.Popover

/**
 * Static entry points of `<vaadin-popover>`.
 *
 * Calling these members imports the upstream package and registers the element.
 */
object Popover {
    /**
     * Sets the default focus delay to be used by all popover instances,
     * except for those that have focus delay configured using property.
     */
    fun setDefaultFocusDelay(focusDelay: Double) = PopoverModule.Popover.setDefaultFocusDelay(focusDelay)

    /**
     * Sets the default hide delay to be used by all popover instances,
     * except for those that have hide delay configured using property.
     */
    fun setDefaultHideDelay(hideDelay: Double) = PopoverModule.Popover.setDefaultHideDelay(hideDelay)

    /**
     * Sets the default hover delay to be used by all popover instances,
     * except for those that have hover delay configured using property.
     */
    fun setDefaultHoverDelay(hoverDelay: Double) = PopoverModule.Popover.setDefaultHoverDelay(hoverDelay)
}

/**
 * Creates a `<vaadin-popover>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createPopover(configure: PopoverElement.() -> Unit = {}): PopoverElement {
    registerPopover()
    return document.createElement(VaadinTagName.popover).apply(configure)
}
