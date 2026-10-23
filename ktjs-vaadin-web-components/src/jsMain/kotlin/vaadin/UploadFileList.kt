package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-upload-file-list>` is a Web Component that displays a list of uploaded files.
 * It automatically syncs files from the manager and forwards file events back to it.
 *
 * ```html
 * <vaadin-upload-file-list></vaadin-upload-file-list>
 * ```
 *
 * The file list must be linked to an UploadManager by setting the `manager` property:
 *
 * ```javascript
 * import { UploadManager } from '@vaadin/upload/vaadin-upload-manager.js';
 *
 * const manager = new UploadManager({ target: '/api/upload' });
 * const fileList = document.querySelector('vaadin-upload-file-list');
 * fileList.manager = manager;
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name | Description
 * ----------|-------------
 * `list`    | The `<ul>` element wrapping the file items
 *
 * The following state attributes are available for styling:
 *
 * Attribute  | Description
 * -----------|-------------
 * `disabled` | Set when the element is disabled
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                          |
 * :--------------------------------------------|
 * `--vaadin-upload-file-list-divider-color`    |
 * `--vaadin-upload-file-list-divider-width`    |
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
external class UploadFileListElement : HTMLElement {
    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * The object used to localize this component.
     * To change the default localization, replace this with an object
     * that provides all properties, or just the individual properties
     * you want to change.
     *
     * The object has the following JSON structure and default values:
     * ```js
     * {
     *   file: {
     *     retry: 'Retry',
     *     start: 'Start',
     *     remove: 'Remove'
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
     *   units: {
     *     size: ['B', 'kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
     *   }
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * The array of files being processed, or already uploaded.
     */
    val items: dynamic

    /**
     * Reference to an UploadManager to link this file list to.
     * When set, the file list automatically:
     * - Syncs files from the manager
     * - Forwards retry/abort/start/remove events back to the manager
     */
    var manager: dynamic
}

/**
 * Named exports of `@vaadin/upload/vaadin-upload-file-list.js`.
 *
 * Referencing the module registers the `<vaadin-upload-file-list>` custom element.
 */
@JsModule("@vaadin/upload/vaadin-upload-file-list.js")
private external object UploadFileListModule {
    /** The registered custom element class. */
    val UploadFileList: dynamic
}

/**
 * Imports `@vaadin/upload/vaadin-upload-file-list.js`, registering the `<vaadin-upload-file-list>` custom element,
 * and returns the element class.
 */
fun registerUploadFileList(): dynamic = UploadFileListModule.UploadFileList

/**
 * Creates a `<vaadin-upload-file-list>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createUploadFileList(configure: UploadFileListElement.() -> Unit = {}): UploadFileListElement {
    registerUploadFileList()
    return document.createElement(VaadinTagName.uploadFileList).apply(configure)
}
