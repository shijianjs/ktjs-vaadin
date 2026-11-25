package io.github.shijianjs.ktjs.vaadin.checkboxgroup

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
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
    val vertical: CheckboxGroupVariant = CheckboxGroupVariant("vertical")
    val helperAbove: CheckboxGroupVariant = CheckboxGroupVariant("helper-above-field")
    val horizontal: CheckboxGroupVariant = CheckboxGroupVariant("horizontal")
    val labelAside: CheckboxGroupVariant = CheckboxGroupVariant("label-aside")
}
