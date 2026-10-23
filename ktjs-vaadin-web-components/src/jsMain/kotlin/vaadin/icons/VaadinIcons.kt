package vaadin.icons

/**
 * Named exports of `@vaadin/icons/vaadin-icons.js`.
 */
@JsModule("@vaadin/icons/vaadin-icons.js")
private external object VaadinIconsModule

/**
 * Imports `@vaadin/icons`, registering the `vaadin` icon set used by
 * `<vaadin-icon icon="vaadin:home">`.
 *
 * The icon set adds a few hundred SVG icons to the bundle, so only call this when the page
 * actually uses `vaadin:` icon names.
 */
fun registerVaadinIcons(): dynamic = VaadinIconsModule
