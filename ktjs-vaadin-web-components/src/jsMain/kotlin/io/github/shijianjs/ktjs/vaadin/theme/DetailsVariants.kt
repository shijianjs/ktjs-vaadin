package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of the component: mirrors Vaadin Flow's `DetailsVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface DetailsVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [DetailsVariants]. */
fun DetailsVariant(value: String): DetailsVariant = unsafeCast(value)

/** All variants of [DetailsVariant]. */
object DetailsVariants {
    inline val filled: DetailsVariant get() = DetailsVariant("filled")
    inline val reverse: DetailsVariant get() = DetailsVariant("reverse")
    inline val small: DetailsVariant get() = DetailsVariant("small")
    inline val noPadding: DetailsVariant get() = DetailsVariant("no-padding")
}
