package demo.pages.overlays

import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.jsObject
import js.objects.Record
import kotlin.js.JsAny
import kotlin.js.JsString
import kotlin.js.js
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.ElementId
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.cssom.ClassName

val tooltipEntry: CatalogEntry =
    CatalogEntry(
        id = "tooltip",
        group = Groups.OVERLAYS,
        meta =
            PageMeta(
                title = bi("Tooltip", "Tooltip"),
                summary =
                    bi(
                        "悬停提示：可以放进组件的 tooltip 插槽，也可以用 for 关联任意元素。",
                        "Hover hint: goes into a component's tooltip slot, or is linked to any element " +
                            "with for.",
                    ),
                docsUrl = "$DOCS_BASE/tooltip",
                sourcePath = "pages/overlays/TooltipPage.kt",
            ),
        kitPage = { TooltipPage.NAME() },
    )

internal class TooltipPage : DemoPage() {
    private val texts = Binder(tooltipTexts)

    private val frame = PageFrame(tooltipEntry)

    init {
        frame.mount(this)

        val slotted = SectionBlock.NAME { show() }
        texts.bind(slotted.heading) { it.slottedHeading }
        texts.bind(slotted.hint) { it.slottedHint }
        val buttonWithTooltip = createButton()
        texts.bind(buttonWithTooltip) { it.hoverMe }
        val buttonTooltip = createTooltip()
        texts.bind(buttonTooltip, { it.buttonHint }) { buttonTooltip.text = it }
        buttonTooltip.slot = "tooltip"
        buttonWithTooltip.append(buttonTooltip)
        val fieldWithTooltip = createTextField()
        texts.bind(fieldWithTooltip, { it.fieldLabel }) { fieldWithTooltip.label = it }
        texts.bind(fieldWithTooltip, { it.fieldPlaceholder }) { fieldWithTooltip.placeholder = it }
        val fieldTooltip = createTooltip()
        texts.bind(fieldTooltip, { it.fieldHint }) { fieldTooltip.text = it }
        fieldTooltip.slot = "tooltip"
        fieldWithTooltip.append(fieldTooltip)
        val searchField = createTextField()
        texts.bind(searchField, { it.search }) { searchField.placeholder = it }
        val searchTooltip = createTooltip()
        texts.bind(searchTooltip, { it.exactPhrase }) { searchTooltip.text = it }
        searchTooltip.slot = "tooltip"
        searchField.append(
            createIcon { icon = "lumo:search" }.also { it.slot = "prefix" },
            searchTooltip,
        )
        slotted.body.append(exampleBox(null, kitRow(buttonWithTooltip, fieldWithTooltip, searchField)))
        body.append(slotted.root)

        val linked = SectionBlock.NAME { show() }
        texts.bind(linked.heading) { it.linkedHeading }
        texts.bind(linked.hint) { it.linkedHint }
        val target = HtmlTagName.div { className = EXAMPLE }
        texts.bind(target) { it.blockText }
        target.id = ElementId("tooltip-target")
        target.style.setProperty("padding", "var(--lumo-space-m)")
        val tooltip = createTooltip()
        texts.bind(tooltip, { it.linkedHintText }) { tooltip.text = it }
        tooltip.`for` = "tooltip-target"
        val headingTarget = heading(3)
        texts.bind(headingTarget) { it.headingText }
        headingTarget.id = ElementId("tooltip-heading")
        val headingTooltip = createTooltip()
        texts.bind(headingTooltip, { it.headingHintText }) { headingTooltip.text = it }
        headingTooltip.`for` = "tooltip-heading"
        headingTooltip.position = "top-start"
        linked.body.append(exampleBox(null, kitCol(target, headingTarget), tooltip, headingTooltip))
        body.append(linked.root)

        val manualSection = SectionBlock.NAME { show() }
        texts.bind(manualSection.heading) { it.manualHeading }
        texts.bind(manualSection.hint) { it.manualHint }
        val manualField = createTextField()
        texts.bind(manualField, { it.search }) { manualField.placeholder = it }
        val manualTooltip = createTooltip { manual = true }
        texts.bind(manualTooltip, { it.exactPhrase }) { manualTooltip.text = it }
        manualTooltip.slot = "tooltip"
        val infoButton = createButton { textContent = "" }.addTheme("tertiary").apply {
            style.setProperty("padding", "0")
            style.setProperty("border", "0")
            style.setProperty("min-width", "0")
            append(createIcon { icon = "vaadin:info-circle" })
            clickEvent.addHandler { manualTooltip.opened = !manualTooltip.opened }
        }
        infoButton.slot = "suffix"
        manualField.append(createIcon { icon = "lumo:search" }.also { it.slot = "prefix" }, manualTooltip, infoButton)
        manualSection.body.append(exampleBox(null, manualField))
        body.append(manualSection.root)

        val delays = SectionBlock.NAME { show() }
        texts.bind(delays.heading) { it.markdownHeading }
        texts.bind(delays.hint) { it.markdownHint }
        val mdTarget = createButton()
        texts.bind(mdTarget) { it.markdownButton }
        mdTarget.id = ElementId("tooltip-markdown")
        val mdTooltip =
            createTooltip {
                markdown = true
                hoverDelay = 0.0
            }
        texts.bind(mdTooltip, { it.markdownText }) { mdTooltip.text = it }
        mdTooltip.`for` = "tooltip-markdown"
        val slowTarget = createButton()
        texts.bind(slowTarget) { it.slowButton }
        slowTarget.id = ElementId("tooltip-slow")
        val slowTooltip =
            createTooltip {
                hoverDelay = 1000.0
                hideDelay = 500.0
            }
        texts.bind(slowTooltip, { it.slowText }) { slowTooltip.text = it }
        slowTooltip.`for` = "tooltip-slow"
        delays.body.append(exampleBox(null, kitRow(mdTarget, mdTooltip, slowTarget, slowTooltip)))
        body.append(delays.root)

        val generated = SectionBlock.NAME { show() }
        texts.bind(generated.heading) { it.generatedHeading }
        texts.bind(generated.hint) { it.generatedHint }
        val generatedTargets = mutableListOf<HTMLElement>()
        val chart = createButton { }.addTheme("tertiary")
        texts.bind(chart) { it.chart }
        val table = createButton { }.addTheme("tertiary")
        texts.bind(table) { it.table }
        val map = createButton { }.addTheme("tertiary")
        texts.bind(map) { it.map }
        listOf(chart, table, map).forEachIndexed { index, element ->
            element.id = ElementId("tooltip-generated-$index")
            generatedTargets += element
        }
        val generatedTooltip = createTooltip()
        generatedTooltip.`for` = "tooltip-generated-0"
        // The generator reads whatever `context` carries; the property is a plain record on the element.
        generatedTooltip.context =
            jsObject("""{"index": 0}""").unsafeCast<Record<JsString, JsAny>>()
        generatedTooltip.generator = { context ->
            val index: Int = js("(context) => context.index | 0")(context)
            texts.current.generatedHintFor(index)
        }
        generated.body.append(exampleBox(null, kitRow(*generatedTargets.toTypedArray(), generatedTooltip)))
        body.append(generated.root)

        val positions = SectionBlock.NAME { show() }
        texts.bind(positions.heading) { it.positionsHeading }
        texts.bind(positions.hint) { it.positionsHint }
        val positionedButtons = mutableListOf<HTMLElement>()
        listOf("top", "bottom", "start", "end").forEachIndexed { index, position ->
            val element = newIconButton(position, "lumo:info-circle").apply { addTheme("tertiary") }
            element.id = ElementId("tooltip-position-$index")
            val positioned = createTooltip { text = position }
            positioned.`for` = "tooltip-position-$index"
            positioned.position = position
            positionedButtons += element
            positionedButtons += positioned
        }
        positions.body.append(exampleBox(null, kitRow(*positionedButtons.toTypedArray())))
        body.append(positions.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = tooltipEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
    }

    companion object : CustomElementReferenceAdapter<TooltipPage>(TooltipPage::class.js, "vaadin-demo-tooltip-page") {
        private val english: TooltipTexts =
            TooltipTexts(
                slottedHeading = "Inside a slot",
                slottedHint = "Add the tooltip as a child with slot=\"tooltip\"; the component shows it on " +
                    "hover / focus.",
                hoverMe = "Hover me",
                buttonHint = "Hint on the button",
                fieldLabel = "Field hint",
                fieldPlaceholder = "Shown on focus",
                fieldHint = "Field-level hint text",
                search = "Search",
                exactPhrase = "Wrap in “quotes” for exact phrase",
                linkedHeading = "Linked with for",
                linkedHint = "Outside a component, point for at the id of the target element; position picks " +
                    "the side.",
                blockText = "This block has a hint",
                linkedHintText = "Hint linked to any element",
                headingText = "Heading with tooltip",
                headingHintText = "This is a tooltip",
                manualHeading = "Manual mode",
                manualHint = "manual turns off the automatic behaviour and leaves it to opened; the suffix " +
                    "icon button toggles it.",
                markdownHeading = "Delays and Markdown",
                markdownHint = "hoverDelay / focusDelay / hideDelay are in milliseconds; markdown parses the " +
                    "hint as Markdown.",
                markdownButton = "Markdown hint",
                markdownText = "**Bold** and *italic*, plus `code`",
                slowButton = "One second delay",
                slowText = "Shown after one second",
                generatedHeading = "Generated text",
                generatedHint = "generator receives context and returns the text; one tooltip can serve several " +
                    "elements.",
                chart = "Chart",
                table = "Table",
                map = "Map",
                generatedHintFor = { index -> "Hint for element ${index + 1}" },
                positionsHeading = "Position",
                positionsHint = "position picks the side the overlay opens on; four common values are shown here.",
            )
        private val chinese: TooltipTexts =
            TooltipTexts(
                slottedHeading = "放进插槽",
                slottedHint = "把 tooltip 作为子元素并设置 slot=\"tooltip\"，组件会在悬停 / 聚焦时显示它。",
                hoverMe = "悬停我",
                buttonHint = "这是按钮上的提示",
                fieldLabel = "字段的提示",
                fieldPlaceholder = "聚焦后显示",
                fieldHint = "字段级的提示文本",
                search = "搜索",
                exactPhrase = "用「引号」包裹可以精确匹配短语",
                linkedHeading = "用 for 关联",
                linkedHint = "不在组件内部时，用 for 指向目标元素的 id；position 决定浮层落在哪一侧。",
                blockText = "这个区块带有一个提示",
                linkedHintText = "关联到任意元素的提示",
                headingText = "带提示的标题",
                headingHintText = "这是标题上的提示",
                manualHeading = "手动打开",
                manualHint = "manual 关闭自动显示，由 opened 控制；这里用后缀图标按钮切换。",
                markdownHeading = "延迟与 Markdown",
                markdownHint = "hoverDelay / focusDelay / hideDelay 以毫秒为单位；markdown 让提示文本按 Markdown 解析。",
                markdownButton = "Markdown 提示",
                markdownText = "**粗体** 与 *斜体*，以及 `代码`",
                slowButton = "延迟 1 秒",
                slowText = "悬停一秒后出现",
                generatedHeading = "动态文本",
                generatedHint = "generator 接收 context，返回要显示的文本；同一个 tooltip 可以服务多个元素。",
                chart = "图表",
                table = "表格",
                map = "地图",
                generatedHintFor = { index -> "第 ${index + 1} 个元素的说明" },
                positionsHeading = "位置",
                positionsHint = "position 决定浮层相对触发元素的位置，这里演示四个常用值。",
            )
        private val EXAMPLE: ClassName = ClassName("example")
        private val tooltipTexts: Translations<TooltipTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
