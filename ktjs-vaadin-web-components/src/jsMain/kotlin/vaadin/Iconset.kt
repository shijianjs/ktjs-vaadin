package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-iconset>` is a Web Component for creating SVG icon collections.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/icon)
 */
external class IconsetElement : HTMLElement {
    /**
     * The name of the iconset. Every iconset is required to have its own unique name.
     * All the SVG icons in the iconset must have IDs conforming to its name.
     *
     * See also [`name`](#/elements/vaadin-icon#property-name) property of `vaadin-icon`.
     */
    var name: String?

    /**
     * The size of an individual icon. Note that icons must be square.
     *
     * When using `vaadin-icon`, the size of the iconset will take precedence
     * over the size defined by the user to ensure correct appearance.
     */
    var size: Double?
}

/**
 * Static members of the `<vaadin-iconset>` class.
 */
private external interface IconsetStatics {
    /**
     * Set of the `vaadin-icon` instances in the DOM.
     */
    val attachedIcons: dynamic

    /**
     * Returns an instance of the iconset by its name.
     */
    fun getIconset(name: String): IconsetElement?

    /**
     * Returns SVGTemplateResult for the `icon` ID matching `name` of the
     * iconset, or `nothing` literal if there is no matching icon found.
     */
    fun getIconSvg(icon: String, name: String?)

    /**
     * Register an iconset without adding to the DOM.
     */
    fun register(name: String, size: Double, template: web.components.HTMLTemplateElement?)
}

/**
 * Named exports of `@vaadin/icon/vaadin-iconset.js`.
 *
 * Referencing the module registers the `<vaadin-iconset>` custom element.
 */
@JsModule("@vaadin/icon/vaadin-iconset.js")
private external object IconsetModule {
    /** The registered custom element class. */
    val Iconset: IconsetStatics
}

/**
 * Imports `@vaadin/icon/vaadin-iconset.js`, registering the `<vaadin-iconset>` custom element,
 * and returns the element class.
 */
fun registerIconset(): dynamic = IconsetModule.Iconset

/**
 * Static entry points of `<vaadin-iconset>`.
 *
 * Calling these members imports the upstream package and registers the element.
 */
object Iconset {
    /**
     * Set of the `vaadin-icon` instances in the DOM.
     */
    val attachedIcons: dynamic
        get() = IconsetModule.Iconset.attachedIcons

    /**
     * Returns an instance of the iconset by its name.
     */
    fun getIconset(name: String): IconsetElement? = IconsetModule.Iconset.getIconset(name)

    /**
     * Returns SVGTemplateResult for the `icon` ID matching `name` of the
     * iconset, or `nothing` literal if there is no matching icon found.
     */
    fun getIconSvg(icon: String, name: String?) = IconsetModule.Iconset.getIconSvg(icon, name)

    /**
     * Register an iconset without adding to the DOM.
     */
    fun register(name: String, size: Double, template: web.components.HTMLTemplateElement?) = IconsetModule.Iconset.register(name, size, template)
}

/**
 * Creates a `<vaadin-iconset>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createIconset(configure: IconsetElement.() -> Unit = {}): IconsetElement {
    registerIconset()
    return document.createElement(VaadinTagName.iconset).apply(configure)
}
