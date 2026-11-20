package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import js.objects.unsafeJso
import io.github.shijianjs.ktjs.vaadin.accordion.createAccordionPanel
import io.github.shijianjs.ktjs.vaadin.checkbox.createCheckbox
import io.github.shijianjs.ktjs.vaadin.emailfield.EmailFieldElement
import io.github.shijianjs.ktjs.vaadin.emailfield.createEmailField
import io.github.shijianjs.ktjs.vaadin.select.SelectItemData
import web.html.HTMLElement


internal fun playgroundCheckbox(value: String, label: String) = createCheckbox { this.value = value; this.label = label }

internal fun selectOptions(vararg entries: String): Array<SelectItemData> =
    entries
        .mapIndexed { index, label ->
            unsafeJso<SelectItemData> {
                this.label = label
                value = index.toString()
            }
        }.toTypedArray()


internal fun emailField(label: String): EmailFieldElement = createEmailField { this.label = label }

/** An accordion panel with a summary and one content child. */
internal fun accordionPanel(summary: String): io.github.shijianjs.ktjs.vaadin.accordion.AccordionPanelElement =
    createAccordionPanel { this.summary = summary }
