package demo.ui

import demo.tr
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import web.events.Event
import web.events.EventTargetLike
import web.events.EventType
import web.events.addEventListener

/**
 * Subscribes [handler] to the Vaadin custom event [type].
 *
 * The generators emit typed `EventType` constants (`TextFieldEvents.valueChanged`, ...) so the
 * subscription stays checked; this alias just keeps call sites short.
 */
fun <E : Event> EventTargetLike.listen(type: EventType<E>, handler: (E) -> Unit) {
    addEventListener(type, handler)
}

/** Reads a boolean for the read-outs of the examples. */
fun describeBoolean(value: Boolean): String = if (value) tr("是", "Yes") else tr("否", "No")

/**
 * Joins an array (Kotlin or JS) of primitive values for display, or stringifies a single value.
 *
 * Tolerates `null` / `undefined`, so it can be used directly on properties that may be unset.
 */
fun joinValues(values: Any?): String =
    js("(values) => values == null ? '' : (Array.isArray(values) ? values.join(', ') : String(values))")(values) as String

/**
 * Renders the `detail` payload of a Vaadin event as text.
 *
 * Vaadin custom events carry `detail.value` / `detail.item`; the generated `EventType`s are typed
 * as plain `Event` (the manifest does not describe the payload), so the read-out goes through the
 * one place that knows the shape.
 */
fun detailText(event: Event): String =
    js(
        "(event) => {" +
            " const detail = event.detail;" +
            " if (detail == null) return '';" +
            " if (typeof detail !== 'object') return String(detail);" +
            " if ('value' in detail) return String(detail.value);" +
            " if ('item' in detail) return String(detail.item);" +
            " return JSON.stringify(detail);" +
            "}",
    )(event) as String

/**
 * Parses [json] into a JS value for the wrapper APIs that take plain objects (`items`,
 * `responsiveSteps`, notification options, ...). The text must be real JSON, quoted keys included.
 */
fun jsObject(json: String): JsAny = js("(text) => JSON.parse(text)")(json)

/**
 * Parses a JSON array into the exact array type the wrapper property expects
 * (`Array<JsAny>`, `Array<JsAny?>`, `Array<MenuBarItem>`, ...).
 */
inline fun <reified T> jsArray(json: String): Array<T> = jsObject(json).unsafeCast<Array<T>>()
