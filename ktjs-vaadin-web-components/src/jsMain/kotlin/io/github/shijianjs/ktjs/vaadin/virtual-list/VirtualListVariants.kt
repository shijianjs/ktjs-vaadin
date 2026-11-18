package io.github.shijianjs.ktjs.vaadin.virtuallist

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
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
    val overflowIndicators: VirtualListVariant = VirtualListVariant("overflow-indicators")
    val overflowIndicatorTop: VirtualListVariant = VirtualListVariant("overflow-indicator-top")
    val overflowIndicatorBottom: VirtualListVariant = VirtualListVariant("overflow-indicator-bottom")
}
