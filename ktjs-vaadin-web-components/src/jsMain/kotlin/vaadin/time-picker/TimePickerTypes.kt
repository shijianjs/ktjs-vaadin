package vaadin.timepicker

import js.reflect.unsafeCast

/**
 * A time of day as understood by the time picker helpers.
 *
 * Each part accepts either a number or a string; wrap the value with the [TimePickerTimePart]
 * factory functions.
 */
external interface TimePickerTime {
    /** Hours, `0`–`23`. */
    var hours: TimePickerTimePart

    /** Minutes, `0`–`59`. */
    var minutes: TimePickerTimePart

    /** Seconds, `0`–`59`. */
    var seconds: TimePickerTimePart?

    /** Milliseconds. */
    var milliseconds: TimePickerTimePart?
}

/**
 * Union of a number and a string.
 *
 * Kotlin has no union types, so the members are converted explicitly with the
 * [TimePickerTimePart] factory functions.
 */
external interface TimePickerTimePart

/** Wraps a number as a [TimePickerTimePart]. */
fun TimePickerTimePart(value: Double): TimePickerTimePart = unsafeCast(value)

/** Wraps a string as a [TimePickerTimePart]. */
fun TimePickerTimePart(value: String): TimePickerTimePart = unsafeCast(value)

/** The strings and formatters used to localize the time picker. */
external interface TimePickerI18n {
    /** Parses the input text into a time. */
    var parseTime: ((String) -> TimePickerTime?)?

    /** Formats a time as the input text. */
    var formatTime: ((TimePickerTime?) -> String)?
}
