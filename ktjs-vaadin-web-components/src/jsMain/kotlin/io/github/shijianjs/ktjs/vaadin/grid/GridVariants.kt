package io.github.shijianjs.ktjs.vaadin.grid

import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
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
    val noBorder: GridVariant = GridVariant("no-border")
    val noRowBorders: GridVariant = GridVariant("no-row-borders")
    val columnBorders: GridVariant = GridVariant("column-borders")
    val rowStripes: GridVariant = GridVariant("row-stripes")
    val compact: GridVariant = GridVariant("compact")
    val wrapCellContent: GridVariant = GridVariant("wrap-cell-content")
}
