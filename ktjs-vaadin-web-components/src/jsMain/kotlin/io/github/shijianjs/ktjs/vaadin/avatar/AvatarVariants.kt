package io.github.shijianjs.ktjs.vaadin.avatar

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
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
    val xlarge: AvatarVariant = AvatarVariant("xlarge")
    val large: AvatarVariant = AvatarVariant("large")
    val small: AvatarVariant = AvatarVariant("small")
    val xsmall: AvatarVariant = AvatarVariant("xsmall")
    val filled: AvatarVariant = AvatarVariant("filled")
}
