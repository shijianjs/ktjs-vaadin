package io.github.shijianjs.ktjs.vaadin.radiogroup

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-radio-group`: mirrors Vaadin Flow's `RadioGroupVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface RadioGroupVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [RadioGroupVariants]. */
fun RadioGroupVariant(value: String): RadioGroupVariant = unsafeCast(value)

/** All variants of [RadioGroupVariant]. */
object RadioGroupVariants {
    val vertical: RadioGroupVariant = RadioGroupVariant("vertical")
    val helperAbove: RadioGroupVariant = RadioGroupVariant("helper-above-field")
    val horizontal: RadioGroupVariant = RadioGroupVariant("horizontal")
    val labelAside: RadioGroupVariant = RadioGroupVariant("label-aside")
}
