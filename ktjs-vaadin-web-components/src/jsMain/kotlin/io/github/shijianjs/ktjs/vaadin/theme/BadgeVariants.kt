package io.github.shijianjs.ktjs.vaadin.theme

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
    inline val contrast: BadgeVariant get() = BadgeVariant("contrast")
    inline val dot: BadgeVariant get() = BadgeVariant("dot")
    inline val error: BadgeVariant get() = BadgeVariant("error")
    inline val filled: BadgeVariant get() = BadgeVariant("filled")
    inline val iconOnly: BadgeVariant get() = BadgeVariant("icon-only")
    inline val numberOnly: BadgeVariant get() = BadgeVariant("number-only")
    inline val success: BadgeVariant get() = BadgeVariant("success")
    inline val warning: BadgeVariant get() = BadgeVariant("warning")
    inline val small: BadgeVariant get() = BadgeVariant("small")
}
