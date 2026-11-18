package io.github.shijianjs.ktjs.vaadin.textarea

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-text-area`: mirrors Vaadin Flow's `TextAreaVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface TextAreaVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [TextAreaVariants]. */
fun TextAreaVariant(value: String): TextAreaVariant = unsafeCast(value)

/** All variants of [TextAreaVariant]. */
object TextAreaVariants {
    inline val small: TextAreaVariant get() = TextAreaVariant("small")
    inline val alignCenter: TextAreaVariant get() = TextAreaVariant("align-center")
    inline val alignRight: TextAreaVariant get() = TextAreaVariant("align-right")
    inline val helperAbove: TextAreaVariant get() = TextAreaVariant("helper-above-field")
    inline val alignLeft: TextAreaVariant get() = TextAreaVariant("align-left")
    inline val alignStart: TextAreaVariant get() = TextAreaVariant("align-start")
    inline val alignEnd: TextAreaVariant get() = TextAreaVariant("align-end")
    inline val labelAside: TextAreaVariant get() = TextAreaVariant("label-aside")
}
