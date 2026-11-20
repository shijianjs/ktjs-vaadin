package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.splitlayout.SplitLayoutOrientations
import io.github.shijianjs.ktjs.vaadin.splitlayout.createSplitLayout
import web.dom.clickEvent
import web.events.addHandler

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
        kitPage = { SplitLayoutPage.NAME() },
    )

internal class SplitLayoutPage : DemoPage() {
    private val texts = Binder(splitLayoutTexts)

    private val frame = PageFrame(splitLayoutEntry)

    /** The sidebar toggle: its label is an attribute, so it is written here rather than bound. */
    private val collapseButton = createButton()

    init {
        frame.mount(this)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val horizontal = createSplitLayout()
        horizontal.style.setProperty("max-height", "17rem")
        horizontal.append(SplitMaster.NAME(), SplitDetail.NAME())
        basic.body.append(exampleBox(null, horizontal))
        body.append(basic.root)

        val position = SectionBlock.NAME { show() }
        texts.bind(position.heading) { it.positionHeading }
        texts.bind(position.hint) { it.positionHint }
        val positioned = createSplitLayout()
        positioned.style.setProperty("max-height", "17rem")
        positioned.append(
            SplitMaster.NAME().apply { style.setProperty("width", "70%") },
            SplitDetail.NAME().apply { style.setProperty("width", "30%") },
        )
        position.body.append(exampleBox(null, positioned))
        body.append(position.root)

        val minMax = SectionBlock.NAME { show() }
        texts.bind(minMax.heading) { it.minMaxHeading }
        texts.bind(minMax.hint) { it.minMaxHint }
        val bounded = createSplitLayout()
        bounded.style.setProperty("max-height", "17rem")
        bounded.append(
            SplitMaster.NAME().apply {
                style.setProperty("min-width", "12rem")
                style.setProperty("max-width", "24rem")
            },
            SplitDetail.NAME(),
        )
        minMax.body.append(exampleBox(null, bounded))
        body.append(minMax.root)

        val orientation = SectionBlock.NAME { show() }
        texts.bind(orientation.heading) { it.orientationHeading }
        texts.bind(orientation.hint) { it.orientationHint }
        val vertical = createSplitLayout { this.orientation = SplitLayoutOrientations.vertical }
        vertical.style.setProperty("max-height", "22rem")
        vertical.append(SplitMaster.NAME(), SplitDetail.NAME())
        orientation.body.append(exampleBox(null, vertical))
        body.append(orientation.root)

        val themes = SectionBlock.NAME { show() }
        texts.bind(themes.heading) { it.themesHeading }
        texts.bind(themes.hint) { it.themesHint }
        val minimal = createSplitLayout().apply {
            addTheme("minimal")
            style.setProperty("max-height", "17rem")
            append(SplitMaster.NAME(), SplitDetail.NAME())
        }
        val small = createSplitLayout().apply {
            addTheme("small")
            style.setProperty("max-height", "17rem")
            append(SplitMaster.NAME(), SplitDetail.NAME())
        }
        themes.body.append(exampleBox("minimal", minimal), exampleBox("small", small))
        body.append(themes.root)

        val toggle = SectionBlock.NAME { show() }
        texts.bind(toggle.heading) { it.toggleHeading }
        texts.bind(toggle.hint) { it.toggleHint }
        val layout = createSplitLayout()
        layout.dataset["test"] = "split-collapse"
        layout.style.setProperty("max-height", "17rem")
        val sidebar = HtmlTagName.div().apply {
            dataset["test"] = "split-sidebar"
            style.setProperty("overflow", "hidden")
            style.setProperty("width", "40%")
        }
        val detail = SplitDetail.NAME()
        collapseButton.dataset["test"] = "split-collapse-button"
        collapseButton.addTheme("icon", "tertiary")
        collapseButton.style.setProperty("float", "right")
        var collapsed = false
        fun applyCollapsed() {
            val sidebarWidth = if (collapsed) 13 else 40
            sidebar.style.setProperty("width", "$sidebarWidth%")
            detail.style.setProperty("width", "${100 - sidebarWidth}%")
            collapseButton.replaceChildren(
                createIcon { icon = if (collapsed) "vaadin:arrow-right" else "vaadin:arrow-left" },
            )
        }
        collapseButton.clickEvent.addHandler {
            collapsed = !collapsed
            applyCollapsed()
        }
        applyCollapsed()
        sidebar.append(collapseButton, SplitMaster.NAME().apply { style.setProperty("clear", "right") })
        layout.append(sidebar, detail)
        toggle.body.append(exampleBox(null, layout))
        body.append(toggle.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = splitLayoutEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        // The button carries an icon, its copy is the accessible name: an attribute, not text content.
        collapseButton.setAttribute("aria-label", texts.current.toggleAria)
    }

    companion object : CustomElementReferenceAdapter<SplitLayoutPage>(SplitLayoutPage::class.js, "vaadin-demo-split-layout-page") {
        private val english: SplitLayoutTexts = SplitLayoutTexts(
            basicHeading = "Basic usage",
            basicHint = "A draggable divider sits between the two children; they share the space evenly unless a width " +
                "is set.",
            positionHeading = "Splitter position",
            positionHint = "Set a width or flex on the children to control where the splitter starts.",
            minMaxHeading = "Minimum and maximum size",
            minMaxHint = "min-width / max-width limit how far the splitter can be dragged.",
            orientationHeading = "Orientation",
            orientationHint = "With orientation set to vertical the divider is horizontal and the two areas stack.",
            themesHeading = "Theme variants",
            themesHint = "minimal hides the divider background and small narrows the divider.",
            toggleHeading = "Collapsing a sidebar",
            toggleHint = "Put a button into the sidebar and change the widths of the sidebar and the detail area when " +
                "it is clicked, simulating an expanding and collapsing sidebar.",
            toggleAria = "Expand/collapse sidebar",
        )
        private val chinese: SplitLayoutTexts = SplitLayoutTexts(
            basicHeading = "基本用法",
            basicHint = "两个子元素之间有一条可拖动的分隔条；不指定宽度时两者平分。",
            positionHeading = "分隔条位置",
            positionHint = "给两个子元素设置宽度或 flex，就能决定初始的分隔位置。",
            minMaxHeading = "最小与最大尺寸",
            minMaxHint = "min-width / max-width 限制拖动能到达的范围。",
            orientationHeading = "方向",
            orientationHint = "orientation 设为 vertical 后分隔条水平放置，两侧变成上下两个区域。",
            themesHeading = "主题",
            themesHint = "minimal 隐藏分隔条的底色，small 缩窄分隔条。",
            toggleHeading = "折叠侧栏",
            toggleHint = "把按钮放进侧栏，点击时只改侧栏与详情区的宽度，模拟侧栏的展开与折叠。",
            toggleAria = "展开或折叠侧栏",
        )
        private val splitLayoutTexts: Translations<SplitLayoutTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
