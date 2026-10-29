package demo.pages.overlays

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.heading
import demo.ui.jsObject
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import js.objects.Record
import kotlin.js.JsAny
import kotlin.js.JsString
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.textfield.createTextField
import vaadin.tooltip.createTooltip
import web.dom.ElementId
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

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
        builder = ::buildTooltipPage,
    )

// endregion

// region Tooltip

private fun buildTooltipPage(meta: PageMeta): PageView {
    val view = page(meta)

    val slotted =
        section(
            tr("放进插槽", "Inside a slot"),
            tr(
                "把 tooltip 作为子元素并设置 slot=\"tooltip\"，组件会在悬停 / 聚焦时显示它。",
                "Add the tooltip as a child with slot=\"tooltip\"; the component shows it on hover / focus.",
            ),
        )
    val buttonWithTooltip = createButton { textContent = tr("悬停我", "Hover me") }
    buttonWithTooltip.append(
        createTooltip { text = tr("这是按钮上的提示", "Hint on the button") }.apply { slot = "tooltip" },
    )
    val fieldWithTooltip =
        vaadin.textfield.createTextField {
            label = tr("字段的提示", "Field hint")
            placeholder = tr("聚焦后显示", "Shown on focus")
        }
    fieldWithTooltip.append(
        createTooltip { text = tr("字段级的提示文本", "Field-level hint text") }.apply { slot = "tooltip" },
    )
    val searchField =
        vaadin.textfield.createTextField {
            placeholder = tr("搜索", "Search")
        }
    searchField.append(
        createIcon { icon = "lumo:search" }.also { it.slot = "prefix" },
        createTooltip { text = tr("用「引号」包裹可以精确匹配短语", "Wrap in “quotes” for exact phrase") }
            .also { it.slot = "tooltip" },
    )
    slotted.body.append(example(null, row(buttonWithTooltip, fieldWithTooltip, searchField)))
    view.body.append(slotted.root)

    val linked =
        section(
            tr("用 for 关联", "Linked with for"),
            tr(
                "不在组件内部时，用 for 指向目标元素的 id；position 决定浮层落在哪一侧。",
                "Outside a component, point for at the id of the target element; position picks the side.",
            ),
        )
    val target = div("example").apply { textContent = tr("这个区块带有一个提示", "This block has a hint") }
    target.id = ElementId("tooltip-target")
    target.style.setProperty("padding", "var(--lumo-space-m)")
    val tooltip = createTooltip { text = tr("关联到任意元素的提示", "Hint linked to any element") }
    tooltip.`for` = "tooltip-target"
    val headingTarget = heading(3, tr("带提示的标题", "Heading with tooltip"))
    headingTarget.id = ElementId("tooltip-heading")
    val headingTooltip = createTooltip { text = tr("这是标题上的提示", "This is a tooltip") }
    headingTooltip.`for` = "tooltip-heading"
    headingTooltip.position = "top-start"
    linked.body.append(example(null, column(target, headingTarget), tooltip, headingTooltip))
    view.body.append(linked.root)

    val manualSection =
        section(
            tr("手动打开", "Manual mode"),
            tr(
                "manual 关闭自动显示，由 opened 控制；这里用后缀图标按钮切换。",
                "manual turns off the automatic behaviour and leaves it to opened; the suffix icon button toggles it.",
            ),
        )
    val manualField = vaadin.textfield.createTextField {
        placeholder = tr("搜索", "Search")
    }
    val manualTooltip = createTooltip {
        text = tr("用「引号」包裹可以精确匹配短语", "Wrap in “quotes” for exact phrase")
        manual = true
    }
    manualTooltip.slot = "tooltip"
    val infoButton = createButton { textContent = "" }.withTheme("tertiary").apply {
        style.setProperty("padding", "0")
        style.setProperty("border", "0")
        style.setProperty("min-width", "0")
        append(createIcon { icon = "vaadin:info-circle" })
        clickEvent.addHandler { manualTooltip.opened = !manualTooltip.opened }
    }
    infoButton.slot = "suffix"
    manualField.append(createIcon { icon = "lumo:search" }.also { it.slot = "prefix" }, manualTooltip, infoButton)
    manualSection.body.append(example(null, manualField))
    view.body.append(manualSection.root)

    val delays =
        section(
            tr("延迟与 Markdown", "Delays and Markdown"),
            tr(
                "hoverDelay / focusDelay / hideDelay 以毫秒为单位；markdown 让提示文本按 Markdown 解析。",
                "hoverDelay / focusDelay / hideDelay are in milliseconds; markdown parses the hint as Markdown.",
            ),
        )
    val mdTarget = createButton { textContent = tr("Markdown 提示", "Markdown hint") }
    mdTarget.id = ElementId("tooltip-markdown")
    val mdTooltip =
        createTooltip {
            text = tr("**粗体** 与 *斜体*，以及 `代码`", "**Bold** and *italic*, plus `code`")
            markdown = true
            hoverDelay = 0.0
        }
    mdTooltip.`for` = "tooltip-markdown"
    val slowTarget = createButton { textContent = tr("延迟 1 秒", "One second delay") }
    slowTarget.id = ElementId("tooltip-slow")
    val slowTooltip =
        createTooltip {
            text = tr("悬停一秒后出现", "Shown after one second")
            hoverDelay = 1000.0
            hideDelay = 500.0
        }
    slowTooltip.`for` = "tooltip-slow"
    delays.body.append(example(null, row(mdTarget, mdTooltip, slowTarget, slowTooltip)))
    view.body.append(delays.root)

    val generated =
        section(
            tr("动态文本", "Generated text"),
            tr(
                "generator 接收 context，返回要显示的文本；同一个 tooltip 可以服务多个元素。",
                "generator receives context and returns the text; one tooltip can serve several elements.",
            ),
        )
    val generatedTargets = mutableListOf<HTMLElement>()
    listOf(tr("图表", "Chart"), tr("表格", "Table"), tr("地图", "Map")).forEachIndexed { index, label ->
        val element = createButton { textContent = label }.withTheme("tertiary")
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
        tr("第 ${index + 1} 个元素的说明", "Hint for element ${index + 1}")
    }
    generated.body.append(example(null, row(*generatedTargets.toTypedArray(), generatedTooltip)))
    view.body.append(generated.root)

    val positions =
        section(
            tr("位置", "Position"),
            tr(
                "position 决定浮层相对触发元素的位置，这里演示四个常用值。",
                "position picks the side the overlay opens on; four common values are shown here.",
            ),
        )
    val positionedButtons = mutableListOf<HTMLElement>()
    listOf("top", "bottom", "start", "end").forEachIndexed { index, position ->
        val element = iconButton(position, "lumo:info-circle").withTheme("tertiary")
        element.id = ElementId("tooltip-position-$index")
        val positioned = createTooltip { text = position }
        positioned.`for` = "tooltip-position-$index"
        positioned.position = position
        positionedButtons += element
        positionedButtons += positioned
    }
    positions.body.append(example(null, row(*positionedButtons.toTypedArray())))
    view.body.append(positions.root)

    return view
}
