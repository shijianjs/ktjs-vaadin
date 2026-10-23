package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-password-field>` is an extension of `<vaadin-text-field>` component for entering passwords.
 *
 * ```html
 * <vaadin-password-field label="Password"></vaadin-password-field>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name            | Description
 * ---------------------|----------------
 * `label`              | The label element
 * `input-field`        | The element that wraps prefix, value and suffix
 * `field-button`       | Set on both clear and reveal buttons
 * `clear-button`       | The clear button
 * `error-message`      | The error message element
 * `helper-text`        | The helper text element wrapper
 * `required-indicator` | The `required` state indicator element
 * `reveal-button`      | The eye icon which toggles the password visibility
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
 * `input-prevented`    | Temporarily set when invalid input is prevented
 * `focused`            | Set when the element is focused
 * `focus-ring`         | Set when the element is keyboard focused
 * `readonly`           | Set when the element is readonly
 * `password-visible`   | Set when the password is visible
 *
 * Note, the `input-prevented` state attribute is only supported when `allowedCharPattern` is set.
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
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/password-field)
 */
external class PasswordFieldElement : HTMLElement {
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
     * Whether the value of the control can be automatically completed by the browser.
     * List of available options at:
     * https://developer.mozilla.org/en/docs/Web/HTML/Element/input#attr-autocomplete
     */
    var autocomplete: String?

    /**
     * This is a property supported by Safari that is used to control whether
     * autocorrection should be enabled when the user is entering/editing the text.
     * Possible values are:
     * on: Enable autocorrection.
     * off: Disable autocorrection.
     */
    @JsName("autocorrect")
    var autocorrectValue: String?

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
     * An object with translated strings used for localization.
     * It has the following structure and default values:
     *
     * ```js
     * {
     *   // Translation of the reveal icon button accessible label
     *   reveal: 'Show password'
     * }
     * ```
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
     * Maximum number of characters (in Unicode code points) that the user can enter.
     */
    var maxlength: Double?

    /**
     * Minimum number of characters (in Unicode code points) that the user can enter.
     */
    var minlength: Double?

    /**
     * The name of this field.
     */
    var name: String?

    /**
     * True if the password is visible ([type=text]).
     */
    val passwordVisible: Boolean

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
     * Set to true to hide the eye icon which toggles the password visibility.
     */
    var revealButtonHidden: Boolean

    /**
     * The value of the field.
     */
    var value: String?

    /**
     * Returns true if the current input value satisfies all constraints (if any).
     */
    fun checkValidity(): Boolean

    /**
     * Clear the value of the field.
     */
    fun clear()

    /**
     * Validates the field and sets the `invalid` property based on the result.
     *
     * The method fires a `validated` event with the result of the validation.
     */
    fun validate(): Boolean
}

/**
 * Named exports of `@vaadin/password-field/vaadin-password-field.js`.
 *
 * Referencing the module registers the `<vaadin-password-field>` custom element.
 */
@JsModule("@vaadin/password-field/vaadin-password-field.js")
private external object PasswordFieldModule {
    /** The registered custom element class. */
    val PasswordField: dynamic
}

/**
 * Imports `@vaadin/password-field/vaadin-password-field.js`, registering the `<vaadin-password-field>` custom element,
 * and returns the element class.
 */
fun registerPasswordField(): dynamic = PasswordFieldModule.PasswordField

/**
 * Creates a `<vaadin-password-field>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createPasswordField(configure: PasswordFieldElement.() -> Unit = {}): PasswordFieldElement {
    registerPasswordField()
    return document.createElement(VaadinTagName.passwordField).apply(configure)
}
