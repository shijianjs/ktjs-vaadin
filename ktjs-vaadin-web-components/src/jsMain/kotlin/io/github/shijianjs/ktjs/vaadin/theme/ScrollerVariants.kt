package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-scroller`: mirrors Vaadin Flow's `ScrollerVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface ScrollerVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [ScrollerVariants]. */
fun ScrollerVariant(value: String): ScrollerVariant = unsafeCast(value)

/** All variants of [ScrollerVariant]. */
object ScrollerVariants {
    inline val overflowIndicators: ScrollerVariant get() = ScrollerVariant("overflow-indicators")
    inline val overflowIndicatorTop: ScrollerVariant get() = ScrollerVariant("overflow-indicator-top")
    inline val overflowIndicatorBottom: ScrollerVariant get() = ScrollerVariant("overflow-indicator-bottom")
}
