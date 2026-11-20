package demo

import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts

/**
 * The three links every page frame carries, in one language.
 *
 * The labels are the site's, not a page's: sixty pages show the same three links, so they cannot live
 * in sixty text tables. A page's [PageFrame][demo.ui.PageFrame] reads this table when it is shown.
 */
internal class FrameTexts(
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
)

private val english: FrameTexts = FrameTexts(
    docsLink = "Vaadin documentation",
    sourceLink = "Kotlin source",
    apiLink = "Kotlin API reference",
)

private val chinese: FrameTexts = FrameTexts(
    docsLink = "Vaadin 官方文档",
    sourceLink = "查看 Kotlin 源码",
    apiLink = "Kotlin API 文档",
)

/** The frame's copy per language. */
internal val frameTexts: Translations<FrameTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)
