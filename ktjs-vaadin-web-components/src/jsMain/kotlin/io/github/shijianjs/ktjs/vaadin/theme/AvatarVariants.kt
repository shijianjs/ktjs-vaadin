package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-avatar`: mirrors Vaadin Flow's `AvatarVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface AvatarVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [AvatarVariants]. */
fun AvatarVariant(value: String): AvatarVariant = unsafeCast(value)

/** All variants of [AvatarVariant]. */
object AvatarVariants {
    inline val xlarge: AvatarVariant get() = AvatarVariant("xlarge")
    inline val large: AvatarVariant get() = AvatarVariant("large")
    inline val small: AvatarVariant get() = AvatarVariant("small")
    inline val xsmall: AvatarVariant get() = AvatarVariant("xsmall")
    inline val filled: AvatarVariant get() = AvatarVariant("filled")
}
