package vaadin.tooltip

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/tooltip/test/tooltip.test.js` and the public part of
 * `packages/tooltip/test/tooltip-position.test.js`.
 *
 * The hover/focus timing cases need real pointer and focus sequences; the attribute API, the
 * `for`-based wiring and the static delay configuration are translated.
 */
class TooltipTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerTooltip()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newTooltip(): TooltipElement = attach(createTooltip())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerTooltip()
        val tooltip = newTooltip()

        assertEquals("vaadin-tooltip", tooltip.tagName.lowercase())
        assertEquals("vaadin-tooltip", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val tooltip = newTooltip()

        return nextRender(tooltip).then { assertFalse(tooltip.opened) }
    }

    @Test
    fun exposesTheText(): Promise<Unit> {
        val tooltip = attach(createTooltip { text = "Help" })

        return nextRender(tooltip).then { assertEquals("Help", tooltip.text) }
    }

    @Test
    fun opensThroughTheOpenedProperty(): Promise<Unit> {
        val tooltip = newTooltip()

        return nextRender(tooltip)
            .then {
                tooltip.opened = true
                nextUpdate(tooltip)
            }
            .then { assertTrue(tooltip.opened) }
    }

    @Test
    fun wiresToATargetThroughTheForAttribute(): Promise<Unit> {
        val tooltip = newTooltip()
        val target = attach(document.createElement("button"))
        target.setAttribute("id", "target")

        return nextRender(tooltip)
            .then {
                tooltip.setAttribute("for", "target")
                nextUpdate(tooltip)
            }
            .then {
                assertEquals("target", js("(tooltip) => tooltip.for")(tooltip) as String)
                assertEquals("target", tooltip.getAttribute("for"))
            }
    }

    @Test
    fun appliesThePosition(): Promise<Unit> {
        val tooltip = attach(createTooltip { position = "bottom-start" })

        return nextRender(tooltip).then { assertEquals("bottom-start", tooltip.position) }
    }

    @Test
    fun appliesTheDelays(): Promise<Unit> {
        val tooltip = attach(
            createTooltip {
                hoverDelay = 100.0
                focusDelay = 200.0
                hideDelay = 300.0
            },
        )

        return nextRender(tooltip).then {
            assertEquals(100.0, tooltip.hoverDelay)
            assertEquals(200.0, tooltip.focusDelay)
            assertEquals(300.0, tooltip.hideDelay)
        }
    }

    @Test
    fun supportsManualMode(): Promise<Unit> {
        val tooltip = newTooltip()

        return nextUpdate(tooltip)
            .then {
                tooltip.manual = true
                nextUpdate(tooltip)
            }
            .then { assertTrue(tooltip.manual) }
    }

    @Test
    fun staticDelaySettersAreExposed() {
        // The static API only mutates the defaults used while the tooltip is shown; calling it must
        // not throw and must leave explicitly configured delays untouched.
        Tooltip.setDefaultHoverDelay(150.0)
        Tooltip.setDefaultFocusDelay(250.0)
        Tooltip.setDefaultHideDelay(50.0)

        val tooltip = attach(createTooltip { hoverDelay = 42.0 })
        assertEquals(42.0, tooltip.hoverDelay)
    }

    // Kotlin-only coverage

    @Test
    fun createTooltipAppliesTheConfigureBlock() {
        val tooltip = attach(createTooltip { text = "Help" })

        assertEquals("Help", tooltip.text)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-tooltip", document.createElement(vaadin.VaadinTagName.tooltip).tagName.lowercase())
        assertEquals("for", TooltipAttributes.`for`)
        assertEquals("text", TooltipAttributes.text)
        assertEquals("opened", TooltipAttributes.opened)
        assertEquals("position", TooltipAttributes.position)
        assertEquals("manual", TooltipAttributes.manual)
        assertEquals("markdown", TooltipAttributes.markdown)
        assertEquals("hover-delay", TooltipAttributes.hoverDelay)
        assertEquals("focus-delay", TooltipAttributes.focusDelay)
        assertEquals("hide-delay", TooltipAttributes.hideDelay)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(TooltipEvents.contentChanged)
    }
}
