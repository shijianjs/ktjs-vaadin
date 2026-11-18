package io.github.shijianjs.ktjs.vaadin.theme

import js.reflect.unsafeCast

/**
 * Theme variants of `vaadin-grid`: mirrors Vaadin Flow's `GridVariant`.
 *
 * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and
 * several of them repeat a token that already has a current name.
 */
sealed external interface GridVariant : ThemeVariant

/** Wraps a variant name from JavaScript. Prefer the constants in [GridVariants]. */
fun GridVariant(value: String): GridVariant = unsafeCast(value)

/** All variants of [GridVariant]. */
object GridVariants {
    inline val noBorder: GridVariant get() = GridVariant("no-border")
    inline val noRowBorders: GridVariant get() = GridVariant("no-row-borders")
    inline val columnBorders: GridVariant get() = GridVariant("column-borders")
    inline val rowStripes: GridVariant get() = GridVariant("row-stripes")
    inline val compact: GridVariant get() = GridVariant("compact")
    inline val wrapCellContent: GridVariant get() = GridVariant("wrap-cell-content")
}
