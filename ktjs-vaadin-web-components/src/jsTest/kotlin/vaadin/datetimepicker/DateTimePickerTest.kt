package vaadin.datetimepicker

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
 * Translated from `packages/date-time-picker/test/date-time-picker.test.js`.
 *
 * The dropdown and keyboard cases are skipped; the value API, the date/time placeholders, the
 * `defaultTime` and the attribute contract are translated.
 */
class DateTimePickerTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerDateTimePicker()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newPicker(): DateTimePickerElement = attach(createDateTimePicker())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerDateTimePicker()
        val picker = newPicker()

        assertEquals("vaadin-date-time-picker", picker.tagName.lowercase())
        assertEquals("vaadin-date-time-picker", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val picker = newPicker()

        return nextRender(picker).then { assertEquals("", picker.value) }
    }

    @Test
    fun storesAnIsoDateTime(): Promise<Unit> {
        val picker = newPicker()

        return nextRender(picker)
            .then {
                picker.value = "2024-05-17T12:30"
                nextUpdate(picker)
            }
            .then { assertEquals("2024-05-17T12:30", picker.value) }
    }

    @Test
    fun firesValueChanged(): Promise<Unit> {
        val picker = newPicker()
        var fired = 0
        on(picker, "value-changed") { fired++ }

        return nextRender(picker)
            .then {
                picker.value = "2024-05-17T12:30"
                nextUpdate(picker)
            }
            .then { assertTrue(fired > 0, "expected a value-changed event, got $fired") }
    }

    @Test
    fun appliesThePlaceholdersAndDefaultTime(): Promise<Unit> {
        val picker = attach(
            createDateTimePicker {
                datePlaceholder = "Date"
                timePlaceholder = "Time"
                defaultTime = "00:00"
            },
        )

        return nextRender(picker).then {
            assertEquals("Date", picker.datePlaceholder)
            assertEquals("Time", picker.timePlaceholder)
            assertEquals("00:00", picker.defaultTime)
        }
    }

    @Test
    fun appliesTheBounds(): Promise<Unit> {
        val picker = attach(
            createDateTimePicker {
                min = "2024-01-01T00:00"
                max = "2024-12-31T23:59"
            },
        )

        return nextRender(picker).then {
            assertEquals("2024-01-01T00:00", picker.min)
            assertEquals("2024-12-31T23:59", picker.max)
        }
    }

    @Test
    fun rendersTheDateAndTimePickers(): Promise<Unit> {
        val picker = newPicker()

        return nextRender(picker).then {
            val datePicker = js("(picker) => picker.querySelector('vaadin-date-picker') || picker.shadowRoot.querySelector('vaadin-date-picker')")(picker)
            val timePicker = js("(picker) => picker.querySelector('vaadin-time-picker') || picker.shadowRoot.querySelector('vaadin-time-picker')")(picker)
            assertNotNull(datePicker)
            assertNotNull(timePicker)
        }
    }

    @Test
    fun isInvalidWhenRequiredAndEmpty(): Promise<Unit> {
        val picker = attach(createDateTimePicker { required = true })

        return nextRender(picker).then {
            assertFalse(picker.checkValidity())
            picker.validate()
            assertTrue(picker.invalid)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createDateTimePickerAppliesTheConfigureBlock() {
        val picker = attach(createDateTimePicker { label = "Meeting" })

        assertEquals("Meeting", picker.label)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-date-time-picker",
            document.createElement(vaadin.VaadinTagName.dateTimePicker).tagName.lowercase(),
        )
        assertEquals("date-placeholder", DateTimePickerAttributes.datePlaceholder)
        assertEquals("time-placeholder", DateTimePickerAttributes.timePlaceholder)
        assertEquals("default-time", DateTimePickerAttributes.defaultTime)
        assertEquals("min", DateTimePickerAttributes.min)
        assertEquals("max", DateTimePickerAttributes.max)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(DateTimePickerEvents.valueChanged)
    }
}
