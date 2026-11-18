package io.github.shijianjs.ktjs.vaadin.menubar

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-menu-bar`: mirrors Vaadin Flow's `MenuBarVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface MenuBarVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [MenuBarVariants]. */
fun MenuBarVariant(value: String): MenuBarVariant = unsafeCast(value)

/** All variants of [MenuBarVariant]. */
object MenuBarVariants {
    inline val small: MenuBarVariant get() = MenuBarVariant("small")
    inline val large: MenuBarVariant get() = MenuBarVariant("large")
    inline val tertiary: MenuBarVariant get() = MenuBarVariant("tertiary")
    inline val tertiaryInline: MenuBarVariant get() = MenuBarVariant("tertiary-inline")
    inline val primary: MenuBarVariant get() = MenuBarVariant("primary")
    inline val contrast: MenuBarVariant get() = MenuBarVariant("contrast")
    inline val icon: MenuBarVariant get() = MenuBarVariant("icon")
    inline val alignEnd: MenuBarVariant get() = MenuBarVariant("end-aligned")
    inline val dropdownIndicators: MenuBarVariant get() = MenuBarVariant("dropdown-indicators")
    inline val filled: MenuBarVariant get() = MenuBarVariant("filled")
    inline val success: MenuBarVariant get() = MenuBarVariant("success")
    inline val warning: MenuBarVariant get() = MenuBarVariant("warning")
    inline val error: MenuBarVariant get() = MenuBarVariant("error")
}
