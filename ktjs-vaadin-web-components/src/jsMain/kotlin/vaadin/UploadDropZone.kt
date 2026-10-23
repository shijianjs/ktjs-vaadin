package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-upload-drop-zone>` is a Web Component that can be used as a drop zone
 * for file uploads. When files are dropped on the drop zone, they are added to
 * a linked UploadManager.
 *
 * ```html
 * <vaadin-upload-drop-zone>
 *   <p>Drop files here</p>
 * </vaadin-upload-drop-zone>
 * ```
 *
 * The drop zone must be linked to an UploadManager by setting the
 * `manager` property:
 *
 * ```javascript
 * const dropZone = document.querySelector('vaadin-upload-drop-zone');
 * dropZone.manager = uploadManager;
 * ```
 *
 * ### Styling
 *
 * The component has no styling by default. When files are dragged over,
 * the `dragover` attribute is set and the component uses a hover effect.
 * To override the hover effect, use `vaadin-upload-drop-zone[dragover]::after`
 * selector to style the pseudo-element covering the drop zone during dragover.
 *
 * Attribute          | Description
 * -------------------|--------------------------------------------
 * `dragover`         | Set when files are being dragged over the element
 * `disabled`         | Set when the drop zone is effectively disabled
 * `max-files-reached`| Set when the manager has reached maxFiles
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/upload)
 */
external class UploadDropZoneElement : HTMLElement {
    /**
     * Whether the drop zone is disabled.
     * Returns true if either explicitly disabled, manager is disabled, or no manager is set.
     */
    var disabled: Boolean

    /**
     * Reference to an UploadManager.
     * When set, dropped files will be automatically added to the manager.
     */
    var manager: dynamic

    /**
     * True when max files has been reached on the manager.
     */
    val maxFilesReached: Boolean
}

/**
 * Named exports of `@vaadin/upload/vaadin-upload-drop-zone.js`.
 *
 * Referencing the module registers the `<vaadin-upload-drop-zone>` custom element.
 */
@JsModule("@vaadin/upload/vaadin-upload-drop-zone.js")
private external object UploadDropZoneModule {
    /** The registered custom element class. */
    val UploadDropZone: dynamic
}

/**
 * Imports `@vaadin/upload/vaadin-upload-drop-zone.js`, registering the `<vaadin-upload-drop-zone>` custom element,
 * and returns the element class.
 */
fun registerUploadDropZone(): dynamic = UploadDropZoneModule.UploadDropZone

/**
 * Creates a `<vaadin-upload-drop-zone>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createUploadDropZone(configure: UploadDropZoneElement.() -> Unit = {}): UploadDropZoneElement {
    registerUploadDropZone()
    return document.createElement(VaadinTagName.uploadDropZone).apply(configure)
}
