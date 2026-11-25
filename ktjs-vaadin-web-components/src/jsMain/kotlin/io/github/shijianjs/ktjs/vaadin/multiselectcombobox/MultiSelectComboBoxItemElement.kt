package io.github.shijianjs.ktjs.vaadin.multiselectcombobox

/**
 * `<vaadin-multi-select-combo-box-item>` — the row a multi-select combo box renders into its overlay.
 *
 * `@protected` upstream, so the manifest never describes it and it gets no generated wrapper. Its
 * API is `ComboBoxItemMixin`'s: `item`, `index`, `label`, `selected`, `focused`, `renderer`,
 * `requestContentUpdate()`.
 */
abstract external class MultiSelectComboBoxItemElement : web.html.HTMLElement, io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxItemMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.ThemableMixin, io.github.shijianjs.ktjs.vaadin.componentbase.DirMixin, io.github.shijianjs.ktjs.vaadin.componentbase.PolylitMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.LumoInjectionMixin

@JsModule("@vaadin/multi-select-combo-box/src/vaadin-multi-select-combo-box-item.js")
private external object MultiSelectComboBoxItemElementModule {
    val MultiSelectComboBoxItem: web.components.CustomElementConstructor<MultiSelectComboBoxItemElement>
}

/** Imports `@vaadin/multi-select-combo-box/src/vaadin-multi-select-combo-box-item.js`, registering `<vaadin-multi-select-combo-box-item>`. */
fun registerMultiSelectComboBoxItem(): web.components.CustomElementConstructor<MultiSelectComboBoxItemElement> = MultiSelectComboBoxItemElementModule.MultiSelectComboBoxItem

/** Creates a `<vaadin-multi-select-combo-box-item>` element. The custom element is registered on first use. */
fun createMultiSelectComboBoxItem(configure: MultiSelectComboBoxItemElement.() -> Unit = {}): MultiSelectComboBoxItemElement {
    registerMultiSelectComboBoxItem()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.multiSelectComboBoxItem).apply(configure)
}
