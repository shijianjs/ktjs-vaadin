package io.github.shijianjs.ktjs.vaadin.select

import web.components.customElements

/**
 * `<vaadin-select-value-button>` — the button that shows the current value inside `<vaadin-select>`.
 *
 * `@protected` upstream and described only in the select docs ("has the same API as
 * `<vaadin-button>`"), so the manifest never describes it and it gets no generated wrapper. It is a
 * `ButtonMixin` element, not a `Button` subclass.
 */
abstract external class SelectValueButtonElement : web.html.HTMLElement, io.github.shijianjs.ktjs.vaadin.button.ButtonMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.ThemableMixin, io.github.shijianjs.ktjs.vaadin.componentbase.PolylitMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.LumoInjectionMixin

/**
 * Upstream's module registers the element but exports nothing (`defineCustomElement(SelectValueButton)`
 * is the last line), so there is no class to import; referencing the module still loads it.
 */
@JsModule("@vaadin/select/src/vaadin-select-value-button.js")
private external object SelectValueButtonModule

/** Imports `@vaadin/select/src/vaadin-select-value-button.js`, registering `<vaadin-select-value-button>`, and returns the element class from the registry. */
@Suppress("UNUSED_EXPRESSION")
fun registerSelectValueButton(): web.components.CustomElementConstructor<SelectValueButtonElement> {
    SelectValueButtonModule
    return customElements.get(io.github.shijianjs.ktjs.vaadin.VaadinTagName.selectValueButton)
        ?: error("vaadin-select-value-button was not registered")
}

/** Creates a `<vaadin-select-value-button>` element. The custom element is registered on first use. */
fun createSelectValueButton(configure: SelectValueButtonElement.() -> Unit = {}): SelectValueButtonElement {
    registerSelectValueButton()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.selectValueButton).apply(configure)
}
