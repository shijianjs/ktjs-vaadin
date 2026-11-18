package io.github.shijianjs.ktjs.vaadin.breadcrumbs

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-breadcrumbs`: mirrors Vaadin Flow's `BreadcrumbsVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface BreadcrumbsVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [BreadcrumbsVariants]. */
fun BreadcrumbsVariant(value: String): BreadcrumbsVariant = unsafeCast(value)

/** All variants of [BreadcrumbsVariant]. */
object BreadcrumbsVariants {
    inline val slash: BreadcrumbsVariant get() = BreadcrumbsVariant("slash")
    inline val primary: BreadcrumbsVariant get() = BreadcrumbsVariant("primary")
    inline val accent: BreadcrumbsVariant get() = BreadcrumbsVariant("accent")
}
