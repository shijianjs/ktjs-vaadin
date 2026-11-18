package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-vertical-layout`: mirrors Vaadin Flow's `VerticalLayoutVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface VerticalLayoutVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [VerticalLayoutVariants]. */
fun VerticalLayoutVariant(value: String): VerticalLayoutVariant = unsafeCast(value)

/** All variants of [VerticalLayoutVariant]. */
object VerticalLayoutVariants {
    inline val margin: VerticalLayoutVariant get() = VerticalLayoutVariant("margin")
    inline val padding: VerticalLayoutVariant get() = VerticalLayoutVariant("padding")
    inline val spacingXs: VerticalLayoutVariant get() = VerticalLayoutVariant("spacing-xs")
    inline val spacingS: VerticalLayoutVariant get() = VerticalLayoutVariant("spacing-s")
    inline val spacing: VerticalLayoutVariant get() = VerticalLayoutVariant("spacing")
    inline val spacingL: VerticalLayoutVariant get() = VerticalLayoutVariant("spacing-l")
    inline val spacingXl: VerticalLayoutVariant get() = VerticalLayoutVariant("spacing-xl")
    inline val wrap: VerticalLayoutVariant get() = VerticalLayoutVariant("wrap")
}
