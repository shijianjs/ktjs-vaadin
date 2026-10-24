package vaadin.formlayout

/** One responsive step of a `<vaadin-form-layout>`. */
external interface FormLayoutResponsiveStep {
    /**
     * The minimum width of the layout for this step, as a CSS length, or `0` for the initial
     * step.
     */
    var minWidth: dynamic

    /** The number of columns for this step. */
    var columns: Double

    /** Label position for this step: `aside` or `top`. */
    var labelsPosition: String?
}
