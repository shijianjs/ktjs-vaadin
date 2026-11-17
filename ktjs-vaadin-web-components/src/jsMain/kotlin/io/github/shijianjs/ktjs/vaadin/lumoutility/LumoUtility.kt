package io.github.shijianjs.ktjs.vaadin.lumoutility

import web.cssom.ClassName

/**
 * Every CSS utility class Lumo ships, mirroring `com.vaadin.flow.theme.lumo.LumoUtility`.
 *
 * The class names come from `@vaadin/vaadin-lumo-styles/utility.css`. Add them to an element with
 * `classList.add(LumoUtility.Display.FLEX)`; the nested objects follow the Java class one to one, so
 * `LumoUtility.AlignItems.Breakpoint.Small.CENTER` is `sm:items-center`.
 *
 * Utility classes are a cascade-level API: they apply to the element itself and are meant for the
 * few places where a component's own theme variants are not enough (one-off alignment, a gap, a
 * single margin). Layout and surfaces that more than one element shares still belong in a sheet.
 */
object LumoUtility {
    object Accessibility {
        /** Lumo utility classes of the `Accessibility` group. */
        val SCREEN_READER_ONLY: ClassName = ClassName("sr-only")
    }
    object AlignContent {
        /** Lumo utility classes of the `AlignContent` group. */
        val AROUND: ClassName = ClassName("content-around")
        val BETWEEN: ClassName = ClassName("content-between")
        val CENTER: ClassName = ClassName("content-center")
        val END: ClassName = ClassName("content-end")
        val EVENLY: ClassName = ClassName("content-evenly")
        val START: ClassName = ClassName("content-start")
        val STRETCH: ClassName = ClassName("content-stretch")
    }
    object AlignItems {
        /** Lumo utility classes of the `AlignItems` group. */
        val BASELINE: ClassName = ClassName("items-baseline")
        val CENTER: ClassName = ClassName("items-center")
        val END: ClassName = ClassName("items-end")
        val START: ClassName = ClassName("items-start")
        val STRETCH: ClassName = ClassName("items-stretch")

        object Breakpoint {
            /** Lumo utility classes of the `Breakpoint` group. */
            object Small {
                /** Lumo utility classes of the `Small` group. */
                val BASELINE: ClassName = ClassName("sm:items-baseline")
                val CENTER: ClassName = ClassName("sm:items-center")
                val END: ClassName = ClassName("sm:items-end")
                val START: ClassName = ClassName("sm:items-start")
                val STRETCH: ClassName = ClassName("sm:items-stretch")
            }
            object Medium {
                /** Lumo utility classes of the `Medium` group. */
                val BASELINE: ClassName = ClassName("md:items-baseline")
                val CENTER: ClassName = ClassName("md:items-center")
                val END: ClassName = ClassName("md:items-end")
                val START: ClassName = ClassName("md:items-start")
                val STRETCH: ClassName = ClassName("md:items-stretch")
            }
            object Large {
                /** Lumo utility classes of the `Large` group. */
                val BASELINE: ClassName = ClassName("lg:items-baseline")
                val CENTER: ClassName = ClassName("lg:items-center")
                val END: ClassName = ClassName("lg:items-end")
                val START: ClassName = ClassName("lg:items-start")
                val STRETCH: ClassName = ClassName("lg:items-stretch")
            }
            object XLarge {
                /** Lumo utility classes of the `XLarge` group. */
                val BASELINE: ClassName = ClassName("xl:items-baseline")
                val CENTER: ClassName = ClassName("xl:items-center")
                val END: ClassName = ClassName("xl:items-end")
                val START: ClassName = ClassName("xl:items-start")
                val STRETCH: ClassName = ClassName("xl:items-stretch")
            }
            object XXLarge {
                /** Lumo utility classes of the `XXLarge` group. */
                val BASELINE: ClassName = ClassName("2xl:items-baseline")
                val CENTER: ClassName = ClassName("2xl:items-center")
                val END: ClassName = ClassName("2xl:items-end")
                val START: ClassName = ClassName("2xl:items-start")
                val STRETCH: ClassName = ClassName("2xl:items-stretch")
            }
        }
    }
    object AlignSelf {
        /** Lumo utility classes of the `AlignSelf` group. */
        val AUTO: ClassName = ClassName("self-auto")
        val BASELINE: ClassName = ClassName("self-baseline")
        val CENTER: ClassName = ClassName("self-center")
        val END: ClassName = ClassName("self-end")
        val START: ClassName = ClassName("self-start")
        val STRETCH: ClassName = ClassName("self-stretch")
    }
    object AspectRatio {
        /** Lumo utility classes of the `AspectRatio` group. */
        val SQUARE: ClassName = ClassName("aspect-square")
        val VIDEO: ClassName = ClassName("aspect-video")
    }
    object BackdropBlur {
        /** Lumo utility classes of the `BackdropBlur` group. */
        val NONE: ClassName = ClassName("backdrop-blur-none")
        val SMALL: ClassName = ClassName("backdrop-blur-sm")
        val DEFAULT: ClassName = ClassName("backdrop-blur")
        val MEDIUM: ClassName = ClassName("backdrop-blur-md")
        val LARGE: ClassName = ClassName("backdrop-blur-lg")
        val XLARGE: ClassName = ClassName("backdrop-blur-xl")
        val XXLARGE: ClassName = ClassName("backdrop-blur-2xl")
        val XXXLARGE: ClassName = ClassName("backdrop-blur-3xl")
    }
    object Background {
        /** Lumo utility classes of the `Background` group. */
        val BASE: ClassName = ClassName("bg-base")
        val TRANSPARENT: ClassName = ClassName("bg-transparent")
        val CONTRAST: ClassName = ClassName("bg-contrast")
        val CONTRAST_90: ClassName = ClassName("bg-contrast-90")
        val CONTRAST_80: ClassName = ClassName("bg-contrast-80")
        val CONTRAST_70: ClassName = ClassName("bg-contrast-70")
        val CONTRAST_60: ClassName = ClassName("bg-contrast-60")
        val CONTRAST_50: ClassName = ClassName("bg-contrast-50")
        val CONTRAST_40: ClassName = ClassName("bg-contrast-40")
        val CONTRAST_30: ClassName = ClassName("bg-contrast-30")
        val CONTRAST_20: ClassName = ClassName("bg-contrast-20")
        val CONTRAST_10: ClassName = ClassName("bg-contrast-10")
        val CONTRAST_5: ClassName = ClassName("bg-contrast-5")
        val TINT: ClassName = ClassName("bg-tint")
        val TINT_90: ClassName = ClassName("bg-tint-90")
        val TINT_80: ClassName = ClassName("bg-tint-80")
        val TINT_70: ClassName = ClassName("bg-tint-70")
        val TINT_60: ClassName = ClassName("bg-tint-60")
        val TINT_50: ClassName = ClassName("bg-tint-50")
        val TINT_40: ClassName = ClassName("bg-tint-40")
        val TINT_30: ClassName = ClassName("bg-tint-30")
        val TINT_20: ClassName = ClassName("bg-tint-20")
        val TINT_10: ClassName = ClassName("bg-tint-10")
        val TINT_5: ClassName = ClassName("bg-tint-5")
        val SHADE: ClassName = ClassName("bg-shade")
        val SHADE_90: ClassName = ClassName("bg-shade-90")
        val SHADE_80: ClassName = ClassName("bg-shade-80")
        val SHADE_70: ClassName = ClassName("bg-shade-70")
        val SHADE_60: ClassName = ClassName("bg-shade-60")
        val SHADE_50: ClassName = ClassName("bg-shade-50")
        val SHADE_40: ClassName = ClassName("bg-shade-40")
        val SHADE_30: ClassName = ClassName("bg-shade-30")
        val SHADE_20: ClassName = ClassName("bg-shade-20")
        val SHADE_10: ClassName = ClassName("bg-shade-10")
        val SHADE_5: ClassName = ClassName("bg-shade-5")
        val PRIMARY: ClassName = ClassName("bg-primary")
        val PRIMARY_50: ClassName = ClassName("bg-primary-50")
        val PRIMARY_10: ClassName = ClassName("bg-primary-10")
        val ERROR: ClassName = ClassName("bg-error")
        val ERROR_50: ClassName = ClassName("bg-error-50")
        val ERROR_10: ClassName = ClassName("bg-error-10")
        val WARNING: ClassName = ClassName("bg-warning")
        val WARNING_10: ClassName = ClassName("bg-warning-10")
        val SUCCESS: ClassName = ClassName("bg-success")
        val SUCCESS_50: ClassName = ClassName("bg-success-50")
        val SUCCESS_10: ClassName = ClassName("bg-success-10")
    }
    object Border {
        /** Lumo utility classes of the `Border` group. */
        val NONE: ClassName = ClassName("border-0")
        val DASHED: ClassName = ClassName("border-dashed")
        val DOTTED: ClassName = ClassName("border-dotted")
        val ALL: ClassName = ClassName("border")
        val BOTTOM: ClassName = ClassName("border-b")
        val END: ClassName = ClassName("border-e")
        val LEFT: ClassName = ClassName("border-l")
        val RIGHT: ClassName = ClassName("border-r")
        val START: ClassName = ClassName("border-s")
        val TOP: ClassName = ClassName("border-t")
    }
    object BorderColor {
        /** Lumo utility classes of the `BorderColor` group. */
        val CONTRAST: ClassName = ClassName("border-contrast")
        val CONTRAST_90: ClassName = ClassName("border-contrast-90")
        val CONTRAST_80: ClassName = ClassName("border-contrast-80")
        val CONTRAST_70: ClassName = ClassName("border-contrast-70")
        val CONTRAST_60: ClassName = ClassName("border-contrast-60")
        val CONTRAST_50: ClassName = ClassName("border-contrast-50")
        val CONTRAST_40: ClassName = ClassName("border-contrast-40")
        val CONTRAST_30: ClassName = ClassName("border-contrast-30")
        val CONTRAST_20: ClassName = ClassName("border-contrast-20")
        val CONTRAST_10: ClassName = ClassName("border-contrast-10")
        val CONTRAST_5: ClassName = ClassName("border-contrast-5")
        val PRIMARY: ClassName = ClassName("border-primary")
        val PRIMARY_50: ClassName = ClassName("border-primary-50")
        val PRIMARY_10: ClassName = ClassName("border-primary-10")
        val ERROR: ClassName = ClassName("border-error")
        val ERROR_50: ClassName = ClassName("border-error-50")
        val ERROR_10: ClassName = ClassName("border-error-10")
        val WARNING: ClassName = ClassName("border-warning")
        val WARNING_10: ClassName = ClassName("border-warning-10")
        val WARNING_STRONG: ClassName = ClassName("border-warning-strong")
        val SUCCESS: ClassName = ClassName("border-success")
        val SUCCESS_50: ClassName = ClassName("border-success-50")
        val SUCCESS_10: ClassName = ClassName("border-success-10")
    }
    object BorderRadius {
        /** Lumo utility classes of the `BorderRadius` group. */
        val NONE: ClassName = ClassName("rounded-none")
        val SMALL: ClassName = ClassName("rounded-s")
        val MEDIUM: ClassName = ClassName("rounded-m")
        val LARGE: ClassName = ClassName("rounded-l")
        val FULL: ClassName = ClassName("rounded-full")
    }
    object BoxShadow {
        /** Lumo utility classes of the `BoxShadow` group. */
        val NONE: ClassName = ClassName("shadow-none")
        val XSMALL: ClassName = ClassName("shadow-xs")
        val SMALL: ClassName = ClassName("shadow-s")
        val MEDIUM: ClassName = ClassName("shadow-m")
        val LARGE: ClassName = ClassName("shadow-l")
        val XLARGE: ClassName = ClassName("shadow-xl")
    }
    object BoxSizing {
        /** Lumo utility classes of the `BoxSizing` group. */
        val BORDER: ClassName = ClassName("box-border")
        val CONTENT: ClassName = ClassName("box-content")
    }
    object Display {
        /** Lumo utility classes of the `Display` group. */
        val BLOCK: ClassName = ClassName("block")
        val FLEX: ClassName = ClassName("flex")
        val GRID: ClassName = ClassName("grid")
        val HIDDEN: ClassName = ClassName("hidden")
        val INLINE: ClassName = ClassName("inline")
        val INLINE_BLOCK: ClassName = ClassName("inline-block")
        val INLINE_FLEX: ClassName = ClassName("inline-flex")
        val INLINE_GRID: ClassName = ClassName("inline-grid")

        object Breakpoint {
            /** Lumo utility classes of the `Breakpoint` group. */
            object Small {
                /** Lumo utility classes of the `Small` group. */
                val BLOCK: ClassName = ClassName("sm:block")
                val FLEX: ClassName = ClassName("sm:flex")
                val GRID: ClassName = ClassName("sm:grid")
                val HIDDEN: ClassName = ClassName("sm:hidden")
                val INLINE: ClassName = ClassName("sm:inline")
                val INLINE_BLOCK: ClassName = ClassName("sm:inline-block")
                val INLINE_FLEX: ClassName = ClassName("sm:inline-flex")
                val INLINE_GRID: ClassName = ClassName("sm:inline-grid")
            }
            object Medium {
                /** Lumo utility classes of the `Medium` group. */
                val BLOCK: ClassName = ClassName("md:block")
                val FLEX: ClassName = ClassName("md:flex")
                val GRID: ClassName = ClassName("md:grid")
                val HIDDEN: ClassName = ClassName("md:hidden")
                val INLINE: ClassName = ClassName("md:inline")
                val INLINE_BLOCK: ClassName = ClassName("md:inline-block")
                val INLINE_FLEX: ClassName = ClassName("md:inline-flex")
                val INLINE_GRID: ClassName = ClassName("md:inline-grid")
            }
            object Large {
                /** Lumo utility classes of the `Large` group. */
                val BLOCK: ClassName = ClassName("lg:block")
                val FLEX: ClassName = ClassName("lg:flex")
                val GRID: ClassName = ClassName("lg:grid")
                val HIDDEN: ClassName = ClassName("lg:hidden")
                val INLINE: ClassName = ClassName("lg:inline")
                val INLINE_BLOCK: ClassName = ClassName("lg:inline-block")
                val INLINE_FLEX: ClassName = ClassName("lg:inline-flex")
                val INLINE_GRID: ClassName = ClassName("lg:inline-grid")
            }
            object XLarge {
                /** Lumo utility classes of the `XLarge` group. */
                val BLOCK: ClassName = ClassName("xl:block")
                val FLEX: ClassName = ClassName("xl:flex")
                val GRID: ClassName = ClassName("xl:grid")
                val HIDDEN: ClassName = ClassName("xl:hidden")
                val INLINE: ClassName = ClassName("xl:inline")
                val INLINE_BLOCK: ClassName = ClassName("xl:inline-block")
                val INLINE_FLEX: ClassName = ClassName("xl:inline-flex")
                val INLINE_GRID: ClassName = ClassName("xl:inline-grid")
            }
            object XXLarge {
                /** Lumo utility classes of the `XXLarge` group. */
                val BLOCK: ClassName = ClassName("2xl:block")
                val FLEX: ClassName = ClassName("2xl:flex")
                val GRID: ClassName = ClassName("2xl:grid")
                val HIDDEN: ClassName = ClassName("2xl:hidden")
                val INLINE: ClassName = ClassName("2xl:inline")
                val INLINE_BLOCK: ClassName = ClassName("2xl:inline-block")
                val INLINE_FLEX: ClassName = ClassName("2xl:inline-flex")
                val INLINE_GRID: ClassName = ClassName("2xl:inline-grid")
            }
        }
    }
    object Divide {
        /** Lumo utility classes of the `Divide` group. */
        val X: ClassName = ClassName("divide-x")
        val Y: ClassName = ClassName("divide-y")
    }
    object Flex {
        /** Lumo utility classes of the `Flex` group. */
        val ONE: ClassName = ClassName("flex-1")
        val AUTO: ClassName = ClassName("flex-auto")
        val NONE: ClassName = ClassName("flex-none")
        val GROW: ClassName = ClassName("flex-grow")
        val GROW_NONE: ClassName = ClassName("flex-grow-0")
        val SHRINK: ClassName = ClassName("flex-shrink")
        val SHRINK_NONE: ClassName = ClassName("flex-shrink-0")
    }
    object FlexDirection {
        /** Lumo utility classes of the `FlexDirection` group. */
        val COLUMN: ClassName = ClassName("flex-col")
        val COLUMN_REVERSE: ClassName = ClassName("flex-col-reverse")
        val ROW: ClassName = ClassName("flex-row")
        val ROW_REVERSE: ClassName = ClassName("flex-row-reverse")

        object Breakpoint {
            /** Lumo utility classes of the `Breakpoint` group. */
            object Small {
                /** Lumo utility classes of the `Small` group. */
                val COLUMN: ClassName = ClassName("sm:flex-col")
                val ROW: ClassName = ClassName("sm:flex-row")
            }
            object Medium {
                /** Lumo utility classes of the `Medium` group. */
                val COLUMN: ClassName = ClassName("md:flex-col")
                val ROW: ClassName = ClassName("md:flex-row")
            }
            object Large {
                /** Lumo utility classes of the `Large` group. */
                val COLUMN: ClassName = ClassName("lg:flex-col")
                val ROW: ClassName = ClassName("lg:flex-row")
            }
            object XLarge {
                /** Lumo utility classes of the `XLarge` group. */
                val COLUMN: ClassName = ClassName("xl:flex-col")
                val ROW: ClassName = ClassName("xl:flex-row")
            }
            object XXLarge {
                /** Lumo utility classes of the `XXLarge` group. */
                val COLUMN: ClassName = ClassName("2xl:flex-col")
                val ROW: ClassName = ClassName("2xl:flex-row")
            }
        }
    }
    object FlexWrap {
        /** Lumo utility classes of the `FlexWrap` group. */
        val NOWRAP: ClassName = ClassName("flex-nowrap")
        val WRAP: ClassName = ClassName("flex-wrap")
        val WRAP_REVERSE: ClassName = ClassName("flex-wrap-reverse")
    }
    object FontSize {
        /** Lumo utility classes of the `FontSize` group. */
        val XXSMALL: ClassName = ClassName("text-2xs")
        val XSMALL: ClassName = ClassName("text-xs")
        val SMALL: ClassName = ClassName("text-s")
        val MEDIUM: ClassName = ClassName("text-m")
        val LARGE: ClassName = ClassName("text-l")
        val XLARGE: ClassName = ClassName("text-xl")
        val XXLARGE: ClassName = ClassName("text-2xl")
        val XXXLARGE: ClassName = ClassName("text-3xl")

        object Breakpoint {
            /** Lumo utility classes of the `Breakpoint` group. */
            object Small {
                /** Lumo utility classes of the `Small` group. */
                val XXSMALL: ClassName = ClassName("sm:text-2xs")
                val XSMALL: ClassName = ClassName("sm:text-xs")
                val SMALL: ClassName = ClassName("sm:text-s")
                val MEDIUM: ClassName = ClassName("sm:text-m")
                val LARGE: ClassName = ClassName("sm:text-l")
                val XLARGE: ClassName = ClassName("sm:text-xl")
                val XXLARGE: ClassName = ClassName("sm:text-2xl")
                val XXXLARGE: ClassName = ClassName("sm:text-3xl")
            }
            object Medium {
                /** Lumo utility classes of the `Medium` group. */
                val XXSMALL: ClassName = ClassName("md:text-2xs")
                val XSMALL: ClassName = ClassName("md:text-xs")
                val SMALL: ClassName = ClassName("md:text-s")
                val MEDIUM: ClassName = ClassName("md:text-m")
                val LARGE: ClassName = ClassName("md:text-l")
                val XLARGE: ClassName = ClassName("md:text-xl")
                val XXLARGE: ClassName = ClassName("md:text-2xl")
                val XXXLARGE: ClassName = ClassName("md:text-3xl")
            }
            object Large {
                /** Lumo utility classes of the `Large` group. */
                val XXSMALL: ClassName = ClassName("lg:text-2xs")
                val XSMALL: ClassName = ClassName("lg:text-xs")
                val SMALL: ClassName = ClassName("lg:text-s")
                val MEDIUM: ClassName = ClassName("lg:text-m")
                val LARGE: ClassName = ClassName("lg:text-l")
                val XLARGE: ClassName = ClassName("lg:text-xl")
                val XXLARGE: ClassName = ClassName("lg:text-2xl")
                val XXXLARGE: ClassName = ClassName("lg:text-3xl")
            }
            object XLarge {
                /** Lumo utility classes of the `XLarge` group. */
                val XXSMALL: ClassName = ClassName("xl:text-2xs")
                val XSMALL: ClassName = ClassName("xl:text-xs")
                val SMALL: ClassName = ClassName("xl:text-s")
                val MEDIUM: ClassName = ClassName("xl:text-m")
                val LARGE: ClassName = ClassName("xl:text-l")
                val XLARGE: ClassName = ClassName("xl:text-xl")
                val XXLARGE: ClassName = ClassName("xl:text-2xl")
                val XXXLARGE: ClassName = ClassName("xl:text-3xl")
            }
            object XXLarge {
                /** Lumo utility classes of the `XXLarge` group. */
                val XXSMALL: ClassName = ClassName("2xl:text-2xs")
                val XSMALL: ClassName = ClassName("2xl:text-xs")
                val SMALL: ClassName = ClassName("2xl:text-s")
                val MEDIUM: ClassName = ClassName("2xl:text-m")
                val LARGE: ClassName = ClassName("2xl:text-l")
                val XLARGE: ClassName = ClassName("2xl:text-xl")
                val XXLARGE: ClassName = ClassName("2xl:text-2xl")
                val XXXLARGE: ClassName = ClassName("2xl:text-3xl")
            }
        }
    }
    object FontWeight {
        /** Lumo utility classes of the `FontWeight` group. */
        val THIN: ClassName = ClassName("font-thin")
        val EXTRALIGHT: ClassName = ClassName("font-extralight")
        val LIGHT: ClassName = ClassName("font-light")
        val NORMAL: ClassName = ClassName("font-normal")
        val MEDIUM: ClassName = ClassName("font-medium")
        val SEMIBOLD: ClassName = ClassName("font-semibold")
        val BOLD: ClassName = ClassName("font-bold")
        val EXTRABOLD: ClassName = ClassName("font-extrabold")
        val BLACK: ClassName = ClassName("font-black")
    }
    object Gap {
        /** Lumo utility classes of the `Gap` group. */
        val XSMALL: ClassName = ClassName("gap-xs")
        val SMALL: ClassName = ClassName("gap-s")
        val MEDIUM: ClassName = ClassName("gap-m")
        val LARGE: ClassName = ClassName("gap-l")
        val XLARGE: ClassName = ClassName("gap-xl")

        object Column {
            /** Lumo utility classes of the `Column` group. */
            val XSMALL: ClassName = ClassName("gap-x-xs")
            val SMALL: ClassName = ClassName("gap-x-s")
            val MEDIUM: ClassName = ClassName("gap-x-m")
            val LARGE: ClassName = ClassName("gap-x-l")
            val XLARGE: ClassName = ClassName("gap-x-xl")
        }

        object Row {
            /** Lumo utility classes of the `Row` group. */
            val XSMALL: ClassName = ClassName("gap-y-xs")
            val SMALL: ClassName = ClassName("gap-y-s")
            val MEDIUM: ClassName = ClassName("gap-y-m")
            val LARGE: ClassName = ClassName("gap-y-l")
            val XLARGE: ClassName = ClassName("gap-y-xl")
        }
    }
    object Grid {
        /** Lumo utility classes of the `Grid` group. */
        val FLOW_COLUMN: ClassName = ClassName("grid-flow-col")
        val FLOW_ROW: ClassName = ClassName("grid-flow-row")

        object Column {
            /** Lumo utility classes of the `Column` group. */
            val COLUMNS_1: ClassName = ClassName("grid-cols-1")
            val COLUMNS_2: ClassName = ClassName("grid-cols-2")
            val COLUMNS_3: ClassName = ClassName("grid-cols-3")
            val COLUMNS_4: ClassName = ClassName("grid-cols-4")
            val COLUMNS_5: ClassName = ClassName("grid-cols-5")
            val COLUMNS_6: ClassName = ClassName("grid-cols-6")
            val COLUMNS_7: ClassName = ClassName("grid-cols-7")
            val COLUMNS_8: ClassName = ClassName("grid-cols-8")
            val COLUMNS_9: ClassName = ClassName("grid-cols-9")
            val COLUMNS_10: ClassName = ClassName("grid-cols-10")
            val COLUMNS_11: ClassName = ClassName("grid-cols-11")
            val COLUMNS_12: ClassName = ClassName("grid-cols-12")
            val COLUMN_SPAN_1: ClassName = ClassName("col-span-1")
            val COLUMN_SPAN_2: ClassName = ClassName("col-span-2")
            val COLUMN_SPAN_3: ClassName = ClassName("col-span-3")
            val COLUMN_SPAN_4: ClassName = ClassName("col-span-4")
            val COLUMN_SPAN_5: ClassName = ClassName("col-span-5")
            val COLUMN_SPAN_6: ClassName = ClassName("col-span-6")
            val COLUMN_SPAN_7: ClassName = ClassName("col-span-7")
            val COLUMN_SPAN_8: ClassName = ClassName("col-span-8")
            val COLUMN_SPAN_9: ClassName = ClassName("col-span-9")
            val COLUMN_SPAN_10: ClassName = ClassName("col-span-10")
            val COLUMN_SPAN_11: ClassName = ClassName("col-span-11")
            val COLUMN_SPAN_12: ClassName = ClassName("col-span-12")
        }

        object Row {
            /** Lumo utility classes of the `Row` group. */
            val ROWS_1: ClassName = ClassName("grid-rows-1")
            val ROWS_2: ClassName = ClassName("grid-rows-2")
            val ROWS_3: ClassName = ClassName("grid-rows-3")
            val ROWS_4: ClassName = ClassName("grid-rows-4")
            val ROWS_5: ClassName = ClassName("grid-rows-5")
            val ROWS_6: ClassName = ClassName("grid-rows-6")
            val ROW_SPAN_1: ClassName = ClassName("row-span-1")
            val ROW_SPAN_2: ClassName = ClassName("row-span-2")
            val ROW_SPAN_3: ClassName = ClassName("row-span-3")
            val ROW_SPAN_4: ClassName = ClassName("row-span-4")
            val ROW_SPAN_5: ClassName = ClassName("row-span-5")
            val ROW_SPAN_6: ClassName = ClassName("row-span-6")
        }

        object Breakpoint {
            /** Lumo utility classes of the `Breakpoint` group. */
            object Small {
                /** Lumo utility classes of the `Small` group. */
                val COLUMNS_1: ClassName = ClassName("sm:grid-cols-1")
                val COLUMNS_2: ClassName = ClassName("sm:grid-cols-2")
                val COLUMNS_3: ClassName = ClassName("sm:grid-cols-3")
                val COLUMNS_4: ClassName = ClassName("sm:grid-cols-4")
                val COLUMNS_5: ClassName = ClassName("sm:grid-cols-5")
                val COLUMNS_6: ClassName = ClassName("sm:grid-cols-6")
                val COLUMNS_7: ClassName = ClassName("sm:grid-cols-7")
                val COLUMNS_8: ClassName = ClassName("sm:grid-cols-8")
                val COLUMNS_9: ClassName = ClassName("sm:grid-cols-9")
                val COLUMNS_10: ClassName = ClassName("sm:grid-cols-10")
                val COLUMNS_11: ClassName = ClassName("sm:grid-cols-11")
                val COLUMNS_12: ClassName = ClassName("sm:grid-cols-12")
            }
            object Medium {
                /** Lumo utility classes of the `Medium` group. */
                val COLUMNS_1: ClassName = ClassName("md:grid-cols-1")
                val COLUMNS_2: ClassName = ClassName("md:grid-cols-2")
                val COLUMNS_3: ClassName = ClassName("md:grid-cols-3")
                val COLUMNS_4: ClassName = ClassName("md:grid-cols-4")
                val COLUMNS_5: ClassName = ClassName("md:grid-cols-5")
                val COLUMNS_6: ClassName = ClassName("md:grid-cols-6")
                val COLUMNS_7: ClassName = ClassName("md:grid-cols-7")
                val COLUMNS_8: ClassName = ClassName("md:grid-cols-8")
                val COLUMNS_9: ClassName = ClassName("md:grid-cols-9")
                val COLUMNS_10: ClassName = ClassName("md:grid-cols-10")
                val COLUMNS_11: ClassName = ClassName("md:grid-cols-11")
                val COLUMNS_12: ClassName = ClassName("md:grid-cols-12")
            }
            object Large {
                /** Lumo utility classes of the `Large` group. */
                val COLUMNS_1: ClassName = ClassName("lg:grid-cols-1")
                val COLUMNS_2: ClassName = ClassName("lg:grid-cols-2")
                val COLUMNS_3: ClassName = ClassName("lg:grid-cols-3")
                val COLUMNS_4: ClassName = ClassName("lg:grid-cols-4")
                val COLUMNS_5: ClassName = ClassName("lg:grid-cols-5")
                val COLUMNS_6: ClassName = ClassName("lg:grid-cols-6")
                val COLUMNS_7: ClassName = ClassName("lg:grid-cols-7")
                val COLUMNS_8: ClassName = ClassName("lg:grid-cols-8")
                val COLUMNS_9: ClassName = ClassName("lg:grid-cols-9")
                val COLUMNS_10: ClassName = ClassName("lg:grid-cols-10")
                val COLUMNS_11: ClassName = ClassName("lg:grid-cols-11")
                val COLUMNS_12: ClassName = ClassName("lg:grid-cols-12")
            }
            object XLarge {
                /** Lumo utility classes of the `XLarge` group. */
                val COLUMNS_1: ClassName = ClassName("xl:grid-cols-1")
                val COLUMNS_2: ClassName = ClassName("xl:grid-cols-2")
                val COLUMNS_3: ClassName = ClassName("xl:grid-cols-3")
                val COLUMNS_4: ClassName = ClassName("xl:grid-cols-4")
                val COLUMNS_5: ClassName = ClassName("xl:grid-cols-5")
                val COLUMNS_6: ClassName = ClassName("xl:grid-cols-6")
                val COLUMNS_7: ClassName = ClassName("xl:grid-cols-7")
                val COLUMNS_8: ClassName = ClassName("xl:grid-cols-8")
                val COLUMNS_9: ClassName = ClassName("xl:grid-cols-9")
                val COLUMNS_10: ClassName = ClassName("xl:grid-cols-10")
                val COLUMNS_11: ClassName = ClassName("xl:grid-cols-11")
                val COLUMNS_12: ClassName = ClassName("xl:grid-cols-12")
            }
            object XXLarge {
                /** Lumo utility classes of the `XXLarge` group. */
                val COLUMNS_1: ClassName = ClassName("2xl:grid-cols-1")
                val COLUMNS_2: ClassName = ClassName("2xl:grid-cols-2")
                val COLUMNS_3: ClassName = ClassName("2xl:grid-cols-3")
                val COLUMNS_4: ClassName = ClassName("2xl:grid-cols-4")
                val COLUMNS_5: ClassName = ClassName("2xl:grid-cols-5")
                val COLUMNS_6: ClassName = ClassName("2xl:grid-cols-6")
                val COLUMNS_7: ClassName = ClassName("2xl:grid-cols-7")
                val COLUMNS_8: ClassName = ClassName("2xl:grid-cols-8")
                val COLUMNS_9: ClassName = ClassName("2xl:grid-cols-9")
                val COLUMNS_10: ClassName = ClassName("2xl:grid-cols-10")
                val COLUMNS_11: ClassName = ClassName("2xl:grid-cols-11")
                val COLUMNS_12: ClassName = ClassName("2xl:grid-cols-12")
            }
        }
    }
    object Height {
        /** Lumo utility classes of the `Height` group. */
        val NONE: ClassName = ClassName("h-0")
        val XSMALL: ClassName = ClassName("h-xs")
        val SMALL: ClassName = ClassName("h-s")
        val MEDIUM: ClassName = ClassName("h-m")
        val LARGE: ClassName = ClassName("h-l")
        val XLARGE: ClassName = ClassName("h-xl")
        val AUTO: ClassName = ClassName("h-auto")
        val FULL: ClassName = ClassName("h-full")
        val SCREEN: ClassName = ClassName("h-screen")
    }
    object IconSize {
        /** Lumo utility classes of the `IconSize` group. */
        val SMALL: ClassName = ClassName("icon-s")
        val MEDIUM: ClassName = ClassName("icon-m")
        val LARGE: ClassName = ClassName("icon-l")
    }
    object JustifyContent {
        /** Lumo utility classes of the `JustifyContent` group. */
        val AROUND: ClassName = ClassName("justify-around")
        val BETWEEN: ClassName = ClassName("justify-between")
        val CENTER: ClassName = ClassName("justify-center")
        val END: ClassName = ClassName("justify-end")
        val EVENLY: ClassName = ClassName("justify-evenly")
        val START: ClassName = ClassName("justify-start")
    }
    object LineHeight {
        /** Lumo utility classes of the `LineHeight` group. */
        val NONE: ClassName = ClassName("leading-none")
        val XSMALL: ClassName = ClassName("leading-xs")
        val SMALL: ClassName = ClassName("leading-s")
        val MEDIUM: ClassName = ClassName("leading-m")
    }
    object ListStyleType {
        /** Lumo utility classes of the `ListStyleType` group. */
        val NONE: ClassName = ClassName("list-none")
    }
    object Margin {
        /** Lumo utility classes of the `Margin` group. */
        val NONE: ClassName = ClassName("m-0")
        val XSMALL: ClassName = ClassName("m-xs")
        val SMALL: ClassName = ClassName("m-s")
        val MEDIUM: ClassName = ClassName("m-m")
        val LARGE: ClassName = ClassName("m-l")
        val XLARGE: ClassName = ClassName("m-xl")
        val AUTO: ClassName = ClassName("m-auto")

        object Bottom {
            /** Lumo utility classes of the `Bottom` group. */
            val NONE: ClassName = ClassName("mb-0")
            val XSMALL: ClassName = ClassName("mb-xs")
            val SMALL: ClassName = ClassName("mb-s")
            val MEDIUM: ClassName = ClassName("mb-m")
            val LARGE: ClassName = ClassName("mb-l")
            val XLARGE: ClassName = ClassName("mb-xl")
            val AUTO: ClassName = ClassName("mb-auto")
        }

        object End {
            /** Lumo utility classes of the `End` group. */
            val NONE: ClassName = ClassName("me-0")
            val XSMALL: ClassName = ClassName("me-xs")
            val SMALL: ClassName = ClassName("me-s")
            val MEDIUM: ClassName = ClassName("me-m")
            val LARGE: ClassName = ClassName("me-l")
            val XLARGE: ClassName = ClassName("me-xl")
            val AUTO: ClassName = ClassName("me-auto")
        }

        object Horizontal {
            /** Lumo utility classes of the `Horizontal` group. */
            val NONE: ClassName = ClassName("mx-0")
            val XSMALL: ClassName = ClassName("mx-xs")
            val SMALL: ClassName = ClassName("mx-s")
            val MEDIUM: ClassName = ClassName("mx-m")
            val LARGE: ClassName = ClassName("mx-l")
            val XLARGE: ClassName = ClassName("mx-xl")
            val AUTO: ClassName = ClassName("mx-auto")
        }

        object Left {
            /** Lumo utility classes of the `Left` group. */
            val NONE: ClassName = ClassName("ml-0")
            val XSMALL: ClassName = ClassName("ml-xs")
            val SMALL: ClassName = ClassName("ml-s")
            val MEDIUM: ClassName = ClassName("ml-m")
            val LARGE: ClassName = ClassName("ml-l")
            val XLARGE: ClassName = ClassName("ml-xl")
            val AUTO: ClassName = ClassName("ml-auto")
        }

        object Right {
            /** Lumo utility classes of the `Right` group. */
            val NONE: ClassName = ClassName("mr-0")
            val XSMALL: ClassName = ClassName("mr-xs")
            val SMALL: ClassName = ClassName("mr-s")
            val MEDIUM: ClassName = ClassName("mr-m")
            val LARGE: ClassName = ClassName("mr-l")
            val XLARGE: ClassName = ClassName("mr-xl")
            val AUTO: ClassName = ClassName("mr-auto")
        }

        object Start {
            /** Lumo utility classes of the `Start` group. */
            val NONE: ClassName = ClassName("ms-0")
            val XSMALL: ClassName = ClassName("ms-xs")
            val SMALL: ClassName = ClassName("ms-s")
            val MEDIUM: ClassName = ClassName("ms-m")
            val LARGE: ClassName = ClassName("ms-l")
            val XLARGE: ClassName = ClassName("ms-xl")
            val AUTO: ClassName = ClassName("ms-auto")
        }

        object Top {
            /** Lumo utility classes of the `Top` group. */
            val NONE: ClassName = ClassName("mt-0")
            val XSMALL: ClassName = ClassName("mt-xs")
            val SMALL: ClassName = ClassName("mt-s")
            val MEDIUM: ClassName = ClassName("mt-m")
            val LARGE: ClassName = ClassName("mt-l")
            val XLARGE: ClassName = ClassName("mt-xl")
            val AUTO: ClassName = ClassName("mt-auto")
        }

        object Vertical {
            /** Lumo utility classes of the `Vertical` group. */
            val NONE: ClassName = ClassName("my-0")
            val XSMALL: ClassName = ClassName("my-xs")
            val SMALL: ClassName = ClassName("my-s")
            val MEDIUM: ClassName = ClassName("my-m")
            val LARGE: ClassName = ClassName("my-l")
            val XLARGE: ClassName = ClassName("my-xl")
            val AUTO: ClassName = ClassName("my-auto")
        }

        object Minus {
            /** Lumo utility classes of the `Minus` group. */
            object Bottom {
                /** Lumo utility classes of the `Bottom` group. */
                val XSMALL: ClassName = ClassName("-mb-xs")
                val SMALL: ClassName = ClassName("-mb-s")
                val MEDIUM: ClassName = ClassName("-mb-m")
                val LARGE: ClassName = ClassName("-mb-l")
                val XLARGE: ClassName = ClassName("-mb-xl")
            }
            object End {
                /** Lumo utility classes of the `End` group. */
                val XSMALL: ClassName = ClassName("-me-xs")
                val SMALL: ClassName = ClassName("-me-s")
                val MEDIUM: ClassName = ClassName("-me-m")
                val LARGE: ClassName = ClassName("-me-l")
                val XLARGE: ClassName = ClassName("-me-xl")
            }
            object Horizontal {
                /** Lumo utility classes of the `Horizontal` group. */
                val XSMALL: ClassName = ClassName("-mx-xs")
                val SMALL: ClassName = ClassName("-mx-s")
                val MEDIUM: ClassName = ClassName("-mx-m")
                val LARGE: ClassName = ClassName("-mx-l")
                val XLARGE: ClassName = ClassName("-mx-xl")
            }
            object Left {
                /** Lumo utility classes of the `Left` group. */
                val XSMALL: ClassName = ClassName("-ml-xs")
                val SMALL: ClassName = ClassName("-ml-s")
                val MEDIUM: ClassName = ClassName("-ml-m")
                val LARGE: ClassName = ClassName("-ml-l")
                val XLARGE: ClassName = ClassName("-ml-xl")
            }
            object Right {
                /** Lumo utility classes of the `Right` group. */
                val XSMALL: ClassName = ClassName("-mr-xs")
                val SMALL: ClassName = ClassName("-mr-s")
                val MEDIUM: ClassName = ClassName("-mr-m")
                val LARGE: ClassName = ClassName("-mr-l")
                val XLARGE: ClassName = ClassName("-mr-xl")
            }
            object Start {
                /** Lumo utility classes of the `Start` group. */
                val XSMALL: ClassName = ClassName("-ms-xs")
                val SMALL: ClassName = ClassName("-ms-s")
                val MEDIUM: ClassName = ClassName("-ms-m")
                val LARGE: ClassName = ClassName("-ms-l")
                val XLARGE: ClassName = ClassName("-ms-xl")
            }
            object Top {
                /** Lumo utility classes of the `Top` group. */
                val XSMALL: ClassName = ClassName("-mt-xs")
                val SMALL: ClassName = ClassName("-mt-s")
                val MEDIUM: ClassName = ClassName("-mt-m")
                val LARGE: ClassName = ClassName("-mt-l")
                val XLARGE: ClassName = ClassName("-mt-xl")
            }
            object Vertical {
                /** Lumo utility classes of the `Vertical` group. */
                val XSMALL: ClassName = ClassName("-my-xs")
                val SMALL: ClassName = ClassName("-my-s")
                val MEDIUM: ClassName = ClassName("-my-m")
                val LARGE: ClassName = ClassName("-my-l")
                val XLARGE: ClassName = ClassName("-my-xl")
            }
        }
    }
    object MaxHeight {
        /** Lumo utility classes of the `MaxHeight` group. */
        val FULL: ClassName = ClassName("max-h-full")
        val SCREEN: ClassName = ClassName("max-h-screen")
    }
    object MaxWidth {
        /** Lumo utility classes of the `MaxWidth` group. */
        val FULL: ClassName = ClassName("max-w-full")
        val SCREEN_SMALL: ClassName = ClassName("max-w-screen-sm")
        val SCREEN_MEDIUM: ClassName = ClassName("max-w-screen-md")
        val SCREEN_LARGE: ClassName = ClassName("max-w-screen-lg")
        val SCREEN_XLARGE: ClassName = ClassName("max-w-screen-xl")
        val SCREEN_XXLARGE: ClassName = ClassName("max-w-screen-2xl")
    }
    object MinHeight {
        /** Lumo utility classes of the `MinHeight` group. */
        val NONE: ClassName = ClassName("min-h-0")
        val FULL: ClassName = ClassName("min-h-full")
        val SCREEN: ClassName = ClassName("min-h-screen")
    }
    object MinWidth {
        /** Lumo utility classes of the `MinWidth` group. */
        val NONE: ClassName = ClassName("min-w-0")
        val FULL: ClassName = ClassName("min-w-full")
    }
    object Overflow {
        /** Lumo utility classes of the `Overflow` group. */
        val AUTO: ClassName = ClassName("overflow-auto")
        val HIDDEN: ClassName = ClassName("overflow-hidden")
        val SCROLL: ClassName = ClassName("overflow-scroll")
    }
    object Padding {
        /** Lumo utility classes of the `Padding` group. */
        val NONE: ClassName = ClassName("p-0")
        val XSMALL: ClassName = ClassName("p-xs")
        val SMALL: ClassName = ClassName("p-s")
        val MEDIUM: ClassName = ClassName("p-m")
        val LARGE: ClassName = ClassName("p-l")
        val XLARGE: ClassName = ClassName("p-xl")

        object Bottom {
            /** Lumo utility classes of the `Bottom` group. */
            val NONE: ClassName = ClassName("pb-0")
            val XSMALL: ClassName = ClassName("pb-xs")
            val SMALL: ClassName = ClassName("pb-s")
            val MEDIUM: ClassName = ClassName("pb-m")
            val LARGE: ClassName = ClassName("pb-l")
            val XLARGE: ClassName = ClassName("pb-xl")
        }

        object End {
            /** Lumo utility classes of the `End` group. */
            val NONE: ClassName = ClassName("pe-0")
            val XSMALL: ClassName = ClassName("pe-xs")
            val SMALL: ClassName = ClassName("pe-s")
            val MEDIUM: ClassName = ClassName("pe-m")
            val LARGE: ClassName = ClassName("pe-l")
            val XLARGE: ClassName = ClassName("pe-xl")
        }

        object Horizontal {
            /** Lumo utility classes of the `Horizontal` group. */
            val NONE: ClassName = ClassName("px-0")
            val XSMALL: ClassName = ClassName("px-xs")
            val SMALL: ClassName = ClassName("px-s")
            val MEDIUM: ClassName = ClassName("px-m")
            val LARGE: ClassName = ClassName("px-l")
            val XLARGE: ClassName = ClassName("px-xl")
        }

        object Left {
            /** Lumo utility classes of the `Left` group. */
            val NONE: ClassName = ClassName("pl-0")
            val XSMALL: ClassName = ClassName("pl-xs")
            val SMALL: ClassName = ClassName("pl-s")
            val MEDIUM: ClassName = ClassName("pl-m")
            val LARGE: ClassName = ClassName("pl-l")
            val XLARGE: ClassName = ClassName("pl-xl")
        }

        object Right {
            /** Lumo utility classes of the `Right` group. */
            val NONE: ClassName = ClassName("pr-0")
            val XSMALL: ClassName = ClassName("pr-xs")
            val SMALL: ClassName = ClassName("pr-s")
            val MEDIUM: ClassName = ClassName("pr-m")
            val LARGE: ClassName = ClassName("pr-l")
            val XLARGE: ClassName = ClassName("pr-xl")
        }

        object Start {
            /** Lumo utility classes of the `Start` group. */
            val NONE: ClassName = ClassName("ps-0")
            val XSMALL: ClassName = ClassName("ps-xs")
            val SMALL: ClassName = ClassName("ps-s")
            val MEDIUM: ClassName = ClassName("ps-m")
            val LARGE: ClassName = ClassName("ps-l")
            val XLARGE: ClassName = ClassName("ps-xl")
        }

        object Top {
            /** Lumo utility classes of the `Top` group. */
            val NONE: ClassName = ClassName("pt-0")
            val XSMALL: ClassName = ClassName("pt-xs")
            val SMALL: ClassName = ClassName("pt-s")
            val MEDIUM: ClassName = ClassName("pt-m")
            val LARGE: ClassName = ClassName("pt-l")
            val XLARGE: ClassName = ClassName("pt-xl")
        }

        object Vertical {
            /** Lumo utility classes of the `Vertical` group. */
            val NONE: ClassName = ClassName("py-0")
            val XSMALL: ClassName = ClassName("py-xs")
            val SMALL: ClassName = ClassName("py-s")
            val MEDIUM: ClassName = ClassName("py-m")
            val LARGE: ClassName = ClassName("py-l")
            val XLARGE: ClassName = ClassName("py-xl")
        }
    }
    object Position {
        /** Lumo utility classes of the `Position` group. */
        val ABSOLUTE: ClassName = ClassName("absolute")
        val FIXED: ClassName = ClassName("fixed")
        val RELATIVE: ClassName = ClassName("relative")
        val STATIC: ClassName = ClassName("static")
        val STICKY: ClassName = ClassName("sticky")

        object Bottom {
            /** Lumo utility classes of the `Bottom` group. */
            val NONE: ClassName = ClassName("bottom-0")
            val XSMALL: ClassName = ClassName("bottom-xs")
            val SMALL: ClassName = ClassName("bottom-s")
            val MEDIUM: ClassName = ClassName("bottom-m")
            val LARGE: ClassName = ClassName("bottom-l")
            val XLARGE: ClassName = ClassName("bottom-xl")
            val AUTO: ClassName = ClassName("bottom-auto")
            val FULL: ClassName = ClassName("bottom-full")
        }

        object End {
            /** Lumo utility classes of the `End` group. */
            val NONE: ClassName = ClassName("end-0")
            val XSMALL: ClassName = ClassName("end-xs")
            val SMALL: ClassName = ClassName("end-s")
            val MEDIUM: ClassName = ClassName("end-m")
            val LARGE: ClassName = ClassName("end-l")
            val XLARGE: ClassName = ClassName("end-xl")
            val AUTO: ClassName = ClassName("end-auto")
            val FULL: ClassName = ClassName("end-full")
        }

        object Start {
            /** Lumo utility classes of the `Start` group. */
            val NONE: ClassName = ClassName("start-0")
            val XSMALL: ClassName = ClassName("start-xs")
            val SMALL: ClassName = ClassName("start-s")
            val MEDIUM: ClassName = ClassName("start-m")
            val LARGE: ClassName = ClassName("start-l")
            val XLARGE: ClassName = ClassName("start-xl")
            val AUTO: ClassName = ClassName("start-auto")
            val FULL: ClassName = ClassName("start-full")
        }

        object Top {
            /** Lumo utility classes of the `Top` group. */
            val NONE: ClassName = ClassName("top-0")
            val XSMALL: ClassName = ClassName("top-xs")
            val SMALL: ClassName = ClassName("top-s")
            val MEDIUM: ClassName = ClassName("top-m")
            val LARGE: ClassName = ClassName("top-l")
            val XLARGE: ClassName = ClassName("top-xl")
            val AUTO: ClassName = ClassName("top-auto")
            val FULL: ClassName = ClassName("top-full")
        }

        object Minus {
            /** Lumo utility classes of the `Minus` group. */
            object Bottom {
                /** Lumo utility classes of the `Bottom` group. */
                val XSMALL: ClassName = ClassName("-bottom-xs")
                val SMALL: ClassName = ClassName("-bottom-s")
                val MEDIUM: ClassName = ClassName("-bottom-m")
                val LARGE: ClassName = ClassName("-bottom-l")
                val XLARGE: ClassName = ClassName("-bottom-xl")
                val FULL: ClassName = ClassName("-bottom-full")
            }
            object End {
                /** Lumo utility classes of the `End` group. */
                val XSMALL: ClassName = ClassName("-end-xs")
                val SMALL: ClassName = ClassName("-end-s")
                val MEDIUM: ClassName = ClassName("-end-m")
                val LARGE: ClassName = ClassName("-end-l")
                val XLARGE: ClassName = ClassName("-end-xl")
                val FULL: ClassName = ClassName("-end-full")
            }
            object Start {
                /** Lumo utility classes of the `Start` group. */
                val XSMALL: ClassName = ClassName("-start-xs")
                val SMALL: ClassName = ClassName("-start-s")
                val MEDIUM: ClassName = ClassName("-start-m")
                val LARGE: ClassName = ClassName("-start-l")
                val XLARGE: ClassName = ClassName("-start-xl")
                val FULL: ClassName = ClassName("-start-full")
            }
            object Top {
                /** Lumo utility classes of the `Top` group. */
                val XSMALL: ClassName = ClassName("-top-xs")
                val SMALL: ClassName = ClassName("-top-s")
                val MEDIUM: ClassName = ClassName("-top-m")
                val LARGE: ClassName = ClassName("-top-l")
                val XLARGE: ClassName = ClassName("-top-xl")
                val FULL: ClassName = ClassName("-top-full")
            }
        }

        object Breakpoint {
            /** Lumo utility classes of the `Breakpoint` group. */
            object Small {
                /** Lumo utility classes of the `Small` group. */
                val ABSOLUTE: ClassName = ClassName("sm:absolute")
                val FIXED: ClassName = ClassName("sm:fixed")
                val RELATIVE: ClassName = ClassName("sm:relative")
                val STATIC: ClassName = ClassName("sm:static")
                val STICKY: ClassName = ClassName("sm:sticky")
            }
            object Medium {
                /** Lumo utility classes of the `Medium` group. */
                val ABSOLUTE: ClassName = ClassName("md:absolute")
                val FIXED: ClassName = ClassName("md:fixed")
                val RELATIVE: ClassName = ClassName("md:relative")
                val STATIC: ClassName = ClassName("md:static")
                val STICKY: ClassName = ClassName("md:sticky")
            }
            object Large {
                /** Lumo utility classes of the `Large` group. */
                val ABSOLUTE: ClassName = ClassName("lg:absolute")
                val FIXED: ClassName = ClassName("lg:fixed")
                val RELATIVE: ClassName = ClassName("lg:relative")
                val STATIC: ClassName = ClassName("lg:static")
                val STICKY: ClassName = ClassName("lg:sticky")
            }
            object XLarge {
                /** Lumo utility classes of the `XLarge` group. */
                val ABSOLUTE: ClassName = ClassName("xl:absolute")
                val FIXED: ClassName = ClassName("xl:fixed")
                val RELATIVE: ClassName = ClassName("xl:relative")
                val STATIC: ClassName = ClassName("xl:static")
                val STICKY: ClassName = ClassName("xl:sticky")
            }
            object XXLarge {
                /** Lumo utility classes of the `XXLarge` group. */
                val ABSOLUTE: ClassName = ClassName("2xl:absolute")
                val FIXED: ClassName = ClassName("2xl:fixed")
                val RELATIVE: ClassName = ClassName("2xl:relative")
                val STATIC: ClassName = ClassName("2xl:static")
                val STICKY: ClassName = ClassName("2xl:sticky")
            }
        }
    }
    object TextAlignment {
        /** Lumo utility classes of the `TextAlignment` group. */
        val LEFT: ClassName = ClassName("text-left")
        val CENTER: ClassName = ClassName("text-center")
        val RIGHT: ClassName = ClassName("text-right")
        val JUSTIFY: ClassName = ClassName("text-justify")
    }
    object TextColor {
        /** Lumo utility classes of the `TextColor` group. */
        val HEADER: ClassName = ClassName("text-header")
        val BODY: ClassName = ClassName("text-body")
        val SECONDARY: ClassName = ClassName("text-secondary")
        val TERTIARY: ClassName = ClassName("text-tertiary")
        val DISABLED: ClassName = ClassName("text-disabled")
        val PRIMARY: ClassName = ClassName("text-primary")
        val PRIMARY_CONTRAST: ClassName = ClassName("text-primary-contrast")
        val ERROR: ClassName = ClassName("text-error")
        val ERROR_CONTRAST: ClassName = ClassName("text-error-contrast")
        val WARNING: ClassName = ClassName("text-warning")
        val WARNING_CONTRAST: ClassName = ClassName("text-warning-contrast")
        val SUCCESS: ClassName = ClassName("text-success")
        val SUCCESS_CONTRAST: ClassName = ClassName("text-success-contrast")
    }
    object TextOverflow {
        /** Lumo utility classes of the `TextOverflow` group. */
        val CLIP: ClassName = ClassName("overflow-clip")
        val ELLIPSIS: ClassName = ClassName("overflow-ellipsis")
    }
    object TextTransform {
        /** Lumo utility classes of the `TextTransform` group. */
        val CAPITALIZE: ClassName = ClassName("capitalize")
        val LOWERCASE: ClassName = ClassName("lowercase")
        val UPPERCASE: ClassName = ClassName("uppercase")
    }
    object Transition {
        /** Lumo utility classes of the `Transition` group. */
        val NONE: ClassName = ClassName("transition-none")
        val ALL: ClassName = ClassName("transition-all")
        val DEFAULT: ClassName = ClassName("transition")
        val COLORS: ClassName = ClassName("transition-colors")
        val OPACITY: ClassName = ClassName("transition-opacity")
        val SHADOW: ClassName = ClassName("transition-shadow")
        val TRANSFORM: ClassName = ClassName("transition-transform")
    }
    object Whitespace {
        /** Lumo utility classes of the `Whitespace` group. */
        val NORMAL: ClassName = ClassName("whitespace-normal")
        val NOWRAP: ClassName = ClassName("whitespace-nowrap")
        val PRE: ClassName = ClassName("whitespace-pre")
        val PRE_LINE: ClassName = ClassName("whitespace-pre-line")
        val PRE_WRAP: ClassName = ClassName("whitespace-pre-wrap")
    }
    object Width {
        /** Lumo utility classes of the `Width` group. */
        val XSMALL: ClassName = ClassName("w-xs")
        val SMALL: ClassName = ClassName("w-s")
        val MEDIUM: ClassName = ClassName("w-m")
        val LARGE: ClassName = ClassName("w-l")
        val XLARGE: ClassName = ClassName("w-xl")
        val AUTO: ClassName = ClassName("w-auto")
        val FULL: ClassName = ClassName("w-full")
    }
    object ZIndex {
        /** Lumo utility classes of the `ZIndex` group. */
        val NONE: ClassName = ClassName("z-0")
        val XSMALL: ClassName = ClassName("z-10")
        val SMALL: ClassName = ClassName("z-20")
        val MEDIUM: ClassName = ClassName("z-30")
        val LARGE: ClassName = ClassName("z-40")
        val XLARGE: ClassName = ClassName("z-50")
        val AUTO: ClassName = ClassName("z-auto")
    }
}
