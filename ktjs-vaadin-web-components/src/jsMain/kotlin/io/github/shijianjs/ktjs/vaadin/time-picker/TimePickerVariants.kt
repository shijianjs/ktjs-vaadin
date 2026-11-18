package io.github.shijianjs.ktjs.vaadin.timepicker

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-time-picker`: mirrors Vaadin Flow's `TimePickerVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface TimePickerVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [TimePickerVariants]. */
fun TimePickerVariant(value: String): TimePickerVariant = unsafeCast(value)

/** All variants of [TimePickerVariant]. */
object TimePickerVariants {
    val small: TimePickerVariant = TimePickerVariant("small")
    val alignLeft: TimePickerVariant = TimePickerVariant("align-left")
    val alignCenter: TimePickerVariant = TimePickerVariant("align-center")
    val alignRight: TimePickerVariant = TimePickerVariant("align-right")
    val helperAbove: TimePickerVariant = TimePickerVariant("helper-above-field")
    val filled: TimePickerVariant = TimePickerVariant("filled")
    val alignStart: TimePickerVariant = TimePickerVariant("align-start")
    val alignEnd: TimePickerVariant = TimePickerVariant("align-end")
    val labelAside: TimePickerVariant = TimePickerVariant("label-aside")
}
