package demo.ui

import demo.CatalogEntry
import demo.componentApiUrl
import demo.frameTexts
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.DemoPage
import web.html.HTMLAnchorElement
import web.html.HTMLElement

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
