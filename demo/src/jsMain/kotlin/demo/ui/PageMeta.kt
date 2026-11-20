package demo.ui

import demo.Bilingual
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs

/**
 * Links and identity of one component page. Filled in by the page that implements it.
 */
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
