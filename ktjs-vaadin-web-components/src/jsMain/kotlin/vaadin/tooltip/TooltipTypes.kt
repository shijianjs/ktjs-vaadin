package vaadin.tooltip

import js.reflect.unsafeCast

/**
 * Union of:
 * - `web.html.HTMLElement`
 * - `Array<web.html.HTMLElement>`
 */
external interface HTMLElementOrHTMLElementArray

fun HTMLElementOrHTMLElementArray(value: web.html.HTMLElement): HTMLElementOrHTMLElementArray =
    unsafeCast(value)

fun HTMLElementOrHTMLElementArray(value: Array<web.html.HTMLElement>): HTMLElementOrHTMLElementArray =
    unsafeCast(value)
