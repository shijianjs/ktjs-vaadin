package demo.page.overview

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.Language
import demo.LanguageState
import demo.API_DOCS_URL
import demo.catalog
import demo.ui.REPO_SOURCE_ROOT
import demo.catalogEntriesOf
import demo.catalogGroups
import demo.ui.listen
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.nav.Page
import io.github.shijianjs.ktjs.demo.page.Readout
import io.github.shijianjs.ktjs.demo.page.anchor
import io.github.shijianjs.ktjs.demo.page.heading
import io.github.shijianjs.ktjs.demo.page.newPage
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.row
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.demo.style.Palette
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldEvents
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import web.cssom.Border
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.LineStyle
import web.cssom.Padding
import web.cssom.px
import web.html.HTMLAnchorElement
import web.html.HTMLElement
import web.html.HtmlTagName

/** Every string this page shows, in one language. */
private class OverviewTexts(
    val title: String,
    val summary: String,
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
    val whatHeading: String,
    val whatBody: String,
    val indexHeading: String,
    val indexHint: String,
    val findLabel: String,
    val findPlaceholder: String,
    val showing: (Int, Int) -> String,
)

private val overviewEnglish: OverviewTexts = OverviewTexts(
    title = "Component overview",
    summary = "Kotlin/JS wrappers for the free Vaadin Web Components, demonstrating properties, " +
        "variants and interactions component by component.",
    docsLink = "Vaadin documentation",
    sourceLink = "Kotlin source",
    apiLink = "Kotlin API reference",
    whatHeading = "What this is",
    whatBody = "Every wrapped component has its own page; all examples are interactive and run in both " +
        "the light and dark Lumo themes. The showcase itself is Kotlin/JS too: every node is created " +
        "through the wrapper factories, properties are assigned with typed Kotlin values, and events " +
        "are subscribed through the generated EventTypes. No UI framework is involved.",
    indexHeading = "Component index",
    indexHint = "Filter by name, then open a card to visit its page.",
    findLabel = "Find a component",
    findPlaceholder = "e.g. grid, dialog, text",
    showing = { visible, total -> "Showing $visible of $total pages" },
)

private val overviewChinese: OverviewTexts = OverviewTexts(
    title = "组件总览",
    summary = "免费版 Vaadin Web Components 的 Kotlin/JS 封装，逐组件演示属性、变体与交互。",
    docsLink = "Vaadin 官方文档",
    sourceLink = "查看 Kotlin 源码",
    apiLink = "Kotlin API 文档",
    whatHeading = "这是什么",
    whatBody = "每个已封装的组件都有独立页面，示例全部可交互，并且可以在亮色与暗色 Lumo 主题下运行。" +
        "页面本身也是 Kotlin/JS：所有节点通过 wrapper 工厂创建，属性用 Kotlin 类型直接赋值，" +
        "事件通过生成的 EventType 订阅。没有引入任何 UI 框架。",
    indexHeading = "组件索引",
    indexHint = "输入名称过滤，点击卡片进入对应页面。",
    findLabel = "查找组件",
    findPlaceholder = "例如 grid、dialog、text",
    showing = { visible, total -> "显示 $visible / $total 个页面" },
)

private val overviewTexts: Translations<OverviewTexts> = texts(
    Langs.EN to overviewEnglish,
    Langs.ZH_HANS to overviewChinese,
)

private fun languageOf(lang: Lang): Language = if (lang == Langs.EN) Language.EN else Language.ZH

/** One index entry: the link, its name and its summary, kept together so a switch rewrites both. */
private class Card(val entry: CatalogEntry, val link: HTMLAnchorElement, val name: HTMLElement, val summary: HTMLElement)

/**
 * Landing page: what the showcase covers and an index of every component page.
 *
 * The index is filterable so the 60 entries stay reachable without scrolling; filtering only toggles
 * `hidden` on the cards that are already in the DOM, and a language switch only rewrites their copy.
 */
internal class OverviewPage : Page {
    private val page = newPage()

    private val texts = Binder(overviewTexts)

    private val counter = Readout()

    private val search = createTextField {
        clearButtonVisible = true
        inlineStyle.apply { width = 280.px }
    }

    private val cards = mutableListOf<Card>()

    /** A group's heading and its cards: a group hides when every card in it is filtered out. */
    private val groups = mutableListOf<Pair<HTMLElement, List<Card>>>()

    private var language: Language = Language.EN

    override val node get() = page

    init {
        page.addLink(texts.bind(anchor("", DOCS_BASE, external = true), { it.docsLink }))
        page.addLink(texts.bind(anchor("", REPO_SOURCE_ROOT + "/page/overview/OverviewPage.kt", external = true), { it.sourceLink }))
        page.addLink(texts.bind(anchor("", API_DOCS_URL, external = true), { it.apiLink }))

        val what = sectionBlock()
        texts.bind(what.title, { it.whatHeading })
        what.body.append(texts.bind(paragraph(), { it.whatBody }))

        val index = sectionBlock()
        texts.bind(index.title, { it.indexHeading })
        texts.bind(index.hint, { it.indexHint })
        index.body.append(row(search, counter.root), buildIndex())

        page.body.append(what.root, index.root)

        search.listen(TextFieldEvents.input) { filter() }
    }

    private fun buildIndex(): HTMLElement {
        val index = HtmlTagName.div {
            inlineStyle.apply {
                display = Display.flex
                flexDirection = FlexDirection.column
                rowGap = 20.px
            }
            // The section body aligns children to the start; the index spans the page so the grids
            // below it do too.
            style.setProperty("width", "100%")
        }

        catalogGroups.filter { group -> group != Groups.OVERVIEW }.forEach { group ->
            val groupTitle = heading(3, group.labelFor(LanguageState.current))
            // Cards flow in as many columns as the width allows: a component name plus its one-line
            // summary reads well in a card, and a vertical list of fifty-nine of them is a wall.
            val list = HtmlTagName.div {
                inlineStyle.display = Display.grid
                // Track sizing is one declarative string (`repeat(auto-fill, minmax(…)`); the typed
                // vocabulary has no counterpart for the function forms, so it stays a declaration.
                style.setProperty("grid-template-columns", "repeat(auto-fill, minmax(240px, 1fr))")
                style.setProperty("gap", "8px")
                // The section body is a column layout that sizes children to their content; without
                // this the grid picks its column count from its narrowest possible width.
                style.setProperty("width", "100%")
            }
            val groupCards = catalogEntriesOf(group).map { entry ->
                val card = card(entry)
                list.append(card.link)
                cards += card
                card
            }
            index.append(groupTitle, list)
            groups += groupTitle to groupCards
        }
        return index
    }

    /** One component entry: its name as a link to its page, its summary under it in muted text. */
    private fun card(entry: CatalogEntry): Card {
        val name = HtmlTagName.span {}
        val summary = HtmlTagName.p {
            inlineStyle.apply {
                color = Palette.muted
                fontSize = 13.px
            }
        }
        val link = anchor("", "#/${entry.id}")
        link.classList.add(web.cssom.ClassName("component-card"))
        link.inlineStyle.apply {
            display = Display.flex
            flexDirection = FlexDirection.column
            rowGap = 2.px
            padding = Padding(8.px, 12.px)
            borderRadius = 10.px
            border = Border(1.px, LineStyle.solid, Palette.border)
        }
        // The card is a link, so the browser underlines its whole text — name and summary alike.
        // Only the name is a label; the box itself is the affordance.
        link.style.textDecoration = "none"
        link.dataset["route"] = entry.id
        link.append(name, summary)
        return Card(entry, link, name, summary)
    }

    private fun filter() {
        val needle = search.value.trim().lowercase()
        cards.forEach { card ->
            val matches = needle.isEmpty() ||
                card.entry.meta.titleFor(language).lowercase().contains(needle) ||
                card.entry.id.contains(needle) ||
                card.entry.meta.summaryFor(language).lowercase().contains(needle)
            // `hidden` would lose to the card's inline `display: flex`, so filtering flips the display
            // value itself: the cards stay in the DOM and only their visibility changes.
            card.link.style.display = if (matches) "flex" else "none"
        }
        groups.forEach { (groupTitle, groupCards) ->
            val anyVisible = groupCards.any { it.link.style.display != "none" }
            groupTitle.toggleAttribute("hidden", !anyVisible)
        }
        val visible = cards.count { it.link.style.display != "none" }
        counter.set(overviewTexts[langOf(language)].showing(visible, cards.size))
    }

    override fun applyTranslation(lang: Lang) {
        val current = overviewTexts[lang]
        language = languageOf(lang)
        page.setTitle(current.title)
        page.setSummary(current.summary)
        texts.apply(lang)

        search.label = current.findLabel
        search.placeholder = current.findPlaceholder

        // Every card's copy comes from the catalog, not from this page's table: one place per page name.
        cards.forEach { card ->
            card.name.textContent = card.entry.meta.titleFor(language)
            card.summary.textContent = card.entry.meta.summaryFor(language)
        }
        groups.forEach { (groupTitle, groupCards) ->
            groupTitle.textContent = groupCards.firstOrNull()?.entry?.group?.labelFor(language)
        }
        filter()
    }

    private fun langOf(language: Language): Lang = if (language == Language.EN) Langs.EN else Langs.ZH_HANS
}
