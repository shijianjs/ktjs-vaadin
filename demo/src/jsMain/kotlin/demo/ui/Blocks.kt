package demo.ui

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.page.paragraph
import demo.Bilingual
import demo.CatalogEntry
import demo.componentApiUrl
import demo.frameTexts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.newEventLog
import io.github.shijianjs.ktjs.demo.page.newReadout
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.page.anchor as kitAnchor
import kotlin.js.Date
import web.html.HTMLAnchorElement
import web.html.HTMLElement
import web.html.HtmlTagName

/** Links and identity of one component page. Filled in by the page that implements it. */
class PageMeta(
    title: Bilingual,
    summary: Bilingual,
    /** Chinese Vaadin documentation page for the component; the English URL is derived from it. */
    docsUrl: String,
    /** Set when [docsUrl] is not a page dedicated to this internal or merged docs(). */
    docsNote: Bilingual? = null,
    /** Path of the Kotlin source of the page, relative to `demo/src/jsMain/kotlin/demo`. */
    val sourcePath: String,
) {
    private val titleText = title
    private val summaryText = summary
    private val docsUrlZh = docsUrl
    private val docsNoteText = docsNote

    /** The page's name in [lang]; the site's translation table is declared before anything is built. */
    fun titleFor(lang: Lang): String = titleText.resolve(lang)

    /** The page's one-line summary in [lang]. */
    fun summaryFor(lang: Lang): String = summaryText.resolve(lang)

    /** The official documentation page in [lang]: the English site has no `/zh/` segment. */
    fun docsUrlFor(lang: Lang): String =
        if (lang == Langs.EN) docsUrlZh.replace(ZH_DOCS_SEGMENT, EN_DOCS_SEGMENT) else docsUrlZh

    /** The note that explains why [docsUrlFor] is not a page of its own, in [lang]. */
    fun docsNoteFor(lang: Lang): String? = docsNoteText?.resolve(lang)

    private companion object {
        const val ZH_DOCS_SEGMENT = "/latest/zh/"
        const val EN_DOCS_SEGMENT = "/latest/"
    }
}

internal const val REPO_SOURCE_ROOT = "https://github.com/shijianjs/ktjs-vaadin/blob/main/demo/src/jsMain/kotlin/demo"

/**
 * The frame of a catalog page: the three links every page carries and, when the entry says so, the
 * note explaining the documentation link.
 *
 * The links are built once and localized in place — [show] rewrites their labels and the language of
 * the documentation URL — so a language switch does not build a second frame next to the first. A page
 * holds one as a field, mounts it in `init` and shows it again from `applyTranslation`.
 *
 * The title and the summary are not here: they are the page's own copy, taken from its text table.
 */
internal class PageFrame(private val entry: CatalogEntry) {
    private val meta: PageMeta get() = entry.meta

    private val docsLink: HTMLAnchorElement = kitAnchor("", "", external = true)

    private val sourceLink: HTMLAnchorElement = kitAnchor("", "$REPO_SOURCE_ROOT/${meta.sourcePath}", external = true)

    private val apiLink: HTMLAnchorElement = kitAnchor("", componentApiUrl(entry.id))

    /**
     * The note explains the docs link rather than being one, so it belongs to the body; the link row
     * accepts anchors only and gives every node it takes the link class.
     */
    private val note: HTMLElement? = if (meta.docsNoteFor(Langs.EN) == null) null else paragraph()

    /** Adds the frame to [page]; the copy arrives with [show]. */
    fun mount(page: DemoPage) {
        page.addSheet(TEXT_SHEET)
        page.addLink(docsLink)
        page.addLink(sourceLink)
        page.addLink(apiLink)
        note?.let { page.body.append(it) }
    }

    /** Writes the frame's copy for [lang]. */
    fun show(lang: Lang) {
        val copy = frameTexts[lang]
        docsLink.textContent = copy.docsLink
        docsLink.href = meta.docsUrlFor(lang)
        sourceLink.textContent = copy.sourceLink
        apiLink.textContent = copy.apiLink
        note?.let { it.textContent = meta.docsNoteFor(lang) ?: "" }
    }
}


/**
 * Text that is updated in place, showing the current state of an example.
 *
 * The box and its colour come from the kit; the `prefix / value / suffix` form is this demo's
 * convention for state lines, so it stays here.
 */
class Readout(initial: String) {
    private val readout = newReadout(initial)

    val root: HTMLElement get() = readout.root

    fun set(text: String) {
        readout.set(text)
    }

    /** Renders `prefix value suffix`, with [value] emphasised. */
    fun set(prefix: String, value: String, suffix: String = "") {
        readout.root.replaceChildren(
            HtmlTagName.span { textContent = prefix },
            HtmlTagName.strong { textContent = value },
            HtmlTagName.span { textContent = suffix },
        )
    }
}

/**
 * Rolling log of the events fired by one example.
 *
 * The box, its monospace type and the line cap belong to the kit; this demo prefixes each line with
 * the wall-clock time, which is only meaningful while a reader is clicking around.
 */
class EventLog(maxEntries: Int = 8, placeholder: String = "") {
    private val log = newEventLog(maxEntries, placeholder)

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
