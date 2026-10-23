package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-custom-field>` is a web component for wrapping multiple components as a single field.
 *
 * ```html
 * <vaadin-custom-field label="Appointment time">
 *   <vaadin-date-picker></vaadin-date-picker>
 *   <vaadin-time-picker></vaadin-time-picker>
 * </vaadin-custom-field>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name            | Description
 * ---------------------|----------------
 * `label`              | The slotted label element wrapper
 * `helper-text`        | The slotted helper text element wrapper
 * `error-message`      | The slotted error message element wrapper
 * `required-indicator` | The `required` state indicator element
 * `input-fields`       | The slotted input elements wrapper
 *
 * The following state attributes are available for styling:
 *
 * Attribute           | Description
 * --------------------|--------------------------------
 * `invalid`           | Set when the element is invalid
 * `focused`           | Set when the element is focused
 * `has-label`         | Set when the element has a label
 * `has-value`         | Set when the element has a value
 * `has-helper`        | Set when the element has helper text
 * `has-error-message` | Set when the element has an error message
 * `has-tooltip`       | Set when the element has a slotted tooltip
 *
 * You may also manually set `disabled` or `readonly` attribute on this component to make the label
 * part look visually the same as on a `<vaadin-text-field>` when it is disabled or readonly.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                |
 * :--------------------------------------------------|
 * | `--vaadin-input-field-error-color`               |
 * | `--vaadin-input-field-error-font-size`           |
 * | `--vaadin-input-field-error-font-weight`         |
 * | `--vaadin-input-field-error-line-height`         |
 * | `--vaadin-input-field-label-color`               |
 * | `--vaadin-input-field-label-font-size`           |
 * | `--vaadin-input-field-label-font-weight`         |
 * | `--vaadin-input-field-label-line-height`         |
 * | `--vaadin-input-field-helper-color`              |
 * | `--vaadin-input-field-helper-font-size`          |
 * | `--vaadin-input-field-helper-font-weight`        |
 * | `--vaadin-input-field-helper-line-height`        |
 * | `--vaadin-input-field-required-indicator-color`  |
 * | `--vaadin-input-field-required-indicator`        |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/custom-field)
 */
external class CustomFieldElement : HTMLElement {
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
     * Error to show when the field is invalid.
     */
    var errorMessage: String?

    /**
     * A function to format the values of the individual fields contained by
     * the custom field into a single component value. The function receives
     * an array of all values of the individual fields in the order of their
     * presence in the DOM, and must return a single component value.
     * This function is called each time a value of an internal field is
     * changed.
     *
     * Example:
     * ```js
     * customField.formatValue = (fieldValues) => {
     *   return fieldValues.join("-");
     * }
     * ```
     */
    var formatValue: dynamic

    /**
     * String used for the helper text.
     */
    var helperText: String?

    /**
     * Array of available input nodes
     */
    val inputs: dynamic

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
     * The name of the control, which is submitted with the form data.
     */
    var name: String?

    /**
     * A function to parse the component value into values for the individual
     * fields contained by the custom field. The function receives the
     * component value, and must return an array of values for the individual
     * fields in the order of their presence in the DOM.
     * The function is called each time the value of the component changes.
     *
     * Example:
     * ```js
     * customField.parseValue = (componentValue) => {
     *   return componentValue.split("-");
     * }
     * ```
     */
    var parseValue: dynamic

    /**
     * Specifies that the user must fill in a value.
     */
    var required: Boolean

    /**
     * The value of the field. When wrapping several inputs, it will contain `\t`
     * (Tab character) as a delimiter indicating parts intended to be used as the
     * corresponding inputs values.
     * Use the [`formatValue`](#/elements/vaadin-custom-field#property-formatValue)
     * and [`parseValue`](#/elements/vaadin-custom-field#property-parseValue)
     * properties to customize this behavior.
     */
    var value: String?

    /**
     * Returns true if the current inputs values satisfy all constraints (if any).
     */
    fun checkValidity(): Boolean

    /**
     * Validates the field and sets the `invalid` property based on the result.
     *
     * The method fires a `validated` event with the result of the validation.
     */
    fun validate(): Boolean
}

/**
 * Named exports of `@vaadin/custom-field/vaadin-custom-field.js`.
 *
 * Referencing the module registers the `<vaadin-custom-field>` custom element.
 */
@JsModule("@vaadin/custom-field/vaadin-custom-field.js")
private external object CustomFieldModule {
    /** The registered custom element class. */
    val CustomField: dynamic
}

/**
 * Imports `@vaadin/custom-field/vaadin-custom-field.js`, registering the `<vaadin-custom-field>` custom element,
 * and returns the element class.
 */
fun registerCustomField(): dynamic = CustomFieldModule.CustomField

/**
 * Creates a `<vaadin-custom-field>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createCustomField(configure: CustomFieldElement.() -> Unit = {}): CustomFieldElement {
    registerCustomField()
    return document.createElement(VaadinTagName.customField).apply(configure)
}
