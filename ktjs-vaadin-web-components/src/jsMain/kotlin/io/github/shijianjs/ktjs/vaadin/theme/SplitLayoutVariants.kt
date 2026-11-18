package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-split-layout`: mirrors Vaadin Flow's `SplitLayoutVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface SplitLayoutVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [SplitLayoutVariants]. */
fun SplitLayoutVariant(value: String): SplitLayoutVariant = unsafeCast(value)

/** All variants of [SplitLayoutVariant]. */
object SplitLayoutVariants {
    inline val small: SplitLayoutVariant get() = SplitLayoutVariant("small")
    inline val minimal: SplitLayoutVariant get() = SplitLayoutVariant("minimal")
}
