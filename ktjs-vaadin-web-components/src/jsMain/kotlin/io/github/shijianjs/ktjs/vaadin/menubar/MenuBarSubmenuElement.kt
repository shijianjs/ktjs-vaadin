package io.github.shijianjs.ktjs.vaadin.menubar

/**
 * `<vaadin-menu-bar-submenu>` — the context menu a menu-bar button opens.
 *
 * `@protected` upstream (missing from the manifest); its API is `ContextMenuMixin`'s (`items`,
 * `openOn`, `close()`), with `ThemePropertyMixin` because the menu bar themes it inline.
 */
abstract external class MenuBarSubmenuElement : web.html.HTMLElement, io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuMixin, io.github.shijianjs.ktjs.vaadin.componentbase.DirMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.ThemePropertyMixin, io.github.shijianjs.ktjs.vaadin.componentbase.PolylitMixin

@JsModule("@vaadin/menu-bar/src/vaadin-menu-bar-submenu.js")
private external object MenuBarSubmenuElementModule {
    val MenuBarSubmenu: web.components.CustomElementConstructor<MenuBarSubmenuElement>
}

/** Imports `@vaadin/menu-bar/src/vaadin-menu-bar-submenu.js`, registering `<vaadin-menu-bar-submenu>`. */
fun registerMenuBarSubmenu(): web.components.CustomElementConstructor<MenuBarSubmenuElement> = MenuBarSubmenuElementModule.MenuBarSubmenu

/** Creates a `<vaadin-menu-bar-submenu>` element. The custom element is registered on first use. */
fun createMenuBarSubmenu(configure: MenuBarSubmenuElement.() -> Unit = {}): MenuBarSubmenuElement {
    registerMenuBarSubmenu()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.menuBarSubmenu).apply(configure)
}
