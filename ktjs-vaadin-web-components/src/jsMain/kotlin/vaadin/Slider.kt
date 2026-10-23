package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-slider>` is a web component that represents a range slider
 * for selecting numerical values within a defined range.
 *
 * ```html
 * <vaadin-slider min="0" max="100" step="1"></vaadin-slider>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name            | Description
 * ---------------------|-----------------
 * `label`              | The label element
 * `required-indicator` | The required indicator element
 * `helper-text`        | The helper text element
 * `error-message`      | The error message element
 * `track`              | The slider track
 * `track-fill`         | The filled portion of the track
 * `thumb`              | The slider thumb
 * `marks`              | Container for min/max labels
 * `min`                | Minimum value label
 * `max`                | Maximum value label
 *
 * The following state attributes are available for styling:
 *
 * Attribute          | Description
 * -------------------|-------------
 * `active`           | Set when the slider is activated with mouse or touch
 * `disabled`         | Set when the slider is disabled
 * `readonly`         | Set when the slider is read-only
 * `focused`          | Set when the slider has focus
 * `focus-ring`       | Set when the slider is focused using the keyboard
 * `min-max-visible`  | Set when the min/max labels are displayed
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                              |
 * :------------------------------------------------|
 * | `--vaadin-field-default-width`                 |
 * | `--vaadin-input-field-error-color`             |
 * | `--vaadin-input-field-error-font-size`         |
 * | `--vaadin-input-field-error-font-weight`       |
 * | `--vaadin-input-field-helper-color`            |
 * | `--vaadin-input-field-helper-font-size`        |
 * | `--vaadin-input-field-helper-font-weight`      |
 * | `--vaadin-input-field-label-color`             |
 * | `--vaadin-input-field-label-font-size`         |
 * | `--vaadin-input-field-label-font-weight`       |
 * | `--vaadin-input-field-required-indicator`      |
 * | `--vaadin-slider-bubble-arrow-border-radius`   |
 * | `--vaadin-slider-bubble-arrow-size`            |
 * | `--vaadin-slider-bubble-background`            |
 * | `--vaadin-slider-bubble-border-color`          |
 * | `--vaadin-slider-bubble-border-radius`         |
 * | `--vaadin-slider-bubble-border-width`          |
 * | `--vaadin-slider-bubble-font-size`             |
 * | `--vaadin-slider-bubble-font-weight`           |
 * | `--vaadin-slider-bubble-line-height`           |
 * | `--vaadin-slider-bubble-offset`                |
 * | `--vaadin-slider-bubble-padding`               |
 * | `--vaadin-slider-bubble-shadow`                |
 * | `--vaadin-slider-bubble-text-color`            |
 * | `--vaadin-slider-fill-background`              |
 * | `--vaadin-slider-fill-border-color`            |
 * | `--vaadin-slider-fill-border-width`            |
 * | `--vaadin-slider-marks-color`                  |
 * | `--vaadin-slider-marks-font-size`              |
 * | `--vaadin-slider-marks-font-weight`            |
 * | `--vaadin-slider-thumb-background`             |
 * | `--vaadin-slider-thumb-border-color`           |
 * | `--vaadin-slider-thumb-border-radius`          |
 * | `--vaadin-slider-thumb-border-width`           |
 * | `--vaadin-slider-thumb-cursor`                 |
 * | `--vaadin-slider-thumb-cursor-active`          |
 * | `--vaadin-slider-thumb-height`                 |
 * | `--vaadin-slider-thumb-width`                  |
 * | `--vaadin-slider-track-background`             |
 * | `--vaadin-slider-track-border-color`           |
 * | `--vaadin-slider-track-border-radius`          |
 * | `--vaadin-slider-track-border-width`           |
 * | `--vaadin-slider-track-height`                 |
 *
 * In order to style the slider bubble, use `<vaadin-slider-bubble>` shadow DOM parts:
 *
 * Part name        | Description
 * -----------------|----------------------
 * `overlay`        | The overlay container
 * `content`        | The overlay content
 * `arrow`          | Arrow pointing to the thumb
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/slider)
 */
external class SliderElement : HTMLElement {
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
     * The maximum allowed value.
     */
    var max: Double?

    /**
     * The minimum allowed value.
     */
    var min: Double?

    /**
     * When true, displays the min and max values below the slider track.
     */
    var minMaxVisible: Boolean

    /**
     * When true, the user cannot modify the value of the slider.
     * The difference between `disabled` and `readonly` is that the
     * read-only slider remains focusable and is announced by screen
     * readers.
     */
    var readonly: Boolean

    /**
     * Specifies that the user must fill in a value.
     */
    var required: Boolean

    /**
     * The stepping interval of the slider.
     */
    var step: Double?

    /**
     * The value of the slider.
     */
    var value: Double?

    /**
     * When true, the value bubble is always visible,
     * regardless of focus or hover state.
     */
    var valueAlwaysVisible: Boolean

    /**
     * Returns true if the field value satisfies all constraints (if any).
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
 * Named exports of `@vaadin/slider/vaadin-slider.js`.
 *
 * Referencing the module registers the `<vaadin-slider>` custom element.
 */
@JsModule("@vaadin/slider/vaadin-slider.js")
private external object SliderModule {
    /** The registered custom element class. */
    val Slider: dynamic
}

/**
 * Imports `@vaadin/slider/vaadin-slider.js`, registering the `<vaadin-slider>` custom element,
 * and returns the element class.
 */
fun registerSlider(): dynamic = SliderModule.Slider

/**
 * Creates a `<vaadin-slider>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createSlider(configure: SliderElement.() -> Unit = {}): SliderElement {
    registerSlider()
    return document.createElement(VaadinTagName.slider).apply(configure)
}
