package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-notification`: mirrors Vaadin Flow's `NotificationVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface NotificationVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [NotificationVariants]. */
fun NotificationVariant(value: String): NotificationVariant = unsafeCast(value)

/** All variants of [NotificationVariant]. */
object NotificationVariants {
    inline val primary: NotificationVariant get() = NotificationVariant("primary")
    inline val contrast: NotificationVariant get() = NotificationVariant("contrast")
    inline val success: NotificationVariant get() = NotificationVariant("success")
    inline val error: NotificationVariant get() = NotificationVariant("error")
    inline val warning: NotificationVariant get() = NotificationVariant("warning")
    inline val info: NotificationVariant get() = NotificationVariant("info")
}
