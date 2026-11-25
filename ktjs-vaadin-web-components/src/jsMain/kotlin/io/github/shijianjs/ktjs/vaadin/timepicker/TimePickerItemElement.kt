package io.github.shijianjs.ktjs.vaadin.timepicker

/**
 * `<vaadin-time-picker-item>` — the option row a time picker renders into its overlay.
 *
 * `@protected` upstream, so the manifest never describes it and it gets no generated wrapper. It
 * shares the combo box item API: `item`, `index`, `label`, `selected`, `focused`, `renderer`.
 */
abstract external class TimePickerItemElement : web.html.HTMLElement, io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxItemMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.ThemableMixin, io.github.shijianjs.ktjs.vaadin.componentbase.DirMixin, io.github.shijianjs.ktjs.vaadin.componentbase.PolylitMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.LumoInjectionMixin

@JsModule("@vaadin/time-picker/src/vaadin-time-picker-item.js")
private external object TimePickerItemElementModule {
    val TimePickerItem: web.components.CustomElementConstructor<TimePickerItemElement>
}

/** Imports `@vaadin/time-picker/src/vaadin-time-picker-item.js`, registering `<vaadin-time-picker-item>`. */
fun registerTimePickerItem(): web.components.CustomElementConstructor<TimePickerItemElement> = TimePickerItemElementModule.TimePickerItem

/** Creates a `<vaadin-time-picker-item>` element. The custom element is registered on first use. */
fun createTimePickerItem(configure: TimePickerItemElement.() -> Unit = {}): TimePickerItemElement {
    registerTimePickerItem()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.timePickerItem).apply(configure)
}
