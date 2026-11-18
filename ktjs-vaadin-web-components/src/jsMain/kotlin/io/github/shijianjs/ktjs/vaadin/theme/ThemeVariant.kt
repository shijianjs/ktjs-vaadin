package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Base definition of a typed theme variant, mirroring Vaadin Flow's `ThemeVariant`.
 *
 * At runtime a variant *is* its attribute token — a plain string — so the type only exists in Kotlin
 * and in the docs. Every component declares its own subtype (`ButtonVariant`, `BadgeVariant`, …) in
 * its own package, next to the element it applies to, plus a constants object next to it, which is
 * what makes a variant list read as
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
 *
 * Not `sealed`: a subtype has to live in the same module *and package* as a sealed parent, and each
 * component keeps its variants in its own package (`…vaadin.button.ButtonVariant`). Closure comes from
 * the constants objects instead — a component's vocabulary is the one its `XxxVariants` lists, and
 * anything else is a custom token, which the attribute has always allowed.
 */
external interface ThemeVariant

/** Wraps a variant token from JavaScript. Prefer a constants object, e.g.
 * [io.github.shijianjs.ktjs.vaadin.button.ButtonVariants]. */
fun ThemeVariant(value: String): ThemeVariant = unsafeCast(value)

/** The attribute token of this variant, e.g. `primary` or `tertiary-inline`. */
val ThemeVariant.name: String get() = unsafeCast<String>()
