package demo.page.overview

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor as kitAnchor
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageMeta
import demo.ui.REPO_SOURCE_ROOT
import demo.ui.listen
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.demo.page.Blocks.icon
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.Blocks.row
import io.github.shijianjs.ktjs.demo.page.Grid
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.page.Readout
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.card.CardVariants
import io.github.shijianjs.ktjs.vaadin.card.createCard
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldEvents
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.dom.css.cssSheet
import io.github.shijianjs.ktjs.dom.css.dsl.CssSelector
import io.github.shijianjs.ktjs.dom.css.dsl.attributeSelector
import io.github.shijianjs.ktjs.dom.css.dsl.child
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.compound
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.pseudo
import io.github.shijianjs.ktjs.dom.css.dsl.pseudoClass
import io.github.shijianjs.ktjs.dom.css.dsl.tag
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.VaadinTagName
import web.cssom.ClassName
import web.cssom.HtmlAttributes
import web.cssom.None
import web.cssom.number
import web.cssom.Color
import web.cssom.CSSStyleSheet
import web.cssom.TextDecorationLine
import web.cssom.Display
import web.cssom.px
import web.dom.Node
import web.html.HtmlTagName

/**
 * What the index needs from the wrapper: an `<a>` around a card must stop painting text the way a
 * link paints it, and must pass the row's height through to the card inside it.
 */

/**
 * One section of the index: the heading(whose mark and text are separate nodes, so a language switch
 * rewrites the text without touching the icon) and the cards under it.
 */

/**
 * Landing page: what the showcase covers and an index of every component page.
 *
 * The index is filterable so the 60 entries stay reachable without scrolling; filtering only toggles
 * `hidden` on the cards that are already in the DOM, and a language switch only rewrites their copy.
 */
internal class OverviewPage : DemoPage() {
    private val texts = Binder(overviewTexts)

    private val counter = Readout.NAME { show() }

    private val search = createTextField {
        clearButtonVisible = true
        inlineStyle.apply { width = 280.px }
    }

    private val cards = mutableListOf<Card>()

    /** A group's heading and its cards: a group hides when every card in it is filtered out. */
    private val groups = mutableListOf<GroupSection>()
    private var language: Lang = Langs.EN

    init {
        addLink(texts.bind(kitAnchor("", Catalog.DOCS_BASE), { it.docsLink }))
        addLink(texts.bind(kitAnchor("", REPO_SOURCE_ROOT + "/page/overview/OverviewPage.kt"), { it.sourceLink }))
        addLink(texts.bind(kitAnchor("", Catalog.API_DOCS_URL), { it.apiLink }))

        val what = SectionBlock.NAME { show() }
        texts.bind(what.heading, { it.whatHeading })
        what.body.append(texts.bind(paragraph(), { it.whatBody }))

        val index = SectionBlock.NAME { show() }
        texts.bind(index.heading, { it.indexHeading })
        texts.bind(index.hint, { it.indexHint })
        index.body.append(row(search, counter.root), *buildIndex().toTypedArray())

        addSheet(INDEX_SHEET)
        body.append(what.root, index.root)

        search.listen(TextFieldEvents.input) { filter() }
    }

    private fun buildIndex(): List<Node> {
        val blocks = mutableListOf<Node>()
        Catalog.GROUPS.filter { group -> group != Groups.OVERVIEW }.forEach { group ->
            // The group's mark, the same one the drawer draws: the heading and the drawer row are
            // two views of one section, so they carry one icon.
            val groupLabel = HtmlTagName.span { textContent = group.labelFor(language) }
            // The mark sits beside the title on one line. A horizontal layout *is* that line — it
            // supplies the gap and the vertical centring through its `spacing` variant — so no
            // declaration is written by hand here.
            val groupTitle = row(icon(group.icon), heading(3).apply { append(groupLabel) })
            // Cards flow in as many columns as the width allows: a component name plus its one-line
            // summary reads well in a card, and a vertical list of fifty-nine of them is a wall.
            val list = Grid.NAME { show(240) }
            val groupCards = Catalog.entriesOf(group).map { entry ->
                val card = card(entry)
                list.append(card.link)
                cards += card
                card
            }
            blocks += groupTitle
            blocks += list
            groups += GroupSection(groupTitle, groupLabel, groupCards)
        }
        return blocks
    }

    /**
     * One component entry: a plain `vaadin-card` inside the link that makes the entry navigable.
     *
     * The card is left exactly as the component draws itself — the generated title, its own padding,
     * its own typography — and the link wraps the whole card, so the entire surface is clickable and
     * the keyboard / new-tab behaviour comes from the anchor rather than from a click handler.
     */
    private fun card(entry: CatalogEntry): Card {
        val summary = HtmlTagName.div().apply { textContent = entry.meta.summaryFor(language) }
        val node = createCard {
            // `elevated` is the component's own variant for a card that has to read as a card on a
            // plain background; nothing else is set by hand.
            addTheme(CardVariants.elevated)
            cardTitle = entry.meta.titleFor(language)
            titleHeadingLevel = 4.0
            append(summary)
        }
        val link = HtmlTagName.a {
            href = "#/${entry.id}"
            className = COMPONENT_CARD
            dataset["route"] = entry.id
            append(node)
        }
        return Card(entry, node, link, summary)
    }

    private fun filter() {
        val needle = search.value.trim().lowercase()
        cards.forEach { card ->
            val matches = needle.isEmpty() ||
                card.entry.meta.titleFor(language).lowercase().contains(needle) ||
                card.entry.id.contains(needle) ||
                card.entry.meta.summaryFor(language).lowercase().contains(needle)
            // Filtering only flips `hidden` on the link that holds the whole card, so the entries
            // stay in the DOM and a language switch keeps them.
            card.link.toggleAttribute("hidden", !matches)
        }
        groups.forEach { section ->
            section.title.toggleAttribute("hidden", section.cards.none { !it.link.hasAttribute("hidden") })
        }
        val visible = cards.count { !it.link.hasAttribute("hidden") }
        counter.set(overviewTexts[language].showing(visible, cards.size))
    }

    override fun applyTranslation(lang: Lang) {
        val current = overviewTexts[lang]
        language = lang
        setTitle(current.title)
        setSummary(current.summary)
        texts.apply(lang)

        search.label = current.findLabel
        search.placeholder = current.findPlaceholder

        // Every card's copy comes from the catalog, not from this page's table: one place per page name.
        cards.forEach { card ->
            card.node.cardTitle = card.entry.meta.titleFor(language)
            card.summary.textContent = card.entry.meta.summaryFor(language)
        }
        groups.forEach { section ->
            section.label.textContent = section.cards.first().entry.group.labelFor(language)
        }
        filter()
    }

        companion object : CustomElementReferenceAdapter<OverviewPage>(OverviewPage::class.js, "vaadin-demo-overview-page") {

        /**
         * The landing entry of the catalog.
         *
         * The index page is built from this file's own class, so the entry — route id, group, bilingual
         * copy, and the tag it builds — belongs here rather than in a file of its own.
         */
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "overview",
                group = Groups.OVERVIEW,
                meta =
                    PageMeta(
                        title = bi("组件总览", "Component overview"),
                        summary =
                            bi(
                                "免费版 Vaadin Web Components 的 Kotlin/JS 封装，逐组件演示属性、变体与交互。",
                                "Kotlin/JS wrappers for the free Vaadin Web Components, demonstrating properties, " +
                                    "variants and interactions component by component.",
                            ),
                        docsUrl = Catalog.DOCS_BASE,
                        docsNote = bi("官方文档的组件索引页", "Component index of the official documentation"),
                        sourcePath = "page/overview/OverviewPage.kt",
                    ),
                kitPage = { OverviewPage.NAME() },
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
        /** The class the index cards are found by in tests; it carries no style of its own. */
        private val COMPONENT_CARD: ClassName = ClassName("component-card")
        /** The condition the filter toggles, written as a selector instead of as text. */
        private val NOT_HIDDEN: CssSelector = pseudo(pseudoClass("not"), attributeSelector(HtmlAttributes.hidden))
        private val INDEX_SHEET: CSSStyleSheet = cssSheet {
            // `:not([hidden])`, because a `display` declared here would otherwise beat the `hidden` attribute
            // the filter sets - the same trap as an inline display. `flex` rather than `block`: the link is
            // the grid item, so the row stretches it to the tallest card, and a flex box carries that height
            // into the card instead of leaving it at its own content height.
            compound(className(COMPONENT_CARD), NOT_HIDDEN).invoke {
                display = Display.flex
            }

            // The card takes the whole cell width and the height it was stretched to. A `zh` summary (two
            // lines) and an `en` one (three lines) in the same row only line up because the tallest card
            // decides the row.
            className(COMPONENT_CARD).child(tag(VaadinTagName.card)).invoke {
                flexGrow = number(1.0)
            }

            className(COMPONENT_CARD).invoke {
                color = Color("inherit")
                // `TextDecorationLine` has a `None` subtype (`None.none` is the `none` keyword), so the
                // declaration stays typed - no text goes into this sheet.
                textDecorationLine = None.none
            }
        }
    }
}
