package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-app-layout>` is a Web Component providing a quick and easy way to get a common application layout structure done.
 *
 * ```html
 * <vaadin-app-layout primary-section="navbar|drawer">
 *  <vaadin-drawer-toggle slot="navbar [touch-optimized]"></vaadin-drawer-toggle>
 *  <h3 slot="navbar [touch-optimized]">Company Name</h3>
 *  <vaadin-tabs orientation="vertical" slot="drawer">
 *    <vaadin-tab>Menu item 1</vaadin-tab>
 *  </vaadin-tabs>
 *  <!-- Everything else will be the page content -->
 *  <div>
 *    <h3>Page title</h3>
 *    <p>Page content</p>
 *  </div>
 * </vaadin-app-layout>
 * ```
 *
 * For best results, the component should be added to the root level of your application (i.e., as a direct child of `<body>`).
 *
 * The page should include a viewport meta tag which contains `viewport-fit=cover`, like the following:
 * ```html
 * <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
 * ```
 *
 * This causes the viewport to be scaled to fill the device display.
 * To ensure that important content is displayed, use the provided css variables:
 *
 * - `--safe-area-inset-top`
 * - `--safe-area-inset-right`
 * - `--safe-area-inset-bottom`
 * - `--safe-area-inset-left`
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name        | Description
 * -----------------|---------------------------------------------------------|
 * `backdrop`       | Backdrop covering the layout when drawer is open as an overlay
 * `navbar`         | Container for the navigation bar
 * `navbar-top`     | Container for the top navigation bar
 * `navbar-bottom`  | Container for the bottom navigation bar
 * `drawer`         | Container for the drawer area
 * `content`        | Container for the content area
 *
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|-------------
 * `has-drawer`   | Set when the element has light DOM content in the drawer slot.
 * `has-navbar`   | Set when the element has light DOM content in the navbar slot.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                  |
 * :----------------------------------------------------|
 * | `--vaadin-app-layout-drawer-background`            |
 * | `--vaadin-app-layout-drawer-width`                 |
 * | `--vaadin-app-layout-navbar-background`            |
 * | `--vaadin-app-layout-navbar-gap`                   |
 * | `--vaadin-app-layout-navbar-padding-bottom`        |
 * | `--vaadin-app-layout-navbar-padding-inline-end`    |
 * | `--vaadin-app-layout-navbar-padding-inline-start`  |
 * | `--vaadin-app-layout-navbar-padding-top`           |
 * | `--vaadin-app-layout-transition-duration`          |
 * | `--vaadin-overlay-backdrop-background`             |
 * | `--vaadin-overlay-shadow`                          |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * ### Component's slots
 *
 * The following slots are available to be set
 *
 * Slot name          | Description
 * -------------------|---------------------------------------------------|
 * no name            | Default container for the page content
 * `navbar `          | Container for the top navbar area
 * `drawer`           | Container for an application menu
 * `touch-optimized`  | Container for the bottom navbar area (only visible for mobile devices)
 *
 * #### Touch optimized
 *
 * App Layout has a pseudo-slot `touch-optimized` in order to give more control of the presentation of
 * elements with `slot[navbar]`. Internally, when the user is interacting with App Layout from a
 * touchscreen device, the component will search for elements with `slot[navbar touch-optimized]` and move
 * them to the bottom of the page.
 *
 * ### Navigation
 *
 * As the drawer opens as an overlay in small devices, it makes sense to close it once a navigation happens.
 * If you are using Vaadin Router, this will happen automatically unless you change the `closeDrawerOn` event name.
 *
 * In order to do so, there are two options:
 * - If the `vaadin-app-layout` instance is available, then `drawerOpened` can be set to `false`
 * - If not, a custom event `close-overlay-drawer` can be dispatched either by calling
 *  `window.dispatchEvent(new CustomEvent('close-overlay-drawer'))` or by calling
 *  `AppLayout.dispatchCloseOverlayDrawerEvent()`
 *
 * ### Scrolling areas
 *
 * By default, the component will act with the "body scrolling", so on mobile (iOS Safari and Android Chrome),
 * the toolbars will collapse when a scroll happens.
 *
 * To use the "content scrolling", in case of the content of the page relies on a pre-defined height (for instance,
 * it has a `height:100%`), then the developer can set `height: 100%` to both `html` and `body`.
 * That makes the content part of App Layout scrollable.
 * In that case, the toolbars on mobile device won't collapse.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/app-layout)
 */
external class AppLayoutElement : HTMLElement {
    /**
     * A global event that causes the drawer to close (be hidden) when it is in overlay mode.
     * - The default is `vaadin-router-location-changed` dispatched by Vaadin Router
     */
    var closeDrawerOn: String?

    /**
     * Controls whether the drawer is opened (visible) or not.
     * Its default value depends on the viewport:
     * - `true`, for desktop size views
     * - `false`, for mobile size views
     */
    var drawerOpened: Boolean

    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following structure and default values:
     * ```js
     * {
     *   drawer: 'Drawer'
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * Drawer is an overlay on top of the content
     * Controlled via CSS using `--vaadin-app-layout-drawer-overlay: true|false`;
     */
    val overlay: Boolean

    /**
     * Defines whether navbar or drawer will come first visually.
     * - By default (`primary-section="navbar"`), the navbar takes the full available width and moves the drawer down.
     * - If `primary-section="drawer"` is set, then the drawer will move the navbar, taking the full available height.
     */
    var primarySection: String?
}

/**
 * Static members of the `<vaadin-app-layout>` class.
 */
private external interface AppLayoutStatics {
    /**
     * Helper static method that dispatches a `close-overlay-drawer` event
     */
    fun dispatchCloseOverlayDrawerEvent()
}

/**
 * Named exports of `@vaadin/app-layout/vaadin-app-layout.js`.
 *
 * Referencing the module registers the `<vaadin-app-layout>` custom element.
 */
@JsModule("@vaadin/app-layout/vaadin-app-layout.js")
private external object AppLayoutModule {
    /** The registered custom element class. */
    val AppLayout: AppLayoutStatics
}

/**
 * Imports `@vaadin/app-layout/vaadin-app-layout.js`, registering the `<vaadin-app-layout>` custom element,
 * and returns the element class.
 */
fun registerAppLayout(): dynamic = AppLayoutModule.AppLayout

/**
 * Static entry points of `<vaadin-app-layout>`.
 *
 * Calling these members imports the upstream package and registers the element.
 */
object AppLayout {
    /**
     * Helper static method that dispatches a `close-overlay-drawer` event
     */
    fun dispatchCloseOverlayDrawerEvent() = AppLayoutModule.AppLayout.dispatchCloseOverlayDrawerEvent()
}

/**
 * Creates a `<vaadin-app-layout>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createAppLayout(configure: AppLayoutElement.() -> Unit = {}): AppLayoutElement {
    registerAppLayout()
    return document.createElement(VaadinTagName.appLayout).apply(configure)
}
