package vaadin.aura

/**
 * Aura is a CSS-only design system. There is no JavaScript API to import; include the stylesheet
 * in the application stylesheet pipeline (for example `@import "@vaadin/aura/aura.css";`) instead.
 *
 * Components pick the theme up through the inherited theme custom properties, so the same wrapper
 * API works with both Aura and Lumo.
 */
object Aura {
    /** Entry point of the Aura stylesheet. */
    const val CSS: String = "@vaadin/aura/aura.css"

    /** Pre-built stylesheet, when the bundler imports from `dist`. */
    const val DIST_CSS: String = "@vaadin/aura/dist/aura.css"
}
