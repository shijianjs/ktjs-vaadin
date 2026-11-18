package io.github.shijianjs.ktjs.vaadin.tabs

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-tabs`: mirrors Vaadin Flow's `TabsVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface TabsVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [TabsVariants]. */
fun TabsVariant(value: String): TabsVariant = unsafeCast(value)

/** All variants of [TabsVariant]. */
object TabsVariants {
    val centered: TabsVariant = TabsVariant("centered")
    val small: TabsVariant = TabsVariant("small")
    val minimal: TabsVariant = TabsVariant("minimal")
    val hideScrollButtons: TabsVariant = TabsVariant("hide-scroll-buttons")
    val equalWidthTabs: TabsVariant = TabsVariant("equal-width-tabs")
    val showScrollButtons: TabsVariant = TabsVariant("show-scroll-buttons")
    val filled: TabsVariant = TabsVariant("filled")
}
