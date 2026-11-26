package io.github.shijianjs.ktjs.vaadin.lumostyles

import web.cssom.Background
import web.cssom.BorderStyle
import web.cssom.BoxShadow
import web.cssom.Color
import web.cssom.Content
import web.cssom.Cursor
import web.cssom.CustomPropertyName
import web.cssom.Display
import web.cssom.FontFamily
import web.cssom.Length
import web.cssom.LineHeight
import web.cssom.NumberType
import web.cssom.TransitionDuration
import web.cssom.TransitionTimingFunction

/**
 * Every custom property the Lumo theme defines (`@vaadin/vaadin-lumo-styles`), as typed
 * [custom property names][CustomPropertyName].
 *
 * The names are ordinary strings to CSSOM: `var(--lumo-primary-color)` and
 * `"var(--lumo-primary-color)"` are the same value, but only the former is checked by the
 * compiler and by the IDE. Pair each name with the `var()` function from `web.cssom`:
 *
 * ```kotlin
 * style.color = var(LumoCustomProperties.PRIMARY_TEXT_COLOR)
 * style.padding = var(LumoCustomProperties.SPACE_L)
 * ```
 *
 * The declared type is the one Lumo uses the variable for, so the value can be handed to the
 * CSSOM property it belongs to: colors to `color`/`background-color`, the spacing/size/radius
 * family to [Length] (which `padding`, `gap`, `font-size`, `height` all accept), shadows
 * to `box-shadow`, the icon codepoints to `content`. A property that Lumo only ever uses as
 * shorthand input (`--lumo-space-tall-*`, `--lumo-space-wide-*`) is still typed as
 * [Length] because that is the value type every consumer of it accepts.
 *
 * Names follow the upstream file one to one; a Lumo release that adds or removes one has to
 * update this file (`scripts/` has the generator note in its header).
 */
object LumoCustomProperties {
    /** `--lumo-base-color`. */
    val BASE_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-base-color")
    /** `--lumo-body-text-color`. */
    val BODY_TEXT_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-body-text-color")
    /** `--lumo-border-radius`. */
    val BORDER_RADIUS: CustomPropertyName<Length> = CustomPropertyName("--lumo-border-radius")
    /** `--lumo-border-radius-l`. */
    val BORDER_RADIUS_L: CustomPropertyName<Length> = CustomPropertyName("--lumo-border-radius-l")
    /** `--lumo-border-radius-m`. */
    val BORDER_RADIUS_M: CustomPropertyName<Length> = CustomPropertyName("--lumo-border-radius-m")
    /** `--lumo-border-radius-s`. */
    val BORDER_RADIUS_S: CustomPropertyName<Length> = CustomPropertyName("--lumo-border-radius-s")
    /** `--lumo-box-shadow-l`. */
    val BOX_SHADOW_L: CustomPropertyName<BoxShadow> = CustomPropertyName("--lumo-box-shadow-l")
    /** `--lumo-box-shadow-m`. */
    val BOX_SHADOW_M: CustomPropertyName<BoxShadow> = CustomPropertyName("--lumo-box-shadow-m")
    /** `--lumo-box-shadow-s`. */
    val BOX_SHADOW_S: CustomPropertyName<BoxShadow> = CustomPropertyName("--lumo-box-shadow-s")
    /** `--lumo-box-shadow-xl`. */
    val BOX_SHADOW_XL: CustomPropertyName<BoxShadow> = CustomPropertyName("--lumo-box-shadow-xl")
    /** `--lumo-box-shadow-xs`. */
    val BOX_SHADOW_XS: CustomPropertyName<BoxShadow> = CustomPropertyName("--lumo-box-shadow-xs")
    /** `--lumo-button-size`. */
    val BUTTON_SIZE: CustomPropertyName<Length> = CustomPropertyName("--lumo-button-size")
    /** `--lumo-clickable-cursor`. */
    val CLICKABLE_CURSOR: CustomPropertyName<Cursor> = CustomPropertyName("--lumo-clickable-cursor")
    /** `--lumo-contrast`. */
    val CONTRAST: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast")
    /** `--lumo-contrast-10pct`. */
    val CONTRAST_10PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast-10pct")
    /** `--lumo-contrast-20pct`. */
    val CONTRAST_20PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast-20pct")
    /** `--lumo-contrast-30pct`. */
    val CONTRAST_30PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast-30pct")
    /** `--lumo-contrast-40pct`. */
    val CONTRAST_40PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast-40pct")
    /** `--lumo-contrast-50pct`. */
    val CONTRAST_50PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast-50pct")
    /** `--lumo-contrast-5pct`. */
    val CONTRAST_5PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast-5pct")
    /** `--lumo-contrast-60pct`. */
    val CONTRAST_60PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast-60pct")
    /** `--lumo-contrast-70pct`. */
    val CONTRAST_70PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast-70pct")
    /** `--lumo-contrast-80pct`. */
    val CONTRAST_80PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast-80pct")
    /** `--lumo-contrast-90pct`. */
    val CONTRAST_90PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-contrast-90pct")
    /** `--lumo-disabled-text-color`. */
    val DISABLED_TEXT_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-disabled-text-color")
    /** `--lumo-error-color`. */
    val ERROR_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-error-color")
    /** `--lumo-error-color-10pct`. */
    val ERROR_COLOR_10PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-error-color-10pct")
    /** `--lumo-error-color-50pct`. */
    val ERROR_COLOR_50PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-error-color-50pct")
    /** `--lumo-error-contrast-color`. */
    val ERROR_CONTRAST_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-error-contrast-color")
    /** `--lumo-error-text-color`. */
    val ERROR_TEXT_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-error-text-color")
    /** `--lumo-font-family`. */
    val FONT_FAMILY: CustomPropertyName<FontFamily> = CustomPropertyName("--lumo-font-family")
    /** `--lumo-font-size`. */
    val FONT_SIZE: CustomPropertyName<Length> = CustomPropertyName("--lumo-font-size")
    /** `--lumo-font-size-l`. */
    val FONT_SIZE_L: CustomPropertyName<Length> = CustomPropertyName("--lumo-font-size-l")
    /** `--lumo-font-size-m`. */
    val FONT_SIZE_M: CustomPropertyName<Length> = CustomPropertyName("--lumo-font-size-m")
    /** `--lumo-font-size-s`. */
    val FONT_SIZE_S: CustomPropertyName<Length> = CustomPropertyName("--lumo-font-size-s")
    /** `--lumo-font-size-xl`. */
    val FONT_SIZE_XL: CustomPropertyName<Length> = CustomPropertyName("--lumo-font-size-xl")
    /** `--lumo-font-size-xs`. */
    val FONT_SIZE_XS: CustomPropertyName<Length> = CustomPropertyName("--lumo-font-size-xs")
    /** `--lumo-font-size-xxl`. */
    val FONT_SIZE_XXL: CustomPropertyName<Length> = CustomPropertyName("--lumo-font-size-xxl")
    /** `--lumo-font-size-xxs`. */
    val FONT_SIZE_XXS: CustomPropertyName<Length> = CustomPropertyName("--lumo-font-size-xxs")
    /** `--lumo-font-size-xxxl`. */
    val FONT_SIZE_XXXL: CustomPropertyName<Length> = CustomPropertyName("--lumo-font-size-xxxl")
    /** `--lumo-header-text-color`. */
    val HEADER_TEXT_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-header-text-color")
    /** `--lumo-icon-size`. */
    val ICON_SIZE: CustomPropertyName<Length> = CustomPropertyName("--lumo-icon-size")
    /** `--lumo-icon-size-l`. */
    val ICON_SIZE_L: CustomPropertyName<Length> = CustomPropertyName("--lumo-icon-size-l")
    /** `--lumo-icon-size-m`. */
    val ICON_SIZE_M: CustomPropertyName<Length> = CustomPropertyName("--lumo-icon-size-m")
    /** `--lumo-icon-size-s`. */
    val ICON_SIZE_S: CustomPropertyName<Length> = CustomPropertyName("--lumo-icon-size-s")
    /** `--lumo-input-field-pointer-focus-visible`. */
    val INPUT_FIELD_POINTER_FOCUS_VISIBLE: CustomPropertyName<NumberType> = CustomPropertyName("--lumo-input-field-pointer-focus-visible")
    /** `--lumo-line-height-m`. */
    val LINE_HEIGHT_M: CustomPropertyName<LineHeight> = CustomPropertyName("--lumo-line-height-m")
    /** `--lumo-line-height-s`. */
    val LINE_HEIGHT_S: CustomPropertyName<LineHeight> = CustomPropertyName("--lumo-line-height-s")
    /** `--lumo-line-height-xs`. */
    val LINE_HEIGHT_XS: CustomPropertyName<LineHeight> = CustomPropertyName("--lumo-line-height-xs")
    /** `--lumo-password-field-reveal-button-display`. */
    val PASSWORD_FIELD_REVEAL_BUTTON_DISPLAY: CustomPropertyName<Display> = CustomPropertyName("--lumo-password-field-reveal-button-display")
    /** `--lumo-primary-color`. */
    val PRIMARY_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-primary-color")
    /** `--lumo-primary-color-10pct`. */
    val PRIMARY_COLOR_10PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-primary-color-10pct")
    /** `--lumo-primary-color-50pct`. */
    val PRIMARY_COLOR_50PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-primary-color-50pct")
    /** `--lumo-primary-contrast-color`. */
    val PRIMARY_CONTRAST_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-primary-contrast-color")
    /** `--lumo-primary-text-color`. */
    val PRIMARY_TEXT_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-primary-text-color")
    /** `--lumo-progress-indeterminate-progress-bar-background`. */
    val PROGRESS_INDETERMINATE_PROGRESS_BAR_BACKGROUND: CustomPropertyName<Background> = CustomPropertyName("--lumo-progress-indeterminate-progress-bar-background")
    /** `--lumo-progress-indeterminate-progress-bar-background-reverse`. */
    val PROGRESS_INDETERMINATE_PROGRESS_BAR_BACKGROUND_REVERSE: CustomPropertyName<Background> = CustomPropertyName("--lumo-progress-indeterminate-progress-bar-background-reverse")
    /** `--lumo-required-field-indicator`. */
    val REQUIRED_FIELD_INDICATOR: CustomPropertyName<Content> = CustomPropertyName("--lumo-required-field-indicator")
    /** `--lumo-required-field-indicator-color`. */
    val REQUIRED_FIELD_INDICATOR_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-required-field-indicator-color")
    /** `--lumo-secondary-text-color`. */
    val SECONDARY_TEXT_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-secondary-text-color")
    /** `--lumo-shade`. */
    val SHADE: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade")
    /** `--lumo-shade-10pct`. */
    val SHADE_10PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade-10pct")
    /** `--lumo-shade-20pct`. */
    val SHADE_20PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade-20pct")
    /** `--lumo-shade-30pct`. */
    val SHADE_30PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade-30pct")
    /** `--lumo-shade-40pct`. */
    val SHADE_40PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade-40pct")
    /** `--lumo-shade-50pct`. */
    val SHADE_50PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade-50pct")
    /** `--lumo-shade-5pct`. */
    val SHADE_5PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade-5pct")
    /** `--lumo-shade-60pct`. */
    val SHADE_60PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade-60pct")
    /** `--lumo-shade-70pct`. */
    val SHADE_70PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade-70pct")
    /** `--lumo-shade-80pct`. */
    val SHADE_80PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade-80pct")
    /** `--lumo-shade-90pct`. */
    val SHADE_90PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-shade-90pct")
    /** `--lumo-size-l`. */
    val SIZE_L: CustomPropertyName<Length> = CustomPropertyName("--lumo-size-l")
    /** `--lumo-size-m`. */
    val SIZE_M: CustomPropertyName<Length> = CustomPropertyName("--lumo-size-m")
    /** `--lumo-size-s`. */
    val SIZE_S: CustomPropertyName<Length> = CustomPropertyName("--lumo-size-s")
    /** `--lumo-size-xl`. */
    val SIZE_XL: CustomPropertyName<Length> = CustomPropertyName("--lumo-size-xl")
    /** `--lumo-size-xs`. */
    val SIZE_XS: CustomPropertyName<Length> = CustomPropertyName("--lumo-size-xs")
    /** `--lumo-space-l`. */
    val SPACE_L: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-l")
    /** `--lumo-space-m`. */
    val SPACE_M: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-m")
    /** `--lumo-space-s`. */
    val SPACE_S: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-s")
    /** `--lumo-space-tall-l`. */
    val SPACE_TALL_L: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-tall-l")
    /** `--lumo-space-tall-m`. */
    val SPACE_TALL_M: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-tall-m")
    /** `--lumo-space-tall-s`. */
    val SPACE_TALL_S: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-tall-s")
    /** `--lumo-space-tall-xl`. */
    val SPACE_TALL_XL: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-tall-xl")
    /** `--lumo-space-tall-xs`. */
    val SPACE_TALL_XS: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-tall-xs")
    /** `--lumo-space-wide-l`. */
    val SPACE_WIDE_L: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-wide-l")
    /** `--lumo-space-wide-m`. */
    val SPACE_WIDE_M: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-wide-m")
    /** `--lumo-space-wide-s`. */
    val SPACE_WIDE_S: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-wide-s")
    /** `--lumo-space-wide-xl`. */
    val SPACE_WIDE_XL: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-wide-xl")
    /** `--lumo-space-wide-xs`. */
    val SPACE_WIDE_XS: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-wide-xs")
    /** `--lumo-space-xl`. */
    val SPACE_XL: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-xl")
    /** `--lumo-space-xs`. */
    val SPACE_XS: CustomPropertyName<Length> = CustomPropertyName("--lumo-space-xs")
    /** `--lumo-success-color`. */
    val SUCCESS_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-success-color")
    /** `--lumo-success-color-10pct`. */
    val SUCCESS_COLOR_10PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-success-color-10pct")
    /** `--lumo-success-color-50pct`. */
    val SUCCESS_COLOR_50PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-success-color-50pct")
    /** `--lumo-success-contrast-color`. */
    val SUCCESS_CONTRAST_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-success-contrast-color")
    /** `--lumo-success-text-color`. */
    val SUCCESS_TEXT_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-success-text-color")
    /** `--lumo-tertiary-text-color`. */
    val TERTIARY_TEXT_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-tertiary-text-color")
    /** `--lumo-text-field-size`. */
    val TEXT_FIELD_SIZE: CustomPropertyName<Length> = CustomPropertyName("--lumo-text-field-size")
    /** `--lumo-tint`. */
    val TINT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint")
    /** `--lumo-tint-10pct`. */
    val TINT_10PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint-10pct")
    /** `--lumo-tint-20pct`. */
    val TINT_20PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint-20pct")
    /** `--lumo-tint-30pct`. */
    val TINT_30PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint-30pct")
    /** `--lumo-tint-40pct`. */
    val TINT_40PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint-40pct")
    /** `--lumo-tint-50pct`. */
    val TINT_50PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint-50pct")
    /** `--lumo-tint-5pct`. */
    val TINT_5PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint-5pct")
    /** `--lumo-tint-60pct`. */
    val TINT_60PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint-60pct")
    /** `--lumo-tint-70pct`. */
    val TINT_70PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint-70pct")
    /** `--lumo-tint-80pct`. */
    val TINT_80PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint-80pct")
    /** `--lumo-tint-90pct`. */
    val TINT_90PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-tint-90pct")
    /** `--lumo-utility-border-color`. */
    val UTILITY_BORDER_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-utility-border-color")
    /** `--lumo-utility-border-style`. */
    val UTILITY_BORDER_STYLE: CustomPropertyName<BorderStyle> = CustomPropertyName("--lumo-utility-border-style")
    /** `--lumo-utility-transition-duration`. */
    val UTILITY_TRANSITION_DURATION: CustomPropertyName<TransitionDuration> = CustomPropertyName("--lumo-utility-transition-duration")
    /** `--lumo-utility-transition-timing-function`. */
    val UTILITY_TRANSITION_TIMING_FUNCTION: CustomPropertyName<TransitionTimingFunction> = CustomPropertyName("--lumo-utility-transition-timing-function")
    /** `--lumo-warning-color`. */
    val WARNING_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-warning-color")
    /** `--lumo-warning-color-10pct`. */
    val WARNING_COLOR_10PCT: CustomPropertyName<Color> = CustomPropertyName("--lumo-warning-color-10pct")
    /** `--lumo-warning-contrast-color`. */
    val WARNING_CONTRAST_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-warning-contrast-color")
    /** `--lumo-warning-text-color`. */
    val WARNING_TEXT_COLOR: CustomPropertyName<Color> = CustomPropertyName("--lumo-warning-text-color")

    /** The `lumo` icon set codepoints, for `content` and `--vaadin-*-icon` properties. */
    object Icons {
        /** `--lumo-icons-align-center`. */
        val ALIGN_CENTER: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-align-center")
        /** `--lumo-icons-align-left`. */
        val ALIGN_LEFT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-align-left")
        /** `--lumo-icons-align-right`. */
        val ALIGN_RIGHT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-align-right")
        /** `--lumo-icons-angle-down`. */
        val ANGLE_DOWN: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-angle-down")
        /** `--lumo-icons-angle-left`. */
        val ANGLE_LEFT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-angle-left")
        /** `--lumo-icons-angle-right`. */
        val ANGLE_RIGHT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-angle-right")
        /** `--lumo-icons-angle-up`. */
        val ANGLE_UP: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-angle-up")
        /** `--lumo-icons-arrow-down`. */
        val ARROW_DOWN: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-arrow-down")
        /** `--lumo-icons-arrow-left`. */
        val ARROW_LEFT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-arrow-left")
        /** `--lumo-icons-arrow-right`. */
        val ARROW_RIGHT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-arrow-right")
        /** `--lumo-icons-arrow-up`. */
        val ARROW_UP: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-arrow-up")
        /** `--lumo-icons-bar-chart`. */
        val BAR_CHART: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-bar-chart")
        /** `--lumo-icons-bell`. */
        val BELL: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-bell")
        /** `--lumo-icons-calendar`. */
        val CALENDAR: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-calendar")
        /** `--lumo-icons-checkmark`. */
        val CHECKMARK: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-checkmark")
        /** `--lumo-icons-chevron-down`. */
        val CHEVRON_DOWN: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-chevron-down")
        /** `--lumo-icons-chevron-left`. */
        val CHEVRON_LEFT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-chevron-left")
        /** `--lumo-icons-chevron-right`. */
        val CHEVRON_RIGHT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-chevron-right")
        /** `--lumo-icons-chevron-up`. */
        val CHEVRON_UP: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-chevron-up")
        /** `--lumo-icons-clean`. */
        val CLEAN: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-clean")
        /** `--lumo-icons-clock`. */
        val CLOCK: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-clock")
        /** `--lumo-icons-cog`. */
        val COG: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-cog")
        /** `--lumo-icons-cross`. */
        val CROSS: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-cross")
        /** `--lumo-icons-download`. */
        val DOWNLOAD: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-download")
        /** `--lumo-icons-drag-handle`. */
        val DRAG_HANDLE: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-drag-handle")
        /** `--lumo-icons-dropdown`. */
        val DROPDOWN: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-dropdown")
        /** `--lumo-icons-edit`. */
        val EDIT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-edit")
        /** `--lumo-icons-error`. */
        val ERROR: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-error")
        /** `--lumo-icons-eye`. */
        val EYE: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-eye")
        /** `--lumo-icons-eye-disabled`. */
        val EYE_DISABLED: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-eye-disabled")
        /** `--lumo-icons-indent`. */
        val INDENT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-indent")
        /** `--lumo-icons-link`. */
        val LINK: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-link")
        /** `--lumo-icons-menu`. */
        val MENU: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-menu")
        /** `--lumo-icons-minus`. */
        val MINUS: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-minus")
        /** `--lumo-icons-ordered-list`. */
        val ORDERED_LIST: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-ordered-list")
        /** `--lumo-icons-outdent`. */
        val OUTDENT: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-outdent")
        /** `--lumo-icons-phone`. */
        val PHONE: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-phone")
        /** `--lumo-icons-photo`. */
        val PHOTO: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-photo")
        /** `--lumo-icons-play`. */
        val PLAY: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-play")
        /** `--lumo-icons-plus`. */
        val PLUS: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-plus")
        /** `--lumo-icons-redo`. */
        val REDO: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-redo")
        /** `--lumo-icons-reload`. */
        val RELOAD: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-reload")
        /** `--lumo-icons-resize-handle`. */
        val RESIZE_HANDLE: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-resize-handle")
        /** `--lumo-icons-search`. */
        val SEARCH: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-search")
        /** `--lumo-icons-undo`. */
        val UNDO: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-undo")
        /** `--lumo-icons-unordered-list`. */
        val UNORDERED_LIST: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-unordered-list")
        /** `--lumo-icons-upload`. */
        val UPLOAD: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-upload")
        /** `--lumo-icons-user`. */
        val USER: CustomPropertyName<Content> = CustomPropertyName("--lumo-icons-user")
    }
}
