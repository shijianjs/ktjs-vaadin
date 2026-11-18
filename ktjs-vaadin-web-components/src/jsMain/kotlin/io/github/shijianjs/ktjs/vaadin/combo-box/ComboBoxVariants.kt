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
    inline val small: ComboBoxVariant get() = ComboBoxVariant("small")
    inline val alignLeft: ComboBoxVariant get() = ComboBoxVariant("align-left")
    inline val alignCenter: ComboBoxVariant get() = ComboBoxVariant("align-center")
    inline val alignRight: ComboBoxVariant get() = ComboBoxVariant("align-right")
    inline val helperAbove: ComboBoxVariant get() = ComboBoxVariant("helper-above-field")
    inline val filled: ComboBoxVariant get() = ComboBoxVariant("filled")
    inline val alignStart: ComboBoxVariant get() = ComboBoxVariant("align-start")
    inline val alignEnd: ComboBoxVariant get() = ComboBoxVariant("align-end")
    inline val labelAside: ComboBoxVariant get() = ComboBoxVariant("label-aside")
}
