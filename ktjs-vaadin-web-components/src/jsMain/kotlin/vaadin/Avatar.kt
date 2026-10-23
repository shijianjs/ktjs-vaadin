package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-avatar>` is a Web Component providing avatar displaying functionality.
 *
 * ```html
 * <vaadin-avatar img="avatars/avatar-1.jpg"></vaadin-avatar>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name | Description
 * --------- | ---------------
 * `abbr`    | The abbreviation element
 * `icon`    | The icon element
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                |
 * :----------------------------------|
 * | `--vaadin-avatar-background`     |
 * | `--vaadin-avatar-border-color`   |
 * | `--vaadin-avatar-border-width`   |
 * | `--vaadin-avatar-font-size`      |
 * | `--vaadin-avatar-font-weight`    |
 * | `--vaadin-avatar-size`           |
 * | `--vaadin-avatar-text-color`     |
 *
 * The following state attributes are available for styling:
 *
 * Attribute         | Description
 * ------------------|-------------
 * `focus-ring`      | Set when the avatar is focused using the keyboard.
 * `focused`         | Set when the avatar is focused.
 * `has-color-index` | Set when the avatar has `colorIndex` and the corresponding custom CSS property exists.
 * `has-tooltip`     | Set when the element has a slotted tooltip.
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/avatar)
 */
external class AvatarElement : HTMLElement {
    /**
     * A shortened form of name that is displayed
     * in the avatar when `img` is not provided.
     */
    var abbr: String?

    /**
     * Color index used for avatar background.
     */
    var colorIndex: Double?

    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following JSON structure and default values:
     * ```js
     * {
     *   // Translation of the anonymous user avatar tooltip.
     *   anonymous: 'anonymous'
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * The path to the image
     */
    var img: String?

    /**
     * Full name of the user
     * used for the tooltip of the avatar.
     */
    var name: String?

    /**
     * When true, the avatar has tooltip shown on hover and focus.
     * The tooltip text is based on the `name` and `abbr` properties.
     * When neither is provided, `i18n.anonymous` is used instead.
     */
    var withTooltip: Boolean
}

/**
 * Named exports of `@vaadin/avatar/vaadin-avatar.js`.
 *
 * Referencing the module registers the `<vaadin-avatar>` custom element.
 */
@JsModule("@vaadin/avatar/vaadin-avatar.js")
private external object AvatarModule {
    /** The registered custom element class. */
    val Avatar: dynamic
}

/**
 * Imports `@vaadin/avatar/vaadin-avatar.js`, registering the `<vaadin-avatar>` custom element,
 * and returns the element class.
 */
fun registerAvatar(): dynamic = AvatarModule.Avatar

/**
 * Creates a `<vaadin-avatar>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createAvatar(configure: AvatarElement.() -> Unit = {}): AvatarElement {
    registerAvatar()
    return document.createElement(VaadinTagName.avatar).apply(configure)
}
