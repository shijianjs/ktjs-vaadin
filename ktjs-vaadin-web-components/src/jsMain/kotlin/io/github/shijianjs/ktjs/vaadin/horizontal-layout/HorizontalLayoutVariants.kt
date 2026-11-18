package io.github.shijianjs.ktjs.vaadin.horizontallayout

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-horizontal-layout`: mirrors Vaadin Flow's `HorizontalLayoutVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface HorizontalLayoutVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [HorizontalLayoutVariants]. */
fun HorizontalLayoutVariant(value: String): HorizontalLayoutVariant = unsafeCast(value)

/** All variants of [HorizontalLayoutVariant]. */
object HorizontalLayoutVariants {
    inline val margin: HorizontalLayoutVariant get() = HorizontalLayoutVariant("margin")
    inline val padding: HorizontalLayoutVariant get() = HorizontalLayoutVariant("padding")
    inline val spacingXs: HorizontalLayoutVariant get() = HorizontalLayoutVariant("spacing-xs")
    inline val spacingS: HorizontalLayoutVariant get() = HorizontalLayoutVariant("spacing-s")
    inline val spacing: HorizontalLayoutVariant get() = HorizontalLayoutVariant("spacing")
    inline val spacingL: HorizontalLayoutVariant get() = HorizontalLayoutVariant("spacing-l")
    inline val spacingXl: HorizontalLayoutVariant get() = HorizontalLayoutVariant("spacing-xl")
    inline val wrap: HorizontalLayoutVariant get() = HorizontalLayoutVariant("wrap")
}
