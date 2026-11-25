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
    val small: MenuBarVariant = MenuBarVariant("small")
    val large: MenuBarVariant = MenuBarVariant("large")
    val tertiary: MenuBarVariant = MenuBarVariant("tertiary")
    val tertiaryInline: MenuBarVariant = MenuBarVariant("tertiary-inline")
    val primary: MenuBarVariant = MenuBarVariant("primary")
    val contrast: MenuBarVariant = MenuBarVariant("contrast")
    val icon: MenuBarVariant = MenuBarVariant("icon")
    val alignEnd: MenuBarVariant = MenuBarVariant("end-aligned")
    val dropdownIndicators: MenuBarVariant = MenuBarVariant("dropdown-indicators")
    val filled: MenuBarVariant = MenuBarVariant("filled")
    val success: MenuBarVariant = MenuBarVariant("success")
    val warning: MenuBarVariant = MenuBarVariant("warning")
    val error: MenuBarVariant = MenuBarVariant("error")
}
