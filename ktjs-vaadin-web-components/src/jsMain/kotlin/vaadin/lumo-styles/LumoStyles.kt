package vaadin.lumostyles

import vaadin.themablemixin.CssResult

/**
 * Lumo theme styles exposed as Lit `CSSResult` values.
 *
 * The values can be passed to `registerStyles` or inserted into `adoptedStyleSheets`. For a whole
 * application, importing the `lumo.css` stylesheet is the supported approach; see [LumoThemes].
 */
object LumoStyles {
    /** Styles for the suffix/prefix buttons of input fields. */
    val fieldButton: CssResult
        get() = FieldButtonModule.fieldButton

    val helper: CssResult
        get() = HelperModule.helper

    val inputField: CssResult
        get() = InputFieldModule.inputField

    val inputFieldShared: CssResult
        get() = InputFieldModule.inputFieldShared

    val loader: CssResult
        get() = LoaderModule.loader

    val menuOverlay: Array<CssResult>
        get() = MenuOverlayModule.menuOverlay

    val menuOverlayCore: CssResult
        get() = MenuOverlayModule.menuOverlayCore

    val overlay: CssResult
        get() = OverlayModule.overlay

    val requiredField: CssResult
        get() = RequiredFieldModule.requiredField
}

/**
 * Imports `@vaadin/vaadin-lumo-styles/all-imports.js`, which registers the legacy Lumo style
 * mixins (field button, menu overlay, overlay, required field) and the `lumo` icon set.
 *
 * Prefer the CSS entry points in [LumoThemes] for new applications; the JS mixins are deprecated
 * upstream and scheduled for removal in Vaadin 26.
 */
fun registerLumoStyles(): kotlin.js.JsAny = LumoAllImportsModule

/**
 * Imports `@vaadin/vaadin-lumo-styles/icons.js`: registers the `lumo:` icon names used by the
 * theme and imports the `<vaadin-icon>` element they rely on.
 */
fun registerLumoIcons(): kotlin.js.JsAny = LumoIconsetModule

/** Stylesheet entry points of the Lumo theme. */
object LumoThemes {
    /** Main Lumo stylesheet. */
    const val CSS: String = "@vaadin/vaadin-lumo-styles/lumo.css"

    /** Utility classes such as spacing and typography helpers. */
    const val UTILITY_CSS: String = "@vaadin/vaadin-lumo-styles/utility.css"

    /** Compact-size preset. */
    const val COMPACT_CSS: String = "@vaadin/vaadin-lumo-styles/presets/compact.css"

    /** Pre-built main stylesheet, when the bundler imports from `dist`. */
    const val DIST_CSS: String = "@vaadin/vaadin-lumo-styles/dist/lumo.css"

    /** Pre-built compact preset, when the bundler imports from `dist`. */
    const val DIST_COMPACT_CSS: String = "@vaadin/vaadin-lumo-styles/dist/presets/compact.css"
}

@JsModule("@vaadin/vaadin-lumo-styles/all-imports.js")
private external object LumoAllImportsModule {
    val overlay: CssResult
    val fieldButton: CssResult
    val menuOverlay: Array<CssResult>
    val requiredField: CssResult
}

@JsModule("@vaadin/vaadin-lumo-styles/icons.js")
private external object LumoIconsetModule

@JsModule("@vaadin/vaadin-lumo-styles/mixins/field-button.js")
private external object FieldButtonModule {
    val fieldButton: CssResult
}

@JsModule("@vaadin/vaadin-lumo-styles/mixins/helper.js")
private external object HelperModule {
    val helper: CssResult
}

@JsModule("@vaadin/vaadin-lumo-styles/mixins/input-field-shared.js")
private external object InputFieldModule {
    val inputField: CssResult
    val inputFieldShared: CssResult
}

@JsModule("@vaadin/vaadin-lumo-styles/mixins/loader.js")
private external object LoaderModule {
    val loader: CssResult
}

@JsModule("@vaadin/vaadin-lumo-styles/mixins/menu-overlay.js")
private external object MenuOverlayModule {
    val menuOverlay: Array<CssResult>
    val menuOverlayCore: CssResult
}

@JsModule("@vaadin/vaadin-lumo-styles/mixins/overlay.js")
private external object OverlayModule {
    val overlay: CssResult
}

@JsModule("@vaadin/vaadin-lumo-styles/mixins/required-field.js")
private external object RequiredFieldModule {
    val requiredField: CssResult
}
