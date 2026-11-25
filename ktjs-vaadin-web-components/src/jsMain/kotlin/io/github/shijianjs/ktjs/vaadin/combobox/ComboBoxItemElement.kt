package io.github.shijianjs.ktjs.vaadin.combobox

/**
 * `<vaadin-combo-box-item>` — the row a combo box renders into its overlay.
 *
 * `@protected` upstream, so the manifest never describes it and it gets no generated wrapper. Its
 * API is `ComboBoxItemMixin`'s: `item`, `index`, `label`, `selected`, `focused`, `renderer`,
 * `requestContentUpdate()`.
 */
abstract external class ComboBoxItemElement : web.html.HTMLElement, io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxItemMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.ThemableMixin, io.github.shijianjs.ktjs.vaadin.componentbase.DirMixin, io.github.shijianjs.ktjs.vaadin.componentbase.PolylitMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.LumoInjectionMixin

@JsModule("@vaadin/combo-box/src/vaadin-combo-box-item.js")
private external object ComboBoxItemElementModule {
    val ComboBoxItem: web.components.CustomElementConstructor<ComboBoxItemElement>
}

/** Imports `@vaadin/combo-box/src/vaadin-combo-box-item.js`, registering `<vaadin-combo-box-item>`. */
fun registerComboBoxItem(): web.components.CustomElementConstructor<ComboBoxItemElement> = ComboBoxItemElementModule.ComboBoxItem

/** Creates a `<vaadin-combo-box-item>` element. The custom element is registered on first use. */
fun createComboBoxItem(configure: ComboBoxItemElement.() -> Unit = {}): ComboBoxItemElement {
    registerComboBoxItem()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.comboBoxItem).apply(configure)
}
