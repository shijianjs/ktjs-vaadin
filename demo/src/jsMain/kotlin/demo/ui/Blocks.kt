package demo.ui

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.paragraph
import demo.Bilingual
import demo.Language
import demo.LanguageState
import demo.tr
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.EventLog as KitEventLog
import io.github.shijianjs.ktjs.demo.page.Readout as KitReadout
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.page.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.grid as kitGrid
import io.github.shijianjs.ktjs.demo.page.newPage
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import kotlin.js.Date
import web.cssom.CSSStyleSheet
import web.dom.Node
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

    val title: String
        get() = titleText.resolve()

    val summary: String
        get() = summaryText.resolve()

    /** The page's name in [lang]; the site's translation table is declared before anything is built. */
    fun titleFor(lang: Language): String = titleText.resolve(lang)

    /** The page's one-line summary in [lang]. */
    fun summaryFor(lang: Language): String = summaryText.resolve(lang)

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

/**
 * The built page: [root] is mounted by the shell, [body] receives the demo sections and [addLink]
 * appends to the header's link row.
 *
 * The frame itself belongs to `ktjs-demo-kit` (`<ktjs-demo-page>`): title, summary, links and the body
 * column are its business, so a page here only fills the body.
 */
class PageView(
    val root: HTMLElement,
    val body: HTMLElement,
    val addLink: (Node) -> Unit,
    /** Adopts one more sheet into the page's shadow tree, for rules this page owns. */
    val addSheet: (CSSStyleSheet) -> Unit,
)

internal const val REPO_SOURCE_ROOT = "https://github.com/shijianjs/ktjs-vaadin/blob/main/demo/src/jsMain/kotlin/demo"

/**
 * Builds the common frame of a component page: title, summary, the official documentation link and
 * the link to the Kotlin source of the page itself.
 *
 * Pages that are not converted to `Translations` yet are still built for the language that is active
 * when the shell creates them, and rebuilt when the language changes.
 */
fun page(meta: PageMeta): PageView {
    val demoPage: DemoPage = newPage()
    demoPage.addSheet(TEXT_SHEET)
    demoPage.setTitle(meta.title)
    demoPage.setSummary(meta.summary)
    demoPage.addLink(kitAnchor(tr("Vaadin 官方文档", "Vaadin documentation"), meta.docsUrl, external = true))
    demoPage.addLink(
        kitAnchor(
            tr("查看 Kotlin 源码", "Kotlin source"),
            "$REPO_SOURCE_ROOT/${meta.sourcePath}",
            external = true,
        ),
    )
    meta.docsNote?.let { note ->
        // The note explains the docs link rather than being one, so it belongs in the body; the link
        // row accepts anchors only and gives every node it takes the link class.
        demoPage.body.append(paragraph(note))
    }
    return PageView(demoPage, demoPage.body, demoPage::addLink, demoPage::addSheet)
}

/** One titled block of a page; [body] receives the individual examples. */
class Section(val root: HTMLElement, val body: HTMLElement)

/** Creates a section with [title] and an optional [description]. */
fun section(title: String, description: String = ""): Section {
    val block: SectionBlock = sectionBlock(title, description)
    return Section(block.root, block.body)
}

/** One example box: a card, optionally captioned. */
fun example(caption: String? = null, vararg children: Node): HTMLElement = exampleBox(caption, *children)

/** Horizontal group of controls or components. */
fun row(vararg children: Node): HTMLElement = kitRow(*children)

/** Horizontal group aligned to the top, for cards of different height. */
fun rowStart(vararg children: Node): HTMLElement = kitRow(*children).apply {
    style.setProperty("align-items", "flex-start")
}

/** Vertical group. */
fun column(vararg children: Node): HTMLElement = kitCol(*children)

/** Responsive grid of [children]. */
fun grid(vararg children: Node): HTMLElement = kitGrid(260, *children)

/**
 * Text that is updated in place, showing the current state of an example.
 *
 * The box and its colour come from the kit; the `prefix / value / suffix` form is this demo's
 * convention for state lines, so it stays here.
 */
class Readout(initial: String) {
    private val readout = KitReadout(initial)

    val root: HTMLElement get() = readout.root

    fun set(text: String) {
        readout.set(text)
    }

    /** Renders `prefix value suffix`, with [value] emphasised. */
    fun set(prefix: String, value: String, suffix: String = "") {
        readout.root.replaceChildren(HtmlTagName.span {
                                         textContent = prefix
                                     }, HtmlTagName.strong {
                                            textContent = value
                                        }, HtmlTagName.span {
                                                           textContent = suffix
                                                       })
    }
}

/**
 * Rolling log of the events fired by one example.
 *
 * The box, its monospace type and the line cap belong to the kit; this demo prefixes each line with
 * the wall-clock time, which is only meaningful while a reader is clicking around.
 */
class EventLog(maxEntries: Int = 8) {
    private val log = KitEventLog(maxEntries, tr("尚未触发事件", "No events yet"))

    val root: HTMLElement get() = log.root

    fun add(message: String) {
        log.add("${timestamp()} $message")
    }

    private fun timestamp(): String {
        val now = Date()
        return "${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}"
    }

    private fun pad(value: Int): String = if (value < 10) "0$value" else "$value"
}
