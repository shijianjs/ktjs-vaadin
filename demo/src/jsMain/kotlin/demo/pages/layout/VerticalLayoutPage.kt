package demo.pages.layout

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.div
import demo.ui.example
import demo.ui.page
import demo.ui.section
import demo.ui.withTheme
import vaadin.verticallayout.createVerticalLayout

val verticalLayoutEntry: CatalogEntry =
    CatalogEntry(
        id = "vertical-layout",
        group = Groups.LAYOUT,
        meta =
            PageMeta(
                title = bi("Vertical Layout", "Vertical Layout"),
                summary = bi("纵向排列子元素的容器。", "Container that stacks its children vertically."),
                docsUrl = "$DOCS_BASE/vertical-layout",
                sourcePath = "pages/layout/VerticalLayoutPage.kt",
            ),
        builder = ::buildVerticalLayoutPage,
    )

private fun buildVerticalLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "与 Horizontal Layout 相同的主题，方向改为纵向；spacing 变成行间距。",
                "Same theme variants as Horizontal Layout, laid out vertically; spacing becomes the row gap.",
            ),
        )
    basic.body.append(
        example(
            "theme=\"spacing padding\"",
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                append(
                    box(tr("项目 1", "Item 1")),
                    box(tr("项目 2", "Item 2")),
                    box(tr("项目 3", "Item 3")),
                )
            },
        ),
    )
    view.body.append(basic.root)

    val verticalAlignment =
        section(
            tr("纵向对齐", "Vertical alignment"),
            tr(
                "布局比内容高时，justify-content 控制子项在纵向上的分布。",
                "When the layout is taller than its content, justify-content distributes the children vertically.",
            ),
        )
    verticalAlignment.body.append(
        example(
            "justify-content: center",
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("height", "12rem")
                style.setProperty("justify-content", "center")
                append(
                    box(tr("项目 1", "Item 1")),
                    box(tr("项目 2", "Item 2")),
                    box(tr("项目 3", "Item 3")),
                )
            },
        ),
    )
    view.body.append(verticalAlignment.root)

    val horizontalAlignment =
        section(
            tr("水平对齐", "Horizontal alignment"),
            tr(
                "align-items 控制子项在横向上的位置，例如整体居中。",
                "align-items positions the children horizontally, for example to center them.",
            ),
        )
    horizontalAlignment.body.append(
        example(
            "align-items: center",
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("align-items", "center")
                append(
                    box(tr("项目 1", "Item 1")),
                    box(tr("项目 2", "Item 2")),
                    box(tr("项目 3", "Item 3")),
                )
            },
        ),
    )
    view.body.append(horizontalAlignment.root)

    val individual =
        section(
            tr("单项对齐", "Aligning individual items"),
            tr(
                "子项的 align-self 可以覆盖容器的 align-items，只影响它自己。",
                "align-self on a child overrides the container's align-items for that item only.",
            ),
        )
    individual.body.append(
        example(
            null,
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("align-items", "start")
                append(
                    box(tr("项目 1", "Item 1")).apply { style.setProperty("align-self", "end") },
                    box(tr("项目 2", "Item 2")).apply { style.setProperty("align-self", "center") },
                    box(tr("项目 3", "Item 3")),
                )
            },
        ),
    )
    view.body.append(individual.root)

    val spacing =
        section(
            tr("间距", "Spacing"),
            tr("spacing 在子项之间加入统一间隔。", "spacing adds a uniform gap between the children."),
        )
    spacing.body.append(
        example(
            tr("没有 spacing", "Without spacing"),
            createVerticalLayout().apply {
                withTheme("padding")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
        example(
            tr("有 spacing", "With spacing"),
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
    )
    view.body.append(spacing.root)

    val padding =
        section(
            tr("内边距", "Padding"),
            tr("padding 在容器内部四周留出空间。", "padding adds space inside the container."),
        )
    padding.body.append(
        example(
            tr("没有 padding", "Without padding"),
            createVerticalLayout().apply {
                withTheme("spacing")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
        example(
            tr("有 padding", "With padding"),
            createVerticalLayout().apply {
                withTheme("padding", "spacing")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
    )
    view.body.append(padding.root)

    val margin =
        section(
            tr("外边距", "Margin"),
            tr("margin 在容器外部留出空间，与相邻元素拉开距离。", "margin adds space around the container."),
        )
    margin.body.append(
        example(
            tr("没有 margin", "Without margin"),
            div().apply {
                style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
                append(
                    createVerticalLayout().apply {
                        withTheme("spacing", "padding")
                        append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
                    },
                )
            },
        ),
        example(
            tr("有 margin", "With margin"),
            div().apply {
                style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
                append(
                    createVerticalLayout().apply {
                        withTheme("margin", "spacing", "padding")
                        append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
                    },
                )
            },
        ),
    )
    view.body.append(margin.root)

    val wrapping =
        section(
            tr("换行", "Wrapping"),
            tr(
                "默认空间不足时子项会收缩或溢出；wrap 让它们换到下一列。",
                "By default children shrink or overflow when space runs out; wrap lets them flow into a new column.",
            ),
        )
    wrapping.body.append(
        example(
            tr("没有 wrap", "Without wrap"),
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("height", "10rem")
                style.setProperty("align-items", "stretch")
                append(*(1..4).map { box(tr("项目 $it", "Item $it")) }.toTypedArray())
            },
        ),
        example(
            tr("有 wrap", "With wrap"),
            createVerticalLayout().apply {
                withTheme("wrap", "spacing", "padding")
                style.setProperty("height", "10rem")
                style.setProperty("align-items", "stretch")
                append(*(1..4).map { box(tr("项目 $it", "Item $it")) }.toTypedArray())
            },
        ),
    )
    view.body.append(wrapping.root)

    return view
}
