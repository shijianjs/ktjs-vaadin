package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-upload-file>` element represents a file in the file list of `<vaadin-upload>`.
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name        | Description
 * -----------------|-------------
 * `thumbnail`      | Image thumbnail for image files (used with `thumbnails` theme)
 * `loader`         | Loading spinner (used with `thumbnails` theme)
 * `done-icon`      | File done status icon
 * `warning-icon`   | File warning status icon
 * `meta`           | Container for file name, status and error messages
 * `name`           | File name
 * `error`          | Error message, shown when error happens
 * `status`         | Status message
 * `commands`       | Container for file command buttons
 * `start-button`   | Start file upload button
 * `retry-button`   | Retry file upload button
 * `remove-button`  | Remove file button
 *
 * The following state attributes are available for styling:
 *
 * Attribute        | Description
 * -----------------|-------------
 * `disabled`       | Set when the element is disabled
 * `focus-ring`     | Set when the element is focused using the keyboard.
 * `focused`        | Set when the element is focused.
 * `error`          | An error has happened during uploading.
 * `indeterminate`  | Uploading is in progress, but the progress value is unknown.
 * `uploading`      | Uploading is in progress.
 * `complete`       | Uploading has finished successfully.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                          |
 * :--------------------------------------------|
 * `--vaadin-upload-file-border-radius`         |
 * `--vaadin-upload-file-button-background`     |
 * `--vaadin-upload-file-button-border-color`   |
 * `--vaadin-upload-file-button-border-radius`  |
 * `--vaadin-upload-file-button-border-width`   |
 * `--vaadin-upload-file-button-text-color`     |
 * `--vaadin-upload-file-button-padding`        |
 * `--vaadin-upload-file-done-color`            |
 * `--vaadin-upload-file-error-color`           |
 * `--vaadin-upload-file-error-font-size`       |
 * `--vaadin-upload-file-error-font-weight`     |
 * `--vaadin-upload-file-error-line-height`     |
 * `--vaadin-upload-file-gap`                   |
 * `--vaadin-upload-file-name-color`            |
 * `--vaadin-upload-file-name-font-size`        |
 * `--vaadin-upload-file-name-font-weight`      |
 * `--vaadin-upload-file-name-line-height`      |
 * `--vaadin-upload-file-padding`               |
 * `--vaadin-upload-file-status-color`          |
 * `--vaadin-upload-file-status-font-size`      |
 * `--vaadin-upload-file-status-font-weight`    |
 * `--vaadin-upload-file-status-line-height`    |
 * `--vaadin-upload-file-warning-color`         |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/upload)
 */
external class UploadFileElement : HTMLElement {
    /**
     * True if uploading is completed, false otherwise.
     */
    var complete: Boolean

    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * Error message returned by the server, if any.
     */
    var errorMessage: String?

    /**
     * The object representing a file.
     */
    var `file`: dynamic

    /**
     * Name of the uploading file.
     */
    var fileName: String?

    /**
     * True if uploading is not started, false otherwise.
     */
    var held: Boolean

    /**
     * The object used to localize this component.
     */
    var i18n: dynamic

    /**
     * True if remaining time is unknown, false otherwise.
     */
    var indeterminate: Boolean

    /**
     * Number representing the uploading progress.
     */
    var progress: Double?

    /**
     * Uploading status.
     */
    var status: String?

    /**
     * True if uploading is in progress, false otherwise.
     */
    var uploading: Boolean
}

/**
 * Named exports of `@vaadin/upload/src/vaadin-upload-file.js`.
 *
 * Referencing the module registers the `<vaadin-upload-file>` custom element.
 */
@JsModule("@vaadin/upload/src/vaadin-upload-file.js")
private external object UploadFileModule {
    /** The registered custom element class. */
    val UploadFile: dynamic
}

/**
 * Imports `@vaadin/upload/src/vaadin-upload-file.js`, registering the `<vaadin-upload-file>` custom element,
 * and returns the element class.
 */
fun registerUploadFile(): dynamic = UploadFileModule.UploadFile

/**
 * Creates a `<vaadin-upload-file>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createUploadFile(configure: UploadFileElement.() -> Unit = {}): UploadFileElement {
    registerUploadFile()
    return document.createElement(VaadinTagName.uploadFile).apply(configure)
}
