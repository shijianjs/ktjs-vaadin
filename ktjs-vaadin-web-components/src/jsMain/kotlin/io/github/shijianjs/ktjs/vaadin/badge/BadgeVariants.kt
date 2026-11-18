package io.github.shijianjs.ktjs.vaadin.badge

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-badge`: mirrors Vaadin Flow's `BadgeVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface BadgeVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [BadgeVariants]. */
fun BadgeVariant(value: String): BadgeVariant = unsafeCast(value)

/** All variants of [BadgeVariant]. */
object BadgeVariants {
    val contrast: BadgeVariant = BadgeVariant("contrast")
    val dot: BadgeVariant = BadgeVariant("dot")
    val error: BadgeVariant = BadgeVariant("error")
    val filled: BadgeVariant = BadgeVariant("filled")
    val iconOnly: BadgeVariant = BadgeVariant("icon-only")
    val numberOnly: BadgeVariant = BadgeVariant("number-only")
    val success: BadgeVariant = BadgeVariant("success")
    val warning: BadgeVariant = BadgeVariant("warning")
    val small: BadgeVariant = BadgeVariant("small")
}
