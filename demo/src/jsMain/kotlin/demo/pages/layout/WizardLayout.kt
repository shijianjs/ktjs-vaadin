package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import io.github.shijianjs.ktjs.vaadin.formlayout.FormLayoutElement
import io.github.shijianjs.ktjs.vaadin.formlayout.FormLayoutResponsiveStep
import io.github.shijianjs.ktjs.vaadin.formlayout.StringOrDouble
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormLayout
import js.objects.unsafeJso
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HTMLElement

/**
 * `<demo-wizard-layout>`: the two-step form layout the wizard example and the accordion panels share.
 *
 * Fields stay light-DOM children of the form layout inside this element — not slotted ones — because the
 * form layout reads each field's `colspan` and places it on its grid; a field behind a slot would no
 * longer be a child it can lay out.
 */
internal class WizardLayout : CustomElementAdapter() {
    private val form: FormLayoutElement =
        createFormLayout().apply {
            responsiveSteps =
                arrayOf(
                    unsafeJso<FormLayoutResponsiveStep> {
                        minWidth = StringOrDouble(0.0)
                        columns = 1.0
                    },
                    unsafeJso<FormLayoutResponsiveStep> {
                        minWidth = StringOrDouble("20em")
                        columns = 2.0
                    },
                )
        }

    /**
     * Attached when the element connects, and only once: a reconnect must not move the form out of the
     * element that already owns it.
     */
    override fun connectedCallback() {
        if (form.parentNode == null) append(form)
    }

    /** Adds the fields of the step; callers may set `colspan` on each before or after. */
    fun add(vararg fields: HTMLElement) {
        form.append(*fields)
    }

    companion object : CustomElementReference<WizardLayout>(WizardLayout::class.js) {
        val NAME: TagName<WizardLayout> = TagName("demo-wizard-layout")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<demo-wizard-layout>` holding [fields]. */
internal fun newWizardLayout(vararg fields: HTMLElement): WizardLayout {
    WizardLayout.define()
    return WizardLayout.NAME().also { it.add(*fields) }
}
