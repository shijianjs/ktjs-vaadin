package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.sectionBlock


import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.html.HTMLElement
import web.html.HTMLParagraphElement
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import io.github.shijianjs.ktjs.vaadin.verticallayout.createVerticalLayout

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
        kitPage = { newVerticalLayoutPage() },
    )

/** Every string this page shows, in one language. */
private class VerticalLayoutTexts(
    val basicHeading: String,
    val basicHint: String,
    val verticalHeading: String,
    val verticalHint: String,
    val horizontalHeading: String,
    val horizontalHint: String,
    val individualHeading: String,
    val individualHint: String,
    val spacingHeading: String,
    val spacingHint: String,
    val paddingHeading: String,
    val paddingHint: String,
    val marginHeading: String,
    val marginHint: String,
    val wrappingHeading: String,
    val wrappingHint: String,
    val item: (Int) -> String,
    val withoutSpacing: String,
    val withSpacing: String,
    val withoutPadding: String,
    val withPadding: String,
    val withoutMargin: String,
    val withMargin: String,
    val withoutWrap: String,
    val withWrap: String,
)

private val english: VerticalLayoutTexts =
    VerticalLayoutTexts(
        basicHeading = "Basic usage",
        basicHint = "Same theme variants as Horizontal Layout, laid out vertically; spacing becomes the " +
            "row gap.",
        verticalHeading = "Vertical alignment",
        verticalHint = "When the layout is taller than its content, justify-content distributes the " +
            "children vertically.",
        horizontalHeading = "Horizontal alignment",
        horizontalHint = "align-items positions the children horizontally, for example to center them.",
        individualHeading = "Aligning individual items",
        individualHint = "align-self on a child overrides the container's align-items for that item only.",
        spacingHeading = "Spacing",
        spacingHint = "spacing adds a uniform gap between the children.",
        paddingHeading = "Padding",
        paddingHint = "padding adds space inside the container.",
        marginHeading = "Margin",
        marginHint = "margin adds space around the container.",
        wrappingHeading = "Wrapping",
        wrappingHint = "By default children shrink or overflow when space runs out; wrap lets them flow " +
            "into a new column.",
        item = { "Item $it" },
        withoutSpacing = "Without spacing",
        withSpacing = "With spacing",
        withoutPadding = "Without padding",
        withPadding = "With padding",
        withoutMargin = "Without margin",
        withMargin = "With margin",
        withoutWrap = "Without wrap",
        withWrap = "With wrap",
    )

private val chinese: VerticalLayoutTexts =
    VerticalLayoutTexts(
        basicHeading = "基本用法",
        basicHint = "与 Horizontal Layout 相同的主题，方向改为纵向；spacing 变成行间距。",
        verticalHeading = "纵向对齐",
        verticalHint = "布局比内容高时，justify-content 控制子项在纵向上的分布。",
        horizontalHeading = "水平对齐",
        horizontalHint = "align-items 控制子项在横向上的位置，例如整体居中。",
        individualHeading = "单项对齐",
        individualHint = "子项的 align-self 可以覆盖容器的 align-items，只影响它自己。",
        spacingHeading = "间距",
        spacingHint = "spacing 在子项之间加入统一间隔。",
        paddingHeading = "内边距",
        paddingHint = "padding 在容器内部四周留出空间。",
        marginHeading = "外边距",
        marginHint = "margin 在容器外部留出空间，与相邻元素拉开距离。",
        wrappingHeading = "换行",
        wrappingHint = "默认空间不足时子项会收缩或溢出；wrap 让它们换到下一列。",
        item = { "项目 $it" },
        withoutSpacing = "没有 spacing",
        withSpacing = "有 spacing",
        withoutPadding = "没有 padding",
        withPadding = "有 padding",
        withoutMargin = "没有 margin",
        withMargin = "有 margin",
        withoutWrap = "没有 wrap",
        withWrap = "有 wrap",
    )

private val verticalLayoutTexts: Translations<VerticalLayoutTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class VerticalLayoutPage : DemoPage() {
    private val texts = Binder(verticalLayoutTexts)

    private val frame = PageFrame(verticalLayoutEntry)

    /** Every box built on this page, so a switch can rewrite their "Item n" labels in place. */
    private var boxes: MutableList<HTMLElement> = mutableListOf()

    /** The captions that are copy rather than a CSS snippet, keyed by their own text node. */
    private val captionKeys: MutableList<Pair<HTMLParagraphElement, (VerticalLayoutTexts) -> String>> = mutableListOf()

    init {
        frame.mount(this)

        fun box(): HTMLElement = newBox("").also { boxes += it }

        fun caption(copy: (VerticalLayoutTexts) -> String): HTMLParagraphElement {
            val node = HtmlTagName.p().apply {
                style.setProperty("margin", "0 0 0.75rem")
                style.setProperty("font-size", "0.8125rem")
                style.setProperty("color", "var(--lumo-secondary-text-color)")
            }
            texts.bind(node, copy) { node.textContent = it }
            captionKeys += node to copy
            return node
        }

        val basic = sectionBlock()
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        basic.body.append(
            exampleBox(
                "theme=\"spacing padding\"",
                createVerticalLayout().apply {
                    addTheme("spacing", "padding")
                    append(box(), box(), box())
                },
            ),
        )
        body.append(basic.root)

        val verticalAlignment = sectionBlock()
        texts.bind(verticalAlignment.heading) { it.verticalHeading }
        texts.bind(verticalAlignment.hint) { it.verticalHint }
        verticalAlignment.body.append(
            exampleBox(
                "justify-content: center",
                createVerticalLayout().apply {
                    addTheme("spacing", "padding")
                    style.setProperty("height", "12rem")
                    style.setProperty("justify-content", "center")
                    append(box(), box(), box())
                },
            ),
        )
        body.append(verticalAlignment.root)

        val horizontalAlignment = sectionBlock()
        texts.bind(horizontalAlignment.heading) { it.horizontalHeading }
        texts.bind(horizontalAlignment.hint) { it.horizontalHint }
        horizontalAlignment.body.append(
            exampleBox(
                "align-items: center",
                createVerticalLayout().apply {
                    addTheme("spacing", "padding")
                    style.setProperty("align-items", "center")
                    append(box(), box(), box())
                },
            ),
        )
        body.append(horizontalAlignment.root)

        val individual = sectionBlock()
        texts.bind(individual.heading) { it.individualHeading }
        texts.bind(individual.hint) { it.individualHint }
        individual.body.append(
            exampleBox(
                null,
                createVerticalLayout().apply {
                    addTheme("spacing", "padding")
                    style.setProperty("align-items", "start")
                    append(
                        box().apply { style.setProperty("align-self", "end") },
                        box().apply { style.setProperty("align-self", "center") },
                        box(),
                    )
                },
            ),
        )
        body.append(individual.root)

        val spacing = sectionBlock()
        texts.bind(spacing.heading) { it.spacingHeading }
        texts.bind(spacing.hint) { it.spacingHint }
        spacing.body.append(
            exampleBox(
                null,
                caption { it.withoutSpacing },
                createVerticalLayout().apply {
                    addTheme("padding")
                    append(box(), box(), box())
                },
            ),
            exampleBox(
                null,
                caption { it.withSpacing },
                createVerticalLayout().apply {
                    addTheme("spacing", "padding")
                    append(box(), box(), box())
                },
            ),
        )
        body.append(spacing.root)

        val padding = sectionBlock()
        texts.bind(padding.heading) { it.paddingHeading }
        texts.bind(padding.hint) { it.paddingHint }
        padding.body.append(
            exampleBox(
                null,
                caption { it.withoutPadding },
                createVerticalLayout().apply {
                    addTheme("spacing")
                    append(box(), box(), box())
                },
            ),
            exampleBox(
                null,
                caption { it.withPadding },
                createVerticalLayout().apply {
                    addTheme("padding", "spacing")
                    append(box(), box(), box())
                },
            ),
        )
        body.append(padding.root)

        val margin = sectionBlock()
        texts.bind(margin.heading) { it.marginHeading }
        texts.bind(margin.hint) { it.marginHint }
        margin.body.append(
            exampleBox(
                null,
                caption { it.withoutMargin },
                HtmlTagName.div().apply {
                    style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
                    append(
                        createVerticalLayout().apply {
                            addTheme("spacing", "padding")
                            append(box(), box(), box())
                        },
                    )
                },
            ),
            exampleBox(
                null,
                caption { it.withMargin },
                HtmlTagName.div().apply {
                    style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
                    append(
                        createVerticalLayout().apply {
                            addTheme("margin", "spacing", "padding")
                            append(box(), box(), box())
                        },
                    )
                },
            ),
        )
        body.append(margin.root)

        val wrapping = sectionBlock()
        texts.bind(wrapping.heading) { it.wrappingHeading }
        texts.bind(wrapping.hint) { it.wrappingHint }
        wrapping.body.append(
            exampleBox(
                null,
                caption { it.withoutWrap },
                createVerticalLayout().apply {
                    addTheme("spacing", "padding")
                    style.setProperty("height", "10rem")
                    style.setProperty("align-items", "stretch")
                    append(box(), box(), box(), box())
                },
            ),
            exampleBox(
                null,
                caption { it.withWrap },
                createVerticalLayout().apply {
                    addTheme("wrap", "spacing", "padding")
                    style.setProperty("height", "10rem")
                    style.setProperty("align-items", "stretch")
                    append(box(), box(), box(), box())
                },
            ),
        )
        body.append(wrapping.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = verticalLayoutEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        boxes.forEachIndexed { index, node -> node.textContent = texts.current.item(index + 1) }
        captionKeys.forEach { (node, copy) -> node.textContent = copy(texts.current) }
    }

    companion object : CustomElementReference<VerticalLayoutPage>(VerticalLayoutPage::class.js) {
        val NAME: TagName<VerticalLayoutPage> = TagName("vaadin-demo-vertical-layout-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-vertical-layout-page>`. */
internal fun newVerticalLayoutPage(): VerticalLayoutPage {
    VerticalLayoutPage.define()
    return VerticalLayoutPage.NAME()
}
