package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-checkbox>` is an input field representing a binary choice.
 *
 * ```html
 * <vaadin-checkbox label="I accept the terms and conditions"></vaadin-checkbox>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name            | Description
 * ---------------------|-------------
 * `checkbox`           | The element representing a stylable custom checkbox
 * `label`              | The slotted label element wrapper
 * `helper-text`        | The slotted helper text element wrapper
 * `error-message`      | The slotted error message element wrapper
 * `required-indicator` | The `required` state indicator element
 *
 * The following state attributes are available for styling:
 *
 * Attribute            | Description
 * ---------------------|-------------
 * `active`             | Set when the checkbox is activated with mouse, touch or the keyboard.
 * `checked`            | Set when the checkbox is checked.
 * `disabled`           | Set when the checkbox is disabled.
 * `readonly`           | Set when the checkbox is readonly.
 * `focus-ring`         | Set when the checkbox is focused using the keyboard.
 * `focused`            | Set when the checkbox is focused.
 * `indeterminate`      | Set when the checkbox is in the indeterminate state.
 * `invalid`            | Set when the checkbox is invalid.
 * `has-label`          | Set when the checkbox has a label.
 * `has-helper`         | Set when the checkbox has helper text.
 * `has-error-message`  | Set when the checkbox has an error message.
 * `has-tooltip`        | Set when the checkbox has a slotted tooltip.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                |
 * :--------------------------------------------------|
 * | `--vaadin-checkbox-background`                   |
 * | `--vaadin-checkbox-border-color`                 |
 * | `--vaadin-checkbox-border-radius`                |
 * | `--vaadin-checkbox-border-width`                 |
 * | `--vaadin-checkbox-gap`                          |
 * | `--vaadin-checkbox-label-color`                  |
 * | `--vaadin-checkbox-label-font-size`              |
 * | `--vaadin-checkbox-label-font-weight`            |
 * | `--vaadin-checkbox-label-line-height`            |
 * | `--vaadin-checkbox-marker-color`                 |
 * | `--vaadin-checkbox-marker-size`                  |
 * | `--vaadin-checkbox-size`                         |
 * | `--vaadin-input-field-error-color`               |
 * | `--vaadin-input-field-error-font-size`           |
 * | `--vaadin-input-field-error-font-weight`         |
 * | `--vaadin-input-field-error-line-height`         |
 * | `--vaadin-input-field-helper-color`              |
 * | `--vaadin-input-field-helper-font-size`          |
 * | `--vaadin-input-field-helper-font-weight`        |
 * | `--vaadin-input-field-helper-line-height`        |
 * | `--vaadin-input-field-required-indicator`        |
 * | `--vaadin-input-field-required-indicator-color`  |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/checkbox)
 */
external class CheckboxElement : HTMLElement {
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
     * True if the element is checked.
     */
    var checked: Boolean

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
     * True if the checkbox is in the indeterminate state which means
     * it is not possible to say whether it is checked or unchecked.
     * The state is reset once the user switches the checkbox by hand.
     *
     * https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/input/checkbox#indeterminate_state_checkboxes
     */
    var indeterminate: Boolean

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
     * When true, the user cannot modify the value of the control.
     * The difference between `disabled` and `readonly` is that the
     * read-only element remains focusable, is announced by screen
     * readers and its value can be submitted as part of the form.
     */
    var readonly: Boolean

    /**
     * Specifies that the user must fill in a value.
     */
    var required: Boolean

    /**
     * The value of the field.
     */
    var value: String?

    /**
     * Returns whether the checkbox value satisfies its constraints without changing the `invalid` state.
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
 * Named exports of `@vaadin/checkbox/vaadin-checkbox.js`.
 *
 * Referencing the module registers the `<vaadin-checkbox>` custom element.
 */
@JsModule("@vaadin/checkbox/vaadin-checkbox.js")
private external object CheckboxModule {
    /** The registered custom element class. */
    val Checkbox: dynamic
}

/**
 * Imports `@vaadin/checkbox/vaadin-checkbox.js`, registering the `<vaadin-checkbox>` custom element,
 * and returns the element class.
 */
fun registerCheckbox(): dynamic = CheckboxModule.Checkbox

/**
 * Creates a `<vaadin-checkbox>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createCheckbox(configure: CheckboxElement.() -> Unit = {}): CheckboxElement {
    registerCheckbox()
    return document.createElement(VaadinTagName.checkbox).apply(configure)
}
