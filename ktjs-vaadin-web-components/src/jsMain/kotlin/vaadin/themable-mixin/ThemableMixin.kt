package vaadin.themablemixin

/**
 * The result of a Lit `css` tagged template, exposed by `unsafeCSS` and the built-in Vaadin
 * theme modules.
 */
external interface CssResult {
    /** The CSS text of the stylesheet. */
    val cssText: String
}

/**
 * Adds the protected `_theme` helper property used to propagate the host `theme` attribute into
 * shadow DOM. Applied by every themable Vaadin component.
 *
 * Registered styles are installed through [registerStyles]; the `theme` attribute itself is read
 * with the regular DOM attribute API (`getAttribute("theme")` / `setAttribute("theme", ...)`).
 */
external interface ThemePropertyMixin

/**
 * Allows a component to register theme styles for its tag name. See [registerStyles].
 */
external interface ThemableMixin : ThemePropertyMixin

/**
 * Injects Lumo styles into the component based on the CSS custom properties used by the
 * application. Internal to the Vaadin theme infrastructure.
 */
external interface LumoInjectionMixin

/**
 * Detects whether Lumo or Aura is applied to the application and mirrors the result into the
 * `data-application-theme` attribute of the host element.
 */
external interface ThemeDetectionMixin
