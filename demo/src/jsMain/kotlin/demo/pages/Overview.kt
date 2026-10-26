package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.catalog
import demo.catalogEntriesOf
import demo.catalogGroups
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.anchor
import demo.ui.div
import demo.ui.grid
import demo.ui.heading
import demo.ui.listen
import demo.ui.isHidden
import demo.ui.page
import demo.ui.paragraph
import demo.ui.section
import demo.ui.setHidden
import demo.ui.span
import demo.ui.withClass
import demo.tr
import vaadin.textfield.TextFieldEvents
import vaadin.textfield.createTextField
import web.html.HTMLElement

val overviewEntries: List<CatalogEntry> =
    listOf(
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
                    docsUrl = DOCS_BASE,
                    docsNote = bi("官方文档的组件索引页", "Component index of the official documentation"),
                    sourcePath = "pages/Overview.kt",
                ),
            builder = ::buildOverviewPage,
        ),
    )

/**
 * Landing page: what the showcase covers and an index of every component page.
 *
 * The index is filterable so the 60 entries stay reachable without scrolling; filtering only
 * toggles `hidden` on the cards that are already in the DOM.
 */
private fun buildOverviewPage(meta: PageMeta): PageView {
    val view = page(meta)

    val intro =
        section(
            tr("这是什么", "What this is"),
            tr(
                "每个已封装的组件都有独立页面，示例全部可交互，并且可以在亮色与暗色 Lumo 主题下运行。",
                "Every wrapped component has its own page; all examples are interactive and run in both the " +
                    "light and dark Lumo themes.",
            ),
        )
    intro.body.append(
        paragraph(
            tr(
                "页面本身也是 Kotlin/JS：所有节点通过 wrapper 工厂创建（createButton、createGrid …），" +
                    "属性用 Kotlin 类型直接赋值，事件通过生成的 EventType 订阅。没有引入任何 UI 框架。",
                "The showcase itself is Kotlin/JS too: every node is created through the wrapper factories " +
                    "(createButton, createGrid …), properties are assigned with typed Kotlin values, and events " +
                    "are subscribed through the generated EventTypes. No UI framework is involved.",
            ),
        ),
    )
    view.body.append(intro.root)

    val index =
        section(
            tr("组件索引", "Component index"),
            tr("输入名称过滤，点击卡片进入对应页面。", "Filter by name, then open a card to visit its page."),
        )
    val counter = Readout(tr("共 ${catalog.size} 个页面", "${catalog.size} pages"))
    val search =
        createTextField {
            label = tr("查找组件", "Find a component")
            placeholder = tr("例如 grid、dialog、text", "e.g. grid, dialog, text")
            clearButtonVisible = true
        }
    search.className = web.cssom.ClassName("field-width")

    val cards = mutableListOf<Pair<CatalogEntry, HTMLElement>>()
    val groupBlocks = mutableListOf<Pair<HTMLElement, MutableList<HTMLElement>>>()

    catalogGroups.filter { it != Groups.OVERVIEW }.forEach { group ->
        val groupHeading = heading(3, group.label, "index__group-title")
        val groupGrid = grid().withClass("index__grid")
        val groupCards = mutableListOf<HTMLElement>()
        catalogEntriesOf(group).forEach { entry ->
            val card =
                anchor("", "#/${entry.id}", "component-card").apply {
                    dataset["route"] = entry.id
                }
            card.append(
                span(entry.meta.title, "component-card__title"),
                paragraph(entry.meta.summary, "component-card__summary"),
            )
            groupGrid.append(card)
            groupCards += card
            cards += entry to card
        }
        index.body.append(groupHeading, groupGrid)
        groupBlocks += groupHeading to groupCards
    }

    index.body.prepend(div("index__toolbar", search, counter.root))
    view.body.append(index.root)

    search.listen(TextFieldEvents.input) {
        val needle = search.value.trim().lowercase()
        cards.forEach { (entry, card) ->
            val matches = needle.isEmpty() || entry.meta.title.lowercase().contains(needle) ||
                entry.id.contains(needle) || entry.meta.summary.contains(needle)
            card.setHidden(!matches)
        }
        groupBlocks.forEach { (groupHeading, groupCards) ->
            groupHeading.setHidden(groupCards.all { it.isHidden() })
        }
        val visible = cards.count { !it.second.isHidden() }
        counter.set(tr("显示 ", "Showing "), "$visible", tr(" / ${catalog.size} 个页面", " of ${catalog.size} pages"))
    }

    return view
}
