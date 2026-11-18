package io.github.shijianjs.ktjs.vaadin.avatargroup

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-avatar-group`: mirrors Vaadin Flow's `AvatarGroupVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface AvatarGroupVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [AvatarGroupVariants]. */
fun AvatarGroupVariant(value: String): AvatarGroupVariant = unsafeCast(value)

/** All variants of [AvatarGroupVariant]. */
object AvatarGroupVariants {
    inline val xlarge: AvatarGroupVariant get() = AvatarGroupVariant("xlarge")
    inline val large: AvatarGroupVariant get() = AvatarGroupVariant("large")
    inline val small: AvatarGroupVariant get() = AvatarGroupVariant("small")
    inline val xsmall: AvatarGroupVariant get() = AvatarGroupVariant("xsmall")
    inline val reverse: AvatarGroupVariant get() = AvatarGroupVariant("reverse")
    inline val filled: AvatarGroupVariant get() = AvatarGroupVariant("filled")
}
