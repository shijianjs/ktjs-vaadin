package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-radio-button>` is a web component representing a choice in a radio group.
 * Only one radio button in the group can be selected at the same time.
 *
 * ```html
 * <vaadin-radio-group label="Travel class">
 *   <vaadin-radio-button value="economy" label="Economy"></vaadin-radio-button>
 *   <vaadin-radio-button value="business" label="Business"></vaadin-radio-button>
 *   <vaadin-radio-button value="firstClass" label="First Class"></vaadin-radio-button>
 * </vaadin-radio-group>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name   | Description
 * ------------|----------------
 * `radio`     | The wrapper element that contains slotted `<input type="radio">`.
 *
 * The following state attributes are available for styling:
 *
 * Attribute    | Description
 * -------------|--------------------------
 * `active`     | Set when the radio button is pressed, either with a pointer or the keyboard.
 * `disabled`   | Set when the radio button is disabled.
 * `focus-ring` | Set when the radio button is focused using the keyboard.
 * `focused`    | Set when the radio button is focused.
 * `checked`    | Set when the radio button is checked.
 * `has-label`  | Set when the radio button has a label.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                          |
 * :--------------------------------------------|
 * | `--vaadin-radio-button-background`         |
 * | `--vaadin-radio-button-border-color`       |
 * | `--vaadin-radio-button-border-width`       |
 * | `--vaadin-radio-button-gap`                |
 * | `--vaadin-radio-button-label-color`        |
 * | `--vaadin-radio-button-label-font-size`    |
 * | `--vaadin-radio-button-label-font-weight`  |
 * | `--vaadin-radio-button-label-line-height`  |
 * | `--vaadin-radio-button-marker-color`       |
 * | `--vaadin-radio-button-marker-size`        |
 * | `--vaadin-radio-button-size`               |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/radio-group)
 */
external class RadioButtonElement : HTMLElement {
    /**
     * True if the element is checked.
     */
    var checked: Boolean

    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * The label text for the input node.
     * When no light dom defined via [slot=label], this value will be used.
     */
    var label: String?

    /**
     * The name of the radio button.
     */
    var name: String?

    /**
     * The value of the field.
     */
    var value: String?

    /**
     * Clear the value of the field.
     */
    fun clear()
}

/**
 * Named exports of `@vaadin/radio-group/vaadin-radio-button.js`.
 *
 * Referencing the module registers the `<vaadin-radio-button>` custom element.
 */
@JsModule("@vaadin/radio-group/vaadin-radio-button.js")
private external object RadioButtonModule {
    /** The registered custom element class. */
    val RadioButton: dynamic
}

/**
 * Imports `@vaadin/radio-group/vaadin-radio-button.js`, registering the `<vaadin-radio-button>` custom element,
 * and returns the element class.
 */
fun registerRadioButton(): dynamic = RadioButtonModule.RadioButton

/**
 * Creates a `<vaadin-radio-button>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createRadioButton(configure: RadioButtonElement.() -> Unit = {}): RadioButtonElement {
    registerRadioButton()
    return document.createElement(VaadinTagName.radioButton).apply(configure)
}
