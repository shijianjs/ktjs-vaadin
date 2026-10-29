package demo.pages.display

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.element
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.progressbar.createProgressBar
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import web.dom.clickEvent
import web.events.addHandler
import web.html.HtmlTagName

val progressBarEntry: CatalogEntry =
    CatalogEntry(
        id = "progress-bar",
        group = Groups.DISPLAY,
        meta =
            PageMeta(
                title = bi("Progress Bar", "Progress Bar"),
                summary = bi("进度条：确定值与不确定状态，min / max 决定比例。", "Progress bar: determinate value and indeterminate state; min / max set the ratio."),
                docsUrl = "$DOCS_BASE/progress-bar",
                sourcePath = "pages/display/ProgressBarPage.kt",
            ),
        builder = ::buildProgressBarPage,
    )

// ---------------------------------------------------------------------------------------------
// Progress bar
// ---------------------------------------------------------------------------------------------

private fun buildProgressBarPage(meta: PageMeta): PageView {
    val view = page(meta)

    // --- Basic usage (progressbar/progress-bar-basic.ts) --------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "默认范围是 0–1，value 是当前进度；下面的进度条停在 50%。",
                "The default range is 0–1 and value is the current progress; the bar below sits at 50%.",
            ),
        )
    val half = createProgressBar { value = 0.5 }
    basic.body.append(example(null, half))
    view.body.append(basic.root)

    // --- Determinate (progressbar/progress-bar-determinate.ts) --------------------------------
    val determinate =
        section(
            tr("确定进度", "Determinate"),
            tr(
                "确定模式下 value 按 (value - min) / (max - min) 的比例填充；官方示例只展示静态的 0.5，" +
                    "这里用按钮把它变成可操作的例子。",
                "In determinate mode the bar fills by (value - min) / (max - min); the official example only " +
                    "shows a static 0.5, buttons make it interactive here.",
            ),
        )
    val progress =
        createProgressBar {
            min = 0.0
            max = 100.0
            value = 35.0
        }
    val readout = Readout("value = 35")
    val increase =
        createButton { textContent = "+10" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                progress.value = (progress.value + 10.0).coerceAtMost(progress.max)
                readout.set("value = ${progress.value}")
            }
        }
    val reset =
        createButton { textContent = tr("重置", "Reset") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                progress.value = 0.0
                readout.set("value = 0.0")
            }
        }
    determinate.body.append(example(null, column(progress, row(increase, reset, readout.root))))
    view.body.append(determinate.root)

    // --- Indeterminate (progressbar/progress-bar-indeterminate.ts) ---------------------------
    val indeterminateSection =
        section(
            tr("不确定进度", "Indeterminate"),
            tr(
                "indeterminate 优先于 min / max / value：打开后忽略数值，显示循环动画。",
                "indeterminate takes precedence over min / max / value: the value is ignored and a looping " +
                    "animation is shown.",
            ),
        )
    val indeterminateBar = createProgressBar { indeterminate = true }
    val toggle = createSwitch { label = "indeterminate" }
    toggle.checked = true
    toggle.listen(SwitchEvents.checkedChanged) { indeterminateBar.indeterminate = toggle.checked }
    indeterminateSection.body.append(example(null, column(indeterminateBar, toggle)))
    view.body.append(indeterminateSection.root)

    // --- Bounds and initial value (progressbar/progress-bar-custom-range.ts) -----------------
    val range =
        section(
            tr("范围与初始值", "Bounds and initial value"),
            tr(
                "min / max 不必是 0–1；0–100 配 value=\"50\" 与默认范围的 0.5 等价，只是数值读起来更直观。",
                "min / max do not have to be 0–1; 0–100 with value=\"50\" is the same as 0.5 in the default " +
                    "range, only easier to read in code.",
            ),
        )
    range.body.append(
        example(
            null,
            createProgressBar {
                min = 0.0
                max = 100.0
                value = 50.0
            },
        ),
    )
    view.body.append(range.root)

    // --- Provide a label (progressbar/progress-bar-label.ts) --------------------------------
    val labelSection =
        section(
            tr("提供标签", "Provide a label"),
            tr(
                "进度条没有可见标签：用 aria-labelledby 把外部的 label 关联起来，百分比放在行尾。",
                "The bar has no visible label: aria-labelledby associates an external label, and the percentage " +
                    "sits at the trailing edge.",
            ),
        )
    val labelId = "progress-bar-label"
    val barLabel =
        element(HtmlTagName.label).apply {
            textContent = tr("正在处理 Financials.xlsx", "Processing Financials.xlsx")
            setAttribute("id", labelId)
        }
    val labelled = createProgressBar { value = 0.5 }
    labelled.setAttribute("aria-labelledby", labelId)
    val labelledHeader = row(barLabel, span("50%"))
    labelledHeader.style.setProperty("justify-content", "space-between")
    labelledHeader.style.setProperty("margin-bottom", "var(--lumo-space-s)")
    labelSection.body.append(example(null, column(labelledHeader, labelled)))
    view.body.append(labelSection.root)

    // --- Estimate completion time (progressbar/progress-bar-completion-time.ts) --------------
    val completion =
        section(
            tr("估计完成时间", "Estimate completion time"),
            tr(
                "时间未知时用不确定进度，并用 aria-describedby 说明大概要多久；文字提示同时让所有人看到。",
                "Use an indeterminate bar when the time is unknown and aria-describedby to explain roughly how " +
                    "long it takes; the hint is visible to everyone as well.",
            ),
        )
    val titleId = "progress-bar-title"
    val hintId = "progress-bar-hint"
    val title =
        element(HtmlTagName.label).apply {
            textContent = tr("正在生成报表…", "Generating report...")
            setAttribute("id", titleId)
        }
    val waiting = createProgressBar { indeterminate = true }
    waiting.setAttribute("aria-labelledby", titleId)
    waiting.setAttribute("aria-describedby", hintId)
    waiting.style.setProperty("margin", "var(--lumo-space-s) 0")
    val hint =
        span(tr("处理可能需要十分钟以上", "Process can take upwards of 10 minutes"), "small muted").apply {
            setAttribute("id", hintId)
        }
    completion.body.append(example(null, column(title, waiting, hint)))
    view.body.append(completion.root)

    return view
}
