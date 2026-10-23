package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-confirm-dialog>` is a Web Component for showing alerts and asking for user confirmation.
 *
 * ```html
 * <vaadin-confirm-dialog cancel-button-visible>
 *   There are unsaved changes. Do you really want to leave?
 * </vaadin-confirm-dialog>
 * ```
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
 * `header`         | The header element wrapper
 * `message`        | The message element wrapper
 * `footer`         | The footer element that wraps the buttons
 * `cancel-button`  | The "Cancel" button wrapper
 * `confirm-button` | The "Confirm" button wrapper
 * `reject-button`  | The "Reject" button wrapper
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                      |
 * :----------------------------------------|
 * |`--vaadin-confirm-dialog-max-width`     |
 * |`--vaadin-confirm-dialog-min-width`     |
 * |`--vaadin-dialog-background`            |
 * |`--vaadin-dialog-border-color`          |
 * |`--vaadin-dialog-border-radius`         |
 * |`--vaadin-dialog-border-width`          |
 * |`--vaadin-dialog-padding`               |
 * |`--vaadin-dialog-shadow`                |
 * |`--vaadin-dialog-text-color`            |
 * |`--vaadin-dialog-title-color`           |
 * |`--vaadin-dialog-title-font-size`       |
 * |`--vaadin-dialog-title-font-weight`     |
 * |`--vaadin-dialog-title-line-height`     |
 * |`--vaadin-overlay-backdrop-background`  |
 *
 * Use `confirmTheme`, `cancelTheme` and `rejectTheme` properties to customize buttons theme.
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * ### Custom content
 *
 * The following slots are available for providing custom content:
 *
 * Slot name         | Description
 * ------------------|---------------------------
 * `header`          | Slot for header element
 * `cancel-button`   | Slot for "Cancel" button
 * `confirm-button`  | Slot for "Confirm" button
 * `reject-button`   | Slot for "Reject" button
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/confirm-dialog)
 */
external class ConfirmDialogElement : HTMLElement {
    /**
     * Sets the `aria-describedby` attribute of the dialog.
     *
     * By default, the text contents of all elements inside the message area
     * are combined into the `aria-description` attribute. However, there are
     * cases where this can confuse screen reader users (e.g. the dialog
     * may present a password confirmation form). For these cases,
     * it's better to associate only the elements that will help describe
     * the confirmation dialog through this API.
     */
    var accessibleDescriptionRef: String?

    /**
     * Whether to show cancel button or not.
     */
    var cancelButtonVisible: Boolean

    /**
     * Text displayed on cancel-button.
     * This only affects the default button, custom slotted buttons will not be altered.
     */
    var cancelText: String?

    /**
     * Theme for a cancel-button.
     * This only affects the default button, custom slotted buttons will not be altered.
     */
    var cancelTheme: String?

    /**
     * Text displayed on confirm-button.
     * This only affects the default button, custom slotted buttons will not be altered.
     */
    var confirmText: String?

    /**
     * Theme for a confirm-button.
     * This only affects the default button, custom slotted buttons will not be altered.
     */
    var confirmTheme: String?

    /**
     * Set the confirmation dialog title.
     */
    var header: String?

    /**
     * Set the height of the dialog.
     * If a unitless number is provided, pixels are assumed.
     */
    var height: String?

    /**
     * Set the message or confirmation question.
     */
    var message: String?

    /**
     * Set to true to disable closing dialog on Escape press
     */
    var noCloseOnEsc: Boolean

    /**
     * True if the dialog is visible and available for interaction.
     */
    var opened: Boolean

    /**
     * Whether to show reject button or not.
     */
    var rejectButtonVisible: Boolean

    /**
     * Text displayed on reject-button.
     * This only affects the default button, custom slotted buttons will not be altered.
     */
    var rejectText: String?

    /**
     * Theme for a reject-button.
     * This only affects the default button, custom slotted buttons will not be altered.
     */
    var rejectTheme: String?

    /**
     * Set the width of the dialog.
     * If a unitless number is provided, pixels are assumed.
     */
    var width: String?
}

/**
 * Named exports of `@vaadin/confirm-dialog/vaadin-confirm-dialog.js`.
 *
 * Referencing the module registers the `<vaadin-confirm-dialog>` custom element.
 */
@JsModule("@vaadin/confirm-dialog/vaadin-confirm-dialog.js")
private external object ConfirmDialogModule {
    /** The registered custom element class. */
    val ConfirmDialog: dynamic
}

/**
 * Imports `@vaadin/confirm-dialog/vaadin-confirm-dialog.js`, registering the `<vaadin-confirm-dialog>` custom element,
 * and returns the element class.
 */
fun registerConfirmDialog(): dynamic = ConfirmDialogModule.ConfirmDialog

/**
 * Creates a `<vaadin-confirm-dialog>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createConfirmDialog(configure: ConfirmDialogElement.() -> Unit = {}): ConfirmDialogElement {
    registerConfirmDialog()
    return document.createElement(VaadinTagName.confirmDialog).apply(configure)
}
