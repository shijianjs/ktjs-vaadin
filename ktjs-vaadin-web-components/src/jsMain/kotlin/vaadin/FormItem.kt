package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-form-item>` is a Web Component providing labelled form item wrapper
 * for using inside `<vaadin-form-layout>`.
 *
 * `<vaadin-form-item>` accepts a single child as the input content,
 * and also has a separate named `label` slot:
 *
 * ```html
 * <vaadin-form-item>
 *   <label slot="label">Label aside</label>
 *   <input>
 * </vaadin-form-item>
 * ```
 *
 * The label is optional and can be omitted:
 *
 * ```html
 * <vaadin-form-item>
 *   <input type="checkbox"> Subscribe to our Newsletter
 * </vaadin-form-item>
 * ```
 *
 * By default, the `label` slot content is displayed aside of the input content.
 * When `label-position="top"` is set, the `label` slot content is displayed on top:
 *
 * ```html
 * <vaadin-form-item label-position="top">
 *   <label slot="label">Label on top</label>
 *   <input>
 * </vaadin-form-item>
 * ```
 *
 * **Note:** Normally, `<vaadin-form-item>` is used as a child of
 * a `<vaadin-form-layout>` element. Setting `label-position` is unnecessary,
 * because the `label-position` attribute is triggered automatically by the parent
 * `<vaadin-form-layout>`, depending on its width and responsive behavior.
 *
 * **Deprecation note:** The `label-position` attribute is deprecated and will be removed
 * in Vaadin 26. Use `labelsAside` property on the form-layout instead.
 *
 * ### Input Width
 *
 * By default, `<vaadin-form-item>` does not manipulate the width of the slotted
 * input element. Optionally you can stretch the child input element to fill
 * the available width for the input content by adding the `full-width` class:
 *
 * ```html
 * <vaadin-form-item>
 *   <label slot="label">Label</label>
 *   <input class="full-width">
 * </vaadin-form-item>
 * ```
 *
 * ### Styling
 *
 * The `label-position` attribute can be used to target the label on top state:
 *
 * ```css
 * vaadin-form-item[label-position="top"] {
 *   padding-top: 0.5rem;
 * }
 * ```
 *
 * **Deprecation note:** The `label-position` attribute is deprecated and will be removed
 * in Vaadin 26. Use `labelsAside` property on the form-layout instead.
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name            | Description
 * ---------------------|-------------
 * `label`              | The slotted label element wrapper
 * `required-indicator` | The `required` state indicator element
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/form-layout)
 */
external class FormItemElement : HTMLElement {
}

/**
 * Named exports of `@vaadin/form-layout/vaadin-form-item.js`.
 *
 * Referencing the module registers the `<vaadin-form-item>` custom element.
 */
@JsModule("@vaadin/form-layout/vaadin-form-item.js")
private external object FormItemModule {
    /** The registered custom element class. */
    val FormItem: dynamic
}

/**
 * Imports `@vaadin/form-layout/vaadin-form-item.js`, registering the `<vaadin-form-item>` custom element,
 * and returns the element class.
 */
fun registerFormItem(): dynamic = FormItemModule.FormItem

/**
 * Creates a `<vaadin-form-item>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createFormItem(configure: FormItemElement.() -> Unit = {}): FormItemElement {
    registerFormItem()
    return document.createElement(VaadinTagName.formItem).apply(configure)
}
