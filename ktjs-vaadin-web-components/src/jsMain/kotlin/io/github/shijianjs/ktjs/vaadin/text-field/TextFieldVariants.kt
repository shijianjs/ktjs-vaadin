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
    inline val small: TextFieldVariant get() = TextFieldVariant("small")
    inline val alignCenter: TextFieldVariant get() = TextFieldVariant("align-center")
    inline val alignRight: TextFieldVariant get() = TextFieldVariant("align-right")
    inline val helperAbove: TextFieldVariant get() = TextFieldVariant("helper-above-field")
    inline val alignLeft: TextFieldVariant get() = TextFieldVariant("align-left")
    inline val alignStart: TextFieldVariant get() = TextFieldVariant("align-start")
    inline val alignEnd: TextFieldVariant get() = TextFieldVariant("align-end")
    inline val labelAside: TextFieldVariant get() = TextFieldVariant("label-aside")
}
