package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-switch`: mirrors Vaadin Flow's `SwitchVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface SwitchVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [SwitchVariants]. */
fun SwitchVariant(value: String): SwitchVariant = unsafeCast(value)

/** All variants of [SwitchVariant]. */
object SwitchVariants {
    inline val reverse: SwitchVariant get() = SwitchVariant("reverse")
    inline val small: SwitchVariant get() = SwitchVariant("small")
    inline val icon: SwitchVariant get() = SwitchVariant("icon")
}
