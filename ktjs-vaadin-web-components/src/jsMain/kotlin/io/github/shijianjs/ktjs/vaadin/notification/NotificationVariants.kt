package io.github.shijianjs.ktjs.vaadin.notification

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
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
    val primary: NotificationVariant = NotificationVariant("primary")
    val contrast: NotificationVariant = NotificationVariant("contrast")
    val success: NotificationVariant = NotificationVariant("success")
    val error: NotificationVariant = NotificationVariant("error")
    val warning: NotificationVariant = NotificationVariant("warning")
    val info: NotificationVariant = NotificationVariant("info")
}
