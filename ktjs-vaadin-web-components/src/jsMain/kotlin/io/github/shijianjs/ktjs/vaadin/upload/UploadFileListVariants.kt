package io.github.shijianjs.ktjs.vaadin.upload

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-upload-file-list`: mirrors Vaadin Flow's `UploadFileListVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface UploadFileListVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [UploadFileListVariants]. */
fun UploadFileListVariant(value: String): UploadFileListVariant = unsafeCast(value)

/** All variants of [UploadFileListVariant]. */
object UploadFileListVariants {
    val thumbnails: UploadFileListVariant = UploadFileListVariant("thumbnails")
}
