package demo.site

import demo.Catalog
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.SiteTexts
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts

/** The name shown in the navbar, the document title and the drawer's home entry. */
private const val SITE_TITLE: String = "ktjs-vaadin"

/**
 * The layout's copy in one language.
 *
 * The page and group names are not repeated here: they already live on the catalog ([PageMeta]'s
 * `Bilingual` title), which is the single place a page is declared. This table only adds the shell's
 * own labels, so adding a component page never means editing two lists of titles.
 */
private fun siteTextsFor(lang: Lang): SiteTexts {
    val en = lang == Langs.EN
    return SiteTexts(
        siteTitle = SITE_TITLE,
        langLabel = if (en) "English" else "中文",
        languageLabel = if (en) "Language" else "语言",
        themeLabel = if (en) "Theme" else "外观",
        docsLabel = if (en) "Docs" else "文档",
        siteDocsLabel = if (en) "Vaadin documentation" else "Vaadin 官方文档",
        apiLabel = if (en) "API reference" else "API 文档",
        sourceLabel = if (en) "GitHub" else "GitHub 仓库",
        lightLabel = if (en) "Light" else "亮色",
        darkLabel = if (en) "Dark" else "暗色",
        systemLabel = if (en) "Follow browser" else "跟随浏览器",
        groupTitles = Catalog.GROUPS.associate { it.id to it.labelFor(lang) },
        pageTitles = Catalog.ENTRIES.associate { it.id to it.meta.titleFor(lang) },
    )
}

/** The layout's translation table: one [SiteTexts] per language the demo offers. */
val siteTexts: Translations<SiteTexts> = texts(
    Langs.EN to siteTextsFor(Langs.EN),
    Langs.ZH_HANS to siteTextsFor(Langs.ZH_HANS),
)
