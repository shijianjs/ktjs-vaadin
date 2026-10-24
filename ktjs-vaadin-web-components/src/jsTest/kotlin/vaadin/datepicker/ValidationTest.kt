package vaadin.datepicker

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.html.HTMLElement

/**
 * Translated from `packages/date-picker/test/validation.test.js`.
 *
 * Dropped upstream cases: the ones that spy on `validate()` to count calls (monkeypatching a method
 * of an external class is not expressible through the wrapper), the `document.hasFocus` stub, and the
 * cases that select a date through `_overlayContent._selectDate`.
 *
 * The `input value` cases commit the typed date with a single `Enter` keydown instead of the
 * upstream `enter(input)` helper, which is the same event.
 */
class ValidationTest {
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

    private fun newDatePicker(configure: DatePickerElement.() -> Unit = {}): DatePickerElement =
        attach(createDatePicker(configure))

    // basic

    @Test
    fun passesValidationByDefault() {
        val picker = newDatePicker()

        assertTrue(picker.checkValidity())
        assertTrue(picker.validate())
    }

    @Test
    fun canForceTheInvalidStatus(): Promise<Unit> {
        val picker = newDatePicker()
        picker.invalid = true

        return nextUpdate(picker).then {
            val input = js("(picker) => picker.inputElement")(picker) as HTMLElement

            assertTrue(input.hasAttribute("invalid"))
        }
    }

    @Test
    fun firesAValidatedEventOnSuccess() {
        val picker = newDatePicker()
        val values = mutableListOf<Boolean>()
        on(picker, "validated") { event ->
            values += js("(event) => event.detail.valid")(event) as Boolean
        }

        picker.validate()

        assertEquals(listOf(true), values)
    }

    @Test
    fun firesAValidatedEventOnFailure(): Promise<Unit> {
        val picker = newDatePicker()
        val values = mutableListOf<Boolean>()
        on(picker, "validated") { event ->
            values += js("(event) => event.detail.valid")(event) as Boolean
        }

        picker.required = true

        return nextUpdate(picker).then {
            picker.validate()

            assertEquals(listOf(false), values)
        }
    }

    // required

    @Test
    fun failsRequiredValidationWithoutAValue() {
        val picker = newDatePicker { required = true }

        assertFalse(picker.checkValidity())
    }

    @Test
    fun passesRequiredValidationWithAValue(): Promise<Unit> {
        val picker = newDatePicker { required = true }

        return nextRender(picker).then {
            picker.value = "2000-01-01"

            assertTrue(picker.checkValidity())
        }
    }

    // min

    @Test
    fun passesMinValidationWithoutAValue(): Promise<Unit> {
        val picker = newDatePicker { min = "2010-01-01" }

        return nextRender(picker).then { assertTrue(picker.checkValidity()) }
    }

    @Test
    fun failsMinValidationBelowTheMinimum(): Promise<Unit> {
        val picker = newDatePicker { min = "2010-01-01" }

        return nextRender(picker).then {
            picker.value = "2000-01-01"

            assertFalse(picker.checkValidity())
        }
    }

    @Test
    fun passesMinValidationAboveTheMinimum(): Promise<Unit> {
        val picker = newDatePicker { min = "2010-01-01" }

        return nextRender(picker).then {
            picker.value = "2020-01-01"

            assertTrue(picker.checkValidity())
        }
    }

    @Test
    fun passesMinValidationOnTheMinimum(): Promise<Unit> {
        val picker = newDatePicker { min = "2010-01-01" }

        return nextRender(picker).then {
            picker.value = "2010-01-01"

            assertTrue(picker.checkValidity())
        }
    }

    // max

    @Test
    fun passesMaxValidationWithoutAValue(): Promise<Unit> {
        val picker = newDatePicker { max = "2010-01-01" }

        return nextRender(picker).then { assertTrue(picker.checkValidity()) }
    }

    @Test
    fun failsMaxValidationAboveTheMaximum(): Promise<Unit> {
        val picker = newDatePicker { max = "2010-01-01" }

        return nextRender(picker).then {
            picker.value = "2020-01-01"

            assertFalse(picker.checkValidity())
        }
    }

    @Test
    fun passesMaxValidationBelowTheMaximum(): Promise<Unit> {
        val picker = newDatePicker { max = "2010-01-01" }

        return nextRender(picker).then {
            picker.value = "2000-01-01"

            assertTrue(picker.checkValidity())
        }
    }

    @Test
    fun passesMaxValidationOnTheMaximum(): Promise<Unit> {
        val picker = newDatePicker { max = "2010-01-01" }

        return nextRender(picker).then {
            picker.value = "2010-01-01"

            assertTrue(picker.checkValidity())
        }
    }

    // signed years

    @Test
    fun failsValidationOutsideASignedYearRange(): Promise<Unit> {
        val picker = newDatePicker {
            min = "-2026-01-01"
            max = "-2026-12-31"
        }

        return nextRender(picker).then {
            picker.value = "2026-01-01"

            assertFalse(picker.checkValidity())
        }
    }

    @Test
    fun passesValidationInsideASignedYearRange(): Promise<Unit> {
        val picker = newDatePicker {
            min = "-2026-01-01"
            max = "-2026-12-31"
        }

        return nextRender(picker).then {
            picker.value = "-2026-06-15"

            assertTrue(picker.checkValidity())
        }
    }

    // typed input value

    @Test
    fun isValidWhenCommittingAValidDate(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker)
            .then {
                setDatePickerInput(picker, "1/1/2022")
                pressEnterInDatePicker(picker)
                nextUpdate(picker)
            }
            .then { assertFalse(picker.invalid) }
    }

    @Test
    fun isInvalidWhenCommittingAnInvalidDate(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker)
            .then {
                setDatePickerInput(picker, "foo")
                pressEnterInDatePicker(picker)
                nextUpdate(picker)
            }
            .then { assertTrue(picker.invalid) }
    }

    @Test
    fun isInvalidWhenCommittingADateThatDoesNotExist(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker)
            .then {
                setDatePickerInput(picker, "2/31/2022")
                pressEnterInDatePicker(picker)
                nextUpdate(picker)
            }
            .then {
                assertEquals("", picker.value)
                assertTrue(picker.invalid)
            }
    }

    @Test
    fun isValidOnBlurAfterAValidDateWithAutoOpenDisabled(): Promise<Unit> {
        val picker = newDatePicker { autoOpenDisabled = true }

        return nextRender(picker).then {
            setDatePickerInput(picker, "1/1/2022")
            blurDatePickerInput(picker)

            assertFalse(picker.invalid)
        }
    }

    @Test
    fun isInvalidOnBlurAfterAnInvalidDateWithAutoOpenDisabled(): Promise<Unit> {
        val picker = newDatePicker { autoOpenDisabled = true }

        return nextRender(picker).then {
            setDatePickerInput(picker, "foo")
            blurDatePickerInput(picker)

            assertTrue(picker.invalid)
        }
    }
}
