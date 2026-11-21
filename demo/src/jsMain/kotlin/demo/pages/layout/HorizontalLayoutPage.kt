package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.html.HTMLElement
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout

internal class HorizontalLayoutPage : DemoPage() {
    private val texts = Binder(horizontalLayoutTexts)

    private val frame = PageFrame(ENTRY)

    init {
        frame.mount(this)

        fun item(number: Int, height: String? = null): HTMLElement =
            Box.NAME { show("") }.also { node ->
                if (height != null) node.style.setProperty("height", height)
                texts.bind(node) { it.item(number) }
            }

        fun label(copy: (HorizontalLayoutTexts) -> String): HTMLElement =
            HtmlTagName.p().apply {
                style.setProperty("margin", "0 0 0.75rem")
                style.setProperty("font-size", "0.8125rem")
                style.setProperty("color", "var(--lumo-secondary-text-color)")
                texts.bind(this, copy) { textContent = it }
            }

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        basic.body.append(
            exampleBox(
                "theme=\"spacing padding\"",
                createHorizontalLayout().apply {
                    addTheme("spacing", "padding")
                    append(item(1), item(2), item(3))
                },
            ),
        )
        body.append(basic.root)

        val verticalAlignment = SectionBlock.NAME { show() }
        texts.bind(verticalAlignment.heading) { it.verticalHeading }
        texts.bind(verticalAlignment.hint) { it.verticalHint }
        verticalAlignment.body.append(
            exampleBox(
                "align-items: center",
                createHorizontalLayout().apply {
                    addTheme("spacing", "padding")
                    style.setProperty("height", "7rem")
                    style.setProperty("align-items", "center")
                    append(item(1, "2.5rem"), item(2), item(3, "4rem"))
                },
            ),
            exampleBox(
                "align-items: end",
                createHorizontalLayout().apply {
                    addTheme("spacing", "padding")
                    style.setProperty("height", "7rem")
                    style.setProperty("align-items", "end")
                    append(item(1, "2.5rem"), item(2), item(3, "4rem"))
                },
            ),
        )
        body.append(verticalAlignment.root)

        val individual = SectionBlock.NAME { show() }
        texts.bind(individual.heading) { it.individualHeading }
        texts.bind(individual.hint) { it.individualHint }
        individual.body.append(
            exampleBox(
                null,
                createHorizontalLayout().apply {
                    addTheme("spacing", "padding")
                    style.setProperty("height", "8rem")
                    style.setProperty("align-items", "stretch")
                    append(
                        item(1).apply { style.setProperty("align-self", "start") },
                        item(2),
                        item(3).apply { style.setProperty("align-self", "end") },
                    )
                },
            ),
        )
        body.append(individual.root)

        val horizontalAlignment = SectionBlock.NAME { show() }
        texts.bind(horizontalAlignment.heading) { it.horizontalHeading }
        texts.bind(horizontalAlignment.hint) { it.horizontalHint }
        val startBox = Box.NAME { show("") }.also { texts.bind(it, { it.start }) { copy -> it.textContent = copy } }
        val middleBox = Box.NAME { show("") }.also { node ->
            node.slot = "middle"
            texts.bind(node, { it.middle }) { copy -> node.textContent = copy }
        }
        val endBox = Box.NAME { show("") }.also { node ->
            node.slot = "end"
            texts.bind(node, { it.end }) { copy -> node.textContent = copy }
        }
        horizontalAlignment.body.append(
            exampleBox(
                "slot=\"middle\" / slot=\"end\"",
                createHorizontalLayout().apply {
                    addTheme("spacing", "padding")
                    append(startBox, middleBox, endBox)
                },
            ),
            exampleBox(
                "justify-content: center",
                createHorizontalLayout().apply {
                    addTheme("spacing", "padding")
                    style.setProperty("justify-content", "center")
                    append(item(1), item(2), item(3))
                },
            ),
        )
        body.append(horizontalAlignment.root)

        val spacing = SectionBlock.NAME { show() }
        texts.bind(spacing.heading) { it.spacingHeading }
        texts.bind(spacing.hint) { it.spacingHint }
        spacing.body.append(
            exampleBox(
                null,
                label { it.withoutSpacing },
                createHorizontalLayout().apply {
                    addTheme("padding")
                    append(item(1), item(2), item(3))
                },
            ),
            exampleBox(
                null,
                label { it.withSpacing },
                createHorizontalLayout().apply {
                    addTheme("spacing", "padding")
                    append(item(1), item(2), item(3))
                },
            ),
        )
        body.append(spacing.root)

        val padding = SectionBlock.NAME { show() }
        texts.bind(padding.heading) { it.paddingHeading }
        texts.bind(padding.hint) { it.paddingHint }
        padding.body.append(
            exampleBox(
                null,
                label { it.withoutPadding },
                createHorizontalLayout().apply {
                    addTheme("spacing")
                    append(item(1), item(2), item(3))
                },
            ),
            exampleBox(
                null,
                label { it.withPadding },
                createHorizontalLayout().apply {
                    addTheme("padding", "spacing")
                    append(item(1), item(2), item(3))
                },
            ),
        )
        body.append(padding.root)

        val margin = SectionBlock.NAME { show() }
        texts.bind(margin.heading) { it.marginHeading }
        texts.bind(margin.hint) { it.marginHint }
        margin.body.append(
            exampleBox(
                null,
                label { it.withoutMargin },
                HtmlTagName.div().apply {
                    style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
                    append(
                        createHorizontalLayout().apply {
                            addTheme("spacing", "padding")
                            append(item(1), item(2), item(3))
                        },
                    )
                },
            ),
            exampleBox(
                null,
                label { it.withMargin },
                HtmlTagName.div().apply {
                    style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
                    append(
                        createHorizontalLayout().apply {
                            addTheme("margin", "spacing", "padding")
                            append(item(1), item(2), item(3))
                        },
                    )
                },
            ),
        )
        body.append(margin.root)

        val wrapping = SectionBlock.NAME { show() }
        texts.bind(wrapping.heading) { it.wrappingHeading }
        texts.bind(wrapping.hint) { it.wrappingHint }
        wrapping.body.append(
            exampleBox(
                null,
                label { it.withoutWrap },
                createHorizontalLayout().apply {
                    addTheme("spacing", "margin", "padding")
                    style.setProperty("width", "22rem")
                    append(item(1), item(2), item(3), item(4), item(5))
                },
            ),
            exampleBox(
                null,
                label { it.withWrap },
                createHorizontalLayout().apply {
                    addTheme("wrap", "spacing", "margin", "padding")
                    style.setProperty("width", "22rem")
                    append(item(1), item(2), item(3), item(4), item(5))
                },
            ),
        )
        body.append(wrapping.root)

        val wrappingSlots = SectionBlock.NAME { show() }
        texts.bind(wrappingSlots.heading) { it.wrappingSlotsHeading }
        texts.bind(wrappingSlots.hint) { it.wrappingSlotsHint }
        fun group(slot: String?, copy: (HorizontalLayoutTexts) -> String, count: Int): HTMLElement =
            createHorizontalLayout().apply {
                if (slot != null) this.slot = slot
                addTheme("spacing", "padding")
                repeat(count) {
                    val node = Box.NAME { show("") }
                    texts.bind(node, copy) { text -> node.textContent = text }
                    append(node)
                }
            }
        wrappingSlots.body.append(
            exampleBox(
                null,
                createHorizontalLayout().apply {
                    addTheme("spacing", "padding", "wrap")
                    append(
                        group(null, { it.start }, 2),
                        group("middle", { it.middle }, 3),
                        group("end", { it.end }, 2),
                    )
                },
            ),
        )
        body.append(wrappingSlots.root)

        val expanding = SectionBlock.NAME { show() }
        texts.bind(expanding.heading) { it.expandingHeading }
        texts.bind(expanding.hint) { it.expandingHint }
        expanding.body.append(
            exampleBox(
                "flex-grow: 1",
                createHorizontalLayout().apply {
                    addTheme("padding", "spacing")
                    append(
                        item(1).apply { style.setProperty("flex-grow", "1") },
                        item(2),
                        item(3),
                    )
                },
            ),
        )
        body.append(expanding.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
    }

    companion object : HorizontalLayoutPageDefaultReference() {
        val ENTRY: CatalogEntry =
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
                        docsUrl = "${Catalog.DOCS_BASE}/horizontal-layout",
                        sourcePath = "pages/layout/HorizontalLayoutPage.kt",
                    ),
                kitPage = { HorizontalLayoutPage.NAME() },
            )

        private val english: HorizontalLayoutTexts =
            HorizontalLayoutTexts(
                basicHeading = "Basic usage",
                basicHint = "spacing and padding are the most common theme variants and can be combined.",
                verticalHeading = "Vertical alignment",
                verticalHint = "align-items positions the children vertically: stretch (default), start, center, " +
                    "end or baseline.",
                individualHeading = "Aligning individual items",
                individualHint = "align-self on a child overrides the container's align-items for that item only.",
                horizontalHeading = "Horizontal alignment",
                horizontalHint = "Children can be grouped into start / middle / end with slots, or distributed as " +
                    "a whole with justify-content. Do not use both at the same time.",
                spacingHeading = "Spacing",
                spacingHint = "spacing adds a uniform gap between the children.",
                paddingHeading = "Padding",
                paddingHint = "padding adds space inside the container.",
                marginHeading = "Margin",
                marginHint = "margin adds space around the container.",
                wrappingHeading = "Wrapping",
                wrappingHint = "By default children shrink or overflow when space runs out; wrap lets them flow " +
                    "onto a new line.",
                wrappingSlotsHeading = "Wrapping aligned items",
                wrappingSlotsHint = "When combined with horizontal alignment, group the children that should wrap " +
                    "together into sub-layouts and give those the slot.",
                expandingHeading = "Expanding items",
                expandingHint = "flex-grow makes a child take the excess space; several children share it " +
                    "proportionally.",
                item = { "Item $it" },
                start = "Start",
                middle = "Middle",
                end = "End",
                withoutSpacing = "Without spacing",
                withSpacing = "With spacing",
                withoutPadding = "Without padding",
                withPadding = "With padding",
                withoutMargin = "Without margin",
                withMargin = "With margin",
                withoutWrap = "Without wrap",
                withWrap = "With wrap",
            )
        private val chinese: HorizontalLayoutTexts =
            HorizontalLayoutTexts(
                basicHeading = "基本用法",
                basicHint = "spacing 和 padding 是最常用的两个主题，可以叠加使用。",
                verticalHeading = "纵向对齐",
                verticalHint = "用 align-items 控制子项在纵向上的位置：stretch（默认）、start、center、end、baseline。",
                individualHeading = "单项纵向对齐",
                individualHint = "子项的 align-self 覆盖容器的 align-items，只影响它自己。",
                horizontalHeading = "水平对齐",
                horizontalHint = "子项可以用 slot 分成 start / middle / end 三组，也可以用 justify-content 统一分布。" +
                    "两种方式不要同时使用。",
                spacingHeading = "间距",
                spacingHint = "spacing 在子项之间加入统一间隔。",
                paddingHeading = "内边距",
                paddingHint = "padding 在容器内部四周留出空间。",
                marginHeading = "外边距",
                marginHint = "margin 在容器外部留出空间，与相邻元素拉开距离。",
                wrappingHeading = "换行",
                wrappingHint = "默认情况下空间不足时子项会收缩或溢出；wrap 让它们换到下一行。",
                wrappingSlotsHeading = "分组后换行",
                wrappingSlotsHint = "与水平对齐组合时，把应该一起换行的子项放进子布局，再给子布局加 slot。",
                expandingHeading = "扩展子项",
                expandingHint = "给子项设置 flex-grow 让它占据多余空间；多个子项按比例分配。",
                item = { "项目 $it" },
                start = "开始",
                middle = "中间",
                end = "末尾",
                withoutSpacing = "没有 spacing",
                withSpacing = "有 spacing",
                withoutPadding = "没有 padding",
                withPadding = "有 padding",
                withoutMargin = "没有 margin",
                withMargin = "有 margin",
                withoutWrap = "没有 wrap",
                withWrap = "有 wrap",
            )
        private val horizontalLayoutTexts: Translations<HorizontalLayoutTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
