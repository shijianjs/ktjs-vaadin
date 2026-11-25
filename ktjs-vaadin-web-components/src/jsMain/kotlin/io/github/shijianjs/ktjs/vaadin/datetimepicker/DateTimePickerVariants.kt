package io.github.shijianjs.ktjs.vaadin.datetimepicker

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-date-time-picker`: mirrors Vaadin Flow's `DateTimePickerVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface DateTimePickerVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [DateTimePickerVariants]. */
fun DateTimePickerVariant(value: String): DateTimePickerVariant = unsafeCast(value)

/** All variants of [DateTimePickerVariant]. */
object DateTimePickerVariants {
    val small: DateTimePickerVariant = DateTimePickerVariant("small")
    val alignLeft: DateTimePickerVariant = DateTimePickerVariant("align-left")
    val alignCenter: DateTimePickerVariant = DateTimePickerVariant("align-center")
    val alignRight: DateTimePickerVariant = DateTimePickerVariant("align-right")
    val helperAbove: DateTimePickerVariant = DateTimePickerVariant("helper-above-field")
    val alignStart: DateTimePickerVariant = DateTimePickerVariant("align-start")
    val alignEnd: DateTimePickerVariant = DateTimePickerVariant("align-end")
    val labelAside: DateTimePickerVariant = DateTimePickerVariant("label-aside")
}
