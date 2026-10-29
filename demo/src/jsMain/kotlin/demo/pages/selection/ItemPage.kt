package demo.pages.selection

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.column
import demo.ui.example
import demo.ui.page
import demo.ui.row
import demo.ui.section
import vaadin.button.createButton
import vaadin.item.createItem
import web.dom.clickEvent
import web.events.addHandler

val itemEntry: CatalogEntry =
    CatalogEntry(
        id = "item",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Item", "Item"),
                summary =
                    bi(
                        "List Box / Select / Context Menu 使用的条目元素，也可以单独使用。",
                        "Item element used by List Box / Select / Context Menu; it also works on its own.",
                    ),
                docsUrl = "$DOCS_BASE/list-box",
                docsNote =
                    bi(
                        "vaadin-item 没有独立文档页面，用法见 List Box。",
                        "vaadin-item has no dedicated documentation page; see List Box.",
                    ),
                sourcePath = "pages/selection/ItemPage.kt",
            ),
        builder = ::buildItemPage,
    )

private fun buildItemPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("独立使用", "Standalone use"),
            tr(
                "vaadin-item 通常由 List Box / Select / Context Menu 内部创建；这里单独展示它的属性。",
                "vaadin-item is usually created inside List Box / Select / Context Menu; this page shows its " +
                    "properties on its own.",
            ),
        )
    val log = EventLog(4)
    val items =
        row(
            createItem {
                label = tr("带 label 的条目", "Item with a label")
                value = "one"
            }.apply { dataset["test"] = "item-one" },
            createItem { textContent = tr("默认插槽文本", "Default slot text") },
            createItem {
                textContent = tr("已禁用", "Disabled")
                disabled = true
            },
        )
    basic.body.append(example(null, items))
    view.body.append(basic.root)

    val selection =
        section(
            tr("选中状态", "Selected state"),
            tr(
                "selected 只是视觉状态，条目本身不维护选中逻辑。",
                "selected is purely visual; the item does not maintain selection logic itself.",
            ),
        )
    val selectedItem =
        createItem {
            textContent = tr("被选中的条目", "Selected item")
            selected = true
        }
    val toggleButton =
        createButton { textContent = tr("切换 selected", "Toggle selected") }.apply {
            clickEvent.addHandler {
                selectedItem.selected = !selectedItem.selected
                log.add("selected = ${selectedItem.selected}")
            }
        }
    selection.body.append(example(null, column(selectedItem, toggleButton, log.root)))
    view.body.append(selection.root)

    return view
}
