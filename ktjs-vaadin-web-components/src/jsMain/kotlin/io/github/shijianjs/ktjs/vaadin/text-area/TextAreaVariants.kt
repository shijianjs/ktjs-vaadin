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
    val small: TextAreaVariant = TextAreaVariant("small")
    val alignCenter: TextAreaVariant = TextAreaVariant("align-center")
    val alignRight: TextAreaVariant = TextAreaVariant("align-right")
    val helperAbove: TextAreaVariant = TextAreaVariant("helper-above-field")
    val alignLeft: TextAreaVariant = TextAreaVariant("align-left")
    val alignStart: TextAreaVariant = TextAreaVariant("align-start")
    val alignEnd: TextAreaVariant = TextAreaVariant("align-end")
    val labelAside: TextAreaVariant = TextAreaVariant("label-aside")
}
