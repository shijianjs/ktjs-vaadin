package demo.pages.upload

import demo.pages.data.message
import demo.tr
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.notification.Notification
import vaadin.notification.NotificationShowOptions
import vaadin.upload.UploadElement
import vaadin.upload.UploadFile
import vaadin.upload.createUpload
import web.events.Event

/**
 * Builds fake `UploadFile`s from a JSON array.
 *
 * Every entry is assigned onto a real `File` instance, exactly like the docs helper, so the file
 * list renders the name, the size and the extra upload state fields.
 */
internal fun uploadFiles(json: String): Array<UploadFile> =
    js(
        "(json) => JSON.parse(json).map((props) => {" +
            " const file = new File([], props.name, props.type ? { type: props.type } : undefined);" +
            " Object.keys(props).forEach((key) => {" +
            "  if (key !== 'name' && key !== 'type') { file[key] = props[key]; }" +
            " });" +
            " return file;" +
            "})",
    )(json).unsafeCast<Array<UploadFile>>()

/** Fake files waiting for an explicit start, one JSON entry per [names]. */
internal fun heldFiles(vararg names: String): Array<UploadFile> {
    val entries = names.joinToString(", ") { """{"name": "$it", "status": "${tr("排队中", "Queued")}", "held": true}""" }
    return uploadFiles("[$entries]")
}

/** Creates an upload element whose requests are answered by the local mock instead of a backend. */
internal fun mockedUpload(configure: UploadElement.() -> Unit = {}): UploadElement =
    createUpload(configure).also { mockSuccessfulRequests(it) }

/** Replaces `_createXhr` on an upload element or upload manager with a mock that transfers 5 MB and returns 200. */
internal fun mockSuccessfulRequests(target: JsAny) {
    js(
        """
        (target) => {
          target._createXhr = () => {
            const xhr = { upload: {}, readyState: 0, status: 0, timeout: 0, withCredentials: false };
            xhr.open = () => { xhr.readyState = 1; };
            xhr.setRequestHeader = () => {};
            xhr.abort = () => {};
            xhr.send = () => {
              const total = 5 * 1000 * 1000;
              let loaded = 0;
              if (xhr.upload.onloadstart) xhr.upload.onloadstart();
              const timer = setInterval(() => {
                loaded = Math.min(total, loaded + 254000);
                if (xhr.upload.onprogress) {
                  xhr.upload.onprogress({ lengthComputable: true, loaded, total });
                }
                if (loaded >= total) {
                  clearInterval(timer);
                  xhr.status = 200;
                  xhr.readyState = 4;
                  if (xhr.onreadystatechange) xhr.onreadystatechange();
                }
              }, 100);
            };
            return xhr;
          };
        }
        """,
    )(target)
}

/** `file.progress` may be unset on fake files, so the read-out guards it in JS. */
internal fun fileProgress(file: UploadFile?): String =
    js("(file) => (file == null || file.progress == null ? '-' : Math.round(file.progress) + '%')")(file) as String

/** Renders `name progress` for every file of [upload], used by the auto-upload read-out. */
internal fun fileSummary(upload: UploadElement): String =
    js("(files) => files.map((file) => file.name + ' ' + (file.progress == null ? '-' : Math.round(file.progress) + '%')).join(', ')")(
        upload.files,
    ) as String

internal fun rejectText(event: Event): String =
    js("(event) => (event.detail && event.detail.error) || ''")(event) as String

internal fun rejectedFileName(event: Event): String =
    js("(event) => (event.detail && event.detail.file && event.detail.file.name) || ''")(event) as String

internal fun showRejection(event: Event) {
    val message = rejectText(event)
    Notification.show(
        message.ifEmpty { tr("文件不符合上传限制", "The file does not meet the upload restrictions") },
        unsafeJso<NotificationShowOptions> {
            position = "middle"
            theme = "error"
        },
    )
}
