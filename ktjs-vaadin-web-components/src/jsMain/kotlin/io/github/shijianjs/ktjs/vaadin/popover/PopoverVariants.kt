package io.github.shijianjs.ktjs.vaadin.popover

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of the component: mirrors Vaadin Flow's `PopoverVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface PopoverVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [PopoverVariants]. */
fun PopoverVariant(value: String): PopoverVariant = unsafeCast(value)

/** All variants of [PopoverVariant]. */
object PopoverVariants {
    val noPadding: PopoverVariant = PopoverVariant("no-padding")
    val arrow: PopoverVariant = PopoverVariant("arrow")
}
