package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Base definition of a typed theme variant, mirroring Vaadin Flow's `ThemeVariant`.
 *
 * At runtime a variant *is* its attribute token — a plain string — so the type only exists in Kotlin
 * and in the docs. Every component declares its own subtype (`ButtonVariant`, `BadgeVariant`, …) and
 * a [constants object][ButtonVariants] next to it, which is what makes a variant list read as
 * [addTheme(ButtonVariants.primary)][io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme]
 * instead of a bare `"primary"`.
 *
 * ```kotlin
 * button.addTheme(ButtonVariants.primary, ButtonVariants.small)
 * button.hasTheme(ButtonVariants.tertiaryInline)
 * ```
 *
 * Variants of these Flow classes are **not** generated here, because this module does not wrap the
 * corresponding component:
 * - `ChartVariant` — no `vaadin-charts` wrapper
 * - `CrudVariant` — no `vaadin-crud` wrapper
 * - `DashboardVariant` — no `vaadin-dashboard` wrapper
 * - `GridProVariant` — no `vaadin-grid-pro` wrapper
 * - `MapVariant` — no `vaadin-map` wrapper
 * - `RichTextEditorVariant` — no `vaadin-rich-text-editor` wrapper
 */
sealed external interface ThemeVariant

/** Wraps a variant token from JavaScript. Prefer a constants object, e.g. [ButtonVariants]. */
fun ThemeVariant(value: String): ThemeVariant = unsafeCast(value)

/** The attribute token of this variant, e.g. `primary` or `tertiary-inline`. */
val ThemeVariant.name: String get() = unsafeCast<String>()
