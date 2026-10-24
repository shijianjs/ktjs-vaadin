package vaadin.timepicker

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
 * Translated from `packages/time-picker/test/time-picker.test.js`.
 *
 * The dropdown and keyboard cases are skipped; the value API, `min`/`max`/`step`, the
 * `isInputValueValid` helper and the attribute contract are translated.
 */
class TimePickerTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerTimePicker()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newTimePicker(): TimePickerElement = attach(createTimePicker())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerTimePicker()
        val picker = newTimePicker()

        assertEquals("vaadin-time-picker", picker.tagName.lowercase())
        assertEquals("vaadin-time-picker", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val picker = newTimePicker()

        return nextRender(picker).then { assertEquals("", picker.value) }
    }

    @Test
    fun storesATime(): Promise<Unit> {
        val picker = newTimePicker()

        return nextRender(picker)
            .then {
                picker.value = "12:30"
                nextUpdate(picker)
            }
            .then { assertEquals("12:30", picker.value) }
    }

    @Test
    fun firesValueChanged(): Promise<Unit> {
        val picker = newTimePicker()
        var fired = 0
        on(picker, "value-changed") { fired++ }

        return nextRender(picker)
            .then {
                picker.value = "08:00"
                nextUpdate(picker)
            }
            .then { assertTrue(fired > 0, "expected a value-changed event, got $fired") }
    }

    @Test
    fun appliesTheBoundsAndStep(): Promise<Unit> {
        val picker = attach(createTimePicker { min = "08:00"; max = "17:00"; step = 900.0 })

        return nextRender(picker).then {
            assertEquals("08:00", picker.min)
            assertEquals("17:00", picker.max)
            assertEquals(900.0, picker.step)
        }
    }

    @Test
    fun rejectsAnOutOfRangeTime(): Promise<Unit> {
        val picker = attach(createTimePicker { min = "08:00"; max = "17:00" })

        return nextRender(picker)
            .then {
                picker.value = "20:00"
                nextUpdate(picker)
            }
            .then { assertFalse(picker.checkValidity()) }
    }

    @Test
    fun keepsAValueInsideTheStep(): Promise<Unit> {
        val picker = attach(createTimePicker { step = 900.0 })

        return nextRender(picker)
            .then {
                picker.value = "08:15"
                nextUpdate(picker)
            }
            .then {
                // A multiple of the 15 minute step is accepted.
                assertEquals("08:15", picker.value)
                assertTrue(picker.checkValidity())
            }
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val picker = newTimePicker()

        return nextRender(picker).then { assertFalse(picker.opened) }
    }

    // Kotlin-only coverage

    @Test
    fun createTimePickerAppliesTheConfigureBlock() {
        val picker = attach(createTimePicker { label = "Start" })

        assertEquals("Start", picker.label)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-time-picker",
            document.createElement(vaadin.VaadinTagName.timePicker).tagName.lowercase(),
        )
        assertEquals("min", TimePickerAttributes.min)
        assertEquals("max", TimePickerAttributes.max)
        assertEquals("step", TimePickerAttributes.step)
        assertEquals("opened", TimePickerAttributes.opened)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(TimePickerEvents.valueChanged)
        assertNotNull(TimePickerEvents.openedChanged)
    }
}
