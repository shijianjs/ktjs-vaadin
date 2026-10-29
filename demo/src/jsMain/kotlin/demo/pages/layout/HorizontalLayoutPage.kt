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
import vaadin.horizontallayout.createHorizontalLayout

val horizontalLayoutEntry: CatalogEntry =
    CatalogEntry(
        id = "horizontal-layout",
        group = Groups.LAYOUT,
        meta =
            PageMeta(
                title = bi("Horizontal Layout", "Horizontal Layout"),
                summary =
                    bi(
                        "横向排列子元素的容器，通过 theme 控制间距、内边距与换行。",
                        "Container that arranges its children horizontally; theme controls spacing, padding " +
                            "and wrapping.",
                    ),
                docsUrl = "$DOCS_BASE/horizontal-layout",
                sourcePath = "pages/layout/HorizontalLayoutPage.kt",
            ),
        builder = ::buildHorizontalLayoutPage,
    )

private fun buildHorizontalLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "spacing 和 padding 是最常用的两个主题，可以叠加使用。",
                "spacing and padding are the most common theme variants and can be combined.",
            ),
        )
    basic.body.append(
        example(
            "theme=\"spacing padding\"",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
    )
    view.body.append(basic.root)

    val verticalAlignment =
        section(
            tr("纵向对齐", "Vertical alignment"),
            tr(
                "用 align-items 控制子项在纵向上的位置：stretch（默认）、start、center、end、baseline。",
                "align-items positions the children vertically: stretch (default), start, center, end or " +
                    "baseline.",
            ),
        )
    verticalAlignment.body.append(
        example(
            "align-items: center",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("height", "7rem")
                style.setProperty("align-items", "center")
                append(box(tr("项目 1", "Item 1"), "2.5rem"), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3"), "4rem"))
            },
        ),
        example(
            "align-items: end",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("height", "7rem")
                style.setProperty("align-items", "end")
                append(box(tr("项目 1", "Item 1"), "2.5rem"), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3"), "4rem"))
            },
        ),
    )
    view.body.append(verticalAlignment.root)

    val individual =
        section(
            tr("单项纵向对齐", "Aligning individual items"),
            tr(
                "子项的 align-self 覆盖容器的 align-items，只影响它自己。",
                "align-self on a child overrides the container's align-items for that item only.",
            ),
        )
    individual.body.append(
        example(
            null,
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("height", "8rem")
                style.setProperty("align-items", "stretch")
                append(
                    box(tr("项目 1", "Item 1")).apply { style.setProperty("align-self", "start") },
                    box(tr("项目 2", "Item 2")),
                    box(tr("项目 3", "Item 3")).apply { style.setProperty("align-self", "end") },
                )
            },
        ),
    )
    view.body.append(individual.root)

    val horizontalAlignment =
        section(
            tr("水平对齐", "Horizontal alignment"),
            tr(
                "子项可以用 slot 分成 start / middle / end 三组，也可以用 justify-content 统一分布。两种方式" +
                    "不要同时使用。",
                "Children can be grouped into start / middle / end with slots, or distributed as a whole with " +
                    "justify-content. Do not use both at the same time.",
            ),
        )
    horizontalAlignment.body.append(
        example(
            "slot=\"middle\" / slot=\"end\"",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                append(
                    box(tr("开始", "Start")),
                    box(tr("中间", "Middle")).apply { slot = "middle" },
                    box(tr("末尾", "End")).apply { slot = "end" },
                )
            },
        ),
        example(
            "justify-content: center",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("justify-content", "center")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
    )
    view.body.append(horizontalAlignment.root)

    val spacing =
        section(
            tr("间距", "Spacing"),
            tr("spacing 在子项之间加入统一间隔。", "spacing adds a uniform gap between the children."),
        )
    spacing.body.append(
        example(
            tr("没有 spacing", "Without spacing"),
            createHorizontalLayout().apply {
                withTheme("padding")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
        example(
            tr("有 spacing", "With spacing"),
            createHorizontalLayout().apply {
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
            createHorizontalLayout().apply {
                withTheme("spacing")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
        example(
            tr("有 padding", "With padding"),
            createHorizontalLayout().apply {
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
                    createHorizontalLayout().apply {
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
                    createHorizontalLayout().apply {
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
                "默认情况下空间不足时子项会收缩或溢出；wrap 让它们换到下一行。",
                "By default children shrink or overflow when space runs out; wrap lets them flow onto a new line.",
            ),
        )
    wrapping.body.append(
        example(
            tr("没有 wrap", "Without wrap"),
            createHorizontalLayout().apply {
                withTheme("spacing", "margin", "padding")
                style.setProperty("width", "22rem")
                append(*(1..5).map { box(tr("项目 $it", "Item $it")) }.toTypedArray())
            },
        ),
        example(
            tr("有 wrap", "With wrap"),
            createHorizontalLayout().apply {
                withTheme("wrap", "spacing", "margin", "padding")
                style.setProperty("width", "22rem")
                append(*(1..5).map { box(tr("项目 $it", "Item $it")) }.toTypedArray())
            },
        ),
    )
    view.body.append(wrapping.root)

    val wrappingSlots =
        section(
            tr("分组后换行", "Wrapping aligned items"),
            tr(
                "与水平对齐组合时，把应该一起换行的子项放进子布局，再给子布局加 slot。",
                "When combined with horizontal alignment, group the children that should wrap together into " +
                    "sub-layouts and give those the slot.",
            ),
        )
    wrappingSlots.body.append(
        example(
            null,
            createHorizontalLayout().apply {
                withTheme("spacing", "padding", "wrap")
                append(
                    createHorizontalLayout().apply {
                        withTheme("spacing", "padding")
                        append(box(tr("开始", "Start")), box(tr("开始", "Start")))
                    },
                    createHorizontalLayout().apply {
                        slot = "middle"
                        withTheme("spacing", "padding")
                        append(box(tr("中间", "Middle")), box(tr("中间", "Middle")), box(tr("中间", "Middle")))
                    },
                    createHorizontalLayout().apply {
                        slot = "end"
                        withTheme("spacing", "padding")
                        append(box(tr("末尾", "End")), box(tr("末尾", "End")))
                    },
                )
            },
        ),
    )
    view.body.append(wrappingSlots.root)

    val expanding =
        section(
            tr("扩展子项", "Expanding items"),
            tr(
                "给子项设置 flex-grow 让它占据多余空间；多个子项按比例分配。",
                "flex-grow makes a child take the excess space; several children share it proportionally.",
            ),
        )
    expanding.body.append(
        example(
            "flex-grow: 1",
            createHorizontalLayout().apply {
                withTheme("padding", "spacing")
                append(
                    box(tr("项目 1", "Item 1")).apply { style.setProperty("flex-grow", "1") },
                    box(tr("项目 2", "Item 2")),
                    box(tr("项目 3", "Item 3")),
                )
            },
        ),
    )
    view.body.append(expanding.root)

    return view
}
