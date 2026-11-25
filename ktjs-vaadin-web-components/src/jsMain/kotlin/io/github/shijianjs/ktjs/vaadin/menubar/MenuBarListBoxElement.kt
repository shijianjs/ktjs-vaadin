package io.github.shijianjs.ktjs.vaadin.menubar

/**
 * `<vaadin-menu-bar-list-box>` — the list box inside a menu-bar submenu.
 *
 * `@protected` upstream (missing from the manifest), but its API is `ListMixin`'s, like any other
 * list box: `items`, `selected`, `selectedValues`, `deselectItem`.
 */
abstract external class MenuBarListBoxElement : web.html.HTMLElement, io.github.shijianjs.ktjs.vaadin.a11ybase.ListMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.ThemableMixin, io.github.shijianjs.ktjs.vaadin.componentbase.DirMixin, io.github.shijianjs.ktjs.vaadin.componentbase.PolylitMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.LumoInjectionMixin

@JsModule("@vaadin/menu-bar/src/vaadin-menu-bar-list-box.js")
private external object MenuBarListBoxElementModule {
    val MenuBarListBox: web.components.CustomElementConstructor<MenuBarListBoxElement>
}

/** Imports `@vaadin/menu-bar/src/vaadin-menu-bar-list-box.js`, registering `<vaadin-menu-bar-list-box>`. */
fun registerMenuBarListBox(): web.components.CustomElementConstructor<MenuBarListBoxElement> = MenuBarListBoxElementModule.MenuBarListBox

/** Creates a `<vaadin-menu-bar-list-box>` element. The custom element is registered on first use. */
fun createMenuBarListBox(configure: MenuBarListBoxElement.() -> Unit = {}): MenuBarListBoxElement {
    registerMenuBarListBox()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.menuBarListBox).apply(configure)
}
