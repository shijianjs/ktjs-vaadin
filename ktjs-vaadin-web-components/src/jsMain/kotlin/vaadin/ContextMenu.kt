package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-context-menu>` is a Web Component for creating context menus.
 *
 * ### Items
 *
 * Items is a higher level convenience API for defining a (hierarchical) menu structure for the component.
 * If a menu item has a non-empty `children` set, a sub-menu with the child items is opened
 * next to the parent menu on mouseover, tap or a right arrow keypress.
 *
 * When an item is selected, `<vaadin-context-menu>` dispatches an "item-selected" event
 * with the selected item as `event.detail.value` property.
 * If item does not have `keepOpen` property the menu will be closed.
 *
 * ```javascript
 * contextMenu.items = [
 *   { text: 'Menu Item 1', theme: 'primary', className: 'first', children:
 *     [
 *       { text: 'Menu Item 1-1', checked: true, keepOpen: true },
 *       { text: 'Menu Item 1-2' }
 *     ]
 *   },
 *   { component: 'hr' },
 *   { text: 'Menu Item 2', children:
 *     [
 *       { text: 'Menu Item 2-1' },
 *       { text: 'Menu Item 2-2', disabled: true }
 *     ]
 *   },
 *   { text: 'Menu Item 3', disabled: true, className: 'last' }
 * ];
 *
 * contextMenu.addEventListener('item-selected', e => {
 *   const item = e.detail.value;
 *   console.log(`${item.text} selected`);
 * });
 * ```
 *
 * **NOTE:** when the `items` array is defined, the renderer cannot be used.
 *
 * #### Disabled menu items
 *
 * When disabled, menu items are rendered as "dimmed".
 *
 * By default, disabled items are not focusable and don't react to hover.
 * As a result, they are hidden from assistive technologies, and it's not
 * possible to show a tooltip to explain why they are disabled. This can
 * be addressed by enabling the feature flag `accessibleDisabledMenuItems`,
 * which makes disabled items focusable and hoverable, while still
 * preventing them from being activated:
 *
 * ```js
 * // Set before any context menu is attached to the DOM.
 * window.Vaadin.featureFlags.accessibleDisabledMenuItems = true;
 * ```
 *
 * #### Item tooltips
 *
 * Menu items can have tooltips that are shown on hover and keyboard
 * focus. To enable them, add a slotted `<vaadin-tooltip>` element
 * and set the `tooltip` property on each item that should have one:
 *
 * ```html
 * <vaadin-context-menu>
 *   <vaadin-tooltip slot="tooltip"></vaadin-tooltip>
 * </vaadin-context-menu>
 * ```
 *
 * ### Slotted list-box
 *
 * The content of the menu can also be populated by providing a custom
 * `<vaadin-context-menu-list-box>` with the `overlay` slot:
 *
 * ```html
 * <vaadin-context-menu>
 *   <vaadin-context-menu-list-box slot="overlay">
 *     <vaadin-context-menu-item>Edit</vaadin-context-menu-item>
 *     <vaadin-context-menu-item>Delete</vaadin-context-menu-item>
 *   </vaadin-context-menu-list-box>
 * </vaadin-context-menu>
 * ```
 *
 * **Note:** slotted list-box supports a single root-level menu only and
 * cannot be combined with the `items` or `renderer` API.
 *
 * ### Rendering
 *
 * The content of the menu can be populated by using the renderer callback function.
 *
 * The renderer function provides `root`, `contextMenu`, `model` arguments when applicable.
 * Generate DOM content by using `model` object properties if needed, append it to the `root`
 * element and control the state of the host element by accessing `contextMenu`. Before generating
 * new content, the renderer function should check if there is already content in `root` for reusing it.
 *
 * ```html
 * <vaadin-context-menu id="contextMenu">
 *  <p>This paragraph has a context menu.</p>
 * </vaadin-context-menu>
 * ```
 * ```js
 * const contextMenu = document.querySelector('#contextMenu');
 * contextMenu.renderer = (root, contextMenu, context) => {
 *   let listBox = root.firstElementChild;
 *   if (!listBox) {
 *     listBox = document.createElement('vaadin-list-box');
 *     root.appendChild(listBox);
 *   }
 *
 *   let item = listBox.querySelector('vaadin-item');
 *   if (!item) {
 *     item = document.createElement('vaadin-item');
 *     listBox.appendChild(item);
 *   }
 *   item.textContent = 'Content of the selector: ' + context.target.textContent;
 * };
 * ```
 *
 * You can access the menu context inside the renderer using
 * `context.target` and `context.detail`.
 *
 * Renderer is called on the opening of the context-menu and each time the related context is updated.
 * DOM generated during the renderer call can be reused
 * in the next renderer call and will be provided with the `root` argument.
 * On first call it will be empty.
 *
 * ### `vaadin-contextmenu` Gesture Event
 *
 * `vaadin-contextmenu` is a gesture event (a custom event),
 * which is dispatched after either `contextmenu` or long touch events.
 * This enables support for both mouse and touch environments in a uniform way.
 *
 * `<vaadin-context-menu>` opens the menu overlay on the `vaadin-contextmenu`
 * event by default.
 *
 * ### Menu Listener
 *
 * By default, the `<vaadin-context-menu>` element listens for the menu opening
 * event on itself. In case if you do not want to wrap the target, you can listen for
 * events on an element outside the `<vaadin-context-menu>` by setting the
 * `listenOn` property:
 *
 * ```html
 * <vaadin-context-menu id="contextMenu"></vaadin-context-menu>
 *
 * <div id="menuListener">The element that listens for the contextmenu event.</div>
 * ```
 * ```javascript
 * const contextMenu = document.querySelector('#contextMenu');
 * contextMenu.listenOn = document.querySelector('#menuListener');
 * ```
 *
 * ### Filtering Menu Targets
 *
 * By default, the listener element and all its descendants open the context
 * menu. You can filter the menu targets to a smaller set of elements inside
 * the listener element by setting the `selector` property.
 *
 * In the following example, only the elements matching `.has-menu` will open the context menu:
 *
 * ```html
 * <vaadin-context-menu selector=".has-menu">
 *   <p class="has-menu">This paragraph opens the context menu</p>
 *   <p>This paragraph does not open the context menu</p>
 * </vaadin-context-menu>
 * ```
 *
 * ### Menu Context
 *
 * The following properties are available in the `context` argument:
 *
 * - `target` is the menu opening event target, which is the element that
 * the user has called the context menu for
 * - `detail` is the menu opening event detail
 *
 * In the following example, the menu item text is composed with the contents
 * of the element that opened the menu:
 *
 * ```html
 * <vaadin-context-menu selector="li" id="contextMenu">
 *   <ul>
 *     <li>Foo</li>
 *     <li>Bar</li>
 *     <li>Baz</li>
 *   </ul>
 * </vaadin-context-menu>
 * ```
 * ```js
 * const contextMenu = document.querySelector('#contextMenu');
 * contextMenu.renderer = (root, contextMenu, context) => {
 *   let listBox = root.firstElementChild;
 *   if (!listBox) {
 *     listBox = document.createElement('vaadin-list-box');
 *     root.appendChild(listBox);
 *   }
 *
 *   let item = listBox.querySelector('vaadin-item');
 *   if (!item) {
 *     item = document.createElement('vaadin-item');
 *     listBox.appendChild(item);
 *   }
 *   item.textContent = 'The menu target: ' + context.target.textContent;
 * };
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name        | Description
 * -----------------|-------------------------------------------
 * `backdrop`       | Backdrop of the overlay
 * `overlay`        | The overlay container
 * `content`        | The overlay content
 *
 * ### Custom CSS Properties
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                   | Description
 * --------------------------------------|-------------
 * `--vaadin-context-menu-offset-top`    | Used as an offset when using `position` and the context menu is aligned vertically below the target
 * `--vaadin-context-menu-offset-bottom` | Used as an offset when using `position` and the context menu is aligned vertically above the target
 * `--vaadin-context-menu-offset-start`  | Used as an offset when using `position` and the context menu is aligned horizontally after the target
 * `--vaadin-context-menu-offset-end`    | Used as an offset when using `position` and the context menu is aligned horizontally before the target
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * ### Related components
 *
 * In addition to `<vaadin-context-menu>` itself, the following components are themable:
 *
 * - [`<vaadin-context-menu-item>`](#/elements/vaadin-context-menu-item) - an item element.
 * - [`<vaadin-context-menu-list-box>`](#/elements/vaadin-context-menu-list-box) - a list-box element.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/context-menu)
 */
external class ContextMenuElement : HTMLElement {
    /**
     * Event name to listen for closing the context menu.
     */
    var closeOn: String?

    /**
     * Defines a (hierarchical) menu structure for the component.
     * If a menu item has a non-empty `children` set, a sub-menu with the child items is opened
     * next to the parent menu on mouseover, tap or a right arrow keypress.
     *
     * The items API can't be used together with a renderer!
     *
     * #### Example
     *
     * ```javascript
     * contextMenu.items = [
     *   { text: 'Menu Item 1', theme: 'primary', className: 'first', children:
     *     [
     *       { text: 'Menu Item 1-1', checked: true, keepOpen: true },
     *       { text: 'Menu Item 1-2' }
     *     ]
     *   },
     *   { component: 'hr' },
     *   { text: 'Menu Item 2', children:
     *     [
     *       { text: 'Menu Item 2-1' },
     *       { text: 'Menu Item 2-2', disabled: true }
     *     ]
     *   },
     *   { text: 'Menu Item 3', disabled: true, className: 'last' }
     * ];
     * ```
     *
     * #### Item tooltips
     *
     * Menu items can have tooltips that are shown on hover and keyboard
     * focus. To enable them, add a slotted `<vaadin-tooltip>` element
     * and set the `tooltip` property on each item that should have one:
     *
     * ```html
     * <vaadin-context-menu>
     *   <vaadin-tooltip slot="tooltip"></vaadin-tooltip>
     * </vaadin-context-menu>
     * ```
     */
    var items: dynamic

    /**
     * The target element that's listened to for context menu opening events.
     * By default the vaadin-context-menu listens to the target's `vaadin-contextmenu`
     * events.
     */
    var listenOn: web.html.HTMLElement?

    /**
     * True if the overlay is currently displayed.
     */
    val opened: Boolean

    /**
     * Event name to listen for opening the context menu.
     */
    var openOn: String?

    /**
     * Position of the overlay with respect to the target.
     * Supported values: null, `top-start`, `top`, `top-end`,
     * `bottom-start`, `bottom`, `bottom-end`, `start-top`,
     * `start`, `start-bottom`, `end-top`, `end`, `end-bottom`.
     */
    var position: String?

    /**
     * Custom function for rendering the content of the menu overlay.
     * Receives three arguments:
     *
     * - `root` The root container DOM element. Append your content to it.
     * - `contextMenu` The reference to the `<vaadin-context-menu>` element.
     * - `context` The object with the menu context, contains:
     *   - `context.target`  the target of the menu opening event,
     *   - `context.detail` the menu opening event detail.
     */
    var renderer: dynamic

    /**
     * CSS selector that can be used to target any child element
     * of the context menu to listen for `openOn` events.
     */
    var selector: String?

    /**
     * Closes the overlay.
     */
    fun close()

    /**
     * Opens the overlay.
     * @param e used as the context for the menu. Overlay coordinates are taken from this event.
     */
    fun `open`(e: web.events.Event?)

    /**
     * Requests an update for the content of the menu overlay.
     * While performing the update, it invokes the renderer passed in the `renderer` property.
     *
     * It is not guaranteed that the update happens immediately (synchronously) after it is requested.
     */
    fun requestContentUpdate()
}

/**
 * Named exports of `@vaadin/context-menu/vaadin-context-menu.js`.
 *
 * Referencing the module registers the `<vaadin-context-menu>` custom element.
 */
@JsModule("@vaadin/context-menu/vaadin-context-menu.js")
private external object ContextMenuModule {
    /** The registered custom element class. */
    val ContextMenu: dynamic
}

/**
 * Imports `@vaadin/context-menu/vaadin-context-menu.js`, registering the `<vaadin-context-menu>` custom element,
 * and returns the element class.
 */
fun registerContextMenu(): dynamic = ContextMenuModule.ContextMenu

/**
 * Creates a `<vaadin-context-menu>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createContextMenu(configure: ContextMenuElement.() -> Unit = {}): ContextMenuElement {
    registerContextMenu()
    return document.createElement(VaadinTagName.contextMenu).apply(configure)
}
