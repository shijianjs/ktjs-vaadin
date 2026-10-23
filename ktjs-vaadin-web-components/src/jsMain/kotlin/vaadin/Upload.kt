package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-upload>` is a Web Component for uploading multiple files with drag and drop support.
 *
 * Example:
 *
 * ```html
 * <vaadin-upload></vaadin-upload>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name          | Description
 * -------------------|-------------------------------------
 * `primary-buttons`  | Upload container
 * `drop-label`       | Element wrapping drop label and icon
 *
 * The following state attributes are available for styling:
 *
 * Attribute            | Description
 * ---------------------|---------------------------------
 * `disabled`           | Set when the element is disabled
 * `nodrop`             | Set when drag and drop is disabled (e.g., on touch devices)
 * `dragover`           | Set when the file is being dragged over the element
 * `dragover-valid`     | Set when the dragged file is valid with `maxFiles` and `accept` criteria
 * `max-files-reached`  | Set when maximum number of files that the user is allowed to add has been reached
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                             |
 * :-----------------------------------------------|
 * | `--vaadin-upload-background`                  |
 * | `--vaadin-upload-border-color`                |
 * | `--vaadin-upload-border-radius`               |
 * | `--vaadin-upload-border-width`                |
 * | `--vaadin-upload-drop-label-color`            |
 * | `--vaadin-upload-drop-label-font-size`        |
 * | `--vaadin-upload-drop-label-font-weight`      |
 * | `--vaadin-upload-drop-label-gap`              |
 * | `--vaadin-upload-drop-label-line-height`      |
 * | `--vaadin-upload-file-border-radius`          |
 * | `--vaadin-upload-file-button-background`      |
 * | `--vaadin-upload-file-button-border-color`    |
 * | `--vaadin-upload-file-button-border-radius`   |
 * | `--vaadin-upload-file-button-border-width`    |
 * | `--vaadin-upload-file-button-padding`         |
 * | `--vaadin-upload-file-button-text-color`      |
 * | `--vaadin-upload-file-done-color`             |
 * | `--vaadin-upload-file-error-color`            |
 * | `--vaadin-upload-file-error-font-size`        |
 * | `--vaadin-upload-file-error-font-weight`      |
 * | `--vaadin-upload-file-error-line-height`      |
 * | `--vaadin-upload-file-gap`                    |
 * | `--vaadin-upload-file-list-divider-color`     |
 * | `--vaadin-upload-file-list-divider-width`     |
 * | `--vaadin-upload-file-name-color`             |
 * | `--vaadin-upload-file-name-font-size`         |
 * | `--vaadin-upload-file-name-font-weight`       |
 * | `--vaadin-upload-file-name-line-height`       |
 * | `--vaadin-upload-file-padding`                |
 * | `--vaadin-upload-file-status-color`           |
 * | `--vaadin-upload-file-status-font-size`       |
 * | `--vaadin-upload-file-status-font-weight`     |
 * | `--vaadin-upload-file-status-line-height`     |
 * | `--vaadin-upload-file-warning-color`          |
 * | `--vaadin-upload-gap`                         |
 * | `--vaadin-upload-icon-color`                  |
 * | `--vaadin-upload-padding`                     |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/upload)
 */
external class UploadElement : HTMLElement {
    /**
     * Specifies the types of files that the server accepts.
     * Syntax: a comma-separated list of MIME type patterns (wildcards are
     * allowed) or file extensions.
     * Notice that MIME types are widely supported, while file extensions
     * are only implemented in certain browsers, so avoid using it.
     * Example: accept="video/&#42;,image/tiff" or accept=".pdf,audio/mp3"
     */
    var accept: String?

    /**
     * Pass-through to input's capture attribute. Allows user to trigger device inputs
     * such as camera or microphone immediately.
     */
    var capture: String?

    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * The array of files being processed, or already uploaded.
     *
     * Each element is a [`File`](https://developer.mozilla.org/en-US/docs/Web/API/File)
     * object with a number of extra properties  to track the upload process:
     * - `uploadTarget`: The target URL used to upload this file.
     * - `elapsed`: Elapsed time since the upload started.
     * - `elapsedStr`: Human-readable elapsed time.
     * - `remaining`: Number of seconds remaining for the upload to finish.
     * - `remainingStr`: Human-readable remaining time for the upload to finish.
     * - `progress`: Percentage of the file already uploaded.
     * - `speed`: Upload speed in kB/s.
     * - `size`: File size in bytes.
     * - `totalStr`: Human-readable total size of the file.
     * - `loaded`: Bytes transferred so far.
     * - `loadedStr`: Human-readable uploaded size at the moment.
     * - `status`: Status of the upload process.
     * - `error`: Error message in case the upload failed.
     * - `abort`: True if the file was canceled by the user.
     * - `complete`: True when the file was transferred to the server.
     * - `uploading`: True while transferring data to the server.
     */
    var files: dynamic

    /**
     * Specifies the 'name' property at Content-Disposition for multipart uploads.
     * This property is ignored when uploadFormat is 'raw'.
     */
    var formDataName: String?

    /**
     * Key-Value map to send to the server. If you set this property as an
     * attribute, use a valid JSON string, for example:
     * ```html
     * <vaadin-upload headers='{"X-Foo": "Bar"}'></vaadin-upload>
     * ```
     */
    var headers: dynamic

    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following JSON structure and default values:
     *
     * ```js
     * {
     *   dropFiles: {
     *     one: 'Drop file here',
     *     many: 'Drop files here'
     *   },
     *   addFiles: {
     *     one: 'Upload File...',
     *     many: 'Upload Files...'
     *   },
     *   error: {
     *     tooManyFiles: 'Too Many Files.',
     *     fileIsTooBig: 'File is Too Big.',
     *     incorrectFileType: 'Incorrect File Type.'
     *   },
     *   uploading: {
     *     status: {
     *       connecting: 'Connecting...',
     *       stalled: 'Stalled',
     *       processing: 'Processing File...',
     *       held: 'Queued'
     *     },
     *     remainingTime: {
     *       prefix: 'remaining time: ',
     *       unknown: 'unknown remaining time'
     *     },
     *     error: {
     *       serverUnavailable: 'Upload failed, please try again later',
     *       unexpectedServerError: 'Upload failed due to server error',
     *       forbidden: 'Upload forbidden',
     *       fileTooLarge: 'File is too large'
     *     }
     *   },
     *   file: {
     *     retry: 'Retry',
     *     start: 'Start',
     *     remove: 'Remove'
     *   },
     *   units: {
     *     size: ['B', 'kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
     *     sizeBase: 1000
     *   },
     *   formatSize: function(bytes) {
     *     // returns the size followed by the best suitable unit
     *   },
     *   formatTime: function(seconds, [secs, mins, hours]) {
     *     // returns a 'HH:MM:SS' string
     *   }
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * Specifies the maximum number of files that can be uploaded simultaneously.
     * This helps prevent browser performance degradation and XHR limitations when
     * uploading large numbers of files. Files exceeding this limit will be queued
     * and uploaded as active uploads complete.
     */
    var maxConcurrentUploads: Double?

    /**
     * Limit of files to upload, by default it is unlimited. If the value is
     * set to one, native file browser will prevent selecting multiple files.
     */
    var maxFiles: Double?

    /**
     * Specifies the maximum file size in bytes allowed to upload.
     * Notice that it is a client-side constraint, which will be checked before
     * sending the request. Obviously you need to do the same validation in
     * the server-side and be sure that they are aligned.
     */
    var maxFileSize: Double?

    /**
     * Specifies if the maximum number of files have been uploaded
     */
    val maxFilesReached: Boolean

    /**
     * HTTP Method used to send the files. Only POST and PUT are allowed.
     */
    var method: String?

    /**
     * Prevents upload(s) from immediately uploading upon adding file(s).
     * When set, you must manually trigger uploads using the `uploadFiles` method
     */
    var noAuto: Boolean

    /**
     * Define whether the element supports dropping files on it for uploading.
     * By default it's enabled in desktop and disabled in touch devices
     * because mobile devices do not support drag events in general. Setting
     * it false means that drop is enabled even in touch-devices, and true
     * disables drop in all devices.
     */
    var nodrop: Boolean

    /**
     * The server URL. The default value is an empty string, which means that
     * _window.location_ will be used.
     */
    var target: String?

    /**
     * Max time in milliseconds for the entire upload process, if exceeded the
     * request will be aborted. Zero means that there is no timeout.
     */
    var timeout: Double?

    /**
     * Specifies the upload format to use when sending files to the server.
     * - 'raw': Send file as raw binary data with the file's MIME type as Content-Type (default)
     * - 'multipart': Send file using multipart/form-data encoding
     */
    var uploadFormat: String?

    /**
     * Set the withCredentials flag on the request.
     */
    var withCredentials: Boolean

    /**
     * Triggers the upload of any files that are not completed
     * @param files Files being uploaded. Defaults to all outstanding files
     */
    fun uploadFiles(files: dynamic = definedExternally)
}

/**
 * Named exports of `@vaadin/upload/vaadin-upload.js`.
 *
 * Referencing the module registers the `<vaadin-upload>` custom element.
 */
@JsModule("@vaadin/upload/vaadin-upload.js")
private external object UploadModule {
    /** The registered custom element class. */
    val Upload: dynamic
}

/**
 * Imports `@vaadin/upload/vaadin-upload.js`, registering the `<vaadin-upload>` custom element,
 * and returns the element class.
 */
fun registerUpload(): dynamic = UploadModule.Upload

/**
 * Creates a `<vaadin-upload>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createUpload(configure: UploadElement.() -> Unit = {}): UploadElement {
    registerUpload()
    return document.createElement(VaadinTagName.upload).apply(configure)
}
