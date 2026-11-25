package io.github.shijianjs.ktjs.vaadin.menubar

/**
 * `<vaadin-menu-bar-button>` — the top-level button `<vaadin-menu-bar>` renders for each item.
 *
 * Upstream marks the class `@protected`, so it is absent from `@vaadin/menu-bar`'s
 * `custom-elements.json` and gets no generated wrapper. It *is* a `<vaadin-button>` subclass
 * (`class MenuBarButton extends Button`), so it inherits the whole button API here.
 */
abstract external class MenuBarButtonElement : io.github.shijianjs.ktjs.vaadin.button.ButtonElement

@JsModule("@vaadin/menu-bar/src/vaadin-menu-bar-button.js")
private external object MenuBarButtonElementModule {
    val MenuBarButton: web.components.CustomElementConstructor<MenuBarButtonElement>
}

/** Imports `@vaadin/menu-bar/src/vaadin-menu-bar-button.js`, registering `<vaadin-menu-bar-button>`. */
fun registerMenuBarButton(): web.components.CustomElementConstructor<MenuBarButtonElement> = MenuBarButtonElementModule.MenuBarButton

/** Creates a `<vaadin-menu-bar-button>` element. The custom element is registered on first use. */
fun createMenuBarButton(configure: MenuBarButtonElement.() -> Unit = {}): MenuBarButtonElement {
    registerMenuBarButton()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.menuBarButton).apply(configure)
}
