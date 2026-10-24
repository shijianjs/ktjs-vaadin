package vaadin.datepicker

import kotlin.js.js

/**
 * Helpers shared by the date picker suites, mirroring `packages/date-picker/test/helpers.js`.
 *
 * `inputElement` has no typed wrapper (it is a slotted `<input>` created by the field controller),
 * so the few helpers below reach it from JS instead of casting a light DOM query result.
 */

/** Reads the text currently shown in the input of [picker]. */
internal fun datePickerInputValue(picker: DatePickerElement): String =
    js("(picker) => picker.inputElement.value")(picker) as String

/** Emulates the user typing [value] into the input of [picker], mirroring `setInputValue`. */
internal fun setDatePickerInput(picker: DatePickerElement, value: String) {
    js(
        "(picker, value) => {" +
            " picker.inputElement.value = value;" +
            " picker.inputElement.dispatchEvent(new Event('input', { bubbles: true, composed: true }));" +
            "}",
    )(picker, value)
}

/** Sends an `Enter` keydown to the input of [picker], mirroring the upstream `enter()` helper. */
internal fun pressEnterInDatePicker(picker: DatePickerElement) {
    js(
        "(picker) => picker.inputElement.dispatchEvent(" +
            "new KeyboardEvent('keydown', { key: 'Enter', bubbles: true, composed: true, cancelable: true }))",
    )(picker)
}

/** Focuses and blurs the input of [picker], mirroring `input.focus(); input.blur();`. */
internal fun blurDatePickerInput(picker: DatePickerElement) {
    js(
        "(picker) => {" +
            " picker.inputElement.focus();" +
            " picker.inputElement.blur();" +
            "}",
    )(picker)
}

/*
 * Upstream files of `packages/date-picker/test/` that are not translated yet, and why:
 *
 * - `keyboard-input.test.js`, `keyboard-navigation.test.js`, `value-commit.test.js`,
 *   `value-commit-auto-open-disabled.test.js`: they type and navigate with `sendKeys` and assert on
 *   `_focusedDate` / `_overlayContent` state after the real event sequence.
 * - `month-calendar.test.js`, `scroller.test.js`, `overlay-content.test.js`, `dropdown.test.js`,
 *   `fullscreen.test.js`, `overlay-content.test.js`: they assert on the calendar overlay internals
 *   (`$.overlay`, `_overlayContent.$`, scroll offsets, `--vaadin-date-picker-overlay-*` geometry).
 * - `wai-aria.test.js`: the host-level `aria-expanded` case is covered by `BasicTest`; the rest reads
 *   the overlay content parts (`role`, `aria-label`, `aria-hidden` of the calendars).
 * - `date-metadata-*.test.js`: they drive the `DateMetadataController` through the calendar overlay
 *   and its metadata parts.
 * - `theme-propagation.test.js`: it asserts computed CSS custom properties from the overlay.
 */

