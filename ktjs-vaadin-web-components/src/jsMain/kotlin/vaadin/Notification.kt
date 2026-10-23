package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-notification>` is a Web Component providing accessible and customizable notifications (toasts).
 *
 * ### Rendering
 *
 * The content of the notification can be populated by using the renderer callback function.
 *
 * The renderer function provides `root`, `notification` arguments.
 * Generate DOM content, append it to the `root` element and control the state
 * of the host element by accessing `notification`. Before generating new content,
 * users are able to check if there is already content in `root` for reusing it.
 *
 * ```html
 * <vaadin-notification id="notification"></vaadin-notification>
 * ```
 * ```js
 * const notification = document.querySelector('#notification');
 * notification.renderer = function(root, notification) {
 *   root.textContent = "Your work has been saved";
 * };
 * ```
 *
 * Renderer is called on the opening of the notification.
 * DOM generated during the renderer call can be reused
 * in the next renderer call and will be provided with the `root` argument.
 * On first call it will be empty.
 *
 * ### Styling
 *
 * `<vaadin-notification>` uses `<vaadin-notification-card>` internal
 * themable component as the actual visible notification cards.
 *
 * The following shadow DOM parts of the `<vaadin-notification-card>` are available for styling:
 *
 * Part name | Description
 * ----------|----------------
 * `overlay` | The notification container
 * `content` | The content of the notification
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                       |
 * :-----------------------------------------|
 * | `--vaadin-notification-background`      |
 * | `--vaadin-notification-border-color`    |
 * | `--vaadin-notification-border-radius`   |
 * | `--vaadin-notification-border-width`    |
 * | `--vaadin-notification-container-gap`   |
 * | `--vaadin-notification-padding`         |
 * | `--vaadin-notification-shadow`          |
 * | `--vaadin-notification-viewport-inset`  |
 * | `--vaadin-notification-width`           |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * Note: the `theme` attribute value set on `<vaadin-notification>` is
 * propagated to the internal `<vaadin-notification-card>`.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/notification)
 */
external class NotificationElement : HTMLElement {
    /**
     * When true, the notification card has `aria-live` attribute set to
     * `assertive` instead of `polite`. This makes screen readers announce
     * the notification content immediately when it appears.
     */
    var assertive: Boolean

    /**
     * The duration in milliseconds to show the notification.
     * Set to `0` or a negative number to disable the notification auto-closing.
     */
    var duration: Double?

    /**
     * True if the notification is currently displayed.
     */
    var opened: Boolean

    /**
     * A space-delimited list of CSS class names to set on the overlay element.
     * This property does not affect other CSS class names set manually via JS.
     *
     * Note, if the CSS class name was set with this property, clearing it will
     * remove it from the overlay, even if the same class name was also added
     * manually, e.g. by using `classList.add()` in the `renderer` function.
     */
    var overlayClass: String?

    /**
     * Alignment of the notification in the viewport
     * Valid values are `top-stretch|top-start|top-center|top-end|middle|bottom-start|bottom-center|bottom-end|bottom-stretch`
     */
    var position: String?

    /**
     * Custom function for rendering the content of the notification.
     * Receives two arguments:
     *
     * - `root` The `<vaadin-notification-card>` DOM element. Append
     *   your content to it.
     * - `notification` The reference to the `<vaadin-notification>` element.
     */
    var renderer: dynamic

    /**
     * Closes the notification.
     */
    fun close()

    /**
     * Opens the notification.
     */
    fun `open`()

    /**
     * Requests an update for the content of the notification.
     * While performing the update, it invokes the renderer passed in the `renderer` property.
     *
     * It is not guaranteed that the update happens immediately (synchronously) after it is requested.
     */
    fun requestContentUpdate()
}

/**
 * Static members of the `<vaadin-notification>` class.
 */
private external interface NotificationStatics {
    /**
     * Shows a notification with the given content.
     * By default, positions the notification at `bottom-start` and uses a 5 second duration.
     * An options object can be passed to configure the notification.
     * The options object has the following structure:
     *
     * ```ts
     * {
     *   assertive?: boolean
     *   position?: string
     *   duration?: number
     *   theme?: string
     * }
     * ```
     *
     * See the individual documentation for:
     * - [`assertive`](#/elements/vaadin-notification#property-assertive)
     * - [`position`](#/elements/vaadin-notification#property-position)
     * - [`duration`](#/elements/vaadin-notification#property-duration)
     */
    fun show(contents: dynamic, options: dynamic): NotificationElement
}

/**
 * Named exports of `@vaadin/notification/vaadin-notification.js`.
 *
 * Referencing the module registers the `<vaadin-notification>` custom element.
 */
@JsModule("@vaadin/notification/vaadin-notification.js")
private external object NotificationModule {
    /** The registered custom element class. */
    val Notification: NotificationStatics
}

/**
 * Imports `@vaadin/notification/vaadin-notification.js`, registering the `<vaadin-notification>` custom element,
 * and returns the element class.
 */
fun registerNotification(): dynamic = NotificationModule.Notification

/**
 * Static entry points of `<vaadin-notification>`.
 *
 * Calling these members imports the upstream package and registers the element.
 */
object Notification {
    /**
     * Shows a notification with the given content.
     * By default, positions the notification at `bottom-start` and uses a 5 second duration.
     * An options object can be passed to configure the notification.
     * The options object has the following structure:
     *
     * ```ts
     * {
     *   assertive?: boolean
     *   position?: string
     *   duration?: number
     *   theme?: string
     * }
     * ```
     *
     * See the individual documentation for:
     * - [`assertive`](#/elements/vaadin-notification#property-assertive)
     * - [`position`](#/elements/vaadin-notification#property-position)
     * - [`duration`](#/elements/vaadin-notification#property-duration)
     * @param contents the contents to show, either as a string or a Lit template.
     * @param options optional options for customizing the notification.
     */
    fun show(contents: dynamic, options: dynamic = undefined): NotificationElement = NotificationModule.Notification.show(contents, options)
}

/**
 * Creates a `<vaadin-notification>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createNotification(configure: NotificationElement.() -> Unit = {}): NotificationElement {
    registerNotification()
    return document.createElement(VaadinTagName.notification).apply(configure)
}
