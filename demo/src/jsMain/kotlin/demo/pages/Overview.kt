package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.catalog
import demo.catalogEntriesOf
import demo.catalogGroups
import demo.ui.PageMeta
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
                    title = "组件总览",
                    summary = "免费版 Vaadin Web Components 的 Kotlin/JS 封装，逐组件演示属性、变体与交互。",
                    docsUrl = DOCS_BASE,
                    docsNote = "官方文档的组件索引页",
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
private fun buildOverviewPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val intro = section("这是什么", "每个已封装的组件都有独立页面，示例全部可交互，并且可以在亮色与暗色 Lumo 主题下运行。")
    intro.body.append(
        paragraph(
            "页面本身也是 Kotlin/JS：所有节点通过 wrapper 工厂创建（createButton、createGrid …），" +
                "属性用 Kotlin 类型直接赋值，事件通过生成的 EventType 订阅。没有引入任何 UI 框架。",
        ),
    )
    view.body.append(intro.root)

    val index = section("组件索引", "输入名称过滤，点击卡片进入对应页面。")
    val counter = Readout("共 ${catalog.size} 个页面")
    val search =
        createTextField {
            label = "查找组件"
            placeholder = "例如 grid、dialog、text"
            clearButtonVisible = true
        }
    search.className = web.cssom.ClassName("field-width")

    val cards = mutableListOf<Pair<CatalogEntry, HTMLElement>>()
    val groupBlocks = mutableListOf<Pair<HTMLElement, MutableList<HTMLElement>>>()

    catalogGroups.filter { it != Groups.OVERVIEW }.forEach { group ->
        val groupHeading = heading(3, group)
        val groupGrid = grid()
        val groupCards = mutableListOf<HTMLElement>()
        catalogEntriesOf(group).forEach { entry ->
            val card =
                anchor(entry.meta.title, "#/${entry.id}", "component-card").apply {
                    dataset["route"] = entry.id
                }
            card.append(paragraph(entry.meta.summary, "small muted"))
            groupGrid.append(card)
            groupCards += card
            cards += entry to card
        }
        index.body.append(groupHeading, groupGrid)
        groupBlocks += groupHeading to groupCards
    }

    index.body.prepend(counter.root, search)
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
        counter.set("显示 ", "$visible", " / ${catalog.size} 个页面")
    }

    return view.root
}
