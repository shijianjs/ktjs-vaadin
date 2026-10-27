package vaadin.datepicker

import js.reflect.unsafeCast
import kotlin.js.Promise

/** A calendar date as understood by the date picker helpers. */
external interface DatePickerDate {
    var day: Double
    var month: Double
    var year: Double
}

/** A range of dates the calendar is about to render, always covering whole months. */
external interface DatePickerDateRange {
    /** The first date of the range (inclusive), in ISO 8601 format. */
    var start: String

    /** The last date of the range (inclusive), in ISO 8601 format. */
    var end: String
}

/** Metadata for a single date, returned by a [DatePickerDateMetadataProvider]. */
external interface DatePickerDateMetadata {
    /** The date the metadata applies to, in ISO 8601 format. */
    var date: String

    /** Whether the date cannot be selected. */
    var disabled: Boolean?

    /** Part names added to the date element, for styling with `::part()`. */
    var part: String?
}

/** The strings and formatters used to localize the date picker and its calendar. */
external interface DatePickerI18n {
    /** Full month names, starting with January. */
    var monthNames: Array<String>?

    /** Weekday names starting with Sunday, used in screen reader announcements. */
    var weekdays: Array<String>?

    /** Short weekday names starting with Sunday, displayed in the calendar. */
    var weekdaysShort: Array<String>?

    /** The first day of the week: `0` is Sunday, `1` is Monday, and so on. */
    var firstDayOfWeek: Double?

    /** The label of the today shortcut button. */
    var today: String?

    /** The label of the cancel button. */
    var cancel: String?

    /** The accessible name of the overlay content, announced when the overlay opens. */
    var dialogAccessibleName: String?

    /** The reference date used to adjust years in the range 0–99, in ISO 8601 format. */
    var referenceDate: String?

    /** Parses text into a date. Set to `null` to disable keyboard input. */
    var parseDate: ((String) -> DatePickerDate?)?

    /** Formats a date as text. */
    var formatDate: ((DatePickerDate) -> String)?

    /** Formats the calendar title from the month name and the full year. */
    var formatTitle: ((String, Double) -> String)?
}

/**
 * 上游 `DatePickerDateMetadata[] | Promise<DatePickerDateMetadata[] | null | undefined>`。
 *
 * 用同名工厂函数包装返回值：同步返回的数组用 [DatePickerDateMetadataArrayOrPromise] 的数组重载，
 * 异步加载用 Promise 重载。
 */
external interface DatePickerDateMetadataArrayOrPromise

fun DatePickerDateMetadataArrayOrPromise(
    value: Array<DatePickerDateMetadata>,
): DatePickerDateMetadataArrayOrPromise = unsafeCast(value)

fun DatePickerDateMetadataArrayOrPromise(
    value: Promise<Array<DatePickerDateMetadata>?>,
): DatePickerDateMetadataArrayOrPromise = unsafeCast(value)

/**
 * Loads metadata for the given date range.
 *
 * May return the metadata directly, a [Promise] of metadata, or `null`/`undefined` when no date in
 * the range has metadata; the latter is expressed as a nullable return type.
 */
typealias DatePickerDateMetadataProvider =
    (DatePickerDateRange) -> DatePickerDateMetadataArrayOrPromise?
