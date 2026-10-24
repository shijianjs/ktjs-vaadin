package vaadin.datepicker

import kotlin.js.Promise
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.on
import web.html.HTMLElement

/**
 * Translated from `packages/date-picker/test/events.test.js`.
 *
 * Both upstream cases type into the input with `sendKeys` before changing the value
 * programmatically; here the same input state is produced with [setDatePickerInput], which writes the
 * value and dispatches the `input` event the component listens to.
 */
class EventsTest {
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
    fun doesNotFireChangeOnAProgrammaticValueChangeWhileOpened(): Promise<Unit> {
        val picker = newDatePicker()
        var change = 0
        on(picker, "change") { change++ }

        return nextRender(picker)
            .then {
                picker.`open`()
                nextRender(picker)
            }
            .then {
                picker.value = "2000-01-01"
                picker.close()

                assertEquals(0, change)
            }
    }

    @Test
    fun doesNotFireChangeOnAProgrammaticValueChangeWithUserInput(): Promise<Unit> {
        val picker = newDatePicker()
        var change = 0
        on(picker, "change") { change++ }

        return nextRender(picker)
            .then {
                picker.`open`()
                setDatePickerInput(picker, "1/2/2000")
                nextRender(picker)
            }
            .then {
                picker.value = "2000-01-01"
                picker.close()

                assertEquals(0, change)
            }
    }
}
