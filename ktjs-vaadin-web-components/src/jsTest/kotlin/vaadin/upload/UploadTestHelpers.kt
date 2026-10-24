package vaadin.upload

import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.click
import vaadin.queryAll
import vaadin.shadowQuery
import web.html.HTMLElement

/**
 * Helpers shared by the upload test suites, translating the `helpers.js` module of
 * `packages/upload/test`.
 *
 * The upstream `xhrCreator` is deliberately not ported: it is only ever injected through the
 * private `_createXhr` member, and the suites that depend on it are listed as dropped in the
 * KDoc of the corresponding test classes. Everything here reaches the component through public
 * DOM only.
 */

private var fileCounter = 0

/** Translates `createFile(fileSize, contentType)`; the returned blob carries a unique `name`. */
internal fun createFile(size: Int = 512, contentType: String = "application/x-octet-stream"): UploadFile {
    val blob = js(
        "(size, type) => {" +
            " const array = [];" +
            " for (let i = 0; i < size; i++) array.push('A');" +
            " return new Blob([new Uint8Array(array)], { type });" +
            "}",
    )(size, contentType)
    val file = blob as UploadFile
    js("(file, name) => { file.name = name; }")(file, "file-$fileCounter")
    fileCounter += 1
    return file
}

/** Translates `createFiles(arraySize, fileSize, contentType)`. */
internal fun createFiles(
    count: Int,
    size: Int = 512,
    contentType: String = "application/x-octet-stream",
): Array<UploadFile> = Array(count) { createFile(size, contentType) }

/**
 * Parses [json] into a JS value.
 *
 * The Kotlin/JS compiler parses the content of `js(...)` and folds `() => { return <literal>; }`
 * into `() => <literal>`, which silently drops the object-literal parentheses and produces invalid
 * JavaScript. Building the value through `JSON.parse` avoids object literals inside the `js` string.
 */
internal fun jsObject(json: String): JsAny = js("(json) => JSON.parse(json)")(json).unsafeCast<JsAny>()

/**
 * Translates `addFilesViaInput(upload, files)`: assigns [files] to the hidden file input and
 * dispatches a `change` event.
 *
 * The upstream helper replaces the input prototype because `HTMLInputElement.files` is read-only;
 * the same trick is used here so the component's `change` handler reads the supplied files.
 */
internal fun addFilesViaInput(upload: HTMLElement, files: Array<UploadFile>) {
    js(
        "(upload, files) => {" +
            " const input = upload.shadowRoot.querySelector('#fileInput');" +
            " Object.setPrototypeOf(input, HTMLElement.prototype);" +
            " input.files = files;" +
            " input.dispatchEvent(new Event('change'));" +
            "}",
    )(upload, files)
}

/** Translates `removeFile(upload, idx)`: clicks the remove button of the file item at [index]. */
internal fun removeFile(upload: HTMLElement, index: Int = 0) {
    val item = queryAll(upload, "vaadin-upload-file")[index]
    click(shadowQuery(item, "[part=remove-button]")!!)
}

/** Returns the `vaadin-upload-file` items rendered by [upload]. */
internal fun fileListItems(upload: HTMLElement): Array<HTMLElement> = queryAll(upload, "vaadin-upload-file")

/** Returns the `name` of the file rendered by the item [element]. */
internal fun itemFileName(element: HTMLElement): String =
    js("(element) => element.file && element.file.name")(element) as String

/** Returns the hidden file input of [upload]. */
internal fun fileInput(upload: HTMLElement): HTMLElement = shadowQuery(upload, "#fileInput")!!

/** Reads the `multiple` attribute of the file input of [upload]. */
internal fun fileInputMultiple(upload: HTMLElement): String? = shadowQuery(upload, "#fileInput")?.getAttribute("multiple")

/** Reads the `capture` attribute of the file input of [upload]. */
internal fun fileInputCapture(upload: HTMLElement): String? = shadowQuery(upload, "#fileInput")?.getAttribute("capture")
