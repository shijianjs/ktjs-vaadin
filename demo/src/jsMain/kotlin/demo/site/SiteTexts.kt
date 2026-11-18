package demo.site

import demo.Language
import demo.catalog
import demo.catalogGroups
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
private fun siteTextsFor(lang: Language): SiteTexts = SiteTexts(
    siteTitle = SITE_TITLE,
    langLabel = if (lang == Language.EN) "English" else "中文",
    languageLabel = if (lang == Language.EN) "Language" else "语言",
    themeLabel = if (lang == Language.EN) "Theme" else "外观",
    docsLabel = if (lang == Language.EN) "Docs" else "文档",
    siteDocsLabel = if (lang == Language.EN) "Vaadin documentation" else "Vaadin 官方文档",
    apiLabel = if (lang == Language.EN) "API reference" else "API 文档",
    sourceLabel = if (lang == Language.EN) "GitHub" else "GitHub 仓库",
    lightLabel = if (lang == Language.EN) "Light" else "亮色",
    darkLabel = if (lang == Language.EN) "Dark" else "暗色",
    systemLabel = if (lang == Language.EN) "Follow browser" else "跟随浏览器",
    groupTitles = catalogGroups.associate { it.id to it.labelFor(lang) },
    pageTitles = catalog.associate { it.id to it.meta.titleFor(lang) },
)

/** The layout's translation table: one [SiteTexts] per language the demo offers. */
val siteTexts: Translations<SiteTexts> = texts(
    Langs.EN to siteTextsFor(Language.EN),
    Langs.ZH_HANS to siteTextsFor(Language.ZH),
)
