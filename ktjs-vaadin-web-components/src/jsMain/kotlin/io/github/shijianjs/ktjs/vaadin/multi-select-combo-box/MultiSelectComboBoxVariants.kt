package io.github.shijianjs.ktjs.vaadin.multiselectcombobox

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-multi-select-combo-box`: mirrors Vaadin Flow's `MultiSelectComboBoxVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface MultiSelectComboBoxVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [MultiSelectComboBoxVariants]. */
fun MultiSelectComboBoxVariant(value: String): MultiSelectComboBoxVariant = unsafeCast(value)

/** All variants of [MultiSelectComboBoxVariant]. */
object MultiSelectComboBoxVariants {
    val small: MultiSelectComboBoxVariant = MultiSelectComboBoxVariant("small")
    val alignLeft: MultiSelectComboBoxVariant = MultiSelectComboBoxVariant("align-left")
    val alignCenter: MultiSelectComboBoxVariant = MultiSelectComboBoxVariant("align-center")
    val alignRight: MultiSelectComboBoxVariant = MultiSelectComboBoxVariant("align-right")
    val helperAbove: MultiSelectComboBoxVariant = MultiSelectComboBoxVariant("helper-above-field")
    val filled: MultiSelectComboBoxVariant = MultiSelectComboBoxVariant("filled")
    val alignStart: MultiSelectComboBoxVariant = MultiSelectComboBoxVariant("align-start")
    val alignEnd: MultiSelectComboBoxVariant = MultiSelectComboBoxVariant("align-end")
    val labelAside: MultiSelectComboBoxVariant = MultiSelectComboBoxVariant("label-aside")
}
