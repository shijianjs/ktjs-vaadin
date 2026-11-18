package io.github.shijianjs.ktjs.vaadin.tabsheet

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
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
    val tabsCentered: TabSheetVariant = TabSheetVariant("centered")
    val tabsSmall: TabSheetVariant = TabSheetVariant("small")
    val tabsMinimal: TabSheetVariant = TabSheetVariant("minimal")
    val tabsHideScrollButtons: TabSheetVariant = TabSheetVariant("hide-scroll-buttons")
    val tabsEqualWidthTabs: TabSheetVariant = TabSheetVariant("equal-width-tabs")
    val bordered: TabSheetVariant = TabSheetVariant("bordered")
    val noPadding: TabSheetVariant = TabSheetVariant("no-padding")
    val tabsShowScrollButtons: TabSheetVariant = TabSheetVariant("show-scroll-buttons")
    val noBorder: TabSheetVariant = TabSheetVariant("no-border")
    val tabsFilled: TabSheetVariant = TabSheetVariant("filled")
}
