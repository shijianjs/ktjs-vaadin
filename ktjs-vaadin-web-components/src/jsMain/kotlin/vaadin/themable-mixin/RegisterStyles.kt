@file:JsModule("@vaadin/vaadin-themable-mixin/register-styles.js")

package vaadin.themablemixin

/** Options accepted by [registerStyles]. */
external interface RegisterStylesOptions {
    /** An identifier that lets other registrations include these styles. */
    var moduleId: String?

    /** A `moduleId` or an array of them whose styles are included in this registration. */
    var include: dynamic
}

/**
 * Registers CSS styles for a component type. Register the styles before the first instance of the
 * component is attached to the DOM.
 *
 * @param themeFor the tag name the styles apply to, or `null` for global styles.
 * @param styles the CSS rules to register.
 * @param options registration options, for example `{ moduleId: "my-theme" }`.
 */
external fun registerStyles(
    themeFor: String?,
    styles: CssResult,
    options: RegisterStylesOptions = definedExternally,
)

/**
 * Registers several CSS results for a component type. Register the styles before the first
 * instance of the component is attached to the DOM.
 *
 * @param themeFor the tag name the styles apply to, or `null` for global styles.
 * @param styles the CSS rules to register.
 * @param options registration options, for example `{ moduleId: "my-theme" }`.
 */
external fun registerStyles(
    themeFor: String?,
    styles: Array<CssResult>,
    options: RegisterStylesOptions = definedExternally,
)

/**
 * Wraps a plain CSS string in a Lit `CSSResult` so it can be passed to [registerStyles] or
 * assigned to `adoptedStyleSheets`.
 *
 * Prefer a real `css` tagged template in JavaScript when possible; Kotlin cannot express tagged
 * template literals, so this is the supported entry point for CSS defined in Kotlin.
 */
external fun unsafeCSS(cssText: String): CssResult
