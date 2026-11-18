package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-button`: mirrors Vaadin Flow's `ButtonVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface ButtonVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [ButtonVariants]. */
fun ButtonVariant(value: String): ButtonVariant = unsafeCast(value)

/** All variants of [ButtonVariant]. */
object ButtonVariants {
    inline val small: ButtonVariant get() = ButtonVariant("small")
    inline val large: ButtonVariant get() = ButtonVariant("large")
    inline val tertiary: ButtonVariant get() = ButtonVariant("tertiary")
    inline val tertiaryInline: ButtonVariant get() = ButtonVariant("tertiary-inline")
    inline val primary: ButtonVariant get() = ButtonVariant("primary")
    inline val success: ButtonVariant get() = ButtonVariant("success")
    inline val warning: ButtonVariant get() = ButtonVariant("warning")
    inline val error: ButtonVariant get() = ButtonVariant("error")
    inline val contrast: ButtonVariant get() = ButtonVariant("contrast")
    inline val icon: ButtonVariant get() = ButtonVariant("icon")
}
