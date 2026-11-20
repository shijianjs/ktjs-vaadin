package demo.pages.layout

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.splitlayout.createSplitLayout
import web.html.HTMLElement
import web.html.HtmlTagName

internal class FormFrame : CustomElementAdapter() {
    private val split = createSplitLayout().apply {
        style.setProperty("height", "18rem")
    }

    /** Takes [form] into the frame; the form fills the first pane and nothing fills the second. */
    fun show(form: HTMLElement) {
        form.style.setProperty("width", "100%")
        split.append(form, HtmlTagName.div().apply { style.setProperty("background", "var(--lumo-contrast-5pct)") })
    }

    init {
        attachShadowRoot().append(split)
    }

    companion object : CustomElementReferenceAdapter<FormFrame>(FormFrame::class.js, "vaadin-demo-form-frame")
}
