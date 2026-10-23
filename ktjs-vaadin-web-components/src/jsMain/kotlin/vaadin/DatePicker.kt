package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-date-picker>` is an input field that allows to enter a date by typing or by selecting from a calendar overlay.
 *
 * ```html
 * <vaadin-date-picker label="Birthday"></vaadin-date-picker>
 * ```
 *
 * ```js
 * datePicker.value = '2016-03-02';
 * ```
 *
 * When the selected `value` is changed, a `value-changed` event is triggered.
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name            | Description
 * ---------------------|----------------
 * `label`              | The label element
 * `input-field`        | The element that wraps prefix, value and buttons
 * `field-button`       | Set on both clear and toggle buttons
 * `clear-button`       | The clear button
 * `error-message`      | The error message element
 * `helper-text`        | The helper text element wrapper
 * `required-indicator` | The `required` state indicator element
 * `toggle-button`      | The toggle button
 * `backdrop`           | Backdrop of the overlay
 * `overlay`            | The overlay container
 * `content`            | The overlay content
 *
 * The following state attributes are available for styling:
 *
 * Attribute            | Description
 * ---------------------|---------------------------------
 * `disabled`           | Set when the element is disabled
 * `has-value`          | Set when the element has a value
 * `has-label`          | Set when the element has a label
 * `has-helper`         | Set when the element has helper text or slot
 * `has-error-message`  | Set when the element has an error message
 * `has-tooltip`        | Set when the element has a slotted tooltip
 * `invalid`            | Set when the element is invalid
 * `focused`            | Set when the element is focused
 * `focus-ring`         | Set when the element is keyboard focused
 * `readonly`           | Set when the element is readonly
 * `opened`             | Set when the overlay is opened
 * `week-numbers`       | Set when week numbers are shown in the calendar
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                        |
 * :----------------------------------------------------------|
 * | `--vaadin-date-picker-date-border-radius`                |
 * | `--vaadin-date-picker-date-disabled-color`               |
 * | `--vaadin-date-picker-date-height`                       |
 * | `--vaadin-date-picker-date-selected-background`          |
 * | `--vaadin-date-picker-date-selected-color`               |
 * | `--vaadin-date-picker-date-today-color`                  |
 * | `--vaadin-date-picker-date-width`                        |
 * | `--vaadin-date-picker-month-header-color`                |
 * | `--vaadin-date-picker-month-header-font-size`            |
 * | `--vaadin-date-picker-month-header-font-weight`          |
 * | `--vaadin-date-picker-month-padding`                     |
 * | `--vaadin-date-picker-overlay-max-height`                |
 * | `--vaadin-date-picker-overlay-width`                     |
 * | `--vaadin-date-picker-toolbar-padding`                   |
 * | `--vaadin-date-picker-week-divider-color`                |
 * | `--vaadin-date-picker-week-number-color`                 |
 * | `--vaadin-date-picker-week-number-font-size`             |
 * | `--vaadin-date-picker-weekday-color`                     |
 * | `--vaadin-date-picker-weekday-font-size`                 |
 * | `--vaadin-date-picker-weekday-font-weight`               |
 * | `--vaadin-date-picker-year-scroller-background`          |
 * | `--vaadin-date-picker-year-scroller-border-color`        |
 * | `--vaadin-date-picker-year-scroller-current-year-color`  |
 * | `--vaadin-date-picker-year-scroller-width`               |
 * | `--vaadin-field-default-width`                           |
 * | `--vaadin-input-field-background`                        |
 * | `--vaadin-input-field-border-color`                      |
 * | `--vaadin-input-field-border-radius`                     |
 * | `--vaadin-input-field-border-width`                      |
 * | `--vaadin-input-field-bottom-end-radius`                 |
 * | `--vaadin-input-field-bottom-start-radius`               |
 * | `--vaadin-input-field-button-text-color`                 |
 * | `--vaadin-input-field-container-gap`                     |
 * | `--vaadin-input-field-disabled-background`               |
 * | `--vaadin-input-field-disabled-text-color`               |
 * | `--vaadin-input-field-error-color`                       |
 * | `--vaadin-input-field-error-font-size`                   |
 * | `--vaadin-input-field-error-font-weight`                 |
 * | `--vaadin-input-field-error-line-height`                 |
 * | `--vaadin-input-field-gap`                               |
 * | `--vaadin-input-field-helper-color`                      |
 * | `--vaadin-input-field-helper-font-size`                  |
 * | `--vaadin-input-field-helper-font-weight`                |
 * | `--vaadin-input-field-helper-line-height`                |
 * | `--vaadin-input-field-label-color`                       |
 * | `--vaadin-input-field-label-font-size`                   |
 * | `--vaadin-input-field-label-font-weight`                 |
 * | `--vaadin-input-field-label-line-height`                 |
 * | `--vaadin-input-field-padding`                           |
 * | `--vaadin-input-field-placeholder-color`                 |
 * | `--vaadin-input-field-required-indicator`                |
 * | `--vaadin-input-field-required-indicator-color`          |
 * | `--vaadin-input-field-top-end-radius`                    |
 * | `--vaadin-input-field-top-start-radius`                  |
 * | `--vaadin-input-field-value-color`                       |
 * | `--vaadin-input-field-value-font-size`                   |
 * | `--vaadin-input-field-value-font-weight`                 |
 * | `--vaadin-input-field-value-line-height`                 |
 *
 * ### Internal components
 *
 * In addition to `<vaadin-date-picker>` itself, the following internal
 * components are themable:
 *
 * - `<vaadin-date-picker-overlay-content>`
 * - `<vaadin-date-picker-month-scroller>`
 * - `<vaadin-date-picker-year-scroller>`
 * - `<vaadin-date-picker-year>`
 * - `<vaadin-month-calendar>`
 *
 * In order to style the overlay content, use `<vaadin-date-picker-overlay-content>` shadow DOM parts:
 *
 * Part name             | Description
 * ----------------------|--------------------
 * `years-toggle-button` | Fullscreen mode years scroller toggle
 * `toolbar`             | Toolbar with slotted buttons
 * `loader`              | Loading spinner shown while the date metadata provider is resolving
 *
 * The following state attributes are available on the `<vaadin-date-picker-overlay-content>` element:
 *
 * Attribute       | Description
 * ----------------|-------------------------------------------------
 * `desktop`       | Set when the overlay content is in desktop mode
 * `fullscreen`    | Set when the overlay content is in fullscreen mode
 * `years-visible` | Set when the year scroller is visible in fullscreen mode
 * `loading`       | Set while the date metadata provider is resolving
 *
 * In order to style the month calendar, use `<vaadin-month-calendar>` shadow DOM parts:
 *
 * Part name             | Description
 * ----------------------|--------------------
 * `month-header`        | Month title
 * `weekdays`            | Weekday container
 * `weekday`             | Weekday element
 * `week-numbers`        | Week numbers container
 * `week-number`         | Week number element
 * `date`                | Date element
 * `disabled`            | Disabled date element
 * `loading`             | Date element in a month whose metadata is currently being fetched
 * `focused`             | Focused date element
 * `selected`            | Selected date element
 * `today`               | Date element corresponding to the current day
 * `past`                | Date element corresponding to the date in the past
 * `future`              | Date element corresponding to the date in the future
 *
 * In order to style year scroller elements, use `<vaadin-date-picker-year>` shadow DOM parts:
 *
 * Part name             | Description
 * ----------------------|--------------------
 * `year-number`         | Year number
 * `year-separator`      | Year separator
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * ### Change events
 *
 * Depending on the nature of the value change that the user attempts to commit e.g. by pressing Enter,
 * the component can fire either a `change` event or an `unparsable-change` event:
 *
 * Value change             | Event
 * :------------------------|:------------------
 * empty => parsable        | change
 * empty => unparsable      | unparsable-change
 * parsable => empty        | change
 * parsable => parsable     | change
 * parsable => unparsable   | change
 * unparsable => empty      | unparsable-change
 * unparsable => parsable   | change
 * unparsable => unparsable | unparsable-change
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/date-picker)
 */
external class DatePickerElement : HTMLElement {
    /**
     * A space-separated list of IDs referencing the elements that
     * describe the component for screen reader users. The referenced
     * elements are announced in addition to the helper text and
     * the error message.
     */
    var accessibleDescriptionRef: String?

    /**
     * String used to label the component for screen reader users.
     */
    var accessibleName: String?

    /**
     * A space-separated list of IDs referencing the elements that
     * label the component for screen reader users.
     */
    var accessibleNameRef: String?

    /**
     * A pattern matched against individual characters the user inputs.
     *
     * When set, the field will prevent:
     * - `keydown` events if the entered key doesn't match `/^allowedCharPattern$/`
     * - `paste` events if the pasted text doesn't match `/^allowedCharPattern*$/`
     * - `drop` events if the dropped text doesn't match `/^allowedCharPattern*$/`
     *
     * For example, to allow entering only numbers and minus signs, use:
     * `allowedCharPattern = "[\\d-]"`
     */
    var allowedCharPattern: String?

    /**
     * Set true to prevent the overlay from opening automatically.
     */
    var autoOpenDisabled: Boolean

    /**
     * If true, the input text gets fully selected when the field is focused using click or touch / tap.
     */
    var autoselect: Boolean

    /**
     * Set to true to display the clear icon which clears the input.
     * This also enables clearing the input when pressing Esc key.
     */
    var clearButtonVisible: Boolean

    /**
     * A function that provides metadata for the dates the calendar is about to render: whether they
     * are disabled, and CSS `part` names for styling from outside using the `::part()` selector.
     * Unlike `isDateDisabled`, which is called once per date, the metadata provider is called for
     * a range of dates at a time, and again as the calendar renders further dates.
     *
     * It receives a `DatePickerDateRange` and returns an array of `DatePickerDateMetadata` objects
     * for the dates in that range that have metadata. It can return a `Promise` to load the metadata
     * asynchronously, and `null` or `undefined` when no date in the range has metadata.
     *
     * The returned array has the following structure:
     *
     * ```js
     * [
     *   // The date is an ISO 8601 string.
     *   { date: '2026-01-01', disabled: true },
     *
     *   // Adds a custom part name to the date.
     *   { date: '2026-01-02', part: 'busy' },
     * ]
     * ```
     *
     * A date is disabled if its metadata marks it disabled, or `isDateDisabled` returns `true`, or
     * it is outside `min` and `max`. Disabled dates are not selectable, and typing a disabled date in
     * the field makes it invalid. The provider does not affect which date is focused when opening the
     * overlay. Use `initialPosition` property to provide a selectable date.
     *
     * While a returned `Promise` is pending, the dates it covers are not disabled yet and render with
     * the `loading` part. If the function throws or rejects, corresponding dates are requested again
     * the next time the user navigates.
     *
     * The provider is used for validation also when the overlay is closed. Date is considered valid
     * while the provider is pending, and is re-validated again after the metadata is loaded.
     *
     * Keep a stable reference to the function: assigning a new one clears the cache and re-fetches
     * visible range. Call `clearCache()` to re-fetch when the data behind the same function changed.
     */
    var dateMetadataProvider: dynamic

    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * Error to show when the field is invalid.
     */
    var errorMessage: String?

    /**
     * String used for the helper text.
     */
    var helperText: String?

    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following JSON structure and default values:
     *
     * ```js
     * {
     *   // An array with the full names of months starting
     *   // with January.
     *   monthNames: [
     *     'January', 'February', 'March', 'April', 'May',
     *     'June', 'July', 'August', 'September',
     *     'October', 'November', 'December'
     *   ],
     *
     *   // An array of weekday names starting with Sunday. Used
     *   // in screen reader announcements.
     *   weekdays: [
     *     'Sunday', 'Monday', 'Tuesday', 'Wednesday',
     *     'Thursday', 'Friday', 'Saturday'
     *   ],
     *
     *   // An array of short weekday names starting with Sunday.
     *   // Displayed in the calendar.
     *   weekdaysShort: [
     *     'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'
     *   ],
     *
     *   // An integer indicating the first day of the week
     *   // (0 = Sunday, 1 = Monday, etc.).
     *   firstDayOfWeek: 0,
     *
     *   // Translation of the Today shortcut button text.
     *   today: 'Today',
     *
     *   // Translation of the Cancel button text.
     *   cancel: 'Cancel',
     *
     *   // Accessible name of the overlay content, announced by screen readers
     *   // when the overlay opens.
     *   dialogAccessibleName: 'Calendar',
     *
     *   // Used for adjusting the year value when parsing dates with short years.
     *   // The year values between 0 and 99 are evaluated and adjusted.
     *   // Example: for a referenceDate of 1970-10-30;
     *   //   dateToBeParsed: 40-10-30, result: 1940-10-30
     *   //   dateToBeParsed: 80-10-30, result: 1980-10-30
     *   //   dateToBeParsed: 10-10-30, result: 2010-10-30
     *   // Supported date format: ISO 8601 `"YYYY-MM-DD"` (default)
     *   // The default value is the current date.
     *   referenceDate: '',
     *
     *   // A function to format given `Object` as
     *   // date string. Object is in the format `{ day: ..., month: ..., year: ... }`
     *   // Note: The argument month is 0-based. This means that January = 0 and December = 11.
     *   formatDate: d => {
     *     // returns a string representation of the given
     *     // object in 'MM/DD/YYYY' -format
     *   },
     *
     *   // A function to parse the given text to an `Object` in the format `{ day: ..., month: ..., year: ... }`.
     *   // Must properly parse (at least) text formatted by `formatDate`.
     *   // Setting the property to null will disable keyboard input feature.
     *   // Note: The argument month is 0-based. This means that January = 0 and December = 11.
     *   parseDate: text => {
     *     // Parses a string in 'MM/DD/YY', 'MM/DD' or 'DD' -format to
     *     // an `Object` in the format `{ day: ..., month: ..., year: ... }`.
     *   }
     *
     *   // A function to format given `monthName` and
     *   // `fullYear` integer as calendar title string.
     *   formatTitle: (monthName, fullYear) => {
     *     return monthName + ' ' + fullYear;
     *   }
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * Date which should be visible when there is no value selected.
     *
     * The same date formats as for the `value` property are supported.
     */
    var initialPosition: String?

    /**
     * Set to true when the field is invalid.
     */
    var invalid: Boolean

    /**
     * A function to be used to determine whether the user can select a given date.
     * Receives a `DatePickerDate` object of the date to be selected and should return a
     * boolean.
     *
     * The function is called once per date and has to answer synchronously. Use
     * `dateMetadataProvider` when the answer has to be loaded first, or when dates also need
     * custom part names. A date is disabled when either of the two disables it.
     */
    var isDateDisabled: dynamic

    /**
     * The label text for the input node.
     * When no light dom defined via [slot=label], this value will be used.
     */
    var label: String?

    /**
     * Set to true to enable manual validation mode. This mode disables automatic
     * constraint validation, allowing you to control the validation process yourself.
     * You can still trigger constraint validation manually with the `validate()` method
     * or use `checkValidity()` to assess the component's validity without affecting
     * the invalid state. In manual validation mode, you can also manipulate
     * the `invalid` property directly through your application logic without conflicts
     * with the component's internal validation.
     */
    var manualValidation: Boolean

    /**
     * The latest date that can be selected. All later dates will be disabled.
     *
     * Supported date formats:
     * - ISO 8601 `"YYYY-MM-DD"` (default)
     * - 6-digit extended ISO 8601 `"+YYYYYY-MM-DD"`, `"-YYYYYY-MM-DD"`
     */
    var max: String?

    /**
     * The earliest date that can be selected. All earlier dates will be disabled.
     *
     * Supported date formats:
     * - ISO 8601 `"YYYY-MM-DD"` (default)
     * - 6-digit extended ISO 8601 `"+YYYYYY-MM-DD"`, `"-YYYYYY-MM-DD"`
     */
    var min: String?

    /**
     * The name of this field.
     */
    var name: String?

    /**
     * Set true to open the date selector overlay.
     */
    var opened: Boolean

    /**
     * A hint to the user of what can be entered in the field.
     */
    var placeholder: String?

    /**
     * When present, it specifies that the field is read-only.
     */
    var readonly: Boolean

    /**
     * Specifies that the user must fill in a value.
     */
    var required: Boolean

    /**
     * Set true to display ISO-8601 week numbers in the calendar. Notice that
     * displaying week numbers is only supported when `i18n.firstDayOfWeek`
     * is 1 (Monday).
     */
    var showWeekNumbers: Boolean

    /**
     * Selected date.
     *
     * Supported date formats:
     * - ISO 8601 `"YYYY-MM-DD"` (default)
     * - 6-digit extended ISO 8601 `"+YYYYYY-MM-DD"`, `"-YYYYYY-MM-DD"`
     */
    var value: String?

    /**
     * Returns true if the current input value satisfies all constraints (if any)
     *
     * Override the `checkValidity` method for custom validations.
     */
    fun checkValidity(): Boolean

    /**
     * Clear the value of the field.
     */
    fun clear()

    /**
     * Clears the `dateMetadataProvider` cache and reloads the date metadata.
     */
    fun clearCache()

    /**
     * Closes the dropdown.
     */
    fun close()

    /**
     * Opens the dropdown.
     */
    fun `open`()

    /**
     * Validates the field and sets the `invalid` property based on the result.
     *
     * The method fires a `validated` event with the result of the validation.
     */
    fun validate(): Boolean
}

/**
 * Named exports of `@vaadin/date-picker/vaadin-date-picker.js`.
 *
 * Referencing the module registers the `<vaadin-date-picker>` custom element.
 */
@JsModule("@vaadin/date-picker/vaadin-date-picker.js")
private external object DatePickerModule {
    /** The registered custom element class. */
    val DatePicker: dynamic
}

/**
 * Imports `@vaadin/date-picker/vaadin-date-picker.js`, registering the `<vaadin-date-picker>` custom element,
 * and returns the element class.
 */
fun registerDatePicker(): dynamic = DatePickerModule.DatePicker

/**
 * Creates a `<vaadin-date-picker>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createDatePicker(configure: DatePickerElement.() -> Unit = {}): DatePickerElement {
    registerDatePicker()
    return document.createElement(VaadinTagName.datePicker).apply(configure)
}
