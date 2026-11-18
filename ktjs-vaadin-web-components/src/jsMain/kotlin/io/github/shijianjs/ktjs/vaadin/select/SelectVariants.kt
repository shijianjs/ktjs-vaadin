package io.github.shijianjs.ktjs.vaadin.select

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-select`: mirrors Vaadin Flow's `SelectVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface SelectVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [SelectVariants]. */
fun SelectVariant(value: String): SelectVariant = unsafeCast(value)

/** All variants of [SelectVariant]. */
object SelectVariants {
    val small: SelectVariant = SelectVariant("small")
    val alignLeft: SelectVariant = SelectVariant("align-left")
    val alignCenter: SelectVariant = SelectVariant("align-center")
    val alignRight: SelectVariant = SelectVariant("align-right")
    val helperAbove: SelectVariant = SelectVariant("helper-above-field")
    val filled: SelectVariant = SelectVariant("filled")
    val alignStart: SelectVariant = SelectVariant("align-start")
    val alignEnd: SelectVariant = SelectVariant("align-end")
    val labelAside: SelectVariant = SelectVariant("label-aside")
}
