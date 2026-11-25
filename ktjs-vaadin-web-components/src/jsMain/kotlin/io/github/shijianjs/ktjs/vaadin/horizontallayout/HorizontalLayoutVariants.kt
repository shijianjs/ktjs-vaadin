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
    val margin: HorizontalLayoutVariant = HorizontalLayoutVariant("margin")
    val padding: HorizontalLayoutVariant = HorizontalLayoutVariant("padding")
    val spacingXs: HorizontalLayoutVariant = HorizontalLayoutVariant("spacing-xs")
    val spacingS: HorizontalLayoutVariant = HorizontalLayoutVariant("spacing-s")
    val spacing: HorizontalLayoutVariant = HorizontalLayoutVariant("spacing")
    val spacingL: HorizontalLayoutVariant = HorizontalLayoutVariant("spacing-l")
    val spacingXl: HorizontalLayoutVariant = HorizontalLayoutVariant("spacing-xl")
    val wrap: HorizontalLayoutVariant = HorizontalLayoutVariant("wrap")
}
