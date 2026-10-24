package vaadin.markdown

import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import vaadin.attach
import vaadin.computedStyle
import vaadin.detachAll
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/markdown/test/markdown.test.js`, `rendering.test.js` and
 * `dom-sync.test.js`.
 *
 * The upstream suites cover the same surface through the rendered light DOM, which is what the
 * assertions below read: the element renders Markdown into its own children.
 */
class MarkdownTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerMarkdown()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newMarkdown(): MarkdownElement = attach(createMarkdown())

    private fun innerHtml(element: HTMLElement): String = js("(element) => element.innerHTML.trim()")(element) as String

    @Test
    fun isDefined() {
        val element = newMarkdown()

        assertEquals("vaadin-markdown", element.localName)
        assertEquals(true, js("(name) => !!window.customElements.get(name)")("vaadin-markdown") as Boolean)
    }

    @Test
    fun isHiddenWithTheHiddenAttribute(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element).then {
            js("(element) => { element.hidden = true; }")(element)
            assertEquals("none", computedStyle(element, "display"))
        }
    }

    @Test
    fun hasEmptyContentByDefault(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element).then { assertEquals("", innerHtml(element)) }
    }

    @Test
    fun rendersMarkdownFromTheContentProperty(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element)
            .then {
                element.content = "# Heading"
                nextUpdate(element)
            }
            .then {
                val heading = js("(element) => element.querySelector('h1')")(element) as HTMLElement?
                assertNotNull(heading)
                assertEquals("Heading", js("(element) => element.textContent")(heading) as String)
            }
    }

    @Test
    fun updatesTheRenderedMarkdown(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element)
            .then {
                element.content = "# First heading"
                nextUpdate(element)
            }
            .then {
                element.content = "# Updated heading"
                nextUpdate(element)
            }
            .then {
                val heading = js("(element) => element.querySelector('h1')")(element) as HTMLElement?
                assertEquals("Updated heading", js("(element) => element.textContent")(heading) as String)
            }
    }

    @Test
    fun clearsTheContentWhenSetToUndefined(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element)
            .then {
                element.content = "# Heading"
                nextUpdate(element)
            }
            .then {
                js("(element) => { element.content = undefined; }")(element)
                nextUpdate(element)
            }
            .then { assertEquals("", innerHtml(element)) }
    }

    @Test
    fun clearsTheContentWhenSetToNull(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element)
            .then {
                element.content = "# Heading"
                nextUpdate(element)
            }
            .then {
                js("(element) => { element.content = null; }")(element)
                nextUpdate(element)
            }
            .then { assertEquals("", innerHtml(element)) }
    }

    @Test
    fun clearsTheContentWhenSetToAnEmptyString(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element)
            .then {
                element.content = "# Heading"
                nextUpdate(element)
            }
            .then {
                element.content = ""
                nextUpdate(element)
            }
            .then { assertEquals("", innerHtml(element)) }
    }

    @Test
    fun rendersListsAndEmphasis(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element)
            .then {
                element.content = "- one\n- two\n\n**bold**"
                nextUpdate(element)
            }
            .then {
                assertEquals(2, js("(element) => element.querySelectorAll('li').length")(element) as Int)
                assertNotNull(js("(element) => element.querySelector('strong')")(element) as HTMLElement?)
            }
    }

    @Test
    fun keepsSingleLineBreaksCollapsedByDefault(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element)
            .then {
                element.content = "first\nsecond"
                nextUpdate(element)
            }
            .then {
                // Without `lineBreaks` a single newline stays a space inside one paragraph.
                assertEquals(1, js("(element) => element.querySelectorAll('p').length")(element) as Int)
                assertNull(js("(element) => element.querySelector('br')")(element) as HTMLElement?)
            }
    }

    @Test
    fun rendersLineBreaksWhenEnabled(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element)
            .then {
                element.lineBreaks = true
                element.content = "first\nsecond"
                nextUpdate(element)
            }
            .then { assertNotNull(js("(element) => element.querySelector('br')")(element) as HTMLElement?) }
    }

    // Kotlin-only coverage

    @Test
    fun createMarkdownAppliesTheConfigureBlock() {
        val element = attach(createMarkdown { content = "**bold**" })

        assertEquals("**bold**", element.content)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-markdown", document.createElement(vaadin.VaadinTagName.markdown).tagName.lowercase())
        assertEquals("content", MarkdownAttributes.content)
        assertEquals("line-breaks", MarkdownAttributes.lineBreaks)
        assertEquals("theme", MarkdownAttributes.theme)
    }

    @Test
    fun contentIsNotReflectedToTheAttribute(): Promise<Unit> {
        val element = newMarkdown()

        return nextUpdate(element)
            .then {
                element.content = "# Heading"
                nextUpdate(element)
            }
            .then { assertNull(element.getAttribute("content")) }
    }
}
