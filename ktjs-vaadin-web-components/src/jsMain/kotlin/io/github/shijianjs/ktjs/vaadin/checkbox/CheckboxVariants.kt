package io.github.shijianjs.ktjs.vaadin.checkbox

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-checkbox`: mirrors Vaadin Flow's `CheckboxVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface CheckboxVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [CheckboxVariants]. */
fun CheckboxVariant(value: String): CheckboxVariant = unsafeCast(value)

/** All variants of [CheckboxVariant]. */
object CheckboxVariants {
    val reverse: CheckboxVariant = CheckboxVariant("reverse")
    val small: CheckboxVariant = CheckboxVariant("small")
}
