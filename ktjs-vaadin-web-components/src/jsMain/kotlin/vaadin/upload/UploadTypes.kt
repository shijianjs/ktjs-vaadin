package vaadin.upload

/**
 * A file tracked by the upload manager.
 *
 * Upstream is a `File` extended with upload progress state. Kotlin external interfaces cannot
 * extend a Kotlin/JS class, so the common `File` properties are repeated here; instances are
 * still real `File` objects at runtime. The [xhr] property is only set while the file is being
 * uploaded.
 */
external interface UploadFile {
    /** The file name. */
    val name: String

    /** The file size in bytes. */
    val size: Double

    /** The MIME type, or an empty string when unknown. */
    val type: String

    /** The last modified time, in milliseconds since the Unix epoch. */
    val lastModified: Double

    /** The URL the file is uploaded to. */
    var uploadTarget: String

    /** Elapsed upload time in seconds. */
    var elapsed: Double

    /** Remaining upload time in seconds. */
    var remaining: Double

    /** Upload progress in percent. */
    var progress: Double

    /** Upload speed in bytes per second. */
    var speed: Double

    /** Total size in bytes. */
    var total: Double

    /** Uploaded size in bytes. */
    var loaded: Double

    /** The upload status, for example `queued`, `uploading`, `complete`, or `error`. */
    var status: String

    /** The error key when [status] is `error`. */
    var errorKey: String

    /** Whether the upload was aborted. */
    var abort: Boolean?

    /** Whether the upload completed. */
    var complete: Boolean?

    /** Whether the upload is held. */
    var held: Boolean?

    /** Whether the upload is in progress. */
    var uploading: Boolean?

    /** Whether the progress is indeterminate. */
    var indeterminate: Boolean?

    /** Whether the upload stalled. */
    var stalled: Boolean?

    /** The form data field name of the file. */
    var formDataName: String?

    /** The request performing the upload. */
    var xhr: web.xhr.XMLHttpRequest?
}
