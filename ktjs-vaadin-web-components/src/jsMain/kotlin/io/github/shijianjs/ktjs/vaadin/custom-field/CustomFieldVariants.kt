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
    val small: CustomFieldVariant = CustomFieldVariant("small")
    val helperAbove: CustomFieldVariant = CustomFieldVariant("helper-above-field")
    val labelAside: CustomFieldVariant = CustomFieldVariant("label-aside")
}
