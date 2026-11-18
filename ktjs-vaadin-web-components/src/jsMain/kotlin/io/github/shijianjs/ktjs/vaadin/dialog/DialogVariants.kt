package io.github.shijianjs.ktjs.vaadin.dialog

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-dialog`: mirrors Vaadin Flow's `DialogVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface DialogVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [DialogVariants]. */
fun DialogVariant(value: String): DialogVariant = unsafeCast(value)

/** All variants of [DialogVariant]. */
object DialogVariants {
    val noPadding: DialogVariant = DialogVariant("no-padding")
}
