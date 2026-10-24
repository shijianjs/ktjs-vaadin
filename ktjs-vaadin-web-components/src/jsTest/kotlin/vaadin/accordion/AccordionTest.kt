package vaadin.accordion

import vaadin.query
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.click
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/accordion/test/accordion.test.js` and
 * `packages/accordion/test/accordion-panel.test.js`.
 *
 * The keyboard navigation cases (`arrowDownKeyDown`, `homeKeyDown`, ...) are not translated: they
 * need the real key sequences of `@vaadin/test-runner-commands`, which the Kotlin test DSL cannot
 * send. The cases that rely on the private `_observer` use the public `items` array after the
 * element updated instead.
 */
class AccordionTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerAccordion()
        registerAccordionPanel()
        registerAccordionHeading()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newAccordion(): AccordionElement = attach(createAccordion())

    private val markup = """
        <vaadin-accordion>
          <vaadin-accordion-panel>
            <vaadin-accordion-heading slot="summary">Panel 1</vaadin-accordion-heading>
            <div><input /></div>
          </vaadin-accordion-panel>
          <vaadin-accordion-panel>
            <vaadin-accordion-heading slot="summary">Panel 2</vaadin-accordion-heading>
            <div>Content 2</div>
          </vaadin-accordion-panel>
          <vaadin-accordion-panel>
            <vaadin-accordion-heading slot="summary"><div>Panel 3</div></vaadin-accordion-heading>
            <div>Content 3</div>
          </vaadin-accordion-panel>
        </vaadin-accordion>
    """.trimIndent()

    private fun accordionFixture(): AccordionElement = attach(fixture(markup).unsafeCast<AccordionElement>())

    private fun heading(accordion: AccordionElement, index: Int): HTMLElement =
        js("(accordion, index) => accordion.querySelectorAll('vaadin-accordion-heading')[index]")(
            accordion,
            index,
        ) as HTMLElement

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerAccordion()
        val accordion = newAccordion()

        assertEquals("vaadin-accordion", accordion.tagName.lowercase())
        assertEquals("vaadin-accordion", js("(ctor) => ctor.is")(ctor) as String)
    }

    // items

    @Test
    fun exposesTheChildPanelsAsItems(): Promise<Unit> {
        val accordion = accordionFixture()

        return nextRender(accordion).then { assertEquals(3, accordion.items.size) }
    }

    @Test
    fun updatesItemsWhenANodeIsRemoved(): Promise<Unit> {
        val accordion = accordionFixture()

        return nextRender(accordion)
            .then {
                accordion.removeChild(accordion.items[2])
                nextUpdate(accordion)
            }
            .then { assertEquals(2, accordion.items.size) }
    }

    @Test
    fun updatesItemsWhenANodeIsAdded(): Promise<Unit> {
        val accordion = accordionFixture()

        return nextRender(accordion)
            .then {
                accordion.appendChild(document.createElement(vaadin.VaadinTagName.accordionPanel))
                nextUpdate(accordion)
            }
            .then { assertEquals(4, accordion.items.size) }
    }

    @Test
    fun updatesItemsWhenANodeIsMoved(): Promise<Unit> {
        val accordion = accordionFixture()

        return nextRender(accordion)
            .then {
                val first = accordion.items[0]
                val third = accordion.items[2]
                accordion.insertBefore(third, first)
                nextUpdate(accordion)
            }
            .then { assertEquals("Panel 3", js("(panel) => panel.textContent.trim().slice(0, 7)")(accordion.items[0])) }
    }

    // opened

    @Test
    fun opensTheFirstPanelByDefault(): Promise<Unit> {
        val accordion = accordionFixture()

        return nextRender(accordion).then {
            assertEquals(0.0, accordion.opened)
            assertTrue(accordion.items[0].opened)
            assertTrue(accordion.hasAttribute("opened"))
        }
    }

    @Test
    fun updatesOpenedWhenAnotherPanelIsClicked(): Promise<Unit> {
        val accordion = accordionFixture()

        return nextRender(accordion)
            .then {
                click(heading(accordion, 1))
                nextUpdate(accordion)
            }
            .then {
                assertTrue(accordion.items[1].opened)
                assertEquals(1.0, accordion.opened)
            }
    }

    @Test
    fun doesNotOpenADisabledPanel(): Promise<Unit> {
        val accordion = accordionFixture()

        return nextRender(accordion)
            .then {
                accordion.items[1].disabled = true
                nextUpdate(accordion)
            }
            .then {
                click(heading(accordion, 1))
                nextUpdate(accordion)
            }
            .then {
                assertFalse(accordion.items[1].opened)
                assertEquals(0.0, accordion.opened)
            }
    }

    @Test
    fun closesTheOpenedPanelWhenItsHeadingIsClicked(): Promise<Unit> {
        val accordion = accordionFixture()

        return nextRender(accordion)
            .then {
                click(heading(accordion, 0))
                nextUpdate(accordion)
            }
            .then {
                assertFalse(accordion.items[0].opened)
                // The pinned release clears `opened` (the upstream source tree uses -1).
                assertEquals(null, js("(accordion) => accordion.opened")(accordion))
            }
    }

    @Test
    fun firesOpenedChangedWhenAnotherPanelIsOpened(): Promise<Unit> {
        val accordion = accordionFixture()
        var fired = 0
        on(accordion, "opened-changed") { fired++ }

        return nextRender(accordion)
            .then {
                click(heading(accordion, 1))
                nextUpdate(accordion)
            }
            .then { assertTrue(fired > 0, "expected an opened-changed event, got $fired") }
    }

    // heading level

    @Test
    fun delegatesTheHeadingLevelToThePanels(): Promise<Unit> {
        val accordion = accordionFixture()

        return nextRender(accordion)
            .then {
                accordion.headingLevel = 3.0
                nextUpdate(accordion)
            }
            .then {
                // The accordion is not reflected itself: it forwards the level to each heading as
                // `aria-level`, which is what screen readers consume.
                assertEquals("3", heading(accordion, 0).getAttribute("aria-level"))
                assertEquals("3", heading(accordion, 1).getAttribute("aria-level"))
            }
    }

    @Test
    fun readsTheHeadingLevelFromTheAttribute(): Promise<Unit> {
        val accordion = accordionFixture()

        return nextRender(accordion)
            .then {
                accordion.setAttribute("heading-level", "4")
                nextUpdate(accordion)
            }
            .then {
                assertEquals(4.0, accordion.headingLevel)
                assertEquals("4", heading(accordion, 0).getAttribute("aria-level"))
            }
    }

    // Kotlin-only coverage

    @Test
    fun createAccordionAppliesTheConfigureBlock() {
        val accordion = attach(createAccordion { headingLevel = 4.0 })

        assertEquals(4.0, accordion.headingLevel)
    }

    @Test
    fun createAccordionPanelAndHeadingAppliesTheConfigureBlock() {
        val panel = attach(createAccordionPanel { summary = "Summary" })
        val heading = attach(createAccordionHeading { opened = true })

        assertEquals("Summary", panel.summary)
        assertTrue(heading.opened)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-accordion", document.createElement(vaadin.VaadinTagName.accordion).tagName.lowercase())
        assertEquals(
            "vaadin-accordion-panel",
            document.createElement(vaadin.VaadinTagName.accordionPanel).tagName.lowercase(),
        )
        assertEquals(
            "vaadin-accordion-heading",
            document.createElement(vaadin.VaadinTagName.accordionHeading).tagName.lowercase(),
        )
        assertEquals("heading-level", AccordionAttributes.headingLevel)
        assertEquals("opened", AccordionAttributes.opened)
        assertEquals("theme", AccordionAttributes.theme)
        assertEquals("summary", AccordionPanelAttributes.summary)
        assertEquals("opened", AccordionPanelAttributes.opened)
        assertEquals("disabled", AccordionPanelAttributes.disabled)
        assertEquals("autofocus", AccordionPanelAttributes.autofocus)
        assertEquals("opened", AccordionHeadingAttributes.opened)
        assertEquals("disabled", AccordionHeadingAttributes.disabled)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(AccordionEvents.openedChanged)
        assertNotNull(AccordionEvents.itemsChanged)
        assertNotNull(AccordionPanelEvents.openedChanged)
    }

    @Test
    fun panelRendersTheSummaryProperty(): Promise<Unit> {
        val panel = attach(createAccordionPanel())
        panel.summary = "Summary"

        return nextRender(panel).then {
            val summary = query(panel, "[slot=summary]")
            assertNotNull(summary)
            assertEquals("vaadin-accordion-heading", summary.tagName.lowercase())
        }
    }
}
