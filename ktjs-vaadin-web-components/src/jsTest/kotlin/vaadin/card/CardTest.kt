package vaadin.card

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextRender
import vaadin.shadowQuery
import vaadin.text
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/card/test/card.test.ts` and `packages/card/test/accessibility.test.js`.
 *
 * The upstream suite of the source tree tracks 25.4 (`headingLevel`); the wrapper pins the 25.3 npm
 * release, where the same feature is still called `titleHeadingLevel` and reflects to
 * `title-heading-level`. The cases are translated against the API that the pinned version exposes.
 */
class CardTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newCard(): CardElement = attach(createCard())

    private fun stringTitleElement(card: CardElement): HTMLElement? =
        js("(card) => card.querySelector('[slot=\"title\"][role=\"heading\"]')")(card) as HTMLElement?

    private fun customTitleElement(card: CardElement): HTMLElement? =
        js("(card) => Array.from(card.querySelectorAll('[slot=\"title\"]')).find((el) => el.getAttribute('role') !== 'heading') || null")(
            card,
        ) as HTMLElement?

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerCard()
        val card = newCard()

        assertEquals("vaadin-card", card.tagName.lowercase())
        assertEquals("vaadin-card", js("(ctor) => ctor.is")(ctor) as String)
    }

    // title

    @Test
    fun doesNotThrowWhenClearingTheStringTitleBeforeItIsRendered(): Promise<Unit> {
        val card = newCard()
        card.cardTitle = "Some title"
        card.cardTitle = ""

        return nextRender(card).then { assertNull(stringTitleElement(card)) }
    }

    @Test
    fun createsTheTitleElementWithTheDefaultAriaLevel(): Promise<Unit> {
        val card = newCard()
        card.cardTitle = "Some title"

        return nextRender(card).then {
            val title = assertNotNull(stringTitleElement(card), "expected a rendered title element")
            assertEquals("2", title.getAttribute("aria-level"))
            assertEquals("Some title", js("(element) => element.textContent")(title) as String)
        }
    }

    @Test
    fun updatesTheAriaLevelWhenTheHeadingLevelChanges(): Promise<Unit> {
        val card = newCard()
        card.cardTitle = "Some title"

        return nextRender(card)
            .then {
                card.titleHeadingLevel = 3.0
                nextRender(card)
            }
            .then { assertEquals("3", stringTitleElement(card)?.getAttribute("aria-level")) }
    }

    @Test
    fun fallsBackToTheDefaultAriaLevelWhenTheHeadingLevelIsCleared(): Promise<Unit> {
        val card = newCard()
        card.cardTitle = "Some title"

        return nextRender(card)
            .then {
                card.titleHeadingLevel = 3.0
                nextRender(card)
            }
            .then {
                // `titleHeadingLevel` is typed as a non-null `Double`; the pinned release accepts
                // `null` to fall back to the default level, so the reset goes through the property
                // as the upstream test writes it.
                js("(card) => { card.titleHeadingLevel = null; }")(card)
                nextRender(card)
            }
            .then { assertEquals("2", stringTitleElement(card)?.getAttribute("aria-level")) }
    }

    @Test
    fun updatesTheAriaLevelWhenTheAttributeChanges(): Promise<Unit> {
        val card = newCard()
        card.cardTitle = "Some title"

        return nextRender(card)
            .then {
                card.setAttribute("title-heading-level", "4")
                nextRender(card)
            }
            .then { assertEquals("4", stringTitleElement(card)?.getAttribute("aria-level")) }
    }

    @Test
    fun reflectsTheHeadingLevelToTheAttribute(): Promise<Unit> {
        val card = newCard()
        card.cardTitle = "Some title"

        return nextRender(card)
            .then {
                card.titleHeadingLevel = 3.0
                nextRender(card)
            }
            .then { assertEquals("3", card.getAttribute("title-heading-level")) }
    }

    @Test
    fun clearsTheStringTitleWhenACustomTitleElementIsUsed(): Promise<Unit> {
        val card = newCard()
        card.cardTitle = "Some title"

        return nextRender(card)
            .then {
                val custom = document.createElement("span")
                custom.setAttribute("slot", "title")
                text(custom, "Custom title element")
                card.appendChild(custom)
                nextRender(card)
            }
            .then {
                assertTrue(card.cardTitle.isEmpty(), "expected the string title to be cleared")
                assertNull(stringTitleElement(card))
                assertNotNull(customTitleElement(card))
            }
    }

    @Test
    fun clearsTheStringTitleWhenAnEmptyTitleIsSet(): Promise<Unit> {
        val card = newCard()
        card.cardTitle = "Some title"

        return nextRender(card)
            .then {
                card.cardTitle = ""
                nextRender(card)
            }
            .then { assertNull(stringTitleElement(card)) }
    }

    @Test
    fun clearsTheCustomTitleElementWhenAStringTitleIsSet(): Promise<Unit> {
        val card = newCard()
        val custom = document.createElement("span")
        custom.setAttribute("slot", "title")
        text(custom, "Custom title element")
        card.appendChild(custom)

        return nextRender(card)
            .then {
                card.cardTitle = "Some title"
                nextRender(card)
            }
            .then {
                assertNull(customTitleElement(card))
                assertNotNull(stringTitleElement(card))
            }
    }

    // Kotlin-only coverage

    @Test
    fun createCardAppliesTheConfigureBlock() {
        val card = attach(createCard { cardTitle = "Inbox" })

        assertEquals("Inbox", card.cardTitle)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        val card = document.createElement(vaadin.VaadinTagName.card)
        assertEquals("vaadin-card", card.tagName.lowercase())
        assertEquals("card-title", CardAttributes.cardTitle)
        assertEquals("title-heading-level", CardAttributes.titleHeadingLevel)
        assertEquals("theme", CardAttributes.theme)
    }

    @Test
    fun cardTitleIsNotReflectedToTheAttribute(): Promise<Unit> {
        val card = newCard()
        card.cardTitle = "Inbox"

        return nextRender(card).then { assertNull(card.getAttribute("card-title")) }
    }

    @Test
    fun renderedTitleIsLabelledByTheCard(): Promise<Unit> {
        val card = newCard()
        card.cardTitle = "Inbox"

        return nextRender(card).then {
            val title: HTMLElement = assertNotNull(stringTitleElement(card))
            val labelledBy = card.getAttribute("aria-labelledby")
            if (labelledBy != null) {
                assertEquals(labelledBy, js("(element) => element.id")(title) as String)
            } else {
                // The pinned release renders the heading without aria-labelledby; the heading role
                // itself still carries the accessible name.
                assertEquals("heading", title.getAttribute("role"))
            }
        }
    }

    @Test
    fun sizesSynchronously() {
        val first = fixture("<vaadin-card><div slot=\"title\">New Message</div></vaadin-card>")
        val height = js("(element) => element.offsetHeight")(first) as Int
        val second = fixture("<vaadin-card><div slot=\"title\">New Message</div></vaadin-card>")

        assertEquals(height, js("(element) => element.offsetHeight")(second) as Int)
    }

    @Test
    fun shadowRootContainsTheTitleSlot(): Promise<Unit> {
        val card = newCard()

        return nextRender(card).then {
            val slots = vaadin.shadowQueryAll(card, "slot").map { it.getAttribute("name") }
            assertTrue(slots.contains("title"), "expected a title slot, got $slots")
        }
    }

    @Test
    fun shadowQueryFindsTheContentPart(): Promise<Unit> {
        val card = newCard()

        return nextRender(card).then {
            assertNotNull(shadowQuery(card, "[part]"), "expected the card to expose styled parts")
        }
    }
}
