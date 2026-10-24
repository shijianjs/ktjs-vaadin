package vaadin

import kotlin.js.JsAny
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

/**
 * Parses [html], attaches the resulting nodes to `document.body` and returns the first element,
 * mirroring `fixtureSync` from `@vaadin/testing-helpers`. The container is attached as well, so
 * markup that relies on a parent (flex containers, `slot` children) behaves like upstream.
 */
internal fun fixture(html: String): HTMLElement {
    val container = document.createElement("div")
    js("(container, html) => { container.innerHTML = html; }")(container, html)
    document.body!!.appendChild(container)
    return (js("(container) => container.firstElementChild")(container) as HTMLElement?) ?: container
}

/** Returns the shadow root of [element] as an element, or `null` when it has none. */
internal fun shadowRootOf(element: HTMLElement): HTMLElement? =
    js("(element) => element.shadowRoot || null")(element) as HTMLElement?

/** Returns the first element matching [selector] inside the shadow root of [element]. */
internal fun shadowQuery(element: HTMLElement, selector: String): HTMLElement? =
    js("(element, selector) => element.shadowRoot ? element.shadowRoot.querySelector(selector) : null")(
        element,
        selector,
    ) as HTMLElement?

/**
 * Returns the first node matching [selector] inside the shadow root of [element] as a plain
 * [web.dom.Element]. Used for parts that are not HTML elements, such as the `<svg>` abbreviation of
 * `<vaadin-avatar>`.
 */
internal fun shadowQueryNode(element: HTMLElement, selector: String): web.dom.Element? =
    js("(element, selector) => element.shadowRoot ? element.shadowRoot.querySelector(selector) : null")(
        element,
        selector,
    ) as web.dom.Element?

/** Returns every element matching [selector] inside the shadow root of [element]. */
internal fun shadowQueryAll(element: HTMLElement, selector: String): Array<HTMLElement> =
    js("(element, selector) => element.shadowRoot ? Array.from(element.shadowRoot.querySelectorAll(selector)) : []")(
        element,
        selector,
    ) as Array<HTMLElement>

/** Sets the `textContent` of [element], mirroring `element.textContent = ...`. */
internal fun text(element: HTMLElement, value: String) {
    js("(element, value) => { element.textContent = value; }")(element, value)
}

/** Clicks [element], mirroring `click()` from `@vaadin/testing-helpers`. */
internal fun click(element: HTMLElement) {
    js("(element) => element.click()")(element)
}

/** Resolves after the browser painted the next frame, mirroring `nextFrame`. */
internal fun nextFrame(): Promise<Unit> = Promise { resolve, _ ->
    js("(resolve) => requestAnimationFrame(() => resolve())")(resolve)
}

/** Resolves after the next `ResizeObserver` notification for [element], mirroring `nextResize`. */
internal fun nextResize(element: HTMLElement): Promise<Unit> = Promise { resolve, _ ->
    val observe: (HTMLElement, () -> Unit) -> Unit =
        js(
            "(element, callback) => {" +
                " const observer = new ResizeObserver(() => { observer.disconnect(); callback(); });" +
                " observer.observe(element);" +
                "}",
        )
    observe(element) { resolve(Unit) }
}

/** Resolves after the element finished its update cycle, mirroring `nextUpdate`. */
internal fun nextUpdate(element: HTMLElement): Promise<Unit> {
    val updateComplete = js("(element) => element.updateComplete || null")(element)
    if (updateComplete == null) return nextFrame()
    return (updateComplete as Promise<Any?>).then { }
}

/** Resolves after the element rendered and the browser painted, mirroring `nextRender`. */
internal fun nextRender(element: HTMLElement): Promise<Unit> = nextUpdate(element).then { nextFrame() }

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

/**
 * Registers [handler] for the custom event [type] on [element].
 *
 * Custom event names have no entry in `web.events`, and the typed `addEventListener` overloads
 * resolve the event type through `EventHandler`, which does not accept a plain `() -> Unit`
 * counter. The test helper stays on the string form so the assertions read like the upstream ones.
 */
internal fun <T : HTMLElement> on(element: T, type: String, handler: (web.events.Event) -> Unit) {
    js("(element, type, handler) => element.addEventListener(type, handler)")(element, type, handler)
}

/** Sets a CSS custom property on [element], used by the style-driven upstream assertions. */
internal fun setStyleProperty(element: HTMLElement, name: String, value: String) {
    js("(element, name, value) => element.style.setProperty(name, value)")(element, name, value)
}

/** Reads a computed CSS property of [element], mirroring `getComputedStyle(element).getPropertyValue()`. */
internal fun computedStyleProperty(element: HTMLElement, name: String): String =
    js("(element, name) => getComputedStyle(element).getPropertyValue(name)")(element, name) as String

/** Reads a computed CSS property of [element] in camelCase form, mirroring `getComputedStyle(element)[prop]`. */
internal fun computedStyle(element: HTMLElement, property: String): String =
    js("(element, property) => getComputedStyle(element)[property]")(element, property) as String

/** Dispatches a `PointerEvent` of [type] with [init] applied, mirroring `fire(...)` from the test helpers. */
internal fun firePointer(element: HTMLElement, type: String, init: JsAny? = null): Boolean =
    js("(element, type, init) => element.dispatchEvent(new PointerEvent(type, Object.assign({ bubbles: true, composed: true, cancelable: true }, init)))")(
        element,
        type,
        init,
    ) as Boolean

/** Dispatches a `MouseEvent` of [type] with [init] applied. */
internal fun fireMouse(element: HTMLElement, type: String, init: JsAny? = null): Boolean =
    js("(element, type, init) => element.dispatchEvent(new MouseEvent(type, Object.assign({ bubbles: true, composed: true, cancelable: true }, init)))")(
        element,
        type,
        init,
    ) as Boolean

/** Dispatches a `TouchEvent` of [type] with [init] applied. */
internal fun fireTouch(element: HTMLElement, type: String, init: JsAny? = null): Boolean =
    js("(element, type, init) => element.dispatchEvent(new TouchEvent(type, Object.assign({ bubbles: true, composed: true, cancelable: true }, init)))")(
        element,
        type,
        init,
    ) as Boolean

/** Dispatches a `FocusEvent` of [type], mirroring the focus/blur helpers of the upstream suite. */
internal fun fireFocus(element: HTMLElement, type: String): Boolean =
    js("(element, type) => element.dispatchEvent(new FocusEvent(type, { bubbles: false, composed: true }))")(element, type) as Boolean
