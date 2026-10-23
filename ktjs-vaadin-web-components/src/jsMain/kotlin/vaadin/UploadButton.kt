package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-upload-button>` is a button component for file uploads.
 * When clicked, it opens a file picker dialog and calls addFiles
 * on a linked UploadManager.
 *
 * ```html
 * <vaadin-upload-button>Upload Files</vaadin-upload-button>
 * ```
 *
 * The button must be linked to an UploadManager by setting the
 * `manager` property:
 *
 * ```javascript
 * const button = document.querySelector('vaadin-upload-button');
 * button.manager = uploadManager;
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
 * | `--vaadin-button-font-size`      |
 * | `--vaadin-button-font-weight`    |
 * | `--vaadin-button-gap`            |
 * | `--vaadin-button-height`         |
 * | `--vaadin-button-line-height`    |
 * | `--vaadin-button-margin`         |
 * | `--vaadin-button-padding`        |
 * | `--vaadin-button-text-color`     |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/upload)
 */
external class UploadButtonElement : HTMLElement {
    /**
     * Capture attribute for mobile file input.
     */
    var capture: String?

    /**
     * Whether the button is disabled.
     * Returns true if either explicitly disabled, manager is disabled, or maxFilesReached is true.
     */
    var disabled: Boolean

    /**
     * Reference to an UploadManager.
     * When set, the button will automatically disable when maxFilesReached
     * becomes true on the manager.
     */
    var manager: dynamic

    /**
     * True when max files has been reached on the manager.
     */
    var maxFilesReached: Boolean

    /**
     * Opens the file picker dialog.
     */
    fun openFilePicker()
}

/**
 * Named exports of `@vaadin/upload/vaadin-upload-button.js`.
 *
 * Referencing the module registers the `<vaadin-upload-button>` custom element.
 */
@JsModule("@vaadin/upload/vaadin-upload-button.js")
private external object UploadButtonModule {
    /** The registered custom element class. */
    val UploadButton: dynamic
}

/**
 * Imports `@vaadin/upload/vaadin-upload-button.js`, registering the `<vaadin-upload-button>` custom element,
 * and returns the element class.
 */
fun registerUploadButton(): dynamic = UploadButtonModule.UploadButton

/**
 * Creates a `<vaadin-upload-button>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createUploadButton(configure: UploadButtonElement.() -> Unit = {}): UploadButtonElement {
    registerUploadButton()
    return document.createElement(VaadinTagName.uploadButton).apply(configure)
}
