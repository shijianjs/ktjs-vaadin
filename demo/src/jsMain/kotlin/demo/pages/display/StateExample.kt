package demo.pages.display

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.vaadin.inputcontainer.createInputContainer
import web.cssom.rem
import web.html.HTMLElement
import web.html.HtmlTagName

/** One state example: the container, the input inside it, and the caption under it. */
internal class StateExample(readOnly: Boolean = false, invalid: Boolean = false, disabled: Boolean = false) {
    val input = containerInput()

    val container: HTMLElement = createInputContainer().apply {
        this.readonly = readOnly
        this.invalid = invalid
        this.disabled = disabled
        append(input)
    }

    val caption: HTMLElement = paragraph("")

    val root: HTMLElement = HtmlTagName.div().apply {
        inlineStyle.apply { width = 12.rem }
        append(container, caption)
    }
}
