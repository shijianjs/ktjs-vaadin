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
    inline val centered: TabsVariant get() = TabsVariant("centered")
    inline val small: TabsVariant get() = TabsVariant("small")
    inline val minimal: TabsVariant get() = TabsVariant("minimal")
    inline val hideScrollButtons: TabsVariant get() = TabsVariant("hide-scroll-buttons")
    inline val equalWidthTabs: TabsVariant get() = TabsVariant("equal-width-tabs")
    inline val showScrollButtons: TabsVariant get() = TabsVariant("show-scroll-buttons")
    inline val filled: TabsVariant get() = TabsVariant("filled")
}
