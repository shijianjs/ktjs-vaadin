package demo.ui

import io.github.shijianjs.ktjs.demo.page.EventLog as KitEventLog
import io.github.shijianjs.ktjs.dom.element.invoke
import kotlin.js.Date
import web.html.HTMLElement

/**
 * Rolling log of the events fired by one example.
 *
 * The box, its monospace type and the line cap belong to the kit; this demo prefixes each line with
 * the wall-clock time, which is only meaningful while a reader is clicking around.
 */
class EventLog(maxEntries: Int = 8, placeholder: String = "") {
    private val log = KitEventLog.NAME { show(maxEntries, placeholder) }

    val root: HTMLElement get() = log.root

    fun add(message: String) {
        log.add("${timestamp()} $message")
    }

    /** Rewrites the line shown while no event has arrived, so a language switch keeps it local. */
    fun placeholder(text: String) {
        log.placeholder(text)
    }

    /** Drops every line; a language switch replays the recorded events with the new copy. */
    fun clear() {
        log.clear()
    }

    /** The clock a line would carry now; a page records it when the event happens. */
    fun now(): String = timestamp()

    /** Adds a line under the time it originally happened, so a replay keeps the clock, not just the copy. */
    fun addAt(time: String, message: String) {
        log.add("$time $message")
    }

    private fun timestamp(): String {
        val now = Date()
        return "${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}"
    }

    private fun pad(value: Int): String = if (value < 10) "0$value" else "$value"
}
