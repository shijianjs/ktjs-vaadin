package vaadin.overlay

import web.html.HTMLElement

/** Renders the content of an overlay. */
typealias OverlayRenderer = (HTMLElement, HTMLElement, kotlin.js.JsAny?) -> Unit

/**
 * The bounds an overlay is positioned at.
 *
 * Each value is either a number of pixels or a CSS length string, so the properties are
 * `dynamic`.
 */
external interface OverlayBounds {
    var top: dynamic
    var left: dynamic
    var width: dynamic
    var height: dynamic
}

/** The bounds accepted by `setBounds`. */
typealias OverlayBoundsParam = OverlayBounds
