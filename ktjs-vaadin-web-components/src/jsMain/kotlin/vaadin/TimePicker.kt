package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-time-picker>` is a Web Component providing a time-selection field.
 *
 * ```html
 * <vaadin-time-picker></vaadin-time-picker>
 * ```
 * ```js
 * timePicker.value = '14:30';
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
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                |
 * :--------------------------------------------------|
 * | `--vaadin-field-default-width`                   |
 * | `--vaadin-input-field-background`                |
 * | `--vaadin-input-field-border-color`              |
 * | `--vaadin-input-field-border-radius`             |
 * | `--vaadin-input-field-border-width`              |
 * | `--vaadin-input-field-bottom-end-radius`         |
 * | `--vaadin-input-field-bottom-start-radius`       |
 * | `--vaadin-input-field-button-text-color`         |
 * | `--vaadin-input-field-container-gap`             |
 * | `--vaadin-input-field-disabled-background`       |
 * | `--vaadin-input-field-disabled-text-color`       |
 * | `--vaadin-input-field-error-color`               |
 * | `--vaadin-input-field-error-font-size`           |
 * | `--vaadin-input-field-error-font-weight`         |
 * | `--vaadin-input-field-error-line-height`         |
 * | `--vaadin-input-field-gap`                       |
 * | `--vaadin-input-field-helper-color`              |
 * | `--vaadin-input-field-helper-font-size`          |
 * | `--vaadin-input-field-helper-font-weight`        |
 * | `--vaadin-input-field-helper-line-height`        |
 * | `--vaadin-input-field-label-color`               |
 * | `--vaadin-input-field-label-font-size`           |
 * | `--vaadin-input-field-label-font-weight`         |
 * | `--vaadin-input-field-label-line-height`         |
 * | `--vaadin-input-field-padding`                   |
 * | `--vaadin-input-field-placeholder-color`         |
 * | `--vaadin-input-field-required-indicator`        |
 * | `--vaadin-input-field-required-indicator-color`  |
 * | `--vaadin-input-field-top-end-radius`            |
 * | `--vaadin-input-field-top-start-radius`          |
 * | `--vaadin-input-field-value-color`               |
 * | `--vaadin-input-field-value-font-size`           |
 * | `--vaadin-input-field-value-font-weight`         |
 * | `--vaadin-input-field-value-line-height`         |
 * | `--vaadin-item-overlay-padding`                  |
 * | `--vaadin-time-picker-overlay-max-height`        |
 * | `--vaadin-time-picker-overlay-width`             |
 *
 * ### Internal components
 *
 * In addition to `<vaadin-time-picker>` itself, the following internal
 * components are themable:
 *
 * - `<vaadin-time-picker-item>` - has the same API as [`<vaadin-item>`](#/elements/vaadin-item).
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
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/time-picker)
 */
external class TimePickerElement : HTMLElement {
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
     * localization, replace this with an object that provides both the
     * time parsing and formatting functions.
     *
     * The object has the following JSON structure:
     *
     * ```js
     * {
     *   // A function to format given `Object` as
     *   // time string. Object is in the format `{ hours: ..., minutes: ..., seconds: ..., milliseconds: ... }`
     *   formatTime: (time) => {
     *     // returns a string representation of the given
     *     // object in `hh` / 'hh:mm' / 'hh:mm:ss' / 'hh:mm:ss.fff' - formats
     *   },
     *
     *   // A function to parse the given text to an `Object` in the format
     *   // `{ hours: ..., minutes: ..., seconds: ..., milliseconds: ... }`.
     *   // Must properly parse (at least) text
     *   // formatted by `formatTime`.
     *   parseTime: text => {
     *     // Parses a string in object/string that can be formatted by`formatTime`.
     *   }
     * }
     * ```
     *
     * NOTE: `formatTime` and `parseTime` must be implemented in a
     * compatible manner to ensure the component works properly.
     *
     * NOTE: these functions only apply to the text shown in the input field and
     * in the dropdown. The `value`, `min` and `max` properties always use the
     * ISO 8601 format, and are never passed to `parseTime`, so implementations
     * do not need to accept ISO 8601 input.
     */
    var i18n: dynamic

    /**
     * Set to true when the field is invalid.
     */
    var invalid: Boolean

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
     * Maximum time allowed.
     *
     * Supported time formats are in ISO 8601:
     * - `hh:mm`
     * - `hh:mm:ss`
     * - `hh:mm:ss.fff`
     */
    var max: String?

    /**
     * Minimum time allowed.
     *
     * Supported time formats are in ISO 8601:
     * - `hh:mm`
     * - `hh:mm:ss`
     * - `hh:mm:ss.fff`
     */
    var min: String?

    /**
     * The name of this field.
     */
    var name: String?

    /**
     * True if the dropdown is open, false otherwise.
     */
    var opened: Boolean

    /**
     * A regular expression that the value is checked against.
     * The pattern must match the entire value, not just some subset.
     */
    var pattern: String?

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
     * Defines the time interval (in seconds) between the items displayed
     * in the time selection box. The default is 1 hour (i.e. `3600`).
     *
     * It also configures the precision of the value string. By default
     * the component formats values as `hh:mm` but setting a step value
     * lower than one minute or one second, format resolution changes to
     * `hh:mm:ss` and `hh:mm:ss.fff` respectively.
     *
     * Unit must be set in seconds, and for correctly configuring intervals
     * in the dropdown, it need to evenly divide a day.
     *
     * Note: it is possible to define step that is dividing an hour in inexact
     * fragments (i.e. 5760 seconds which equals 1 hour 36 minutes), but it is
     * not recommended to use it for better UX experience.
     */
    var step: Double?

    /**
     * The time value for this element.
     *
     * Supported time formats are in ISO 8601:
     * - `hh:mm` (default)
     * - `hh:mm:ss`
     * - `hh:mm:ss.fff`
     */
    var value: String?

    /**
     * Returns true if the current input value satisfies all constraints (if any).
     * You can override this method for custom validations.
     */
    fun checkValidity(): Boolean

    /**
     * Clear the value of the field.
     */
    fun clear()

    /**
     * Closes the dropdown list.
     */
    fun close()

    /**
     * Opens the dropdown list.
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
 * Named exports of `@vaadin/time-picker/vaadin-time-picker.js`.
 *
 * Referencing the module registers the `<vaadin-time-picker>` custom element.
 */
@JsModule("@vaadin/time-picker/vaadin-time-picker.js")
private external object TimePickerModule {
    /** The registered custom element class. */
    val TimePicker: dynamic
}

/**
 * Imports `@vaadin/time-picker/vaadin-time-picker.js`, registering the `<vaadin-time-picker>` custom element,
 * and returns the element class.
 */
fun registerTimePicker(): dynamic = TimePickerModule.TimePicker

/**
 * Creates a `<vaadin-time-picker>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createTimePicker(configure: TimePickerElement.() -> Unit = {}): TimePickerElement {
    registerTimePicker()
    return document.createElement(VaadinTagName.timePicker).apply(configure)
}
