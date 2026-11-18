package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-tabsheet`: mirrors Vaadin Flow's `TabSheetVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface TabSheetVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [TabSheetVariants]. */
fun TabSheetVariant(value: String): TabSheetVariant = unsafeCast(value)

/** All variants of [TabSheetVariant]. */
object TabSheetVariants {
    inline val tabsCentered: TabSheetVariant get() = TabSheetVariant("centered")
    inline val tabsSmall: TabSheetVariant get() = TabSheetVariant("small")
    inline val tabsMinimal: TabSheetVariant get() = TabSheetVariant("minimal")
    inline val tabsHideScrollButtons: TabSheetVariant get() = TabSheetVariant("hide-scroll-buttons")
    inline val tabsEqualWidthTabs: TabSheetVariant get() = TabSheetVariant("equal-width-tabs")
    inline val bordered: TabSheetVariant get() = TabSheetVariant("bordered")
    inline val noPadding: TabSheetVariant get() = TabSheetVariant("no-padding")
    inline val tabsShowScrollButtons: TabSheetVariant get() = TabSheetVariant("show-scroll-buttons")
    inline val noBorder: TabSheetVariant get() = TabSheetVariant("no-border")
    inline val tabsFilled: TabSheetVariant get() = TabSheetVariant("filled")
}
