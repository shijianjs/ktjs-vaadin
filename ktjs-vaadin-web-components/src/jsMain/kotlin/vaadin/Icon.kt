package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-icon>` is a Web Component for displaying SVG icons.
 *
 * ### Icon property
 *
 * The `<vaadin-icon>` component is designed to be used as a drop-in replacement for `<iron-icon>`.
 * For example, you can use it with `vaadin-icons` like this:
 *
 * ```html
 * <vaadin-icon icon="vaadin:angle-down"></vaadin-icon>
 * ```
 *
 * Alternatively, you can also pick one of the Lumo icons:
 *
 * ```html
 * <vaadin-icon icon="lumo:user"></vaadin-icon>
 * ```
 *
 * ### Custom SVG icon
 *
 * Alternatively, instead of selecting an icon from an iconset by name, you can pass any custom `svg`
 * literal using the [`svg`](#/elements/vaadin-icon#property-svg) property. In this case you can also
 * define the size of the SVG `viewBox` using the [`size`](#/elements/vaadin-icon#property-size) property:
 *
 * ```js
 * import { html, svg } from 'lit';
 *
 * // in your component
 * render() {
 *   const svgIcon = svg`<path d="M13 4v2l-5 5-5-5v-2l5 5z"></path>`;
 *   return html`
 *     <vaadin-icon
 *       .svg="${svgIcon}"
 *       size="16"
 *     ></vaadin-icon>
 *   `;
 * }
 * ```
 *
 * ### Styling
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property            |
 * :------------------------------|
 * | `--vaadin-icon-color`        |
 * | `--vaadin-icon-size`         |
 * | `--vaadin-icon-stroke-width` |
 * | `--vaadin-icon-visual-size`  |
 *
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|-------------
 * `has-tooltip`  | Set when the icon has a slotted tooltip
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/icon)
 */
external class IconElement : HTMLElement {
    /**
     * A hexadecimal code point that specifies a glyph from an icon font.
     *
     * Example: "e001"
     */
    var char: String?

    /**
     * The font family to use for the font icon.
     */
    var fontFamily: String?

    /**
     * The name of the icon to use. The name should be of the form:
     * `iconset_name:icon_name`. When using `vaadin-icons` it is possible
     * to omit the first part and only use `icon_name` as a value.
     *
     * Setting the `icon` property updates the `svg` and `size` based on the
     * values provided by the corresponding `vaadin-iconset` element.
     *
     * See also [`name`](#/elements/vaadin-iconset#property-name) property of `vaadin-iconset`.
     */
    var icon: String?

    /**
     * Class names defining an icon font and/or a specific glyph inside an icon font.
     *
     * Example: "fa-solid fa-user"
     */
    var iconClass: String?

    /**
     * A ligature name that specifies an icon from an icon font with support for ligatures.
     *
     * Example: "home".
     */
    var ligature: String?

    /**
     * The size of an icon, used to set the `viewBox` attribute.
     */
    var size: Double?

    /**
     * The SVG source to be loaded as the icon. It can be:
     * - an URL to a file containing the icon
     * - an URL in the format `/path/to/file.svg#objectID`, where the `objectID` refers to an ID attribute contained
     *   inside the SVG referenced by the path. Note that the file needs to follow the same-origin policy.
     * - a string in the format `data:image/svg+xml,<svg>...</svg>`. You may need to use the `encodeURIComponent`
     *   function for the SVG content passed
     */
    var src: String?

    /**
     * The SVG icon wrapped in a Lit template literal.
     */
    var svg: dynamic

    /**
     * The symbol identifier that references an ID of an element contained in the SVG element assigned to the
     * `src` property
     */
    var symbol: String?
}

/**
 * Named exports of `@vaadin/icon/vaadin-icon.js`.
 *
 * Referencing the module registers the `<vaadin-icon>` custom element.
 */
@JsModule("@vaadin/icon/vaadin-icon.js")
private external object IconModule {
    /** The registered custom element class. */
    val Icon: dynamic
}

/**
 * Imports `@vaadin/icon/vaadin-icon.js`, registering the `<vaadin-icon>` custom element,
 * and returns the element class.
 */
fun registerIcon(): dynamic = IconModule.Icon

/**
 * Creates a `<vaadin-icon>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createIcon(configure: IconElement.() -> Unit = {}): IconElement {
    registerIcon()
    return document.createElement(VaadinTagName.icon).apply(configure)
}
