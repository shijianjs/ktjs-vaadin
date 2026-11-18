package io.github.shijianjs.ktjs.vaadin.card

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-card`: mirrors Vaadin Flow's `CardVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface CardVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [CardVariants]. */
fun CardVariant(value: String): CardVariant = unsafeCast(value)

/** All variants of [CardVariant]. */
object CardVariants {
    val elevated: CardVariant = CardVariant("elevated")
    val outlined: CardVariant = CardVariant("outlined")
    val horizontal: CardVariant = CardVariant("horizontal")
    val stretchMedia: CardVariant = CardVariant("stretch-media")
    val coverMedia: CardVariant = CardVariant("cover-media")
}
