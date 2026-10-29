package demo.pages.pickers

import kotlin.js.Date
import kotlin.js.unsafeCast
import vaadin.datepicker.DatePickerDate
import vaadin.datepicker.createDate
import vaadin.datepicker.extractDateParts
import vaadin.fieldbase.InputMixin
import web.html.HTMLInputElement

// ---------------------------------------------------------------------------------------------
// Local date and time helpers
//
// The docs examples use date-fns; the demo keeps the equivalent arithmetic on `kotlin.js.Date` and
// the helper functions exported by the wrapper packages, so nothing here depends on another
// library.
// ---------------------------------------------------------------------------------------------



internal fun pad2(value: Int): String = if (value < 10) "0$value" else "$value"

internal fun todayAtMidnight(): Date {
    val now = Date()
    return createDate(now.getFullYear().toDouble(), now.getMonth().toDouble(), now.getDate().toDouble())
}

internal fun addDays(date: Date, days: Int): Date =
    createDate(date.getFullYear().toDouble(), date.getMonth().toDouble(), date.getDate().toDouble() + days)

internal fun isWeekend(date: Date): Boolean = date.getDay() == 0 || date.getDay() == 6

/** Parses `MM/dd/yyyy`. */
internal fun parseMonthDayYear(text: String): DatePickerDate? {
    val pieces = text.trim().split("/")
    if (pieces.size != 3) return null
    val month = pieces[0].toIntOrNull() ?: return null
    val day = pieces[1].toIntOrNull() ?: return null
    val year = pieces[2].toIntOrNull() ?: return null
    return numericDate(year, month, day)
}

/** Parses `dd.MM.yyyy`, the second additional format of the custom-format example. */
internal fun parseDottedDayMonthYear(text: String): DatePickerDate? {
    val pieces = text.trim().split(".")
    if (pieces.size != 3) return null
    val day = pieces[0].toIntOrNull() ?: return null
    val month = pieces[1].toIntOrNull() ?: return null
    val year = pieces[2].toIntOrNull() ?: return null
    return numericDate(year, month, day)
}

internal fun numericDate(year: Int, month: Int, day: Int): DatePickerDate? {
    if (month !in 1..12 || day !in 1..31) return null
    val date = createDate(year.toDouble(), (month - 1).toDouble(), day.toDouble())
    // Rejects dates that do not exist, e.g. 2026-02-30, instead of silently shifting the month.
    if (date.getMonth() != month - 1 || date.getDate() != day) return null
    return extractDateParts(date)
}

/**
 * Reads the raw text of the `<input>` behind a field, including text the component could not parse;
 * it is empty before the field has rendered.
 */
internal fun rawInputText(field: InputMixin): String =
    field.inputElement.unsafeCast<HTMLInputElement>().value
