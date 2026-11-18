package io.github.shijianjs.ktjs.vaadin.messagelist

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-message-list`: mirrors Vaadin Flow's `MessageListVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface MessageListVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [MessageListVariants]. */
fun MessageListVariant(value: String): MessageListVariant = unsafeCast(value)

/** All variants of [MessageListVariant]. */
object MessageListVariants {
    inline val bubble: MessageListVariant get() = MessageListVariant("bubble")
    inline val oneToOne: MessageListVariant get() = MessageListVariant("one-to-one")
}
