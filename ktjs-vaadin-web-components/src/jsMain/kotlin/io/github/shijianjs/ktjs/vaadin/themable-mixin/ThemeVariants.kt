package io.github.shijianjs.ktjs.vaadin.themablemixin

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import io.github.shijianjs.ktjs.vaadin.theme.name
import js.reflect.unsafeCast
import web.html.HTMLElement

/**
 * Reading and editing the `theme` attribute of a themable Vaadin component.
 *
 * Upstream `ThemePropertyMixin` only *observes* the attribute: there is no public `theme` property
 * to assign, the value is propagated into the component's shadow DOM as the read-only `_theme`
 * helper. That is why the CRUD operations are declared here — on the marker interface itself — and
 * not on `HTMLElement`: a plain element has no `theme`, only the components that mix the interface
 * in do (buttons, layouts, cards, fields, overlays…). The attribute is a space-separated list of
 * variants (`theme="primary small"`), and every function below keeps it normalised to single spaces.
 *
 * Two ways in: a plain `String` for anything (custom tokens, third-party themes) and a typed
 * [ThemeVariant] from `io.github.shijianjs.ktjs.vaadin.theme` for the tokens Vaadin documents. Both
 * end up as the same attribute text, so the store never needs to keep them apart.
 */
private const val THEME = "theme"

/** Every implementor is a Vaadin element: upstream applies the mixin to element classes only. */
private val ThemePropertyMixin.host: HTMLElement get() = this.unsafeCast<HTMLElement>()

/** The variants currently set, in attribute order; empty when the attribute is absent. */
var ThemePropertyMixin.themes: List<String>
    get() = host.getAttribute(THEME).orEmpty().split(' ').filter { it.isNotEmpty() }
    set(value) {
        val tokens = value.flatMap { it.split(' ') }.filter { it.isNotEmpty() }.distinct()
        if (tokens.isEmpty()) {
            host.removeAttribute(THEME)
        } else {
            host.setAttribute(THEME, tokens.joinToString(" "))
        }
    }

/** Whether [variant] is part of the current theme. */
fun ThemePropertyMixin.hasTheme(variant: String): Boolean = variant in themes

/** Whether a typed [variant] is part of the current theme. */
fun ThemePropertyMixin.hasTheme(variant: ThemeVariant): Boolean = variant.name in themes

/** Replaces the whole theme with [variants]; calling it without variants removes the attribute. */
fun <T : ThemePropertyMixin> T.setTheme(vararg variants: String): T {
    themes = variants.toList()
    return this
}

/** Replaces the whole theme with the typed [variant] and [variants]. */
fun <T : ThemePropertyMixin> T.setTheme(variant: ThemeVariant, vararg variants: ThemeVariant): T {
    themes = arrayOf(variant, *variants).map { it.name }
    return this
}

/** Adds the missing of [variants] and keeps the existing ones; applying it twice changes nothing. */
fun <T : ThemePropertyMixin> T.addTheme(vararg variants: String): T {
    val current = themes
    val missing = variants.flatMap { it.split(' ') }.filter { it.isNotEmpty() && it !in current }
    if (missing.isNotEmpty()) themes = current + missing
    return this
}

/** Adds the missing of the typed [variant] and [variants], keeping the existing tokens. */
fun <T : ThemePropertyMixin> T.addTheme(variant: ThemeVariant, vararg variants: ThemeVariant): T {
    val current = themes
    val missing = arrayOf(variant, *variants).map { it.name }.filter { it !in current }
    if (missing.isNotEmpty()) themes = current + missing
    return this
}

/** Removes [variants]; variants that are not set are ignored. */
fun <T : ThemePropertyMixin> T.removeTheme(vararg variants: String): T {
    val current = themes
    val removed = variants.flatMap { it.split(' ') }.filter { it.isNotEmpty() }
    val kept = current.filterNot { it in removed }
    if (kept.size != current.size) themes = kept
    return this
}

/** Removes the typed [variant] and [variants]; tokens that are not set are ignored. */
fun <T : ThemePropertyMixin> T.removeTheme(variant: ThemeVariant, vararg variants: ThemeVariant): T {
    val removed = arrayOf(variant, *variants).map { it.name }
    return removeTheme(*removed.toTypedArray())
}
