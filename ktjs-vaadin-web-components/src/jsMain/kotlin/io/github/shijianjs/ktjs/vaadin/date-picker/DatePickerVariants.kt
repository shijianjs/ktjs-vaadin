package io.github.shijianjs.ktjs.vaadin.datepicker

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
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
    val small: DatePickerVariant = DatePickerVariant("small")
    val alignLeft: DatePickerVariant = DatePickerVariant("align-left")
    val alignCenter: DatePickerVariant = DatePickerVariant("align-center")
    val alignRight: DatePickerVariant = DatePickerVariant("align-right")
    val helperAbove: DatePickerVariant = DatePickerVariant("helper-above-field")
    val alignStart: DatePickerVariant = DatePickerVariant("align-start")
    val alignEnd: DatePickerVariant = DatePickerVariant("align-end")
    val labelAside: DatePickerVariant = DatePickerVariant("label-aside")
}
