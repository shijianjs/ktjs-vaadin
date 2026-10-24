package vaadin.themablemixin

import js.reflect.unsafeCast

/** Union of a list of module ids and a single module id, used by `registerStyles` options. */
external interface StringArrayOrString

/** Wraps a list of module ids as a [StringArrayOrString]. */
fun StringArrayOrString(value: Array<String>): StringArrayOrString = unsafeCast(value)

/** Wraps a single module id as a [StringArrayOrString]. */
fun StringArrayOrString(value: String): StringArrayOrString = unsafeCast(value)
