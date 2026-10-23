package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-combo-box>` is a web component for choosing a value from a filterable list of options
 * presented in a dropdown overlay. The options can be provided as a list of strings or objects
 * by setting [`items`](#/elements/vaadin-combo-box#property-items) property on the element.
 *
 * ```html
 * <vaadin-combo-box id="combo-box"></vaadin-combo-box>
 * ```
 *
 * ```js
 * document.querySelector('#combo-box').items = ['apple', 'orange', 'banana'];
 * ```
 *
 * When the selected `value` is changed, a `value-changed` event is triggered.
 *
 * ### Item rendering
 *
 * To customize the content of the `<vaadin-combo-box-item>` elements placed in the dropdown, use
 * [`renderer`](#/elements/vaadin-combo-box#property-renderer) property which accepts a function.
 * The renderer function is called with `root`, `comboBox`, and `model` as arguments.
 *
 * Generate DOM content by using `model` object properties if needed, and append it to the `root`
 * element. The `comboBox` reference is provided to access the combo-box element state. Do not
 * set combo-box properties in a `renderer` function.
 *
 * ```js
 * const comboBox = document.querySelector('#combo-box');
 * comboBox.items = [{'label': 'Hydrogen', 'value': 'H'}];
 * comboBox.renderer = (root, comboBox, model) => {
 *   const item = model.item;
 *   root.innerHTML = `${model.index}: ${item.label} <b>${item.value}</b>`;
 * };
 * ```
 *
 * Renderer is called on the opening of the combo-box and each time the related model is updated.
 * Before creating new content, it is recommended to check if there is already an existing DOM
 * element in `root` from a previous renderer call for reusing it. Even though combo-box uses
 * infinite scrolling, reducing DOM operations might improve performance.
 *
 * The following properties are available in the `model` argument:
 *
 * Property   | Type             | Description
 * -----------|------------------|-------------
 * `index`    | Number           | Index of the item in the `items` array
 * `item`     | String or Object | The item reference
 * `selected` | Boolean          | True when item is selected
 * `focused`  | Boolean          | True when item is focused
 *
 * ### Lazy Loading with Function Data Provider
 *
 * In addition to assigning an array to the items property, you can alternatively use the
 * [`dataProvider`](#/elements/vaadin-combo-box#property-dataProvider) function property.
 * The `<vaadin-combo-box>` calls this function lazily, only when it needs more data
 * to be displayed.
 *
 * __Note that when using function data providers, the total number of items
 * needs to be set manually. The total number of items can be returned
 * in the second argument of the data provider callback:__
 *
 * ```js
 * comboBox.dataProvider = async (params, callback) => {
 *   const API = 'https://demo.vaadin.com/demo-data/1.0/filtered-countries';
 *   const { filter, page, pageSize } = params;
 *   const index = page * pageSize;
 *
 *   const res = await fetch(`${API}?index=${index}&count=${pageSize}&filter=${filter}`);
 *   if (res.ok) {
 *     const { result, size } = await res.json();
 *     callback(result, size);
 *   }
 * };
 * ```
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
 * `loader`             | The loading indicator shown while loading items
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
 * `loading`            | Set when loading items from the data provider
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                |
 * :--------------------------------------------------|
 * | `--vaadin-combo-box-overlay-max-height`          |
 * | `--vaadin-combo-box-overlay-width`               |
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
 *
 * ### Internal components
 *
 * In addition to `<vaadin-combo-box>` itself, the following internal
 * components are themable:
 *
 * - `<vaadin-combo-box-item>` - has the same API as [`<vaadin-item>`](#/elements/vaadin-item).
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/combo-box)
 */
external class ComboBoxElement : HTMLElement {
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
     * If `true`, the user can input a value that is not present in the items list.
     * `value` property will be set to the input value in this case.
     * Also, when `value` is set programmatically, the input value will be set
     * to reflect that value.
     */
    var allowCustomValue: Boolean

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
     * Function that provides items lazily. Receives arguments `params`, `callback`
     *
     * `params.page` Requested page index
     *
     * `params.pageSize` Current page size
     *
     * `params.filter` Currently applied filter
     *
     * `callback(items, size)` Callback function with arguments:
     *   - `items` Current page of items
     *   - `size` Total number of items.
     */
    var dataProvider: dynamic

    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * Error to show when the field is invalid.
     */
    var errorMessage: String?

    /**
     * Filtering string the user has typed into the input field.
     */
    var filter: String?

    /**
     * A subset of items, filtered based on the user input. Filtered items
     * can be assigned directly to omit the internal filtering functionality.
     * The items can be of either `String` or `Object` type.
     */
    var filteredItems: dynamic

    /**
     * String used for the helper text.
     */
    var helperText: String?

    /**
     * Set to true when the field is invalid.
     */
    var invalid: Boolean

    /**
     * A function used to generate CSS class names for dropdown
     * items based on the item. The return value should be the
     * generated class name as a string, or multiple class names
     * separated by whitespace characters.
     */
    var itemClassNameGenerator: dynamic

    /**
     * Path for the id of the item. If `items` is an array of objects,
     * the `itemIdPath` is used to compare and identify the same item
     * in the selection and in `filteredItems` (items given by the
     * `dataProvider` callback).
     */
    var itemIdPath: String?

    /**
     * A function that is used to generate the label for dropdown
     * items based on the item. Receives one argument:
     * - `item` The item to generate the label for.
     */
    var itemLabelGenerator: dynamic

    /**
     * Path for label of the item. If `items` is an array of objects, the
     * `itemLabelPath` is used to fetch the displayed string label for each
     * item.
     *
     * The item label is also used for matching items when processing user
     * input, i.e., for filtering and selecting items.
     */
    var itemLabelPath: String?

    /**
     * A full set of items to filter the visible options from.
     * The items can be of either `String` or `Object` type.
     */
    var items: dynamic

    /**
     * Path for the value of the item. If `items` is an array of objects, the
     * `itemValuePath:` is used to fetch the string value for the selected
     * item.
     *
     * The item value is used in the `value` property of the combo box,
     * to provide the form value.
     */
    var itemValuePath: String?

    /**
     * The label text for the input node.
     * When no light dom defined via [slot=label], this value will be used.
     */
    var label: String?

    /**
     * When set to `true`, "loading" attribute is added to host and the overlay element.
     */
    var loading: Boolean

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
     * The name of this field.
     */
    var name: String?

    /**
     * True if the dropdown is open, false otherwise.
     */
    var opened: Boolean

    /**
     * Number of items fetched at a time from the dataprovider.
     */
    var pageSize: Double?

    /**
     * Controls which item is automatically set to be selected, for
     * example on Enter, when the typed filter only partially matches
     * its label. The item that will be selected is highlighted in the
     * dropdown while typing:
     *
     * - `none` (default): an item is automatically set to be selected only when the filter matches its label exactly.
     * - `first-match`: the first item in the filtered results is automatically set to be selected.
     * - `only-match`: the item is automatically set to be selected when filtering narrows the results to a single item.
     *
     * In general, an exact match is always set to be selected and takes
     * precedence over partial matches, regardless of the mode.
     *
     * A partial match is only applied while the dropdown is open. For
     * example, when auto-open is disabled with `autoOpenDisabled`, typing
     * does not highlight a match or set it to be selected until the
     * dropdown is opened.
     *
     * This feature cannot be used together with custom values, because a
     * partial match is also a valid custom value. A partial match is not
     * applied when custom values are allowed with `allowCustomValue`.
     */
    var partialMatchMode: String?

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
     * Custom function for rendering the content of every item.
     * Receives three arguments:
     *
     * - `root` The `<vaadin-combo-box-item>` internal container DOM element.
     * - `comboBox` The reference to the `<vaadin-combo-box>` element.
     * - `model` The object with the properties related with the rendered
     *   item, contains:
     *   - `model.index` The index of the rendered item.
     *   - `model.item` The item.
     */
    var renderer: dynamic

    /**
     * Specifies that the user must fill in a value.
     */
    var required: Boolean

    /**
     * The selected item from the `items` array.
     */
    var selectedItem: dynamic

    /**
     * Total number of items.
     */
    var size: Double?

    /**
     * The value of the field.
     */
    var value: String?

    /**
     * Reverts back to original value.
     */
    fun cancel()

    /**
     * Returns true if the field value satisfies all constraints (if any).
     */
    fun checkValidity(): Boolean

    /**
     * Clear the value of the field.
     */
    fun clear()

    /**
     * Clears the cached pages and reloads data from dataprovider when needed.
     */
    fun clearCache()

    /**
     * Closes the dropdown list.
     */
    fun close()

    /**
     * Opens the dropdown list.
     */
    fun `open`()

    /**
     * Requests an update for the content of items.
     * While performing the update, it invokes the renderer (passed in the `renderer` property) once an item.
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
 * Named exports of `@vaadin/combo-box/vaadin-combo-box.js`.
 *
 * Referencing the module registers the `<vaadin-combo-box>` custom element.
 */
@JsModule("@vaadin/combo-box/vaadin-combo-box.js")
private external object ComboBoxModule {
    /** The registered custom element class. */
    val ComboBox: dynamic
}

/**
 * Imports `@vaadin/combo-box/vaadin-combo-box.js`, registering the `<vaadin-combo-box>` custom element,
 * and returns the element class.
 */
fun registerComboBox(): dynamic = ComboBoxModule.ComboBox

/**
 * Creates a `<vaadin-combo-box>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createComboBox(configure: ComboBoxElement.() -> Unit = {}): ComboBoxElement {
    registerComboBox()
    return document.createElement(VaadinTagName.comboBox).apply(configure)
}
