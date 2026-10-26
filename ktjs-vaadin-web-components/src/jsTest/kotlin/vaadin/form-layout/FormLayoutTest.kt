package vaadin.formlayout

import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import vaadin.nextUpdate
import vaadin.textfield.createTextField
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/form-layout/test/form-layout.test.js` and
 * `packages/form-layout/test/form-layout-auto-responsive.test.js`.
 *
 * The upstream suites additionally resize the browser window and assert grid geometry; those
 * cases depend on the layout engine and are not translated (see AGENTS.md). Everything that can
 * be observed through the public property/attribute API is covered here.
 */
class FormLayoutTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun div(): HTMLElement = document.createElement("div")

    private fun step(
        columns: Int,
        minWidth: String? = null,
        labelsPosition: FormLayoutLabelsPosition? = null,
    ): FormLayoutResponsiveStep {
        val step = js("({})").unsafeCast<FormLayoutResponsiveStep>()
        step.columns = columns.toDouble()
        minWidth?.let { step.minWidth = js("(value) => value")(it).unsafeCast<StringOrDouble>() }
        labelsPosition?.let { step.labelsPosition = it }
        return step
    }

    private fun steps(vararg steps: FormLayoutResponsiveStep): Array<FormLayoutResponsiveStep> = steps.toList().toTypedArray()

    private fun widthPercentage(element: HTMLElement): Double =
        js("(element) => parseFloat(element.style.width.match(/([\\d.]+)%/)[1])")(element) as Double

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerFormLayout()
        val layout = attach(createFormLayout())

        assertEquals("vaadin-form-layout", layout.tagName.lowercase())
        assertEquals("vaadin-form-layout", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun autoResponsiveDefaultsToFalse(): Promise<Unit> {
        val layout = attach(createFormLayout())

        return nextUpdate(layout).then {
            assertFalse(layout.autoResponsive)
            assertFalse(layout.hasAttribute("auto-responsive"))
        }
    }

    @Test
    fun autoResponsiveReflectsToTheAttribute(): Promise<Unit> {
        val layout = attach(createFormLayout())
        layout.autoResponsive = true

        return nextUpdate(layout).then { assertTrue(layout.hasAttribute("auto-responsive")) }
    }

    @Test
    fun booleanFlagsDefaultToFalseAndReflect(): Promise<Unit> {
        val layout = attach(createFormLayout())

        return nextUpdate(layout)
            .then {
                assertFalse(layout.autoRows)
                assertFalse(layout.labelsAside)
                assertFalse(layout.expandColumns)
                assertFalse(layout.expandFields)

                layout.autoRows = true
                layout.labelsAside = true
                layout.expandColumns = true
                layout.expandFields = true
                nextUpdate(layout)
            }
            .then {
                assertTrue(layout.hasAttribute("auto-rows"))
                assertTrue(layout.hasAttribute("labels-aside"))
                assertTrue(layout.hasAttribute("expand-columns"))
                assertTrue(layout.hasAttribute("expand-fields"))
            }
    }

    @Test
    fun columnLimitsHaveDefaults() {
        val layout = attach(createFormLayout())
        assertEquals(10.0, layout.maxColumns)
        assertEquals(1.0, layout.minColumns)
    }

    @Test
    fun columnWidthUpdatesTheCustomProperty(): Promise<Unit> {
        val layout = attach(createFormLayout())
        layout.autoResponsive = true
        layout.columnWidth = "100px"

        return nextUpdate(layout).then {
            assertEquals("100px", layout.style.getPropertyValue("--_column-width"))
        }
    }

    @Test
    fun responsiveStepsHaveUpstreamDefaults() {
        val layout = attach(createFormLayout())

        val count = js("(layout) => layout.responsiveSteps.length")(layout) as Int
        assertEquals(3, count)
        assertEquals(1.0, js("(layout) => layout.responsiveSteps[0].columns")(layout) as Double)
        assertEquals("top", js("(layout) => layout.responsiveSteps[0].labelsPosition")(layout) as String)
        assertEquals("20em", js("(layout) => layout.responsiveSteps[1].minWidth")(layout) as String)
        assertEquals(2.0, js("(layout) => layout.responsiveSteps[2].columns")(layout) as Double)
    }

    @Test
    fun responsiveStepsAssignInlineWidthToChildren(): Promise<Unit> {
        val layout = attach(createFormLayout())
        val field = div()
        layout.appendChild(field)
        layout.responsiveSteps = steps(step(3))

        return nextUpdate(layout).then {
            val percentage = widthPercentage(field)
            assertTrue(percentage > 32.5 && percentage < 33.5, "expected ~33%, got $percentage")
        }
    }

    @Test
    fun labelPositionIsSetOnFormItemsOnly(): Promise<Unit> {
        val layout = attach(createFormLayout())
        val field = createTextField()
        val item = createFormItem()
        layout.appendChild(field)
        layout.appendChild(item)
        layout.responsiveSteps = steps(step(1, labelsPosition = FormLayoutLabelsPositions.top))

        return nextUpdate(layout).then {
            assertNull(field.getAttribute("label-position"))
            assertEquals("top", item.getAttribute("label-position"))
        }
    }

    @Test
    fun customLabelPositionIsPreserved(): Promise<Unit> {
        val layout = attach(createFormLayout())
        val item = createFormItem()
        item.setAttribute("label-position", "top")
        layout.appendChild(item)

        return nextUpdate(layout)
            .then {
                layout.responsiveSteps = steps(step(1, labelsPosition = FormLayoutLabelsPositions.top))
                nextUpdate(layout)
            }
            .then {
                assertEquals("top", item.getAttribute("label-position"))
                layout.responsiveSteps = steps(step(1))
                nextUpdate(layout)
            }
            .then { assertEquals("top", item.getAttribute("label-position")) }
    }

    @Test
    fun colspanSpansChildrenCorrectly(): Promise<Unit> {
        val layout = attach(createFormLayout())
        val noColspan = div()
        val one = div().also { it.setAttribute("colspan", "1") }
        val two = div().also { it.setAttribute("colspan", "2") }
        val three = div().also { it.setAttribute("colspan", "3") }
        val four = div().also { it.setAttribute("colspan", "4") }
        val invalid = div().also { it.setAttribute("colspan", "non-number") }
        listOf(noColspan, one, two, three, four, invalid).forEach { layout.appendChild(it) }
        layout.responsiveSteps = steps(step(3))

        return nextUpdate(layout).then {
            val column = 100.0 / 3
            assertTrue(kotlin.math.abs(widthPercentage(noColspan) / column - 1) < 0.1)
            assertTrue(kotlin.math.abs(widthPercentage(one) / column - 1) < 0.1)
            assertTrue(kotlin.math.abs(widthPercentage(two) / column - 2) < 0.1)
            assertTrue(kotlin.math.abs(widthPercentage(three) / column - 3) < 0.1)
            // More than the number of columns is clamped to the column count.
            assertTrue(kotlin.math.abs(widthPercentage(four) / column - 3) < 0.1)
            // Invalid values mean a single column.
            assertTrue(kotlin.math.abs(widthPercentage(invalid) / column - 1) < 0.1)
        }
    }

    @Test
    fun formItemIsRegisteredAndCreatesALabelSlot(): Promise<Unit> {
        val ctor = registerFormItem()
        val item = attach(createFormItem())
        val label = document.createElement("label")
        label.setAttribute("slot", "label")
        label.textContent = "Label"
        item.appendChild(label)

        return nextFrame().then {
            assertEquals("vaadin-form-item", item.tagName.lowercase())
            assertEquals("vaadin-form-item", js("(ctor) => ctor.is")(ctor) as String)
            assertEquals("label", label.getAttribute("slot"))
        }
    }

    @Test
    fun formRowIsRegistered(): Promise<Unit> {
        val ctor = registerFormRow()
        val row = attach(createFormRow())

        return nextFrame().then {
            assertEquals("vaadin-form-row", row.tagName.lowercase())
            assertEquals("vaadin-form-row", js("(ctor) => ctor.is")(ctor) as String)
        }
    }

    @Test
    fun attributeConstantsAreExposed() {
        assertEquals("auto-responsive", FormLayoutAttributes.autoResponsive)
        assertEquals("auto-rows", FormLayoutAttributes.autoRows)
        assertEquals("column-width", FormLayoutAttributes.columnWidth)
        assertEquals("expand-columns", FormLayoutAttributes.expandColumns)
        assertEquals("expand-fields", FormLayoutAttributes.expandFields)
        assertEquals("labels-aside", FormLayoutAttributes.labelsAside)
        assertEquals("max-columns", FormLayoutAttributes.maxColumns)
        assertEquals("min-columns", FormLayoutAttributes.minColumns)
    }
}
