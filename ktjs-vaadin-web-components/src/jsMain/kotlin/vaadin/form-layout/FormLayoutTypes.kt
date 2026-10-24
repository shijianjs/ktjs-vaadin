package vaadin.formlayout

import js.reflect.unsafeCast

/** Union of a CSS length string and the literal `0` used by the initial responsive step. */
external interface StringOrDouble

/** Wraps a CSS length as a [StringOrDouble]. */
fun StringOrDouble(value: String): StringOrDouble = unsafeCast(value)

/** Wraps the literal `0` as a [StringOrDouble]. */
fun StringOrDouble(value: Double): StringOrDouble = unsafeCast(value)

/** One responsive step of a `<vaadin-form-layout>`. */
external interface FormLayoutResponsiveStep {
    /**
     * The minimum width of the layout for this step, as a CSS length, or `0` for the initial
     * step.
     */
    var minWidth: StringOrDouble?

    /** The number of columns for this step. */
    var columns: Double

    /** Label position for this step: `aside` or `top`. */
    var labelsPosition: String?
}
