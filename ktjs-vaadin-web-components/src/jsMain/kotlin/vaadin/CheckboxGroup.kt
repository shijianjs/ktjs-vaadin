package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-checkbox-group>` is a web component that allows the user to choose several items from a group of binary choices.
 *
 * ```html
 * <vaadin-checkbox-group label="Export data">
 *   <vaadin-checkbox value="0" label="Order ID"></vaadin-checkbox>
 *   <vaadin-checkbox value="1" label="Product name"></vaadin-checkbox>
 *   <vaadin-checkbox value="2" label="Customer"></vaadin-checkbox>
 *   <vaadin-checkbox value="3" label="Status"></vaadin-checkbox>
 * </vaadin-checkbox-group>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name            | Description
 * ---------------------|----------------
 * `label`              | The slotted label element wrapper
 * `group-field`        | The checkbox elements wrapper
 * `helper-text`        | The slotted helper text element wrapper
 * `error-message`      | The slotted error message element wrapper
 * `required-indicator` | The `required` state indicator element
 *
 * The following state attributes are available for styling:
 *
 * Attribute           | Description
 * --------------------|---------------------------------
 * `disabled`          | Set when the element is disabled
 * `readonly`          | Set when the element is readonly
 * `invalid`           | Set when the element is invalid
 * `focused`           | Set when the element is focused
 * `has-label`         | Set when the element has a label
 * `has-value`         | Set when the element has a value
 * `has-helper`        | Set when the element has helper text
 * `has-error-message` | Set when the element has an error message
 * `has-tooltip`       | Set when the element has a slotted tooltip
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
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/checkbox-group)
 */
external class CheckboxGroupElement : HTMLElement {
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
     * When true, the user cannot modify the value of the checkbox group.
     * The difference between `disabled` and `readonly` is that in the
     * read-only checkbox group, all the checkboxes are also read-only,
     * and therefore remain focusable and announced by screen readers.
     */
    var readonly: Boolean

    /**
     * Specifies that the user must fill in a value.
     */
    var required: Boolean

    /**
     * An array containing values of the currently checked checkboxes.
     *
     * The array is immutable so toggling checkboxes always results in
     * creating a new array.
     */
    var value: dynamic

    /**
     * Override method inherited from `ValidateMixin`
     * to validate the value array.
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
 * Named exports of `@vaadin/checkbox-group/vaadin-checkbox-group.js`.
 *
 * Referencing the module registers the `<vaadin-checkbox-group>` custom element.
 */
@JsModule("@vaadin/checkbox-group/vaadin-checkbox-group.js")
private external object CheckboxGroupModule {
    /** The registered custom element class. */
    val CheckboxGroup: dynamic
}

/**
 * Imports `@vaadin/checkbox-group/vaadin-checkbox-group.js`, registering the `<vaadin-checkbox-group>` custom element,
 * and returns the element class.
 */
fun registerCheckboxGroup(): dynamic = CheckboxGroupModule.CheckboxGroup

/**
 * Creates a `<vaadin-checkbox-group>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createCheckboxGroup(configure: CheckboxGroupElement.() -> Unit = {}): CheckboxGroupElement {
    registerCheckboxGroup()
    return document.createElement(VaadinTagName.checkboxGroup).apply(configure)
}
