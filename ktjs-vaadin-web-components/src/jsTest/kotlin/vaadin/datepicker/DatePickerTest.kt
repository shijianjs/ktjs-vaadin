package vaadin.datepicker

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
 * Translated from `packages/date-picker/test/date-picker.test.js` and the public part of
 * `packages/date-picker/test/date-picker-validation.test.js`.
 *
 * The calendar overlay and keyboard cases are skipped; the value API, the bounds, the helper
 * `createDate` factory and the attribute contract are translated.
 */
class DatePickerTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerDatePicker()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newDatePicker(): DatePickerElement = attach(createDatePicker())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerDatePicker()
        val picker = newDatePicker()

        assertEquals("vaadin-date-picker", picker.tagName.lowercase())
        assertEquals("vaadin-date-picker", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAnEmptyValueByDefault(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker).then { assertEquals("", picker.value) }
    }

    @Test
    fun storesAnIsoDate(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker)
            .then {
                picker.value = "2024-05-17"
                nextUpdate(picker)
            }
            .then { assertEquals("2024-05-17", picker.value) }
    }

    @Test
    fun firesValueChanged(): Promise<Unit> {
        val picker = newDatePicker()
        var fired = 0
        on(picker, "value-changed") { fired++ }

        return nextRender(picker)
            .then {
                picker.value = "2024-05-17"
                nextUpdate(picker)
            }
            .then { assertTrue(fired > 0, "expected a value-changed event, got $fired") }
    }

    @Test
    fun appliesTheBounds(): Promise<Unit> {
        val picker = attach(createDatePicker { min = "2024-01-01"; max = "2024-12-31" })

        return nextRender(picker).then {
            assertEquals("2024-01-01", picker.min)
            assertEquals("2024-12-31", picker.max)
        }
    }

    @Test
    fun reflectsReadonlyAndDisabled(): Promise<Unit> {
        val picker = newDatePicker()

        return nextUpdate(picker)
            .then {
                picker.readonly = true
                picker.disabled = true
                nextUpdate(picker)
            }
            .then {
                assertTrue(picker.hasAttribute("readonly"))
                assertTrue(picker.hasAttribute("disabled"))
            }
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker).then { assertFalse(picker.opened) }
    }

    @Test
    fun isInvalidWhenRequiredAndEmpty(): Promise<Unit> {
        val picker = attach(createDatePicker { required = true })

        return nextRender(picker).then {
            assertFalse(picker.checkValidity())
            picker.validate()
            assertTrue(picker.invalid)
        }
    }

    @Test
    fun createDateBuildsADate(): Unit {
        val date = createDate(2024.0, 4.0, 17.0)

        assertNotNull(date)
        assertEquals(2024, js("(date) => date.getFullYear()")(date) as Int)
    }

    // Kotlin-only coverage

    @Test
    fun createDatePickerAppliesTheConfigureBlock() {
        val picker = attach(createDatePicker { label = "Birthday" })

        assertEquals("Birthday", picker.label)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-date-picker",
            document.createElement(vaadin.VaadinTagName.datePicker).tagName.lowercase(),
        )
        assertEquals("min", DatePickerAttributes.min)
        assertEquals("max", DatePickerAttributes.max)
        assertEquals("opened", DatePickerAttributes.opened)
        assertEquals("auto-open-disabled", DatePickerAttributes.autoOpenDisabled)
        assertEquals("initial-position", DatePickerAttributes.initialPosition)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(DatePickerEvents.valueChanged)
        assertNotNull(DatePickerEvents.openedChanged)
    }
}
