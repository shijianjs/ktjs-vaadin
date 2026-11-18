package io.github.shijianjs.ktjs.vaadin.messageinput

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-message-input`: mirrors Vaadin Flow's `MessageInputVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface MessageInputVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [MessageInputVariants]. */
fun MessageInputVariant(value: String): MessageInputVariant = unsafeCast(value)

/** All variants of [MessageInputVariant]. */
object MessageInputVariants {
    val iconButton: MessageInputVariant = MessageInputVariant("icon-button")
}
