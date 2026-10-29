package vaadin.datetimepicker

/**
 * The strings and formatters used to localize the date-time field.
 *
 * Upstream declares `interface DateTimePickerI18n extends DatePickerI18n, TimePickerI18n` and adds
 * the accessible labels of the two inner fields (`date-time-picker-mixin.d.ts`); the manifest only
 * records the inherited, untyped `i18n` of `I18nMixin`, so this file is hand-written and the
 * generator points the typed accessor at it.
 */
external interface DateTimePickerI18n : vaadin.datepicker.DatePickerI18n, vaadin.timepicker.TimePickerI18n {
    /** Accessible label of the inner date picker, concatenated with `accessibleName` when both are set. */
    var dateLabel: String?

    /** Accessible label of the inner time picker, concatenated with `accessibleName` when both are set. */
    var timeLabel: String?
}
