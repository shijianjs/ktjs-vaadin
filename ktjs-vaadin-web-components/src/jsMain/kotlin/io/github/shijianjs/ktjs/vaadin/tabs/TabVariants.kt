package io.github.shijianjs.ktjs.vaadin.tabs

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-tab`: mirrors Vaadin Flow's `TabVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface TabVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [TabVariants]. */
fun TabVariant(value: String): TabVariant = unsafeCast(value)

/** All variants of [TabVariant]. */
object TabVariants {
    inline val iconOnTop: TabVariant get() = TabVariant("icon-on-top")
}
