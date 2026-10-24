package vaadin

import kotlin.js.Promise
import kotlin.js.js
import web.dom.document
import web.html.HTMLElement
import web.window.window

/**
 * Helpers shared by the tests translated from the upstream Vaadin web component suites.
 *
 * The upstream tests use `@vaadin/testing-helpers`; the Kotlin equivalents are kept small and
 * dependency-free. Everything here is test-only, so the few `js(...)` calls are deliberate: they
 * reach browser APIs that have no typed wrapper yet.
 */

/** Appends [element] to `document.body` and returns it, mirroring `fixtureSync`. */
internal fun <T : HTMLElement> attach(element: T): T {
    document.body!!.appendChild(element)
    return element
}

/** Removes every element previously attached by a test, mirroring the upstream `afterEach`. */
internal fun detachAll(elements: Collection<HTMLElement>) {
    elements.forEach { it.remove() }
}

/** Resolves after the browser painted the next frame, mirroring `nextFrame`. */
internal fun nextFrame(): Promise<Unit> = Promise { resolve, _ ->
    js("(resolve) => requestAnimationFrame(() => resolve())")(resolve)
}

/** Resolves after the element finished its update cycle, mirroring `nextUpdate`. */
internal fun nextUpdate(element: HTMLElement): Promise<Unit> {
    val updateComplete = js("(element) => element.updateComplete || null")(element)
    if (updateComplete == null) return nextFrame()
    return (updateComplete as Promise<Any?>).then { }
}

/** Resolves after the current microtask queue drains. */
internal fun nextMicrotask(): Promise<Unit> = Promise.resolve(Unit)

/** Resolves after [milliseconds], mirroring `aTimeout`. */
internal fun timeout(milliseconds: Int): Promise<Unit> = Promise { resolve, _ ->
    js("(resolve, ms) => setTimeout(() => resolve(), ms)")(resolve, milliseconds)
}

/** Dispatches a bubbling, composed `CustomEvent` with [detail] and returns it. */
internal fun dispatchCustomEvent(element: HTMLElement, type: String, detail: Any? = null): web.events.Event {
    val event = js("(element, type, detail) => element.dispatchEvent(new CustomEvent(type, { detail }))")(element, type, detail)
    return event as web.events.Event
}

/** Sends a `keydown` with [key] to [element]. */
internal fun keyDown(element: HTMLElement, key: String) {
    element.dispatchEvent(
        js("(key) => new KeyboardEvent('keydown', { key, bubbles: true, composed: true, cancelable: true })")(key),
    )
}

/** Sends a `keyup` with [key] to [element]. */
internal fun keyUp(element: HTMLElement, key: String) {
    element.dispatchEvent(
        js("(key) => new KeyboardEvent('keyup', { key, bubbles: true, composed: true, cancelable: true })")(key),
    )
}

/** Resolves with the first [type] event fired by [element]. */
internal fun oneEvent(element: HTMLElement, type: String): Promise<web.events.Event> = Promise { resolve, _ ->
    js("(element, type, resolve) => element.addEventListener(type, resolve, { once: true })")(element, type, resolve)
}
