package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-dialog>` is a Web Component for creating customized modal dialogs.
 *
 * ```html
 * <vaadin-dialog header-title="Title">
 *   <div>Dialog content</div>
 *   <div slot="footer">Footer</div>
 * </vaadin-dialog>
 * ```
 *
 * ### Slots
 *
 * Dialog supports following slots for providing content:
 *
 * Name             | Description
 * -----------------|-------------
 * (none)           | Default slot for the content
 * `header-content` | Slot for the header content
 * `footer`         | Slot for the footer content
 *
 * #### Renderer (deprecated)
 *
 * The content of the dialog can also be populated by using the renderer callback functions,
 * although this approach is deprecated in favor of slotted content.
 *
 * The renderer function provides `root`, `dialog` arguments.
 * Generate DOM content, append it to the `root` element and control the state
 * of the host element by accessing `dialog`. Before generating new content,
 * users are able to check if there is already content in `root` for reusing it.
 *
 * ```html
 * <vaadin-dialog id="dialog"></vaadin-dialog>
 * ```
 * ```js
 * const dialog = document.querySelector('#dialog');
 * dialog.renderer = function(root, dialog) {
 *   root.textContent = "Sample dialog";
 * };
 * ```
 *
 * Renderer is called on the opening of the dialog.
 * DOM generated during the renderer call can be reused
 * in the next renderer call and will be provided with the `root` argument.
 * On first call it will be empty.
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
 * `header`         | Element wrapping title and header content
 * `header-content` | Element wrapping the header content slot
 * `title`          | Element wrapping the title slot
 * `footer`         | Element wrapping the footer slot
 *
 * The following state attributes are available for styling:
 *
 * Attribute        | Description
 * -----------------|--------------------------------------------
 * `has-title`      | Set when the element has a title
 * `has-header`     | Set when the element has header content
 * `has-footer`     | Set when the element has footer content
 * `overflow`       | Set to `top`, `bottom`, none or both
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                          |
 * :--------------------------------------------|
 * |`--vaadin-dialog-background`                |
 * |`--vaadin-dialog-border-color`              |
 * |`--vaadin-dialog-border-radius`             |
 * |`--vaadin-dialog-border-width`              |
 * |`--vaadin-dialog-max-width`                 |
 * |`--vaadin-dialog-min-width`                 |
 * |`--vaadin-dialog-overflow-indicator-color`  |
 * |`--vaadin-dialog-overflow-indicator-height` |
 * |`--vaadin-dialog-padding`                   |
 * |`--vaadin-dialog-shadow`                    |
 * |`--vaadin-dialog-text-color`                |
 * |`--vaadin-dialog-title-color`               |
 * |`--vaadin-dialog-title-font-size`           |
 * |`--vaadin-dialog-title-font-weight`         |
 * |`--vaadin-dialog-title-line-height`         |
 * |`--vaadin-dialog-toolbar-gap`               |
 * |`--vaadin-overlay-backdrop-background`      |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/dialog)
 */
external class DialogElement : HTMLElement {
    /**
     * Custom function for rendering the dialog footer.
     * Receives two arguments:
     *
     * - `root` The root container DOM element. Append your content to it.
     * - `dialog` The reference to the `<vaadin-dialog>` element.
     *
     * When `footerRenderer` is set, the attribute `has-footer` is set on the dialog.
     */
    var footerRenderer: dynamic

    /**
     * Custom function for rendering the dialog header.
     * Receives two arguments:
     *
     * - `root` The root container DOM element. Append your content to it.
     * - `dialog` The reference to the `<vaadin-dialog>` element.
     *
     * If both `headerTitle` and `headerRenderer` are defined, the title
     * and the elements created by the renderer will be placed next to
     * each other, with the title coming first.
     *
     * When `headerRenderer` is set, the attribute `has-header` is set on the dialog.
     */
    var headerRenderer: dynamic

    /**
     * String used for rendering a dialog title.
     *
     * If both `headerTitle` and `headerRenderer` are defined, the title
     * and the elements created by the renderer will be placed next to
     * each other, with the title coming first.
     *
     * When `headerTitle` is set, the attribute `has-title` is set on the dialog.
     */
    var headerTitle: String?

    /**
     * Set the height of the dialog.
     * If a unitless number is provided, pixels are assumed.
     */
    var height: String?

    /**
     * Set to true to prevent the dialog from moving outside the viewport bounds.
     * When enabled, all four edges of the dialog will remain visible, for example
     * when dragging the dialog or when the viewport is resized. Note that the
     * dialog will also adjust any programmatically configured size and position
     * so that it stays within the viewport.
     */
    var keepInViewport: Boolean

    /**
     * Set the distance of the dialog from the left of the viewport.
     * If a unitless number is provided, pixels are assumed.
     *
     * Note that the dialog uses an internal container that has some
     * additional spacing, which can be overridden by the theme.
     */
    var left: String?

    /**
     * Set to true to remove backdrop and allow click events on background elements.
     */
    var modeless: Boolean

    /**
     * Set to true to prevent focus from moving into the dialog on open.
     *
     * This property only works for non-modal dialogs and is ignored
     * for modal ones, where focus must always stay inside.
     *
     * A field with `autofocus` inside the dialog still receives
     * focus on open.
     */
    var noAutofocus: Boolean

    /**
     * Set to true to disable closing dialog on Escape press
     */
    var noCloseOnEsc: Boolean

    /**
     * Set to true to disable closing dialog on outside click
     */
    var noCloseOnOutsideClick: Boolean

    /**
     * Set to true to prevent the dialog from receiving focus
     * on open and trapping it inside.
     *
     * A field with `autofocus` inside the dialog still receives
     * focus on open.
     */
    var noFocusTrap: Boolean

    /**
     * True if the dialog is visible and available for interaction.
     */
    var opened: Boolean

    /**
     * The `role` attribute value to be set on the dialog. Defaults to "dialog".
     */
    var overlayRole: String?

    /**
     * Custom function for rendering the content of the dialog.
     * Receives two arguments:
     *
     * - `root` The root container DOM element. Append your content to it.
     * - `dialog` The reference to the `<vaadin-dialog>` element.
     */
    var renderer: dynamic

    /**
     * Set to true to enable resizing the dialog by dragging the corners and edges.
     */
    var resizable: Boolean

    /**
     * Set the distance of the dialog from the top of the viewport.
     * If a unitless number is provided, pixels are assumed.
     *
     * Note that the dialog uses an internal container that has some
     * additional spacing, which can be overridden by the theme.
     */
    var top: String?

    /**
     * Set the width of the dialog.
     * If a unitless number is provided, pixels are assumed.
     */
    var width: String?

    /**
     * Requests an update for the content of the dialog.
     * While performing the update, it invokes the renderer passed in the `renderer` property,
     * as well as `headerRender` and `footerRenderer` properties, if these are defined.
     *
     * It is not guaranteed that the update happens immediately (synchronously) after it is requested.
     */
    fun requestContentUpdate()
}

/**
 * Named exports of `@vaadin/dialog/vaadin-dialog.js`.
 *
 * Referencing the module registers the `<vaadin-dialog>` custom element.
 */
@JsModule("@vaadin/dialog/vaadin-dialog.js")
private external object DialogModule {
    /** The registered custom element class. */
    val Dialog: dynamic
}

/**
 * Imports `@vaadin/dialog/vaadin-dialog.js`, registering the `<vaadin-dialog>` custom element,
 * and returns the element class.
 */
fun registerDialog(): dynamic = DialogModule.Dialog

/**
 * Creates a `<vaadin-dialog>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createDialog(configure: DialogElement.() -> Unit = {}): DialogElement {
    registerDialog()
    return document.createElement(VaadinTagName.dialog).apply(configure)
}
