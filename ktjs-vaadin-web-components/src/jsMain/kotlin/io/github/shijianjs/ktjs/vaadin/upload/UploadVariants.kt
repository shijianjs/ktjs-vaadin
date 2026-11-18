package io.github.shijianjs.ktjs.vaadin.upload

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-upload`: mirrors Vaadin Flow's `UploadVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface UploadVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [UploadVariants]. */
fun UploadVariant(value: String): UploadVariant = unsafeCast(value)

/** All variants of [UploadVariant]. */
object UploadVariants {
    val noBorder: UploadVariant = UploadVariant("no-border")
}
