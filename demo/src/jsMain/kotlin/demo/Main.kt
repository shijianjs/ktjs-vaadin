package demo

import kotlin.js.js
import vaadin.button.createButton
import vaadin.checkbox.createCheckbox
import vaadin.combobox.createComboBox
import vaadin.emailfield.createEmailField
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.notification.Notification
import vaadin.notification.registerNotification
import vaadin.radiogroup.createRadioButton
import vaadin.radiogroup.createRadioGroup
import vaadin.textfield.createTextField
import web.cssom.ClassName
import web.dom.clickEvent
import web.dom.document
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName

private const val PROJECT_NAME = "ktjs-vaadin"

private val teams: dynamic = js(
    """([
        { label: 'Engineering', value: 'engineering' },
        { label: 'Design', value: 'design' },
        { label: 'Support', value: 'support' }
    ])""",
)

private val members: dynamic = js(
    """([
        { name: 'Ada Lovelace', role: 'Mathematician' },
        { name: 'Grace Hopper', role: 'Computer scientist' },
        { name: 'Margaret Hamilton', role: 'Software engineer' },
        { name: 'Katherine Johnson', role: 'Mathematician' }
    ])""",
)

/**
 * Entry point of the demo page.
 *
 * Every component is created through the wrapper factory, configured with typed properties, and
 * then mutated directly. No markup is assembled from strings.
 */
fun main() {
    registerNotification()

    val page = document.createElement(HtmlTagName.div)
    page.className = ClassName("page")

    val title = document.createElement(HtmlTagName.h1)
    title.textContent = "$PROJECT_NAME demo"

    val hint = document.createElement(HtmlTagName.p)
    hint.className = ClassName("hint")
    hint.textContent = "Kotlin/JS wrappers for the free Vaadin Web Components, built without a UI framework."

    val cards = document.createElement(HtmlTagName.div)
    cards.className = ClassName("cards")
    cards.append(buildFormCard(), buildGridCard())

    page.append(title, hint, cards)
    document.body.append(page)
}

private fun buildFormCard(): HTMLElement {
    val nameField = createTextField {
        label = "Name"
        placeholder = "Ada Lovelace"
        required = true
    }

    val emailField = createEmailField {
        label = "Email"
        placeholder = "ada@example.com"
    }

    val teamBox = createComboBox {
        label = "Team"
        itemLabelPath = "label"
        items = teams
        value = "engineering"
    }

    val planGroup = createRadioGroup {
        label = "Plan"
        value = "pro"
    }
    planGroup.append(
        createRadioButton {
            value = "free"
            textContent = "Free"
        },
        createRadioButton {
            value = "pro"
            textContent = "Pro"
        },
    )

    val notifyBox = createCheckbox {
        label = "Email me about new releases"
        checked = true
    }

    val result = document.createElement(HtmlTagName.p)
    result.className = ClassName("result")
    result.textContent = "Fill in the form and press the button."

    val submit = createButton { textContent = "Say hello" }
    submit.clickEvent.addHandler {
        val name = nameField.value?.trim().orEmpty().ifEmpty { "world" }
        val message = "Hello, $name!"
        result.textContent = message
        Notification.show(message)
    }

    val body = column(className = "form")
    body.append(nameField, emailField, teamBox, planGroup, notifyBox, submit, result)
    return card("Sign up", body)
}

private fun buildGridCard(): HTMLElement {
    val grid = createGrid()
    grid.append(
        createGridColumn {
            header = "Name"
            path = "name"
        },
        createGridColumn {
            header = "Role"
            path = "role"
        },
    )
    grid.items = members

    return card("Members", grid)
}

private fun card(title: String, content: HTMLElement): HTMLElement {
    val section = document.createElement(HtmlTagName.section)
    section.className = ClassName("card")

    val heading = document.createElement(HtmlTagName.h2)
    heading.textContent = title

    section.append(heading, content)
    return section
}

private fun column(className: String): HTMLElement {
    val element = document.createElement(HtmlTagName.div)
    element.className = ClassName(className)
    return element
}
