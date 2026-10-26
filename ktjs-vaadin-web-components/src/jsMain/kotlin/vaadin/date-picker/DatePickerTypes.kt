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
