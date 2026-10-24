package vaadin.datepicker

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
 * Loads metadata for the given date range.
 *
 * May return the metadata directly, a [kotlin.js.Promise] of metadata, or `null`/`undefined` when
 * no date in the range has metadata. Because the return type is a union of all three, it is left
 * as `dynamic`.
 */
typealias DatePickerDateMetadataProvider = (DatePickerDateRange) -> dynamic
