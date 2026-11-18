package io.github.shijianjs.ktjs.vaadin.button

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
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
    val small: ButtonVariant = ButtonVariant("small")
    val large: ButtonVariant = ButtonVariant("large")
    val tertiary: ButtonVariant = ButtonVariant("tertiary")
    val tertiaryInline: ButtonVariant = ButtonVariant("tertiary-inline")
    val primary: ButtonVariant = ButtonVariant("primary")
    val success: ButtonVariant = ButtonVariant("success")
    val warning: ButtonVariant = ButtonVariant("warning")
    val error: ButtonVariant = ButtonVariant("error")
    val contrast: ButtonVariant = ButtonVariant("contrast")
    val icon: ButtonVariant = ButtonVariant("icon")
}
