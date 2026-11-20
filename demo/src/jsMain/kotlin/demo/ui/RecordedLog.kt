package demo.ui

import web.html.HTMLElement

/**
 * A log whose lines are copy rather than data.
 *
 * Each line is kept as the field that wrote it plus the time it happened, so a language switch can
 * replay the history in the new language under its original clocks; the plain [EventLog] keeps the
 * wall-clock prefix and the cap.
 */
class RecordedLog<T>(maxEntries: Int = 6) {
    private val log = EventLog(maxEntries)

    private val lines = mutableListOf<Pair<String, (T) -> String>>()

    val root: HTMLElement get() = log.root

    /** Adds one line and remembers how to write it in every language. */
    fun add(copy: T, line: (T) -> String) {
        val time = log.now()
        lines += time to line
        log.addAt(time, line(copy))
    }

    /** Rewrites every recorded line in [copy], keeping the clocks they were recorded under. */
    fun replay(copy: T) {
        log.clear()
        lines.forEach { (time, line) -> log.addAt(time, line(copy)) }
    }

    fun clear() {
        lines.clear()
        log.clear()
    }
}
