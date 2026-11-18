package io.github.shijianjs.ktjs.vaadin.theme

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
    inline val small: SelectVariant get() = SelectVariant("small")
    inline val alignLeft: SelectVariant get() = SelectVariant("align-left")
    inline val alignCenter: SelectVariant get() = SelectVariant("align-center")
    inline val alignRight: SelectVariant get() = SelectVariant("align-right")
    inline val helperAbove: SelectVariant get() = SelectVariant("helper-above-field")
    inline val filled: SelectVariant get() = SelectVariant("filled")
    inline val alignStart: SelectVariant get() = SelectVariant("align-start")
    inline val alignEnd: SelectVariant get() = SelectVariant("align-end")
    inline val labelAside: SelectVariant get() = SelectVariant("label-aside")
}
