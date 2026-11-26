package io.github.shijianjs.ktjs.vaadin.themablemixin

import io.github.shijianjs.ktjs.dom.attribute.TokenListAttribute
import io.github.shijianjs.ktjs.dom.attribute.tokenListAttribute
import io.github.shijianjs.ktjs.dom.attribute.types.TokenCodec
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
 * There is one storage, [themeAttr], built on the kit's [TokenListAttribute] with a
 * [ThemeVariantTokenCodec]; everything below is shorthand over it. Two vocabularies share the
 * attribute: a plain `String` for anything (custom tokens, third-party themes) and a typed
 * [ThemeVariant] from `io.github.shijianjs.ktjs.vaadin.theme` for the tokens Vaadin documents.
 * Tokens the codec cannot read do not exist here — `ThemeVariant` wraps *any* string — so typed and
 * raw access can never disagree about what the attribute holds.
 */
private const val THEME = "theme"

/** Every implementor is a Vaadin element: upstream applies the mixin to element classes only. */
private val ThemePropertyMixin.host: HTMLElement get() = this.unsafeCast<HTMLElement>()

/**
 * A variant *is* its token (`ThemeVariant` is an opaque string at runtime), so decoding accepts every
 * token and encoding just spells the variant: the typed view covers the whole attribute, custom and
 * third-party tokens included.
 */
private object ThemeVariantTokenCodec : TokenCodec<ThemeVariant> {

    override fun encode(value: ThemeVariant): String = value.name

    override fun decode(token: String): ThemeVariant = ThemeVariant(token)
}

/**
 * The `theme` attribute as a typed [TokenListAttribute]: the same space-separated list [themes]
 * reads, with `add` / `remove` / `toggle` / `contains` on the attribute itself.
 *
 * `values` hands back [ThemeVariant]s, `tokens` the raw strings — one attribute, two views, so a
 * generic shell writing theme tokens passes the same object to a helper that takes an
 * `Attribute`. It is declared on the mixin, not on `HTMLElement`, because only implementations
 * render the attribute.
 */
val ThemePropertyMixin.themeAttr: TokenListAttribute<ThemeVariant>
    get() = host.tokenListAttribute(THEME, ThemeVariantTokenCodec)

/** The variants currently set, in attribute order; empty when the attribute is absent. */
var ThemePropertyMixin.themes: List<String>
    get() = themeAttr.tokens
    set(value) {
        themeAttr.tokens = value
    }

/** Whether [variant] is part of the current theme. */
fun ThemePropertyMixin.hasTheme(variant: String): Boolean = themeAttr.containsToken(variant)

/** Whether a typed [variant] is part of the current theme. */
fun ThemePropertyMixin.hasTheme(variant: ThemeVariant): Boolean = themeAttr.contains(variant)

/** Replaces the whole theme with [variants]; calling it without variants removes the attribute. */
fun <T : ThemePropertyMixin> T.setTheme(vararg variants: String): T {
    themeAttr.tokens = variants.toList()
    return this
}

/**
 * Replaces the whole theme with the typed [variant] and [variants].
 *
 * Typed assignment replaces *the whole* list, so custom tokens that are not among the values are
 * dropped — use [addTheme] to keep them, or `themeAttr.tokens` to write raw tokens.
 */
fun <T : ThemePropertyMixin> T.setTheme(variant: ThemeVariant, vararg variants: ThemeVariant): T {
    themeAttr.value = listOf(variant, *variants)
    return this
}

/** Adds the missing of [variants] and keeps the existing ones; applying it twice changes nothing. */
fun <T : ThemePropertyMixin> T.addTheme(vararg variants: String): T {
    themeAttr.addTokens(*variants)
    return this
}

/** Adds the missing of the typed [variant] and [variants], keeping the existing tokens. */
fun <T : ThemePropertyMixin> T.addTheme(variant: ThemeVariant, vararg variants: ThemeVariant): T {
    themeAttr.add(variant, *variants)
    return this
}

/** Removes [variants]; variants that are not set are ignored. */
fun <T : ThemePropertyMixin> T.removeTheme(vararg variants: String): T {
    themeAttr.removeTokens(*variants)
    return this
}

/** Removes the typed [variant] and [variants]; tokens that are not set are ignored. */
fun <T : ThemePropertyMixin> T.removeTheme(variant: ThemeVariant, vararg variants: ThemeVariant): T {
    themeAttr.remove(variant, *variants)
    return this
}
