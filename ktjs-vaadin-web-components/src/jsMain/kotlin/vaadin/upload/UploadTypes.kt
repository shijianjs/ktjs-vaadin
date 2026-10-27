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

/** The button labels of the `<vaadin-upload-file>` element. */
external interface UploadFileLabelsI18n {
    /** The label of the retry button. */
    var retry: String?

    /** The label of the start button. */
    var start: String?

    /** The label of the remove button. */
    var remove: String?
}

/** The translations of the `<vaadin-upload-file>` element. */
external interface UploadFileI18n {
    /** The button labels. */
    var file: UploadFileLabelsI18n
}

/** The file error messages of the upload file list. */
external interface UploadFileListErrorI18n {
    /** The message shown when too many files are selected. */
    var tooManyFiles: String?

    /** The message shown when a file exceeds the maximum size. */
    var fileIsTooBig: String?

    /** The message shown when a file has an incorrect type. */
    var incorrectFileType: String?
}

/** The upload status messages of the upload file list. */
external interface UploadFileListStatusI18n {
    /** The message shown while connecting. */
    var connecting: String?

    /** The message shown when the upload stalled. */
    var stalled: String?

    /** The message shown while the server processes the file. */
    var processing: String?

    /** The message shown while the upload is held. */
    var held: String?
}

/** The remaining time messages of the upload file list. */
external interface UploadFileListRemainingTimeI18n {
    /** The prefix of the remaining time message. */
    var prefix: String?

    /** The message shown when the remaining time is unknown. */
    var unknown: String?
}

/** The upload error messages of the upload file list. */
external interface UploadFileListUploadingErrorI18n {
    /** The message shown when the server is unavailable. */
    var serverUnavailable: String?

    /** The message shown on an unexpected server error. */
    var unexpectedServerError: String?

    /** The message shown when the upload is forbidden. */
    var forbidden: String?

    /** The message shown when the file is too large. */
    var fileTooLarge: String?
}

/** The ongoing upload messages of the upload file list. */
external interface UploadFileListUploadingI18n {
    /** The status messages. */
    var status: UploadFileListStatusI18n?

    /** The remaining time messages. */
    var remainingTime: UploadFileListRemainingTimeI18n?

    /** The error messages. */
    var error: UploadFileListUploadingErrorI18n?
}

/** The file size unit translations of the upload file list. */
external interface UploadFileListUnitsI18n {
    /** The unit names, from the smallest to the largest. */
    var size: Array<String>?

    /** The base used to convert between units. */
    var sizeBase: Double?
}

/** The translations of the upload file list. */
external interface UploadFileListI18n {
    /** The button labels of a single file entry. */
    var file: UploadFileLabelsI18n?

    /** The file error messages. */
    var error: UploadFileListErrorI18n?

    /** The ongoing upload messages. */
    var uploading: UploadFileListUploadingI18n?

    /** The file size units. */
    var units: UploadFileListUnitsI18n?

    /** Formats the file size. */
    var formatSize: ((bytes: Double) -> String)?

    /** Formats the upload time from seconds and the split parts. */
    var formatTime: ((seconds: Double, split: Array<Double>) -> String)?
}

/** The file count labels shown in the upload drop area. */
external interface UploadFileListCountI18n {
    /** The label used for a single file. */
    var one: String?

    /** The label used for multiple files. */
    var many: String?
}

/** The translations of the `<vaadin-upload>` element. */
external interface UploadI18n : UploadFileListI18n {
    /** The drop area label. */
    var dropFiles: UploadFileListCountI18n?

    /** The add files button label. */
    var addFiles: UploadFileListCountI18n?
}
