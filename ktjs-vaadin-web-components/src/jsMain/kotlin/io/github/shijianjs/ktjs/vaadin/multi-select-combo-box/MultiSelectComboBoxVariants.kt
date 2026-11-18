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
    inline val small: MultiSelectComboBoxVariant get() = MultiSelectComboBoxVariant("small")
    inline val alignLeft: MultiSelectComboBoxVariant get() = MultiSelectComboBoxVariant("align-left")
    inline val alignCenter: MultiSelectComboBoxVariant get() = MultiSelectComboBoxVariant("align-center")
    inline val alignRight: MultiSelectComboBoxVariant get() = MultiSelectComboBoxVariant("align-right")
    inline val helperAbove: MultiSelectComboBoxVariant get() = MultiSelectComboBoxVariant("helper-above-field")
    inline val filled: MultiSelectComboBoxVariant get() = MultiSelectComboBoxVariant("filled")
    inline val alignStart: MultiSelectComboBoxVariant get() = MultiSelectComboBoxVariant("align-start")
    inline val alignEnd: MultiSelectComboBoxVariant get() = MultiSelectComboBoxVariant("align-end")
    inline val labelAside: MultiSelectComboBoxVariant get() = MultiSelectComboBoxVariant("label-aside")
}
