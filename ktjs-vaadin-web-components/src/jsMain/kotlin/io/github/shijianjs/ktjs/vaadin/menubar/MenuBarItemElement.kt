package io.github.shijianjs.ktjs.vaadin.menubar

/**
 * `<vaadin-menu-bar-item>` — the element a menu-bar entry renders when it is kept as DOM
 * (a `component` item rather than a `text` one), holding arbitrary children such as an icon and a
 * label.
 *
 * Upstream marks the class `@protected` and leaves it out of `@vaadin/menu-bar`'s
 * `custom-elements.json`, so the generator cannot derive a wrapper for it; this file is
 * hand-written and its tag is registered in `MANUAL_TAGS`. The element's API is `<vaadin-item>`'s
 * (`ItemMixin`): `selected`, `value`, and the `disabled` state, plus `role="menuitem"` which
 * upstream sets on connect.
 */
abstract external class MenuBarItemElement : web.html.HTMLElement, io.github.shijianjs.ktjs.vaadin.item.ItemMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.ThemableMixin, io.github.shijianjs.ktjs.vaadin.componentbase.DirMixin, io.github.shijianjs.ktjs.vaadin.componentbase.PolylitMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.LumoInjectionMixin

@JsModule("@vaadin/menu-bar/src/vaadin-menu-bar-item.js")
private external object MenuBarItemElementModule {
    val MenuBarItem: web.components.CustomElementConstructor<MenuBarItemElement>
}

/** Imports `@vaadin/menu-bar/src/vaadin-menu-bar-item.js`, registering `<vaadin-menu-bar-item>`, and returns the element class. */
fun registerMenuBarItem(): web.components.CustomElementConstructor<MenuBarItemElement> = MenuBarItemElementModule.MenuBarItem

/** Creates a `<vaadin-menu-bar-item>` element. The custom element is registered on first use. */
fun createMenuBarItem(configure: MenuBarItemElement.() -> Unit = {}): MenuBarItemElement {
    registerMenuBarItem()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.menuBarItem).apply(configure)
}
