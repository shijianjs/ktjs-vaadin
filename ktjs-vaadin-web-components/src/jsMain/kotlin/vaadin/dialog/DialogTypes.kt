package vaadin.dialog

import web.html.HTMLElement

/** Renders the content of a dialog. */
typealias DialogRenderer = (HTMLElement, DialogElement) -> Unit

/** The size and position of the dialog overlay, in pixels. */
external interface DialogOverlayBounds {
    var top: Double
    var left: Double
    var width: Double
    var height: Double
}
