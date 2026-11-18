package io.github.shijianjs.ktjs.vaadin.progressbar

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-progress-bar`: mirrors Vaadin Flow's `ProgressBarVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface ProgressBarVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [ProgressBarVariants]. */
fun ProgressBarVariant(value: String): ProgressBarVariant = unsafeCast(value)

/** All variants of [ProgressBarVariant]. */
object ProgressBarVariants {
    inline val contrast: ProgressBarVariant get() = ProgressBarVariant("contrast")
    inline val error: ProgressBarVariant get() = ProgressBarVariant("error")
    inline val success: ProgressBarVariant get() = ProgressBarVariant("success")
}
