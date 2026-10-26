package demo.ui

import demo.Bilingual
import demo.Language
import demo.LanguageState
import demo.tr
import kotlin.js.Date
import web.dom.Node
import web.html.HTMLElement
import web.html.HtmlTagName

/** Links and identity of one component page. Filled in by the page that implements it. */
class PageMeta(
    title: Bilingual,
    summary: Bilingual,
    /** Chinese Vaadin documentation page for the component; the English URL is derived from it. */
    docsUrl: String,
    /** Set when [docsUrl] is not a page dedicated to this element (internal or merged docs). */
    docsNote: Bilingual? = null,
    /** Path of the Kotlin source of the page, relative to `demo/src/jsMain/kotlin/demo`. */
    val sourcePath: String,
) {
    private val titleText = title
    private val summaryText = summary
    private val docsUrlZh = docsUrl
    private val docsNoteText = docsNote

    val title: String
        get() = titleText.resolve()

    val summary: String
        get() = summaryText.resolve()

    /** The official documentation page in the active language. */
    val docsUrl: String
        get() =
            if (LanguageState.current == Language.EN) docsUrlZh.replace(ZH_DOCS_SEGMENT, EN_DOCS_SEGMENT) else docsUrlZh

    val docsNote: String?
        get() = docsNoteText?.resolve()

    private companion object {
        const val ZH_DOCS_SEGMENT = "/latest/zh/"
        const val EN_DOCS_SEGMENT = "/latest/"
    }
}

/** The built page: [root] is mounted by the shell, [body] receives the demo sections. */
class PageView(val root: HTMLElement, val body: HTMLElement, val links: HTMLElement)

internal const val REPO_SOURCE_ROOT = "https://github.com/shijianjs/ktjs-vaadin/blob/main/demo/src/jsMain/kotlin/demo"

/**
 * Builds the common frame of a component page: title, summary, the official documentation link and
 * the link to the Kotlin source of the page itself.
 */
fun page(meta: PageMeta): PageView {
    val body = div("page__body")
    val links = div("page__links")

    links.append(anchor(tr("Vaadin 官方文档", "Vaadin documentation"), meta.docsUrl, "page__link", external = true))
    links.append(
        anchor(tr("查看 Kotlin 源码", "Kotlin source"), "$REPO_SOURCE_ROOT/${meta.sourcePath}", "page__link", external = true),
    )
    meta.docsNote?.let { links.append(span(it, "page__link-note")) }

    val root = element(HtmlTagName.section, "page")
    root.append(heading(1, meta.title, "page__title"), paragraph(meta.summary, "page__summary"), links, body)
    return PageView(root, body, links)
}

/** One titled block of a page; [body] receives the individual examples. */
class Section(val root: HTMLElement, val body: HTMLElement)

/** Creates a section with [title] and an optional [description]. */
fun section(title: String, description: String = ""): Section {
    val body = div("section__body")
    val root = div("section")
    root.append(heading(2, title, "section__title"))
    if (description.isNotEmpty()) root.append(paragraph(description, "section__desc"))
    root.append(body)
    return Section(root, body)
}

/** A bordered area holding one runnable example, optionally captioned. */
fun example(caption: String? = null, vararg children: Node): HTMLElement {
    val box = div("example")
    if (caption != null) box.append(paragraph(caption, "example__caption"))
    box.append(*children)
    return box
}

/** Horizontal group of controls or components. */
fun row(vararg children: Node): HTMLElement = div("row", *children)

/** Horizontal group aligned to the top, for cards of different height. */
fun rowStart(vararg children: Node): HTMLElement = div("row row--start", *children)

/** Vertical group. */
fun column(vararg children: Node): HTMLElement = div("col", *children)

/** Responsive grid of [children]. */
fun grid(vararg children: Node): HTMLElement = div("grid", *children)

/** Text that is updated in place, showing the current state of an example. */
class Readout(initial: String) {
    val root: HTMLElement = paragraph(initial, "readout")

    fun set(text: String) {
        root.textContent = text
    }

    /** Renders `prefix value suffix`, with [value] emphasised. */
    fun set(prefix: String, value: String, suffix: String = "") {
        root.replaceChildren(span(prefix), strong(value), span(suffix))
    }
}

/** Rolling log of the events fired by one example. */
class EventLog(private val maxEntries: Int = 8) {
    val root: HTMLElement = element(HtmlTagName.ul, "event-log")

    private val placeholder =
        element(HtmlTagName.li, "event-log__empty").apply { textContent = tr("尚未触发事件", "No events yet") }

    init {
        root.append(placeholder)
    }

    fun add(message: String) {
        placeholder.remove()
        val entry = element(HtmlTagName.li)
        entry.append(span(timestamp(), "event-log__time"), span(message))
        root.append(entry)
        while (root.childElementCount > maxEntries) {
            root.firstElementChild?.remove()
        }
        root.scrollTop = root.scrollHeight.toDouble()
    }

    private fun timestamp(): String {
        val now = Date()
        return "${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}"
    }

    private fun pad(value: Int): String = if (value < 10) "0$value" else "$value"
}
