package vaadin.details

import kotlin.js.Promise
import kotlin.js.unsafeCast
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.click
import vaadin.computedStyle
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import vaadin.shadowQuery
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/details/test/details.test.js`.
 *
 * The keyboard cases (`sendKeys`) are covered through the public `opened` API instead, because the
 * Kotlin test DSL cannot send real key sequences. Everything else — default state, reflection,
 * visibility, `aria-hidden` of the slotted content and the click behaviour of the three summary
 * variants — is translated directly.
 */
class DetailsTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        // Markup fixtures need the element definitions; `createDetails` registers them as a side
        // effect, but the translated tests build their fixtures from HTML like upstream does.
        registerDetails()
        registerDetailsSummary()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newDetails(): DetailsElement = attach(createDetails())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerDetails()
        val details = newDetails()

        assertEquals("vaadin-details", details.tagName.lowercase())
        assertEquals("vaadin-details", js("(ctor) => ctor.is")(ctor) as String)
    }

    // opened

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val details = attach(vaadin.fixture("<vaadin-details><div>Content</div></vaadin-details>").unsafeCast<DetailsElement>())

        return nextRender(details).then { assertFalse(details.opened) }
    }

    @Test
    fun reflectsOpenedToTheAttribute(): Promise<Unit> {
        val details = attach(vaadin.fixture("<vaadin-details><div>Content</div></vaadin-details>").unsafeCast<DetailsElement>())

        return nextRender(details)
            .then {
                details.opened = true
                nextUpdate(details)
            }
            .then { assertTrue(details.hasAttribute("opened")) }
    }

    @Test
    fun hidesTheContentWhenClosed(): Promise<Unit> {
        val details = attach(vaadin.fixture("<vaadin-details><div>Content</div></vaadin-details>").unsafeCast<DetailsElement>())

        return nextRender(details).then {
            val content = assertNotNull(shadowQuery(details, "[part=\"content\"]"))
            assertEquals("none", computedStyle(content, "display"))
        }
    }

    @Test
    fun showsTheContentWhenOpened(): Promise<Unit> {
        val details = attach(vaadin.fixture("<vaadin-details><div>Content</div></vaadin-details>").unsafeCast<DetailsElement>())

        return nextRender(details)
            .then {
                details.opened = true
                nextUpdate(details)
            }
            .then {
                val content = assertNotNull(shadowQuery(details, "[part=\"content\"]"))
                assertEquals("block", computedStyle(content, "display"))
            }
    }

    @Test
    fun setsAriaHiddenOnTheSlottedElement(): Promise<Unit> {
        val details = attach(vaadin.fixture("<vaadin-details><div>Content</div></vaadin-details>").unsafeCast<DetailsElement>())
        val content = js("(details) => details.querySelector('div')")(details) as HTMLElement

        return nextRender(details)
            .then {
                assertEquals("true", content.getAttribute("aria-hidden"))
                details.opened = true
                nextUpdate(details)
            }
            .then { assertEquals("false", content.getAttribute("aria-hidden")) }
    }

    @Test
    fun doesNotCloseWhenTheContentIsClicked(): Promise<Unit> {
        val details = attach(vaadin.fixture("<vaadin-details><div>Content</div></vaadin-details>").unsafeCast<DetailsElement>())
        val content = js("(details) => details.querySelector('div')")(details) as HTMLElement

        return nextRender(details)
            .then {
                details.opened = true
                nextUpdate(details)
            }
            .then {
                click(content)
                assertTrue(details.opened)
            }
    }

    // summary variants

    private fun summaryFixture(kind: String): DetailsElement {
        val html = when (kind) {
            "default" -> "<vaadin-details summary=\"Summary\"><div>Content</div></vaadin-details>"
            "custom" ->
                "<vaadin-details><vaadin-details-summary slot=\"summary\">Summary</vaadin-details-summary>" +
                    "<div>Content</div></vaadin-details>"
            else ->
                "<vaadin-details><vaadin-details-summary slot=\"summary\"><div>Summary</div>" +
                    "</vaadin-details-summary><div>Content</div></vaadin-details>"
        }
        return attach(vaadin.fixture(html).unsafeCast<DetailsElement>())
    }

    private fun summaryOf(details: DetailsElement): HTMLElement =
        js("(details) => details.querySelector('[slot=\"summary\"]')")(details) as HTMLElement

    @Test
    fun togglesOpenedOnDefaultSummaryClick(): Promise<Unit> {
        val details = summaryFixture("default")

        return nextRender(details)
            .then {
                click(summaryOf(details))
                nextUpdate(details)
            }
            .then {
                assertTrue(details.opened)
                click(summaryOf(details))
                nextUpdate(details)
            }
            .then { assertFalse(details.opened) }
    }

    @Test
    fun togglesOpenedOnCustomSummaryClick(): Promise<Unit> {
        val details = summaryFixture("custom")

        return nextRender(details)
            .then {
                click(summaryOf(details))
                nextUpdate(details)
            }
            .then { assertTrue(details.opened) }
    }

    @Test
    fun togglesOpenedOnCustomSummaryChildClick(): Promise<Unit> {
        val details = summaryFixture("component")
        val child = js("(details) => details.querySelector('[slot=\"summary\"]').firstElementChild")(details) as HTMLElement

        return nextRender(details)
            .then {
                click(child)
                nextUpdate(details)
            }
            .then { assertTrue(details.opened) }
    }

    @Test
    fun doesNotToggleWhenDisabled(): Promise<Unit> {
        val details = summaryFixture("default")

        return nextRender(details)
            .then {
                details.disabled = true
                nextUpdate(details)
            }
            .then {
                click(summaryOf(details))
                nextUpdate(details)
            }
            .then { assertFalse(details.opened) }
    }

    // summary property

    @Test
    fun rendersTheSummaryProperty(): Promise<Unit> {
        val details = newDetails()
        details.summary = "Summary"

        return nextRender(details).then {
            val summary = summaryOf(details)
            assertNotNull(summary, "expected a rendered summary")
            assertEquals("vaadin-details-summary", summary.tagName.lowercase())
        }
    }

    // Kotlin-only coverage

    @Test
    fun createDetailsAppliesTheConfigureBlock() {
        val details = attach(createDetails { summary = "Summary" })

        assertEquals("Summary", details.summary)
    }

    @Test
    fun createDetailsSummaryAppliesTheConfigureBlock() {
        val summary = attach(createDetailsSummary { opened = true })

        assertTrue(summary.opened)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-details", document.createElement(vaadin.VaadinTagName.details).tagName.lowercase())
        assertEquals(
            "vaadin-details-summary",
            document.createElement(vaadin.VaadinTagName.detailsSummary).tagName.lowercase(),
        )
        assertEquals("opened", DetailsAttributes.opened)
        assertEquals("summary", DetailsAttributes.summary)
        assertEquals("disabled", DetailsAttributes.disabled)
        assertEquals("autofocus", DetailsAttributes.autofocus)
        assertEquals("theme", DetailsAttributes.theme)
        assertEquals("opened", DetailsSummaryAttributes.opened)
        assertEquals("disabled", DetailsSummaryAttributes.disabled)
        assertEquals("theme", DetailsSummaryAttributes.theme)
    }

    @Test
    fun firesOpenedChangedWhenOpenedChanges(): Promise<Unit> {
        val details = newDetails()
        var fired = 0
        on(details, "opened-changed") { fired++ }

        return nextRender(details)
            .then {
                details.opened = true
                nextUpdate(details)
            }
            .then { assertTrue(fired > 0, "expected an opened-changed event, got $fired") }
    }

    @Test
    fun openedChangedEventTypeIsExposed() {
        assertNotNull(DetailsEvents.openedChanged)
    }
}
