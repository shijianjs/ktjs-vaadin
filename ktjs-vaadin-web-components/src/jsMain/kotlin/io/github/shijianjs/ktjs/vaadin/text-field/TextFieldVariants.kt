package io.github.shijianjs.ktjs.vaadin.textfield

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of the component: mirrors Vaadin Flow's `TextFieldVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface TextFieldVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [TextFieldVariants]. */
fun TextFieldVariant(value: String): TextFieldVariant = unsafeCast(value)

/** All variants of [TextFieldVariant]. */
object TextFieldVariants {
    val small: TextFieldVariant = TextFieldVariant("small")
    val alignCenter: TextFieldVariant = TextFieldVariant("align-center")
    val alignRight: TextFieldVariant = TextFieldVariant("align-right")
    val helperAbove: TextFieldVariant = TextFieldVariant("helper-above-field")
    val alignLeft: TextFieldVariant = TextFieldVariant("align-left")
    val alignStart: TextFieldVariant = TextFieldVariant("align-start")
    val alignEnd: TextFieldVariant = TextFieldVariant("align-end")
    val labelAside: TextFieldVariant = TextFieldVariant("label-aside")
}
