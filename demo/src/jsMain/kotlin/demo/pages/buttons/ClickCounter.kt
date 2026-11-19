package demo.pages.buttons

import demo.ui.EventLog
import demo.ui.Readout
import io.github.shijianjs.ktjs.demo.page.PAGE_SHEET
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.col
import io.github.shijianjs.ktjs.demo.page.exampleCaption
import io.github.shijianjs.ktjs.demo.page.row
import io.github.shijianjs.ktjs.demo.style.RESET
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachStyledShadow
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import io.github.shijianjs.ktjs.vaadin.button.createButton
import web.components.CustomElementReference
import web.dom.TagName
import web.dom.clickEvent
import web.events.addHandler

/**
 * `<vaadin-demo-click-counter>`: the basic-usage example — a button, a count, and a log of clicks.
 *
 * The element owns the count and the log; the page only hands it the words. It adopts the page sheet so
 * its readout and log look like every other readout and log on the site, and its markup lives in its own
 * shadow tree, so nothing it does can reach the page's.
 */
class ClickCounter : CustomElementAdapter() {
    private val button = createButton().apply { dataset["test"] = "basic-button" }

    private val readout = Readout("")

    private val log = EventLog()

    private val caption = exampleCaption()

    private var count = 0

    /** Text on the button. */
    var buttonLabel: String = ""
        set(value) {
            field = value
            button.textContent = value
        }

    /** Prefix of the count line, e.g. `Clicks: `. */
    var clicksLabel: String = ""
        set(value) {
            field = value
            readout.set(value + count)
        }

    /** Text before the attempt number in a log line. */
    var logPrefix: String = ""

    /** Text after the attempt number in a log line. */
    var logSuffix: String = ""

    /** Caption above the log; an empty one hides the line. */
    var logCaption: String = ""
        set(value) {
            field = value
            caption.textContent = value
            caption.toggleAttribute("hidden", value.isEmpty())
        }

    init {
        button.clickEvent.addHandler {
            count++
            readout.set(clicksLabel + count)
            log.add(logPrefix + count + logSuffix)
        }
        attachStyledShadow(RESET, PAGE_SHEET).append(col(row(button, readout.root), caption, log.root))
    }

    companion object : CustomElementReference<ClickCounter>(ClickCounter::class.js) {
        val NAME: TagName<ClickCounter> = TagName("vaadin-demo-click-counter")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-click-counter>`. */
fun newClickCounter(): ClickCounter {
    ClickCounter.define()
    return ClickCounter.NAME()
}
