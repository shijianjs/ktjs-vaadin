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
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.splitlayout.SplitLayoutOrientations
import vaadin.splitlayout.createSplitLayout
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val splitLayoutEntry: CatalogEntry =
    CatalogEntry(
        id = "split-layout",
        group = Groups.LAYOUT,
        meta =
            PageMeta(
                title = bi("Split Layout", "Split Layout"),
                summary =
                    bi(
                        "可拖动的分栏容器，orientation 决定水平或垂直分割。",
                        "Resizable split container; orientation switches between a horizontal and a " +
                            "vertical split.",
                    ),
                docsUrl = "$DOCS_BASE/split-layout",
                sourcePath = "pages/layout/SplitLayoutPage.kt",
            ),
        builder = ::buildSplitLayoutPage,
    )

private fun buildSplitLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "两个子元素之间有一条可拖动的分隔条；不指定宽度时两者平分。",
                "A draggable divider sits between the two children; they share the space evenly unless a width " +
                    "is set.",
            ),
        )
    val horizontal = createSplitLayout()
    horizontal.style.setProperty("max-height", "17rem")
    horizontal.append(splitMaster(), splitDetail())
    basic.body.append(example(null, horizontal))
    view.body.append(basic.root)

    val position =
        section(
            tr("分隔条位置", "Splitter position"),
            tr(
                "给两个子元素设置宽度或 flex，就能决定初始的分隔位置。",
                "Set a width or flex on the children to control where the splitter starts.",
            ),
        )
    val positioned = createSplitLayout()
    positioned.style.setProperty("max-height", "17rem")
    positioned.append(
        splitMaster().apply { style.setProperty("width", "70%") },
        splitDetail().apply { style.setProperty("width", "30%") },
    )
    position.body.append(example(null, positioned))
    view.body.append(position.root)

    val minMax =
        section(
            tr("最小与最大尺寸", "Minimum and maximum size"),
            tr(
                "min-width / max-width 限制拖动能到达的范围。",
                "min-width / max-width limit how far the splitter can be dragged.",
            ),
        )
    val bounded = createSplitLayout()
    bounded.style.setProperty("max-height", "17rem")
    bounded.append(
        splitMaster().apply {
            style.setProperty("min-width", "12rem")
            style.setProperty("max-width", "24rem")
        },
        splitDetail(),
    )
    minMax.body.append(example(null, bounded))
    view.body.append(minMax.root)

    val orientation =
        section(
            tr("方向", "Orientation"),
            tr("orientation 设为 vertical 后分隔条水平放置，两侧变成上下两个区域。", "With orientation set to vertical the divider is horizontal and the two areas stack."),
        )
    val vertical = createSplitLayout { this.orientation = SplitLayoutOrientations.vertical }
    vertical.style.setProperty("max-height", "22rem")
    vertical.append(splitMaster(), splitDetail())
    orientation.body.append(example(null, vertical))
    view.body.append(orientation.root)

    val themes =
        section(
            tr("主题", "Theme variants"),
            tr(
                "minimal 隐藏分隔条的底色，small 缩窄分隔条。",
                "minimal hides the divider background and small narrows the divider.",
            ),
        )
    val minimal = createSplitLayout().apply {
        withTheme("minimal")
        style.setProperty("max-height", "17rem")
        append(splitMaster(), splitDetail())
    }
    val small = createSplitLayout().apply {
        withTheme("small")
        style.setProperty("max-height", "17rem")
        append(splitMaster(), splitDetail())
    }
    themes.body.append(example("minimal", minimal), example("small", small))
    view.body.append(themes.root)

    val toggle =
        section(
            tr("折叠侧栏", "Collapsing a sidebar"),
            tr(
                "把按钮放进侧栏，点击时只改侧栏与详情区的宽度，模拟侧栏的展开与折叠。",
                "Put a button into the sidebar and change the widths of the sidebar and the detail area when " +
                    "it is clicked, simulating an expanding and collapsing sidebar.",
            ),
        )
    val layout = createSplitLayout()
    layout.dataset["test"] = "split-collapse"
    layout.style.setProperty("max-height", "17rem")
    val sidebar = div().apply {
        dataset["test"] = "split-sidebar"
        style.setProperty("overflow", "hidden")
        style.setProperty("width", "40%")
    }
    val detail = splitDetail()
    val collapseButton = createButton().apply {
        dataset["test"] = "split-collapse-button"
        withTheme("icon", "tertiary")
        setAttribute("aria-label", tr("展开或折叠侧栏", "Expand/collapse sidebar"))
        style.setProperty("float", "right")
    }
    var collapsed = false
    fun applyCollapsed() {
        val sidebarWidth = if (collapsed) 13 else 40
        sidebar.style.setProperty("width", "$sidebarWidth%")
        detail.style.setProperty("width", "${100 - sidebarWidth}%")
        collapseButton.replaceChildren(createIcon { icon = if (collapsed) "vaadin:arrow-right" else "vaadin:arrow-left" })
    }
    collapseButton.apply {
        clickEvent.addHandler {
            collapsed = !collapsed
            applyCollapsed()
        }
    }
    applyCollapsed()
    sidebar.append(collapseButton, splitMaster().apply { style.setProperty("clear", "right") })
    layout.append(sidebar, detail)
    toggle.body.append(example(null, layout))
    view.body.append(toggle.root)

    return view
}

/** A fake master list for the split layout examples: sixteen rows of six cells. */
private fun splitMaster(): HTMLElement =
    div("split-master").apply {
        repeat(16) {
            val row = div("split-master__row")
            repeat(6) { row.append(div("split-master__cell")) }
            append(row)
        }
    }

/** A fake detail form for the split layout examples: ten labelled inputs. */
private fun splitDetail(): HTMLElement =
    div("split-detail").apply {
        repeat(10) {
            val field = div("split-detail__field")
            field.append(div("split-detail__label"), div("split-detail__input"))
            append(field)
        }
    }
