package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.sectionBlock

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.configureFrame
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.splitlayout.SplitLayoutOrientations
import io.github.shijianjs.ktjs.vaadin.splitlayout.createSplitLayout
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
        kitPage = { newSplitLayoutPage() },
        rebuildsOnLanguage = true,
    )

internal class SplitLayoutPage : DemoPage() {

    init {
        configureFrame(splitLayoutEntry)

        val basic =
            sectionBlock(
                tr("基本用法", "Basic usage"),
                tr(
                    "两个子元素之间有一条可拖动的分隔条；不指定宽度时两者平分。",
                    "A draggable divider sits between the two children; they share the space evenly unless a width " +
                        "is set.",
                ),
            )
        val horizontal = createSplitLayout()
        horizontal.style.setProperty("max-height", "17rem")
        horizontal.append(newSplitMaster(), newSplitDetail())
        basic.body.append(exampleBox(null, horizontal))
        body.append(basic.root)

        val position =
            sectionBlock(
                tr("分隔条位置", "Splitter position"),
                tr(
                    "给两个子元素设置宽度或 flex，就能决定初始的分隔位置。",
                    "Set a width or flex on the children to control where the splitter starts.",
                ),
            )
        val positioned = createSplitLayout()
        positioned.style.setProperty("max-height", "17rem")
        positioned.append(
            newSplitMaster().apply { style.setProperty("width", "70%") },
            newSplitDetail().apply { style.setProperty("width", "30%") },
        )
        position.body.append(exampleBox(null, positioned))
        body.append(position.root)

        val minMax =
            sectionBlock(
                tr("最小与最大尺寸", "Minimum and maximum size"),
                tr(
                    "min-width / max-width 限制拖动能到达的范围。",
                    "min-width / max-width limit how far the splitter can be dragged.",
                ),
            )
        val bounded = createSplitLayout()
        bounded.style.setProperty("max-height", "17rem")
        bounded.append(
            newSplitMaster().apply {
                style.setProperty("min-width", "12rem")
                style.setProperty("max-width", "24rem")
            },
            newSplitDetail(),
        )
        minMax.body.append(exampleBox(null, bounded))
        body.append(minMax.root)

        val orientation =
            sectionBlock(
                tr("方向", "Orientation"),
                tr("orientation 设为 vertical 后分隔条水平放置，两侧变成上下两个区域。", "With orientation set to vertical the divider is horizontal and the two areas stack."),
            )
        val vertical = createSplitLayout { this.orientation = SplitLayoutOrientations.vertical }
        vertical.style.setProperty("max-height", "22rem")
        vertical.append(newSplitMaster(), newSplitDetail())
        orientation.body.append(exampleBox(null, vertical))
        body.append(orientation.root)

        val themes =
            sectionBlock(
                tr("主题", "Theme variants"),
                tr(
                    "minimal 隐藏分隔条的底色，small 缩窄分隔条。",
                    "minimal hides the divider background and small narrows the divider.",
                ),
            )
        val minimal = createSplitLayout().apply {
            addTheme("minimal")
            style.setProperty("max-height", "17rem")
            append(newSplitMaster(), newSplitDetail())
        }
        val small = createSplitLayout().apply {
            addTheme("small")
            style.setProperty("max-height", "17rem")
            append(newSplitMaster(), newSplitDetail())
        }
        themes.body.append(exampleBox("minimal", minimal), exampleBox("small", small))
        body.append(themes.root)

        val toggle =
            sectionBlock(
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
        val sidebar = HtmlTagName.div().apply {
            dataset["test"] = "split-sidebar"
            style.setProperty("overflow", "hidden")
            style.setProperty("width", "40%")
        }
        val detail = newSplitDetail()
        val collapseButton = createButton().apply {
            dataset["test"] = "split-collapse-button"
            addTheme("icon", "tertiary")
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
        sidebar.append(collapseButton, newSplitMaster().apply { style.setProperty("clear", "right") })
        layout.append(sidebar, detail)
        toggle.body.append(exampleBox(null, layout))
        body.append(toggle.root)

    }

    companion object : CustomElementReference<SplitLayoutPage>(SplitLayoutPage::class.js) {
        val NAME: TagName<SplitLayoutPage> = TagName("vaadin-demo-split-layout-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-split-layout-page>`. */
internal fun newSplitLayoutPage(): SplitLayoutPage {
    SplitLayoutPage.define()
    return SplitLayoutPage.NAME()
}

/**
 * `<vaadin-demo-split-master>`: the fake master list of the split layout examples — sixteen rows of
 * six grey cells, which is the shape the docs example uses.
 */
internal class SplitMaster : CustomElementAdapter() {
    init {
        attachShadowRoot().append(cells())
    }

    override fun connectedCallback() {
        style.setProperty("display", "block")
        style.setProperty("padding", "var(--lumo-space-s)")
    }

    /** Six grey cells per row, sixteen rows. */
    private fun cells(): HTMLElement =
        HtmlTagName.div {
            repeat(16) {
                val row =
                    HtmlTagName.div {
                        style.setProperty("display", "grid")
                        style.setProperty("grid-template-columns", "repeat(6, 1fr)")
                        style.setProperty("gap", "4px")
                        style.setProperty("margin-bottom", "4px")
                    }
                repeat(6) {
                    row.append(
                        HtmlTagName.div {
                            style.setProperty("height", "12px")
                            style.setProperty("border-radius", "2px")
                            style.setProperty("background", "var(--lumo-contrast-10pct)")
                        },
                    )
                }
                append(row)
            }
        }

    companion object : CustomElementReference<SplitMaster>(SplitMaster::class.js) {
        val NAME: TagName<SplitMaster> = TagName("vaadin-demo-split-master")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-split-master>`. */
internal fun newSplitMaster(): SplitMaster {
    SplitMaster.define()
    return SplitMaster.NAME()
}

/**
 * `<vaadin-demo-split-detail>`: the fake detail form of the split layout examples — ten labelled
 * inputs, grey blocks only, enough shape to read as a form.
 */
internal class SplitDetail : CustomElementAdapter() {
    init {
        attachShadowRoot().append(fields())
    }

    override fun connectedCallback() {
        style.setProperty("display", "block")
        style.setProperty("padding", "var(--lumo-space-s)")
    }

    /** Label above input, both grey blocks: enough shape to read as a form. */
    private fun fields(): HTMLElement =
        HtmlTagName.div {
            repeat(10) {
                val field =
                    HtmlTagName.div {
                        style.setProperty("margin-bottom", "var(--lumo-space-s)")
                    }
                val label =
                    HtmlTagName.div {
                        style.setProperty("width", "40%")
                        style.setProperty("height", "8px")
                        style.setProperty("margin-bottom", "4px")
                        style.setProperty("border-radius", "2px")
                        style.setProperty("background", "var(--lumo-contrast-20pct)")
                    }
                val input =
                    HtmlTagName.div {
                        style.setProperty("height", "24px")
                        style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
                        style.setProperty("border-radius", "var(--lumo-border-radius-s)")
                    }
                field.append(label, input)
                append(field)
            }
        }

    companion object : CustomElementReference<SplitDetail>(SplitDetail::class.js) {
        val NAME: TagName<SplitDetail> = TagName("vaadin-demo-split-detail")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-split-detail>`. */
internal fun newSplitDetail(): SplitDetail {
    SplitDetail.define()
    return SplitDetail.NAME()
}

