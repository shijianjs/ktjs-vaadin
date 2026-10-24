package vaadin.overlay

import js.reflect.unsafeCast
import web.html.HTMLElement

/** Renders the content of an overlay. */
typealias OverlayRenderer = (HTMLElement, HTMLElement, kotlin.js.JsAny?) -> Unit

/** Union of a pixel count and a CSS length string, used by [OverlayBounds]. */
external interface DoubleOrString

/** Wraps a pixel count as a [DoubleOrString]. */
fun DoubleOrString(value: Double): DoubleOrString = unsafeCast(value)

/** Wraps a CSS length as a [DoubleOrString]. */
fun DoubleOrString(value: String): DoubleOrString = unsafeCast(value)

/**
 * The bounds an overlay is positioned at.
 *
 * Each value is either a number of pixels or a CSS length string; wrap it with the
 * [DoubleOrString] factory functions.
 */
external interface OverlayBounds {
    var top: DoubleOrString?
    var left: DoubleOrString?
    var width: DoubleOrString?
    var height: DoubleOrString?
}

/** The bounds accepted by `setBounds`. */
typealias OverlayBoundsParam = OverlayBounds
