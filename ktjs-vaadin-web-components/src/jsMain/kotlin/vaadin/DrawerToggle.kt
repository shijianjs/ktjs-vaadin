package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * The Drawer Toggle component controls the drawer in App Layout component.
 *
 * ```html
 * <vaadin-app-layout>
 *   <vaadin-drawer-toggle slot="navbar">Toggle drawer</vaadin-drawer-toggle>
 * </vaadin-app-layout>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name    | Description
 * -------------|------------
 * `icon`       | The icon element
 *
 * The following state attributes are available for styling:
 *
 * Attribute    | Description
 * -------------|------------
 * `focus-ring` | Set when the element is focused using the keyboard
 * `focused`    | Set when the element is focused
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                |
 * :----------------------------------|
 * | `--vaadin-button-background`     |
 * | `--vaadin-button-border-color`   |
 * | `--vaadin-button-border-radius`  |
 * | `--vaadin-button-border-width`   |
 * | `--vaadin-button-font-size`      |
 * | `--vaadin-button-line-height`    |
 * | `--vaadin-button-margin`         |
 * | `--vaadin-button-padding`        |
 * | `--vaadin-button-text-color`     |
 * | `--vaadin-icon-size`             |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/app-layout)
 */
external class DrawerToggleElement : HTMLElement {
    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean
}

/**
 * Named exports of `@vaadin/app-layout/vaadin-drawer-toggle.js`.
 *
 * Referencing the module registers the `<vaadin-drawer-toggle>` custom element.
 */
@JsModule("@vaadin/app-layout/vaadin-drawer-toggle.js")
private external object DrawerToggleModule {
    /** The registered custom element class. */
    val DrawerToggle: dynamic
}

/**
 * Imports `@vaadin/app-layout/vaadin-drawer-toggle.js`, registering the `<vaadin-drawer-toggle>` custom element,
 * and returns the element class.
 */
fun registerDrawerToggle(): dynamic = DrawerToggleModule.DrawerToggle

/**
 * Creates a `<vaadin-drawer-toggle>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createDrawerToggle(configure: DrawerToggleElement.() -> Unit = {}): DrawerToggleElement {
    registerDrawerToggle()
    return document.createElement(VaadinTagName.drawerToggle).apply(configure)
}
