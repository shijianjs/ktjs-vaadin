package vaadin.masterdetaillayout

import js.reflect.unsafeCast

/** Animation effect used when the detail area appears or disappears: `fade` or `slide`. */
sealed external interface AnimationEffect

/** Wraps a value from JavaScript. Prefer the constants in [AnimationEffects]. */
fun AnimationEffect(value: String): AnimationEffect = unsafeCast(value)

/** All values of [AnimationEffect]. */
object AnimationEffects {
    inline val fade: AnimationEffect get() = AnimationEffect("fade")
    inline val slide: AnimationEffect get() = AnimationEffect("slide")
}
