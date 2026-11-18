package io.github.shijianjs.ktjs.vaadin.theme

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
    inline val small: TimePickerVariant get() = TimePickerVariant("small")
    inline val alignLeft: TimePickerVariant get() = TimePickerVariant("align-left")
    inline val alignCenter: TimePickerVariant get() = TimePickerVariant("align-center")
    inline val alignRight: TimePickerVariant get() = TimePickerVariant("align-right")
    inline val helperAbove: TimePickerVariant get() = TimePickerVariant("helper-above-field")
    inline val filled: TimePickerVariant get() = TimePickerVariant("filled")
    inline val alignStart: TimePickerVariant get() = TimePickerVariant("align-start")
    inline val alignEnd: TimePickerVariant get() = TimePickerVariant("align-end")
    inline val labelAside: TimePickerVariant get() = TimePickerVariant("label-aside")
}
