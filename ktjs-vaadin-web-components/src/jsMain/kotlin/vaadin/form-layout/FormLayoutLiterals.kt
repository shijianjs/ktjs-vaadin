package vaadin.formlayout

import js.reflect.unsafeCast

/** Position of the labels of a responsive step: `aside` or `top`. */
sealed external interface FormLayoutLabelsPosition

/** Wraps a value from JavaScript. Prefer the constants in [FormLayoutLabelsPositions]. */
fun FormLayoutLabelsPosition(value: String): FormLayoutLabelsPosition = unsafeCast(value)

/** All values of [FormLayoutLabelsPosition]. */
object FormLayoutLabelsPositions {
    inline val aside: FormLayoutLabelsPosition get() = FormLayoutLabelsPosition("aside")
    inline val top: FormLayoutLabelsPosition get() = FormLayoutLabelsPosition("top")
}
