package io.github.shijianjs.ktjs.vaadin.theme

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
    inline val elevated: CardVariant get() = CardVariant("elevated")
    inline val outlined: CardVariant get() = CardVariant("outlined")
    inline val horizontal: CardVariant get() = CardVariant("horizontal")
    inline val stretchMedia: CardVariant get() = CardVariant("stretch-media")
    inline val coverMedia: CardVariant get() = CardVariant("cover-media")
}
