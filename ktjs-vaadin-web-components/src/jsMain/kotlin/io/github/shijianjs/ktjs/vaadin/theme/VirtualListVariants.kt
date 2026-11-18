package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-virtual-list`: mirrors Vaadin Flow's `VirtualListVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface VirtualListVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [VirtualListVariants]. */
fun VirtualListVariant(value: String): VirtualListVariant = unsafeCast(value)

/** All variants of [VirtualListVariant]. */
object VirtualListVariants {
    inline val overflowIndicators: VirtualListVariant get() = VirtualListVariant("overflow-indicators")
    inline val overflowIndicatorTop: VirtualListVariant get() = VirtualListVariant("overflow-indicator-top")
    inline val overflowIndicatorBottom: VirtualListVariant get() = VirtualListVariant("overflow-indicator-bottom")
}
