package demo.pages.data

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.column
import demo.ui.example
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.strong
import demo.ui.withTheme
import kotlin.js.JsAny
import vaadin.avatar.createAvatar
import vaadin.button.createButton
import vaadin.details.DetailsEvents
import vaadin.details.createDetails
import vaadin.horizontallayout.createHorizontalLayout
import vaadin.verticallayout.createVerticalLayout
import vaadin.virtuallist.createVirtualList
import web.dom.clickEvent
import web.events.addHandler

val virtualListEntry: CatalogEntry =
    CatalogEntry(
        id = "virtual-list",
        group = Groups.DATA,
        meta =
            PageMeta(
                title = bi("Virtual List", "Virtual List"),
                summary =
                    bi(
                        "只用 renderer 渲染可见行的长列表，适合成千上万条数据。",
                        "Long lists that render only visible rows through a renderer, built for " +
                            "thousands of items.",
                    ),
                docsUrl = "$DOCS_BASE/virtual-list",
                sourcePath = "pages/data/VirtualListPage.kt",
            ),
        builder = ::buildVirtualListPage,
    )

private fun buildVirtualListPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "renderer 只对可见行调用，滚动时复用容器节点；每一行是一张人员卡片，展开联系方式后" +
                    "滚动离开再回来仍保持展开。",
                "The renderer is called only for visible rows and container nodes are reused while scrolling. " +
                    "Each row is a person card, and an expanded contact block stays open across scrolling.",
            ),
        )
    val base =
        listOf(
            PersonCard(
                "Ada",
                "Lovelace",
                tr("数学家", "Mathematician"),
                "ada.lovelace@example.com",
                "+44 20 7946 0101",
                0,
            ),
            PersonCard(
                "Grace",
                "Hopper",
                tr("计算机科学家", "Computer scientist"),
                "grace.hopper@example.com",
                "+1 202 555 0142",
                1,
            ),
            PersonCard(
                "Alan",
                "Turing",
                tr("密码学家", "Cryptanalyst"),
                "alan.turing@example.com",
                "+44 20 7946 0193",
                2,
            ),
            PersonCard(
                "Katherine",
                "Johnson",
                tr("航空航天工程师", "Aerospace engineer"),
                "katherine.johnson@example.com",
                "+1 202 555 0177",
                3,
            ),
            PersonCard(
                "Edsger",
                "Dijkstra",
                tr("计算机科学家", "Computer scientist"),
                "edsger.dijkstra@example.com",
                "+31 20 555 0124",
                4,
            ),
        )
    // The docs load a hundred people; repeating the fixed set is enough to show row reuse.
    val people = List(200) { index -> base[index % base.size] }
    val expanded = mutableSetOf<Int>()

    val list = createVirtualList()
    list.dataset["test"] = "virtual-list"
    list.items = jsArray<JsAny>((0 until people.size).joinToString(",", "[", "]"))
    list.renderer = { container, _, model ->
        val index = model.index.toInt()
        val person = people[index]

        val details =
            createDetails {
                summary = tr("联系方式", "Contact information")
                opened = expanded.contains(index)
            }
        details.listen(DetailsEvents.openedChanged) {
            if (details.opened) expanded.add(index) else expanded.remove(index)
        }
        details.append(
            createVerticalLayout().apply {
                append(span(person.email), span(person.phone))
                withTheme("spacing")
            },
        )

        val text =
            createVerticalLayout().apply {
                append(strong(person.fullName), span(person.profession), details)
                withTheme("spacing")
            }
        val card =
            createHorizontalLayout().apply {
                append(
                    createAvatar {
                        name = person.fullName
                        img = person.photo
                    }.withTheme("xlarge"),
                    text,
                )
                withTheme("spacing", "margin")
            }
        container.replaceChildren(card)
    }
    list.style.setProperty("height", "20rem")
    basic.body.append(example(null, list))
    view.body.append(basic.root)

    val jump =
        section(
            tr("定位", "Scrolling to an index"),
            tr("scrollToIndex 直接跳到指定行。", "scrollToIndex jumps straight to the given row."),
        )
    val jumpList = createVirtualList()
    jumpList.items = jsArray<JsAny>((1..500).joinToString(",", "[", "]"))
    jumpList.renderer = { container, _, model ->
        val row = span(tr("行 ${model.index + 1}", "Row ${model.index + 1}"))
        container.replaceChildren(row)
    }
    jumpList.style.setProperty("height", "12rem")
    jump.body.append(
        example(
            null,
            column(
                jumpList,
                row(
                    createButton { textContent = tr("跳到第 1 行", "Go to row 1") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { jumpList.scrollToIndex(0.0) }
                    },
                    createButton { textContent = tr("跳到第 300 行", "Go to row 300") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { jumpList.scrollToIndex(299.0) }
                    },
                ),
            ),
        ),
    )
    view.body.append(jump.root)

    return view
}
