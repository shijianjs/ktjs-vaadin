package vaadin.scroller

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.computedStyle
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextFrame
import vaadin.nextRender
import vaadin.nextResize
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/scroller/test/scroller.test.js`.
 *
 * The upstream keyboard cases use `sendKeys` from `@vaadin/test-runner-commands`; they need a real
 * key event sequence through the browser, which the Kotlin test DSL does not provide, so focus is
 * driven programmatically instead (same assertions, no synthetic Tab). The `overflow` attribute
 * cases keep the upstream layout setup: a fixed-size font and a child that does not fit.
 */
class ScrollerTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newScroller(): ScrollerElement = attach(createScroller())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerScroller()
        val scroller = newScroller()

        assertEquals("vaadin-scroller", scroller.tagName.lowercase())
        assertEquals("vaadin-scroller", js("(ctor) => ctor.is")(ctor) as String)
    }

    // focus

    @Test
    fun addsFocusRingOnProgrammaticFocus(): Promise<Unit> {
        val scroller = newScroller()

        return nextRender(scroller).then {
            js("(element) => element.focus()")(scroller)
            assertTrue(scroller.hasAttribute("focus-ring"))
        }
    }

    @Test
    fun doesNotAddFocusRingForFocusVisibleFalse(): Promise<Unit> {
        val scroller = newScroller()

        return nextRender(scroller).then {
            js("(element) => element.focus({ focusVisible: false })")(scroller)
            assertTrue(!scroller.hasAttribute("focus-ring"))
        }
    }

    // scrollDirection

    @Test
    fun reflectsTheScrollDirectionToTheAttribute(): Promise<Unit> {
        val scroller = newScroller()

        return nextRender(scroller)
            .then {
                scroller.scrollDirection = "horizontal"
                nextUpdate(scroller)
            }
            .then { assertEquals("horizontal", scroller.getAttribute("scroll-direction")) }
    }

    @Test
    fun showsBothScrollbarsByDefault(): Promise<Unit> {
        val scroller = newScroller()

        return nextRender(scroller).then {
            assertEquals("auto", computedStyle(scroller, "overflowX"))
            assertEquals("auto", computedStyle(scroller, "overflowY"))
        }
    }

    @Test
    fun supportsVerticalOnlyScrollbars(): Promise<Unit> {
        val scroller = newScroller()

        return nextRender(scroller)
            .then {
                scroller.setAttribute("scroll-direction", "vertical")
                nextUpdate(scroller)
            }
            .then {
                assertEquals("auto", computedStyle(scroller, "overflowY"))
                assertEquals("hidden", computedStyle(scroller, "overflowX"))
            }
    }

    @Test
    fun supportsHorizontalOnlyScrollbars(): Promise<Unit> {
        val scroller = newScroller()
        scroller.setAttribute("scroll-direction", "horizontal")

        return nextRender(scroller).then {
            assertEquals("auto", computedStyle(scroller, "overflowX"))
            assertEquals("hidden", computedStyle(scroller, "overflowY"))
        }
    }

    @Test
    fun supportsDisablingBothScrollbars(): Promise<Unit> {
        val scroller = newScroller()
        scroller.setAttribute("scroll-direction", "none")

        return nextRender(scroller).then {
            assertEquals("hidden", computedStyle(scroller, "overflowX"))
            assertEquals("hidden", computedStyle(scroller, "overflowY"))
        }
    }

    // overflow attribute

    private fun horizontalOverflowScroller(): Promise<ScrollerElement> {
        val scroller = newScroller()
        scroller.scrollDirection = "horizontal"
        js("(element) => { element.style.fontSize = '15px'; element.style.maxWidth = '6.75em'; }")(scroller)
        val content = document.createElement("div")
        js("(element) => { element.textContent = 'Long text that does not fit'; }")(content)
        js("(element) => { element.style.fontSize = '1.25em'; element.style.whiteSpace = 'nowrap'; }")(content)
        scroller.appendChild(content)

        return nextRender(scroller).then { nextResize(scroller) }.then { nextFrame() }.then { scroller }
    }

    @Test
    fun setsOverflowToEndAtTheBeginning(): Promise<Unit> = horizontalOverflowScroller().then { scroller ->
        assertEquals("end", scroller.getAttribute("overflow"))
    }

    @Test
    fun setsOverflowToStartEndInTheMiddle(): Promise<Unit> = horizontalOverflowScroller()
        .then { scroller ->
            js("(element) => { element.scrollLeft = 50; }")(scroller)
            nextFrame().then { scroller }
        }
        .then { scroller -> assertEquals("start end", scroller.getAttribute("overflow")) }

    @Test
    fun setsOverflowToStartAtTheEnd(): Promise<Unit> = horizontalOverflowScroller()
        .then { scroller ->
            js("(element) => { element.scrollLeft = element.scrollWidth - element.clientWidth; }")(scroller)
            nextFrame().then { scroller }
        }
        .then { scroller -> assertEquals("start", scroller.getAttribute("overflow")) }

    private fun verticalOverflowScroller(): Promise<ScrollerElement> {
        val scroller = newScroller()
        scroller.scrollDirection = "vertical"
        js("(element) => { element.style.fontSize = '15px'; element.style.maxHeight = '3.75em'; }")(scroller)
        val content = document.createElement("div")
        js("(element) => { element.innerHTML = '<div style=\"font-size: 1.25em;\">Long<br>text<br>that<br>has<br>many<br>lines</div>'; }")(
            content,
        )
        scroller.appendChild(content)

        return nextRender(scroller).then { nextResize(scroller) }.then { nextRender(scroller) }.then { nextFrame() }.then { scroller }
    }

    @Test
    fun setsOverflowToBottomAtTheBeginning(): Promise<Unit> = verticalOverflowScroller().then { scroller ->
        assertEquals("bottom", scroller.getAttribute("overflow"))
    }

    @Test
    fun setsOverflowToTopBottomInTheMiddle(): Promise<Unit> = verticalOverflowScroller()
        .then { scroller ->
            js("(element) => { element.scrollTop = 25; }")(scroller)
            nextFrame().then { scroller }
        }
        .then { scroller -> assertEquals("top bottom", scroller.getAttribute("overflow")) }

    @Test
    fun setsOverflowToTopAtTheEnd(): Promise<Unit> = verticalOverflowScroller()
        .then { scroller ->
            js("(element) => { element.scrollTop = element.scrollHeight - element.clientHeight; }")(scroller)
            nextFrame().then { scroller }
        }
        .then { scroller -> assertEquals("top", scroller.getAttribute("overflow")) }

    // layout

    @Test
    fun respectsAnExplicitHeightInsideAFlexContainer(): Promise<Unit> {
        val container = attach(
            vaadin.fixture(
                "<div style=\"display: flex; flex-direction: column; height: 500px;\">" +
                    "<vaadin-scroller style=\"height: 200px;\"><div>Content</div></vaadin-scroller></div>",
            ),
        )
        val scroller = js("(container) => container.querySelector('vaadin-scroller')")(container) as HTMLElement

        return nextRender(scroller).then {
            assertEquals(200, js("(element) => element.offsetHeight")(scroller) as Int)
        }
    }

    @Test
    fun doesNotOverflowWithFullHeightContent(): Promise<Unit> {
        val container = attach(
            vaadin.fixture(
                "<div style=\"display: flex; flex-direction: column; height: 300px;\">" +
                    "<vaadin-scroller style=\"--vaadin-scroller-padding-block: 16px;\">" +
                    "<div style=\"height: 100%;\">Content</div></vaadin-scroller></div>",
            ),
        )
        val scroller = js("(container) => container.querySelector('vaadin-scroller')")(container) as HTMLElement

        return nextRender(scroller).then {
            val fits = js(
                "(scroller) => { const content = scroller.querySelector('div');" +
                    " return content.getBoundingClientRect().bottom <= scroller.getBoundingClientRect().bottom; }",
            )(scroller) as Boolean
            assertTrue(fits, "content should not exceed the scroller")
            assertEquals(
                js("(element) => element.clientHeight")(scroller) as Int,
                js("(element) => element.scrollHeight")(scroller) as Int,
            )
        }
    }

    // Kotlin-only coverage

    @Test
    fun createScrollerAppliesTheConfigureBlock() {
        val scroller = attach(createScroller { scrollDirection = "vertical" })

        assertEquals("vertical", scroller.scrollDirection)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        val scroller = document.createElement(vaadin.VaadinTagName.scroller)
        assertEquals("vaadin-scroller", scroller.tagName.lowercase())
        assertEquals("scroll-direction", ScrollerAttributes.scrollDirection)
        assertEquals("theme", ScrollerAttributes.theme)
    }
}
