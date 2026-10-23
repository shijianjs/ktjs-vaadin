package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-menu-bar>` is a Web Component providing a set of horizontally stacked buttons offering
 * the user quick access to a consistent set of commands. Each button can toggle a submenu with
 * support for additional levels of nested menus.
 *
 * To create the menu bar, first add the component to the page:
 *
 * ```html
 * <vaadin-menu-bar></vaadin-menu-bar>
 * ```
 *
 * And then use [`items`](#/elements/vaadin-menu-bar#property-items) property to initialize the structure:
 *
 * ```js
 * document.querySelector('vaadin-menu-bar').items = [{text: 'File'}, {text: 'Edit'}];
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are exposed for styling:
 *
 * Part name         | Description
 * ------------------|----------------
 * `container`       | The container wrapping menu bar buttons.
 *
 * The following state attributes are available for styling:
 *
 * Attribute           | Description
 * --------------------|----------------------------------
 * `disabled`          | Set when the menu bar is disabled
 * `has-single-button` | Set when there is only one button visible
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property         |
 * :---------------------------|
 * | `--vaadin-menu-bar-gap`   |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * ### Internal components
 *
 * In addition to `<vaadin-menu-bar>` itself, the following internal
 * components are themable:
 *
 * - `<vaadin-menu-bar-button>` - has the same API as [`<vaadin-button>`](#/elements/vaadin-button).
 * - `<vaadin-menu-bar-item>` - has the same API as [`<vaadin-item>`](#/elements/vaadin-item).
 * - `<vaadin-menu-bar-list-box>` - has the same API as [`<vaadin-list-box>`](#/elements/vaadin-list-box).
 * - `<vaadin-menu-bar-submenu>` - has the same API as [`<vaadin-context-menu>`](#/elements/vaadin-context-menu).
 *
 * The `<vaadin-menu-bar-item>` sub-menu elements have the following additional state attributes
 * on top of the built-in `<vaadin-item>` state attributes:
 *
 * Attribute  | Description
 * ---------- |-------------
 * `expanded` | Expanded parent item.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/menu-bar)
 */
external class MenuBarElement : HTMLElement {
    /**
     * If true, the user cannot interact with this element.
     */
    var disabled: Boolean

    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following JSON structure and default values:
     * ```js
     * {
     *   moreOptions: 'More options'
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * Defines a hierarchical structure, where root level items represent menu bar buttons,
     * and `children` property configures a submenu with items to be opened below
     * the button on click, Enter, Space, Up and Down arrow keys.
     *
     * #### Example
     *
     * ```js
     * menubar.items = [
     *   {
     *     text: 'File',
     *     className: 'file',
     *     children: [
     *       {text: 'Open', className: 'file open'}
     *       {text: 'Auto Save', checked: true},
     *     ]
     *   },
     *   {component: 'hr'},
     *   {
     *     text: 'Edit',
     *     children: [
     *       {text: 'Undo', disabled: true},
     *       {text: 'Redo'}
     *     ]
     *   },
     *   {text: 'Help'}
     * ];
     * ```
     *
     * #### Disabled items
     *
     * When disabled, menu bar items are rendered as "dimmed".
     *
     * By default, disabled items are not focusable and don't react to hover.
     * As a result, they are hidden from assistive technologies, and it's not
     * possible to show a tooltip to explain why they are disabled. This can be
     * addressed by enabling several feature flags, which makes disabled items
     * focusable and hoverable, while still preventing them from being activated:
     *
     * ```js
     * // Allow focus and hover interactions with disabled menu bar root items (buttons)
     * window.Vaadin.featureFlags.accessibleDisabledButtons = true;
     *
     * // Allow focus and hover interactions with disabled menu bar sub-menu items
     * window.Vaadin.featureFlags.accessibleDisabledMenuItems = true;
     * ```
     *
     * Both flags must be set before any menu bar is attached to the DOM.
     *
     * #### Item tooltips
     *
     * Buttons and sub-menu items can have tooltips that are shown on
     * hover and keyboard focus. To enable them, add a slotted
     * `<vaadin-tooltip>` element and set the `tooltip` property on
     * each item that should have one:
     *
     * ```html
     * <vaadin-menu-bar>
     *   <vaadin-tooltip slot="tooltip"></vaadin-tooltip>
     * </vaadin-menu-bar>
     * ```
     */
    var items: dynamic

    /**
     * If true, the submenu will open on hover (mouseover) instead of click.
     */
    var openOnHover: Boolean

    /**
     * If true, the buttons will be collapsed into the overflow menu
     * starting from the "start" end of the bar instead of the "end".
     */
    var reverseCollapse: Boolean

    /**
     * If true, the top-level menu items is traversable by tab
     * instead of arrow keys (i.e. disabling roving tabindex)
     */
    var tabNavigation: Boolean

    /**
     * Closes the current submenu.
     */
    fun close()
}

/**
 * Named exports of `@vaadin/menu-bar/vaadin-menu-bar.js`.
 *
 * Referencing the module registers the `<vaadin-menu-bar>` custom element.
 */
@JsModule("@vaadin/menu-bar/vaadin-menu-bar.js")
private external object MenuBarModule {
    /** The registered custom element class. */
    val MenuBar: dynamic
}

/**
 * Imports `@vaadin/menu-bar/vaadin-menu-bar.js`, registering the `<vaadin-menu-bar>` custom element,
 * and returns the element class.
 */
fun registerMenuBar(): dynamic = MenuBarModule.MenuBar

/**
 * Creates a `<vaadin-menu-bar>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createMenuBar(configure: MenuBarElement.() -> Unit = {}): MenuBarElement {
    registerMenuBar()
    return document.createElement(VaadinTagName.menuBar).apply(configure)
}
