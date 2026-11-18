package io.github.shijianjs.ktjs.vaadin.sidenav

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-side-nav`: mirrors Vaadin Flow's `SideNavVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface SideNavVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [SideNavVariants]. */
fun SideNavVariant(value: String): SideNavVariant = unsafeCast(value)

/** All variants of [SideNavVariant]. */
object SideNavVariants {
    inline val filled: SideNavVariant get() = SideNavVariant("filled")
}
