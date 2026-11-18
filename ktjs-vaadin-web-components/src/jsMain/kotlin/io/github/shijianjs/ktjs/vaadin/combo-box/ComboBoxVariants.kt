package io.github.shijianjs.ktjs.vaadin.combobox

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-combo-box`: mirrors Vaadin Flow's `ComboBoxVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface ComboBoxVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [ComboBoxVariants]. */
fun ComboBoxVariant(value: String): ComboBoxVariant = unsafeCast(value)

/** All variants of [ComboBoxVariant]. */
object ComboBoxVariants {
    val small: ComboBoxVariant = ComboBoxVariant("small")
    val alignLeft: ComboBoxVariant = ComboBoxVariant("align-left")
    val alignCenter: ComboBoxVariant = ComboBoxVariant("align-center")
    val alignRight: ComboBoxVariant = ComboBoxVariant("align-right")
    val helperAbove: ComboBoxVariant = ComboBoxVariant("helper-above-field")
    val filled: ComboBoxVariant = ComboBoxVariant("filled")
    val alignStart: ComboBoxVariant = ComboBoxVariant("align-start")
    val alignEnd: ComboBoxVariant = ComboBoxVariant("align-end")
    val labelAside: ComboBoxVariant = ComboBoxVariant("label-aside")
}
