package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-avatar-group>` is a Web Component providing avatar group displaying functionality.
 *
 * To create the avatar group, first add the component to the page:
 *
 * ```html
 * <vaadin-avatar-group></vaadin-avatar-group>
 * ```
 *
 * And then use [`items`](#/elements/vaadin-avatar-group#property-items) property to initialize the structure:
 *
 * ```js
 * document.querySelector('vaadin-avatar-group').items = [
 *   {name: 'John Doe'},
 *   {abbr: 'AB'}
 * ];
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are exposed for styling:
 *
 * Part name   | Description
 * ----------- | ---------------
 * `container` | The container element
 * `overlay`   | The overflow avatar menu overlay
 * `content`   | The overflow avatar menu overlay content
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                |
 * :----------------------------------|
 * | `--vaadin-avatar-group-gap`      |
 * | `--vaadin-avatar-group-overlap`  |
 *
 * See the [`<vaadin-avatar>`](#/elements/vaadin-avatar) documentation for the available
 * state attributes and stylable shadow parts of avatar elements.
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * ### Internal components
 *
 * In addition to `<vaadin-avatar-group>` itself, the following internal
 * components are themable:
 *
 * - `<vaadin-avatar-group-menu>` - has the same API as [`<vaadin-list-box>`](#/elements/vaadin-list-box).
 * - `<vaadin-avatar-group-menu-item>` - has the same API as [`<vaadin-item>`](#/elements/vaadin-item).
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/avatar-group)
 */
external class AvatarGroupElement : HTMLElement {
    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following JSON structure and default values:
     * ```js
     * {
     *   // Translation of the anonymous user avatar tooltip.
     *   anonymous: 'anonymous',
     *   // Translation of the avatar group accessible label.
     *   // {count} is replaced with the actual count of users.
     *   activeUsers: {
     *     one: 'Currently one active user',
     *     many: 'Currently {count} active users'
     *   },
     *   // Screen reader announcement when user joins group.
     *   // {user} is replaced with the name or abbreviation.
     *   // When neither is set, "anonymous" is used instead.
     *   joined: '{user} joined',
     *   // Screen reader announcement when user leaves group.
     *   // {user} is replaced with the name or abbreviation.
     *   // When neither is set, "anonymous" is used instead.
     *   left: '{user} left'
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * An array containing the items which will be stamped as avatars.
     *
     * The items objects allow to configure [`name`](#/elements/vaadin-avatar#property-name),
     * [`abbr`](#/elements/vaadin-avatar#property-abbr), [`img`](#/elements/vaadin-avatar#property-img)
     * and [`colorIndex`](#/elements/vaadin-avatar#property-colorIndex) properties on the
     * stamped avatars, and set `className` to provide CSS class names.
     *
     * #### Example
     *
     * ```js
     * group.items = [
     *   {
     *     name: 'User name',
     *     img: 'url-to-image.png',
     *     className: 'even'
     *   },
     *   {
     *     abbr: 'JD',
     *     colorIndex: 1,
     *     className: 'odd'
     *   },
     * ];
     * ```
     */
    var items: dynamic

    /**
     * The maximum number of avatars to display. By default, all the avatars are displayed.
     * When _maxItemsVisible_ is set, the overflowing avatars are grouped into one avatar with
     * a dropdown. Setting 0 or 1 has no effect so there are always at least two avatars visible.
     */
    var maxItemsVisible: Double?
}

/**
 * Named exports of `@vaadin/avatar-group/vaadin-avatar-group.js`.
 *
 * Referencing the module registers the `<vaadin-avatar-group>` custom element.
 */
@JsModule("@vaadin/avatar-group/vaadin-avatar-group.js")
private external object AvatarGroupModule {
    /** The registered custom element class. */
    val AvatarGroup: dynamic
}

/**
 * Imports `@vaadin/avatar-group/vaadin-avatar-group.js`, registering the `<vaadin-avatar-group>` custom element,
 * and returns the element class.
 */
fun registerAvatarGroup(): dynamic = AvatarGroupModule.AvatarGroup

/**
 * Creates a `<vaadin-avatar-group>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createAvatarGroup(configure: AvatarGroupElement.() -> Unit = {}): AvatarGroupElement {
    registerAvatarGroup()
    return document.createElement(VaadinTagName.avatarGroup).apply(configure)
}
