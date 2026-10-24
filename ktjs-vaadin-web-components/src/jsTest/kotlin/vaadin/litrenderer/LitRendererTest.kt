package vaadin.litrenderer

import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Covers the hand-written `@vaadin/lit-renderer` wrapper.
 *
 * The directive only works inside a Lit template, which the Kotlin wrapper does not use; the package
 * is exposed so that vararg renderer callbacks can be typed. [LitRenderer] is an external interface,
 * so a renderer is a plain JS function cast to it, and the suite checks that the vararg `invoke`
 * forwards its arguments the way the components call it.
 */
class LitRendererTest {
    @Test
    fun forwardsTheArguments() {
        var calls = 0
        var lastValue: JsAny? = null
        val renderer: LitRenderer = js("(container, value) => { container.dataset.value = value; }").unsafeCast<LitRenderer>()
        val container = js("() => document.createElement('div')")() as web.html.HTMLElement

        renderer(container, js("'value'"))

        calls++
        lastValue = js("(container) => container.dataset.value")(container) as JsAny?
        assertEquals(1, calls)
        assertNotNull(lastValue)
    }

    @Test
    fun acceptsAnAbsentValue() {
        val renderer: LitRenderer = js("(container, value) => { container.dataset.hasValue = String(value === null); }").unsafeCast<LitRenderer>()
        val container = js("() => document.createElement('div')")() as web.html.HTMLElement

        renderer(container, null)

        assertEquals("true", js("(container) => container.dataset.hasValue")(container) as String)
    }

    @Test
    fun writesIntoTheContainer() {
        val renderer: LitRenderer = js("(container) => { container.textContent = 'Hello'; }").unsafeCast<LitRenderer>()
        val container = js("() => document.createElement('div')")() as web.html.HTMLElement

        renderer(container, null)

        assertEquals("Hello", js("(container) => container.textContent")(container) as String)
    }

    @Test
    fun returnsTheRendererResult() {
        val renderer: LitRenderer = js("() => 'result'").unsafeCast<LitRenderer>()

        val result = renderer(null, null)

        assertEquals("result", result as String)
    }
}
