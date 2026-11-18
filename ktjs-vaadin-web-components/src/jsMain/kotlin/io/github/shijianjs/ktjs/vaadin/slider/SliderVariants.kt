package io.github.shijianjs.ktjs.vaadin.slider

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of the component: mirrors Vaadin Flow's `SliderVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface SliderVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [SliderVariants]. */
fun SliderVariant(value: String): SliderVariant = unsafeCast(value)

/** All variants of [SliderVariant]. */
object SliderVariants {
    inline val helperAbove: SliderVariant get() = SliderVariant("helper-above-field")
    inline val labelAside: SliderVariant get() = SliderVariant("label-aside")
}
