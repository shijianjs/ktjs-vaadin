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
import vaadin.click
import vaadin.detachAll
import vaadin.nextRender
import vaadin.on
import vaadin.shadowQuery
import web.html.HTMLElement

/**
 * Translated from `packages/date-picker/test/basic.test.js`.
 *
 * Dropped upstream cases: the ones that need real key sequences or the calendar overlay
 * (`sendKeys({ press: 'ArrowDown' })`, `tap()`, `untilOverlayRendered`, `vaadin-overlay-open`), the
 * layout cases (`should not change datePicker width`, `inside flexbox`), the i18n cases that read the
 * overlay content, and the `_selectedDate` assertions, which read a protected member. The value
 * format cases are asserted through the public `value` / input round trip instead.
 */
class BasicTest {
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

    private fun inputValue(picker: DatePickerElement): String =
        js("(picker) => picker.inputElement.value")(picker) as String

    // parseDate

    @Test
    fun parsesDateComponentsWithVaryingNumberOfDigits() {
        assertTrue(dateEquals(parseDate("2017-11-11"), createDate(2017.0, 10.0, 11.0)))
        assertTrue(dateEquals(parseDate("2016-1-1"), createDate(2016.0, 0.0, 1.0)))
        assertTrue(dateEquals(parseDate("04-11-2"), createDate(4.0, 10.0, 2.0)))
    }

    @Test
    fun parsesASignedYearOfAnyWidth() {
        assertTrue(dateEquals(parseDate("-000001-01-01"), createDate(-1.0, 0.0, 1.0)))
        assertTrue(dateEquals(parseDate("-0001-01-01"), createDate(-1.0, 0.0, 1.0)))
        assertTrue(dateEquals(parseDate("+012026-01-01"), createDate(12026.0, 0.0, 1.0)))
        assertTrue(dateEquals(parseDate("+12026-01-01"), createDate(12026.0, 0.0, 1.0)))
    }

    @Test
    fun doesNotParseADateThatDoesNotExist() {
        assertTrue(js("(value) => value === undefined")(parseDate("2026-02-30")) as Boolean)
        assertTrue(js("(value) => value === undefined")(parseDate("2023-02-29")) as Boolean)
        assertTrue(js("(value) => value === undefined")(parseDate("2026-13-01")) as Boolean)
        assertTrue(js("(value) => value === undefined")(parseDate("2026-00-01")) as Boolean)
        assertTrue(js("(value) => value === undefined")(parseDate("2026-01-32")) as Boolean)
        assertTrue(js("(value) => value === undefined")(parseDate("2026-01-00")) as Boolean)
        assertTrue(dateEquals(parseDate("2024-02-29"), createDate(2024.0, 1.0, 29.0)))
    }

    // value

    @Test
    fun hasAnEmptyValueByDefault() {
        assertEquals("", newDatePicker().value)
    }

    @Test
    fun notifiesValueChange(): Promise<Unit> {
        val picker = newDatePicker()
        var valueChanged = 0
        on(picker, "value-changed") { valueChanged++ }

        return nextRender(picker).then {
            picker.value = "2000-02-01"

            assertEquals(1, valueChanged)
        }
    }

    @Test
    fun notifiesOpenedChangedOnOpenAndClose(): Promise<Unit> {
        val picker = newDatePicker()
        var openedChanged = 0
        on(picker, "opened-changed") { openedChanged++ }

        return nextRender(picker)
            .then {
                picker.`open`()
                nextRender(picker)
            }
            .then {
                assertEquals(1, openedChanged)

                picker.close()
                nextRender(picker)
            }
            .then { assertEquals(2, openedChanged) }
    }

    @Test
    fun setsOpenedToFalseOnClose(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker)
            .then {
                picker.`open`()
                nextRender(picker)
            }
            .then {
                picker.close()

                assertFalse(picker.opened)
            }
    }

    // input value format

    @Test
    fun leadsZerosCorrectly(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker).then {
            picker.value = "+000300-02-01"

            assertEquals("2/1/0300", inputValue(picker))
        }
    }

    @Test
    fun formatsTheDisplayValue(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker).then {
            picker.value = "2000-02-01"
            assertEquals("2/1/2000", inputValue(picker))

            picker.value = "1999-12-31"
            assertEquals("12/31/1999", inputValue(picker))
        }
    }

    @Test
    fun formatsSubHundredYears(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker).then {
            picker.value = "+000001-02-01"
            assertEquals("2/1/0001", inputValue(picker))

            picker.value = "+000099-02-01"
            assertEquals("2/1/0099", inputValue(picker))
        }
    }

    // value property formats

    @Test
    fun acceptsTheIsoFormat(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker).then {
            picker.value = "0000-02-03"
            assertEquals("0000-02-03", picker.value)

            picker.value = "+010000-02-03"
            assertEquals("+010000-02-03", picker.value)

            picker.value = "-010000-02-03"
            assertEquals("-010000-02-03", picker.value)
        }
    }

    @Test
    fun acceptsASignedYear(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker).then {
            picker.value = "-2026-03-15"

            // The upstream case only asserts `_selectedDate`; the public `value` is normalised to the
            // six-digit signed form the component writes back.
            assertEquals("-002026-03-15", picker.value)
            assertTrue(inputValue(picker).isNotEmpty())
        }
    }

    @Test
    fun doesNotAcceptADateThatDoesNotExist(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker).then {
            picker.value = "2026-01-15"
            picker.value = "2026-02-30"

            assertEquals("2026-01-15", picker.value)
        }
    }

    @Test
    fun doesNotAcceptNonIsoFormats(): Promise<Unit> {
        val picker = newDatePicker()

        return nextRender(picker).then {
            listOf("03/02/01", "2010/02/03", "03/02/2010", "3 Feb 2010", "Feb 3, 2010").forEach { value ->
                picker.value = value

                assertEquals("", picker.value)
            }
        }
    }

    // ARIA

    @Test
    fun togglesAriaExpandedOnOpen(): Promise<Unit> {
        val picker = newDatePicker()
        val input = vaadin.query(picker, "input")

        return nextRender(picker)
            .then {
                picker.`open`()
                nextRender(picker)
            }
            .then {
                assertEquals("true", input?.getAttribute("aria-expanded"))

                picker.close()
                nextRender(picker)
            }
            .then { assertEquals("false", input?.getAttribute("aria-expanded")) }
    }

    // autoselect

    @Test
    fun autoselectIsFalseByDefault() {
        assertFalse(newDatePicker().autoselect)
    }

    // clear button

    @Test
    fun exposesTheClearButtonProperty() {
        val picker = attach(createDatePicker { clearButtonVisible = true })

        assertTrue(picker.clearButtonVisible)
    }

    @Test
    fun clearsTheValueOnClearButtonClick(): Promise<Unit> {
        val picker = attach(createDatePicker { clearButtonVisible = true })

        return nextRender(picker)
            .then {
                picker.value = "2024-05-17"
                vaadin.shadowQuery(picker, "[part~=clear-button]")?.let { click(it) }

                assertEquals("", picker.value)
            }
    }

    @Test
    fun removesTheHasValueAttributeOnClear(): Promise<Unit> {
        val picker = attach(createDatePicker { clearButtonVisible = true })

        return nextRender(picker)
            .then {
                picker.value = "2024-05-17"
                assertTrue(picker.hasAttribute("has-value"))

                shadowQuery(picker, "[part~=clear-button]")?.let { click(it) }

                assertFalse(picker.hasAttribute("has-value"))
            }
    }
}
