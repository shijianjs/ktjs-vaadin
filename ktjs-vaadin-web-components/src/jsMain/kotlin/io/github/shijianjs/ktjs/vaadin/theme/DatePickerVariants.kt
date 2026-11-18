package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-date-picker`: mirrors Vaadin Flow's `DatePickerVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface DatePickerVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [DatePickerVariants]. */
fun DatePickerVariant(value: String): DatePickerVariant = unsafeCast(value)

/** All variants of [DatePickerVariant]. */
object DatePickerVariants {
    inline val small: DatePickerVariant get() = DatePickerVariant("small")
    inline val alignLeft: DatePickerVariant get() = DatePickerVariant("align-left")
    inline val alignCenter: DatePickerVariant get() = DatePickerVariant("align-center")
    inline val alignRight: DatePickerVariant get() = DatePickerVariant("align-right")
    inline val helperAbove: DatePickerVariant get() = DatePickerVariant("helper-above-field")
    inline val alignStart: DatePickerVariant get() = DatePickerVariant("align-start")
    inline val alignEnd: DatePickerVariant get() = DatePickerVariant("align-end")
    inline val labelAside: DatePickerVariant get() = DatePickerVariant("label-aside")
}
