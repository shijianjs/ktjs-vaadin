package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of the component: mirrors Vaadin Flow's `MessageListItemVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface MessageListItemVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [MessageListItemVariants]. */
fun MessageListItemVariant(value: String): MessageListItemVariant = unsafeCast(value)

/** All variants of [MessageListItemVariant]. */
object MessageListItemVariants {
    inline val self: MessageListItemVariant get() = MessageListItemVariant("self")
    inline val fullWidth: MessageListItemVariant get() = MessageListItemVariant("full-width")
}
