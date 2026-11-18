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
    inline val small: DateTimePickerVariant get() = DateTimePickerVariant("small")
    inline val alignLeft: DateTimePickerVariant get() = DateTimePickerVariant("align-left")
    inline val alignCenter: DateTimePickerVariant get() = DateTimePickerVariant("align-center")
    inline val alignRight: DateTimePickerVariant get() = DateTimePickerVariant("align-right")
    inline val helperAbove: DateTimePickerVariant get() = DateTimePickerVariant("helper-above-field")
    inline val alignStart: DateTimePickerVariant get() = DateTimePickerVariant("align-start")
    inline val alignEnd: DateTimePickerVariant get() = DateTimePickerVariant("align-end")
    inline val labelAside: DateTimePickerVariant get() = DateTimePickerVariant("label-aside")
}
