package vaadin.badge

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/badge/test/badge.test.ts`.
 *
 * The wrapper types `number` as `Double` (the manifest has no `null` in the type), so the
 * upstream cases that assign `null` / `undefined` go through `js()`; the observable behaviour
 * (`has-number` removal) is still asserted.
 */
class BadgeTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun fixture(): BadgeElement = attach(createBadge())

    // custom element definition

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerBadge()
        val badge = fixture()

        assertEquals("vaadin-badge", badge.tagName.lowercase())
        assertEquals("vaadin-badge", js("(ctor) => ctor.is")(ctor) as String)
    }

    // has-content attribute

    @Test
    fun hasContentIsNotSetByDefault() {
        assertFalse(fixture().hasAttribute("has-content"))
    }

    @Test
    fun hasContentTogglesOnTextContentChange(): Promise<Unit> {
        val badge = fixture()

        return nextUpdate(badge)
            .then {
                badge.textContent = "Text"
                nextUpdate(badge)
            }
            .then {
                assertTrue(badge.hasAttribute("has-content"))
                badge.textContent = ""
                nextUpdate(badge)
            }
            .then { assertFalse(badge.hasAttribute("has-content")) }
    }

    @Test
    fun hasContentTogglesOnElementContentChange(): Promise<Unit> {
        val badge = fixture()

        return nextUpdate(badge)
            .then {
                val span = document.createElement("span")
                span.textContent = "Content"
                badge.appendChild(span)
                nextUpdate(badge)
            }
            .then {
                assertTrue(badge.hasAttribute("has-content"))
                badge.removeChild(badge.firstElementChild!!)
                nextUpdate(badge)
            }
            .then { assertFalse(badge.hasAttribute("has-content")) }
    }

    @Test
    fun hasContentIsNotSetForWhitespaceOnlyContent(): Promise<Unit> {
        val badge = fixture()

        return nextUpdate(badge)
            .then {
                badge.textContent = "   "
                nextUpdate(badge)
            }
            .then { assertFalse(badge.hasAttribute("has-content")) }
    }

    @Test
    fun hasContentTogglesOnSlottedTextNodeDataChange(): Promise<Unit> {
        val badge = fixture()
        val text = document.createTextNode("")
        badge.appendChild(text)

        return nextFrame()
            .then {
                text.data = "Text"
                nextFrame()
            }
            .then {
                assertTrue(badge.hasAttribute("has-content"))
                text.data = ""
                nextFrame()
            }
            .then { assertFalse(badge.hasAttribute("has-content")) }
    }

    @Test
    fun hasContentIsNotSetForWhitespaceOnlyTextNode(): Promise<Unit> {
        val badge = fixture()
        val text = document.createTextNode("")
        badge.appendChild(text)

        return nextFrame()
            .then {
                text.data = "   "
                nextFrame()
            }
            .then { assertFalse(badge.hasAttribute("has-content")) }
    }

    // has-number attribute

    @Test
    fun hasNumberIsNotSetByDefault() {
        assertFalse(fixture().hasAttribute("has-number"))
    }

    @Test
    fun hasNumberIsSetWhenNumberIsAssigned(): Promise<Unit> {
        val badge = fixture()

        return nextUpdate(badge)
            .then {
                badge.number = 5.0
                nextUpdate(badge)
            }
            .then { assertTrue(badge.hasAttribute("has-number")) }
    }

    @Test
    fun hasNumberIsRemovedWhenNumberIsNull(): Promise<Unit> {
        val badge = fixture()

        return nextUpdate(badge)
            .then {
                badge.number = 5.0
                nextUpdate(badge)
            }
            .then {
                js("(element) => { element.number = null; }")(badge)
                nextUpdate(badge)
            }
            .then { assertFalse(badge.hasAttribute("has-number")) }
    }

    @Test
    fun hasNumberIsRemovedWhenNumberIsUndefined(): Promise<Unit> {
        val badge = fixture()

        return nextUpdate(badge)
            .then {
                badge.number = 5.0
                nextUpdate(badge)
            }
            .then {
                js("(element) => { element.number = undefined; }")(badge)
                nextUpdate(badge)
            }
            .then { assertFalse(badge.hasAttribute("has-number")) }
    }

    @Test
    fun hasNumberIsSetWhenNumberIsZero(): Promise<Unit> {
        val badge = fixture()

        return nextUpdate(badge)
            .then {
                badge.number = 0.0
                nextUpdate(badge)
            }
            .then { assertTrue(badge.hasAttribute("has-number")) }
    }

    // has-icon attribute

    @Test
    fun hasIconIsNotSetByDefault() {
        assertFalse(fixture().hasAttribute("has-icon"))
    }

    @Test
    fun hasIconTogglesOnIconSlotContentChange(): Promise<Unit> {
        val badge = fixture()

        return nextUpdate(badge)
            .then {
                val icon = document.createElement("span")
                icon.setAttribute("slot", "icon")
                badge.appendChild(icon)
                nextUpdate(badge)
            }
            .then {
                assertTrue(badge.hasAttribute("has-icon"))
                badge.removeChild(badge.firstElementChild!!)
                nextUpdate(badge)
            }
            .then { assertFalse(badge.hasAttribute("has-icon")) }
    }

    // state attributes on connect

    @Test
    fun hasContentIsSetSynchronouslyWhenConnectedWithText() {
        val badge = attach(createBadge { textContent = "Text" })

        assertTrue(badge.hasAttribute("has-content"))
    }

    @Test
    fun hasIconIsSetSynchronouslyWhenConnectedWithIcon() {
        val badge = createBadge()
        val icon = document.createElement("span")
        icon.setAttribute("slot", "icon")
        badge.appendChild(icon)
        attach(badge)

        assertTrue(badge.hasAttribute("has-icon"))
    }

    @Test
    fun hasContentIsSetSynchronouslyWhenContentIsSetBeforeConnecting() {
        val wrapper = attach(document.createElement("div"))
        val badge = createBadge { textContent = "Text" }
        wrapper.appendChild(badge)

        assertTrue(badge.hasAttribute("has-content"))
    }

    // Kotlin-only coverage

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        val badge = document.createElement(vaadin.VaadinTagName.badge)
        assertEquals("vaadin-badge", badge.tagName.lowercase())
        assertEquals("number", BadgeAttributes.number)
        assertEquals("theme", BadgeAttributes.theme)
    }
}
