package demo.pages.buttons

import io.github.shijianjs.ktjs.demo.page.PAGE_SHEET
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.row
import io.github.shijianjs.ktjs.demo.style.RESET
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachStyledShadow
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.progressbar.createProgressBar
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.components.CustomElementReference
import web.dom.TagName
import web.dom.clickEvent
import web.events.addHandler

private var tickHandle: Int? = null

private fun startTicking(millis: Int, tick: () -> Unit) {
    stopTicking()
    tickHandle = js("(fn, ms) => window.setInterval(fn, ms)")(tick, millis) as Int
}

private fun stopTicking() {
    val handle = tickHandle ?: return
    js("(handle) => window.clearInterval(handle)")(handle)
    tickHandle = null
}

/**
 * `<vaadin-demo-long-action>`: a primary action that disables itself while a simulated job runs.
 *
 * The interval and the progress value are the element's own; the page supplies only the label. A
 * second click cannot arrive because the button is disabled for exactly as long as the bar moves.
 */
class LongAction : CustomElementAdapter() {
    private val perform = createButton().addTheme("primary")

    private val progress = createProgressBar { value = 0.0 }.apply { style.setProperty("flex", "1") }

    /** Text on the action button. */
    var performLabel: String = ""
        set(value) {
            field = value
            perform.textContent = value
        }

    init {
        perform.clickEvent.addHandler {
            perform.disabled = true
            progress.value = 0.0
            startTicking(40) {
                progress.value = (progress.value + 0.04).coerceAtMost(1.0)
                if (progress.value >= 1.0) {
                    stopTicking()
                    perform.disabled = false
                }
            }
        }
        attachStyledShadow(RESET, PAGE_SHEET).append(row(perform, progress))
    }

    companion object : CustomElementReference<LongAction>(LongAction::class.js) {
        val NAME: TagName<LongAction> = TagName("vaadin-demo-long-action")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-long-action>`. */
fun newLongAction(): LongAction {
    LongAction.define()
    return LongAction.NAME()
}
