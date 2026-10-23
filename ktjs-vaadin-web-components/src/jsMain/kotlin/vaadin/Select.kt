package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-select>` is a Web Component for selecting values from a list of items.
 *
 * ### Items
 *
 * Use the `items` property to define possible options for the select:
 *
 * ```html
 * <vaadin-select id="select"></vaadin-select>
 * ```
 * ```js
 * const select = document.querySelector('#select');
 * select.items = [
 *   { label: 'Most recent first', value: 'recent' },
 *   { component: 'hr' },
 *   { label: 'Rating: low to high', value: 'rating-asc', className: 'asc' },
 *   { label: 'Rating: high to low', value: 'rating-desc', className: 'desc' },
 *   { component: 'hr' },
 *   { label: 'Price: low to high', value: 'price-asc', disabled: true },
 *   { label: 'Price: high to low', value: 'price-desc', disabled: true }
 * ];
 * ```
 *
 * ### Rendering
 *
 * Alternatively, the content of the select can be populated by using a slotted `<vaadin-select-list-box>`
 * with `<vaadin-select-item>` children:
 *
 * ```html
 * <vaadin-select>
 *   <vaadin-select-list-box slot="overlay">
 *     <vaadin-select-item value="recent">Most recent first</vaadin-select-item>
 *     <vaadin-select-item value="rating-asc">Rating: low to high</vaadin-select-item>
 *     <vaadin-select-item value="rating-desc">Rating: high to low</vaadin-select-item>
 *   </vaadin-select-list-box>
 * </vaadin-select>
 * ```
 *
 * * Hint: By setting the `label` property of inner vaadin-items you will
 * be able to change the visual representation of the selected value in the input part.
 *
 * #### Renderer (deprecated)
 *
 * The content of the select can also be populated by using the renderer callback function, although
 * this approach is deprecated in favor of the `items` property or a slotted `<vaadin-select-list-box>`.
 *
 * The renderer function provides `root`, `select` arguments.
 * Generate DOM content, append it to the `root` element and control the state
 * of the host element by accessing `select`.
 *
 * ```js
 * const select = document.querySelector('#select');
 * select.renderer = function(root, select) {
 *   const listBox = document.createElement('vaadin-list-box');
 *   // append 3 <vaadin-item> elements
 *   ['Jose', 'Manolo', 'Pedro'].forEach(function(name) {
 *     const item = document.createElement('vaadin-item');
 *     item.textContent = name;
 *     item.setAttribute('label', name)
 *     listBox.appendChild(item);
 *   });
 *
 *   // update the content
 *   root.appendChild(listBox);
 * };
 * ```
 *
 * Renderer is called on initialization of new select and on its opening.
 * DOM generated during the renderer call can be reused
 * in the next renderer call and will be provided with the `root` argument.
 * On first call it will be empty.
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name            | Description
 * ---------------------|----------------
 * `label`              | The label element
 * `input-field`        | The element that wraps prefix, value and toggle button
 * `field-button`       | Set on the toggle button
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
 * `invalid`            | Set when the element is invalid
 * `focused`            | Set when the element is focused
 * `focus-ring`         | Set when the element is keyboard focused
 * `readonly`           | Set when the element is readonly
 * `opened`             | Set when the overlay is opened
 * `phone`              | Set when the overlay is shown in phone mode
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
 * | `--vaadin-select-overlay-width`                  |
 *
 * ### Internal components
 *
 * In addition to `<vaadin-select>` itself, the following internal components are used
 * and themable:
 *
 * - `<vaadin-select-value-button>` - has the same API as [`<vaadin-button>`](#/elements/vaadin-button).
 * - [`<vaadin-select-list-box>`](#/elements/vaadin-select-list-box) - a list-box element.
 * - [`<vaadin-select-item>`](#/elements/vaadin-select-item) - an item element.
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/select)
 */
external class SelectElement : HTMLElement {
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
     * An array containing items that will be rendered as the options of the select.
     *
     * #### Example
     * ```js
     * select.items = [
     *   { label: 'Most recent first', value: 'recent' },
     *   { component: 'hr' },
     *   { label: 'Rating: low to high', value: 'rating-asc', className: 'asc' },
     *   { label: 'Rating: high to low', value: 'rating-desc', className: 'desc' },
     *   { component: 'hr' },
     *   { label: 'Price: low to high', value: 'price-asc', disabled: true },
     *   { label: 'Price: high to low', value: 'price-desc', disabled: true }
     * ];
     * ```
     *
     * Note: each item is rendered by default as the internal `<vaadin-select-item>` that is an extension of `<vaadin-item>`.
     * To render the item with a custom component, provide a tag name by the `component` property.
     */
    var items: dynamic

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
     * The name of this element.
     */
    var name: String?

    /**
     * Defines whether the overlay should overlap the target element
     * in the y-axis, or be positioned right above/below it.
     */
    var noVerticalOverlap: Boolean

    /**
     * Set when the select is open
     */
    var opened: Boolean

    /**
     * A hint to the user of what can be entered in the control.
     * The placeholder will be displayed in the case that there
     * is no item selected, or the selected item has an empty
     * string label, or the selected item has no label and it's
     * DOM content is empty.
     */
    var placeholder: String?

    /**
     * When present, it specifies that the element is read-only.
     */
    var readonly: Boolean

    /**
     * Custom function for rendering the content of the `<vaadin-select>`.
     * Receives two arguments:
     *
     * - `root` The internal container DOM element. Append your content to it.
     * - `select` The reference to the `<vaadin-select>` element.
     */
    var renderer: dynamic

    /**
     * Specifies that the user must fill in a value.
     */
    var required: Boolean

    /**
     * The `value` property of the selected item, or an empty string
     * if no item is selected.
     * On change or initialization, the component finds the item which matches the
     * value and displays it.
     * If no value is provided to the component, it selects the first item without
     * value or empty value.
     * Hint: If you do not want to select any item by default, you can either set all
     * the values of inner vaadin-items, or set the vaadin-select value to
     * an inexistent value in the items list.
     */
    var value: String?

    /**
     * Returns true if the current value satisfies all constraints (if any)
     */
    fun checkValidity(): Boolean

    /**
     * Requests an update for the content of the select.
     * While performing the update, it invokes the renderer passed in the `renderer` property.
     *
     * It is not guaranteed that the update happens immediately (synchronously) after it is requested.
     */
    fun requestContentUpdate()

    /**
     * Validates the field and sets the `invalid` property based on the result.
     *
     * The method fires a `validated` event with the result of the validation.
     */
    fun validate(): Boolean
}

/**
 * Named exports of `@vaadin/select/vaadin-select.js`.
 *
 * Referencing the module registers the `<vaadin-select>` custom element.
 */
@JsModule("@vaadin/select/vaadin-select.js")
private external object SelectModule {
    /** The registered custom element class. */
    val Select: dynamic
}

/**
 * Imports `@vaadin/select/vaadin-select.js`, registering the `<vaadin-select>` custom element,
 * and returns the element class.
 */
fun registerSelect(): dynamic = SelectModule.Select

/**
 * Creates a `<vaadin-select>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createSelect(configure: SelectElement.() -> Unit = {}): SelectElement {
    registerSelect()
    return document.createElement(VaadinTagName.select).apply(configure)
}
