package demo.pages.buttons

import io.github.shijianjs.ktjs.demo.page.PAGE_SHEET
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.Blocks.row
import io.github.shijianjs.ktjs.demo.style.RESET
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachStyledShadow
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.emailfield.createEmailField
import web.dom.clickEvent
import web.events.addHandler

/**
 * `<vaadin-demo-email-row>`: an email field with the button that clears it.
 *
 * The pair is one element because the button only ever acts on that field, and the button's accessible
 * name is built from both labels — so the row keeps the two consistent instead of the page doing it.
 */
class EmailRow : CustomElementAdapter() {
    private val emailField = createEmailField()

    private val remove = createButton()

    /** Text on the clearing button. */
    var removeLabel: String = ""
        set(value) {
            field = value
            render()
        }

    /** What this row clears, used in the button's accessible name. */
    var subject: String = ""
        set(value) {
            field = value
            render()
        }

    init {
        remove.clickEvent.addHandler { emailField.value = "" }
        attachStyledShadow(RESET, PAGE_SHEET).append(row(emailField, remove))
    }

    /** The field's label, and the value it starts with. */
    fun setField(label: String, value: String) {
        emailField.label = label
        emailField.value = value
    }

    private fun render() {
        remove.textContent = removeLabel
        remove.ariaLabel = if (subject.isEmpty()) removeLabel else "$removeLabel $subject"
    }

    companion object : CustomElementReferenceAdapter<EmailRow>(EmailRow::class.js, "vaadin-demo-email-row")
}
