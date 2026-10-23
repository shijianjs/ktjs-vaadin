package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-switch>` is a binary on/off switch control for a single setting.
 *
 * ```html
 * <vaadin-switch label="Notifications"></vaadin-switch>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name            | Description
 * ---------------------|-------------
 * `switch`             | The track element that holds the marker.
 * `marker`             | The marker element inside the track.
 * `label`              | The slotted label element wrapper.
 * `helper-text`        | The slotted helper text element wrapper.
 * `error-message`      | The slotted error message element wrapper.
 * `required-indicator` | The `required` state indicator element.
 *
 * The following state attributes are available for styling:
 *
 * Attribute            | Description
 * ---------------------|-------------
 * `active`             | Set when the switch is activated with mouse, touch or the keyboard.
 * `checked`            | Set when the switch is checked.
 * `disabled`           | Set when the switch is disabled.
 * `readonly`           | Set when the switch is readonly.
 * `focus-ring`         | Set when the switch is focused using the keyboard.
 * `focused`            | Set when the switch is focused.
 * `required`           | Set when the switch is required.
 * `invalid`            | Set when the switch is invalid.
 * `has-label`          | Set when the switch has a label.
 * `has-helper`         | Set when the switch has helper text.
 * `has-error-message`  | Set when the switch has an error message.
 * `has-tooltip`        | Set when the switch has a slotted tooltip.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                |
 * :--------------------------------------------------|
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
 * | `--vaadin-switch-background`                     |
 * | `--vaadin-switch-border-color`                   |
 * | `--vaadin-switch-border-radius`                  |
 * | `--vaadin-switch-border-width`                   |
 * | `--vaadin-switch-gap`                            |
 * | `--vaadin-switch-height`                         |
 * | `--vaadin-switch-width`                          |
 * | `--vaadin-switch-icon-color`                     |
 * | `--vaadin-switch-icon-size`                      |
 * | `--vaadin-switch-label-color`                    |
 * | `--vaadin-switch-label-font-size`                |
 * | `--vaadin-switch-label-font-weight`              |
 * | `--vaadin-switch-label-line-height`              |
 * | `--vaadin-switch-marker-border-color`            |
 * | `--vaadin-switch-marker-border-radius`           |
 * | `--vaadin-switch-marker-border-width`            |
 * | `--vaadin-switch-marker-color`                   |
 * | `--vaadin-switch-marker-height`                  |
 * | `--vaadin-switch-marker-scale`                   |
 * | `--vaadin-switch-marker-width`                   |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/switch)
 */
external class SwitchElement : HTMLElement {
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
     * Returns whether the switch value satisfies its constraints without changing the `invalid` state.
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
 * Named exports of `@vaadin/switch/vaadin-switch.js`.
 *
 * Referencing the module registers the `<vaadin-switch>` custom element.
 */
@JsModule("@vaadin/switch/vaadin-switch.js")
private external object SwitchModule {
    /** The registered custom element class. */
    val Switch: dynamic
}

/**
 * Imports `@vaadin/switch/vaadin-switch.js`, registering the `<vaadin-switch>` custom element,
 * and returns the element class.
 */
fun registerSwitch(): dynamic = SwitchModule.Switch

/**
 * Creates a `<vaadin-switch>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createSwitch(configure: SwitchElement.() -> Unit = {}): SwitchElement {
    registerSwitch()
    return document.createElement(VaadinTagName.switch).apply(configure)
}
