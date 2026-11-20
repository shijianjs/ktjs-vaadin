package demo.pages.buttons

import demo.ui.EventLog
import io.github.shijianjs.ktjs.demo.page.PAGE_SHEET
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.Blocks.col
import io.github.shijianjs.ktjs.demo.page.Blocks.row
import io.github.shijianjs.ktjs.demo.style.RESET
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachStyledShadow
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.dom.clickEvent
import web.events.addHandler

/**
 * `<vaadin-demo-variant-log>`: one button per theme variant, and a log of which one was clicked.
 *
 * Variant names stay literal — they are the values the property accepts — while the variant-less
 * button is translated. The log keeps four lines, so the box never grows the page's height.
 */
class VariantLog : CustomElementAdapter() {
    private val log = EventLog(4)

    private val defaultButton = createButton()

    /** Text of the button that carries no variant. */
    var defaultLabel: String = ""
        set(value) {
            field = value
            defaultButton.textContent = value
        }

    init {
        defaultButton.clickEvent.addHandler { log.add("click · " + defaultButton.textContent) }
        val themed =
            listOf(
                "primary" to listOf("primary"),
                "tertiary" to listOf("tertiary"),
                "tertiary-inline" to listOf("tertiary-inline"),
                "contrast" to listOf("contrast"),
            ).map { (label, themes) ->
                createButton { textContent = label }.addTheme(*themes.toTypedArray()).apply {
                    clickEvent.addHandler { log.add("click · $label") }
                }
            }
        val buttons = row(*listOf(defaultButton).plus(themed).toTypedArray())
        attachStyledShadow(RESET, PAGE_SHEET).append(col(buttons, log.root))
    }

    companion object : CustomElementReferenceAdapter<VariantLog>(VariantLog::class.js, "vaadin-demo-variant-log")
}
