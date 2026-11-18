package io.github.shijianjs.ktjs.vaadin.customfield

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of the component: mirrors Vaadin Flow's `CustomFieldVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface CustomFieldVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [CustomFieldVariants]. */
fun CustomFieldVariant(value: String): CustomFieldVariant = unsafeCast(value)

/** All variants of [CustomFieldVariant]. */
object CustomFieldVariants {
    inline val small: CustomFieldVariant get() = CustomFieldVariant("small")
    inline val helperAbove: CustomFieldVariant get() = CustomFieldVariant("helper-above-field")
    inline val labelAside: CustomFieldVariant get() = CustomFieldVariant("label-aside")
}
