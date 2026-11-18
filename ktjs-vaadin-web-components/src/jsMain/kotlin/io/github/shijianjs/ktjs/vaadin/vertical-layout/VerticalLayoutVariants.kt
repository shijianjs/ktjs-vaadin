package io.github.shijianjs.ktjs.vaadin.verticallayout

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
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
    val margin: VerticalLayoutVariant = VerticalLayoutVariant("margin")
    val padding: VerticalLayoutVariant = VerticalLayoutVariant("padding")
    val spacingXs: VerticalLayoutVariant = VerticalLayoutVariant("spacing-xs")
    val spacingS: VerticalLayoutVariant = VerticalLayoutVariant("spacing-s")
    val spacing: VerticalLayoutVariant = VerticalLayoutVariant("spacing")
    val spacingL: VerticalLayoutVariant = VerticalLayoutVariant("spacing-l")
    val spacingXl: VerticalLayoutVariant = VerticalLayoutVariant("spacing-xl")
    val wrap: VerticalLayoutVariant = VerticalLayoutVariant("wrap")
}
