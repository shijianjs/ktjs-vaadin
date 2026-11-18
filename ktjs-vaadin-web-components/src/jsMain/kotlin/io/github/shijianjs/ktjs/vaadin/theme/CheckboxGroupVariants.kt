package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-checkbox-group`: mirrors Vaadin Flow's `CheckboxGroupVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface CheckboxGroupVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [CheckboxGroupVariants]. */
fun CheckboxGroupVariant(value: String): CheckboxGroupVariant = unsafeCast(value)

/** All variants of [CheckboxGroupVariant]. */
object CheckboxGroupVariants {
    inline val vertical: CheckboxGroupVariant get() = CheckboxGroupVariant("vertical")
    inline val helperAbove: CheckboxGroupVariant get() = CheckboxGroupVariant("helper-above-field")
    inline val horizontal: CheckboxGroupVariant get() = CheckboxGroupVariant("horizontal")
    inline val labelAside: CheckboxGroupVariant get() = CheckboxGroupVariant("label-aside")
}
