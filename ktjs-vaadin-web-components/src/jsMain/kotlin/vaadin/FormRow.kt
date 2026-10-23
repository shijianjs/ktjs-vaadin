package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-form-row>` is a web component for arranging fields into rows
 * inside a `<vaadin-form-layout>` that is set to autoResponsive mode.
 *
 * Each `<vaadin-form-row>` always starts on a new row. Fields that exceed
 * the available columns wrap to a new row, which then remains reserved
 * exclusively for the fields of that `<vaadin-form-row>` element.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/form-layout)
 */
external class FormRowElement : HTMLElement {
}

/**
 * Named exports of `@vaadin/form-layout/vaadin-form-row.js`.
 *
 * Referencing the module registers the `<vaadin-form-row>` custom element.
 */
@JsModule("@vaadin/form-layout/vaadin-form-row.js")
private external object FormRowModule {
    /** The registered custom element class. */
    val FormRow: dynamic
}

/**
 * Imports `@vaadin/form-layout/vaadin-form-row.js`, registering the `<vaadin-form-row>` custom element,
 * and returns the element class.
 */
fun registerFormRow(): dynamic = FormRowModule.FormRow

/**
 * Creates a `<vaadin-form-row>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createFormRow(configure: FormRowElement.() -> Unit = {}): FormRowElement {
    registerFormRow()
    return document.createElement(VaadinTagName.formRow).apply(configure)
}
