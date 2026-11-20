package demo.pages.display

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.progressbar.createProgressBar
import io.github.shijianjs.ktjs.vaadin.switch.SwitchEvents
import io.github.shijianjs.ktjs.vaadin.switch.createSwitch
import web.dom.clickEvent
import web.events.addHandler
import web.html.HtmlTagName
import demo.ui.MUTED_SMALL

// ---------------------------------------------------------------------------------------------
// Progress bar
// ---------------------------------------------------------------------------------------------

internal class ProgressBarPage : DemoPage() {
    private val texts = Binder(progressBarTexts)

    private val frame = PageFrame(ENTRY)

    /** The label and hint of the two labelled examples: external text, so the page owns them. */
    private val barLabel = HtmlTagName.label()
    private val titleLabel = HtmlTagName.label()
    private val slowHint = HtmlTagName.span()

    /** The reset button of the determinate example; the `+10` button needs no copy. */
    private val reset: ButtonElement = createButton()

    init {
        frame.mount(this)

        // --- Basic usage (progressbar/progress-bar-basic.ts) --------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val half = createProgressBar { value = 0.5 }
        basic.body.append(exampleBox(null, half))
        body.append(basic.root)

        // --- Determinate (progressbar/progress-bar-determinate.ts) --------------------------------
        val determinate = SectionBlock.NAME { show() }
        texts.bind(determinate.heading) { it.determinateHeading }
        texts.bind(determinate.hint) { it.determinateHint }
        val progress =
            createProgressBar {
                min = 0.0
                max = 100.0
                value = 35.0
            }
        val readout = Readout("value = 35")
        val increase =
            createButton { textContent = "+10" }.addTheme("tertiary").apply {
                clickEvent.addHandler {
                    progress.value = (progress.value + 10.0).coerceAtMost(progress.max)
                    readout.set("value = ${progress.value}")
                }
            }
        reset.addTheme("tertiary")
        reset.clickEvent.addHandler {
            progress.value = 0.0
            readout.set("value = 0.0")
        }
        determinate.body.append(exampleBox(null, kitCol(progress, kitRow(increase, reset, readout.root))))
        body.append(determinate.root)

        // --- Indeterminate (progressbar/progress-bar-indeterminate.ts) ---------------------------
        val indeterminateSection = SectionBlock.NAME { show() }
        texts.bind(indeterminateSection.heading) { it.indeterminateHeading }
        texts.bind(indeterminateSection.hint) { it.indeterminateHint }
        val indeterminateBar = createProgressBar { indeterminate = true }
        val toggle = createSwitch { label = "indeterminate" }
        toggle.checked = true
        toggle.listen(SwitchEvents.checkedChanged) { indeterminateBar.indeterminate = toggle.checked }
        indeterminateSection.body.append(exampleBox(null, kitCol(indeterminateBar, toggle)))
        body.append(indeterminateSection.root)

        // --- Bounds and initial value (progressbar/progress-bar-custom-range.ts) -----------------
        val range = SectionBlock.NAME { show() }
        texts.bind(range.heading) { it.rangeHeading }
        texts.bind(range.hint) { it.rangeHint }
        range.body.append(
            exampleBox(
                null,
                createProgressBar {
                    min = 0.0
                    max = 100.0
                    value = 50.0
                },
            ),
        )
        body.append(range.root)

        // --- Provide a label (progressbar/progress-bar-label.ts) --------------------------------
        val labelSection = SectionBlock.NAME { show() }
        texts.bind(labelSection.heading) { it.labelHeading }
        texts.bind(labelSection.hint) { it.labelHint }
        val labelId = "progress-bar-label"
        texts.bind(barLabel) { it.processing }
        barLabel.setAttribute("id", labelId)
        val labelled = createProgressBar { value = 0.5 }
        labelled.setAttribute("aria-labelledby", labelId)
        val labelledHeader = kitRow(barLabel, HtmlTagName.span { textContent = "50%" })
        labelledHeader.style.setProperty("justify-content", "space-between")
        labelledHeader.style.setProperty("margin-bottom", "var(--lumo-space-s)")
        labelSection.body.append(exampleBox(null, kitCol(labelledHeader, labelled)))
        body.append(labelSection.root)

        // --- Estimate completion time (progressbar/progress-bar-completion-time.ts) --------------
        val completion = SectionBlock.NAME { show() }
        texts.bind(completion.heading) { it.completionHeading }
        texts.bind(completion.hint) { it.completionHint }
        val titleId = "progress-bar-title"
        val hintId = "progress-bar-hint"
        texts.bind(titleLabel) { it.generating }
        titleLabel.setAttribute("id", titleId)
        val waiting = createProgressBar { indeterminate = true }
        waiting.setAttribute("aria-labelledby", titleId)
        waiting.setAttribute("aria-describedby", hintId)
        waiting.style.setProperty("margin", "var(--lumo-space-s) 0")
        slowHint.className = MUTED_SMALL
        texts.bind(slowHint) { it.slowHint }
        slowHint.setAttribute("id", hintId)
        completion.body.append(exampleBox(null, kitCol(titleLabel, waiting, slowHint)))
        body.append(completion.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        reset.textContent = texts.current.reset
    }

    companion object : CustomElementReferenceAdapter<ProgressBarPage>(ProgressBarPage::class.js, "vaadin-demo-progress-bar-page") {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "progress-bar",
                group = Groups.DISPLAY,
                meta =
                    PageMeta(
                        title = bi("Progress Bar", "Progress Bar"),
                        summary = bi("进度条：确定值与不确定状态，min / max 决定比例。", "Progress bar: determinate value and indeterminate state; min / max set the ratio."),
                        docsUrl = "${Catalog.DOCS_BASE}/progress-bar",
                        sourcePath = "pages/display/ProgressBarPage.kt",
                    ),
                kitPage = { ProgressBarPage.NAME() },
            )

        private val english: ProgressBarTexts = ProgressBarTexts(
            basicHeading = "Basic usage",
            basicHint = "The default range is 0–1 and value is the current progress; the bar below sits at 50%.",
            determinateHeading = "Determinate",
            determinateHint = "In determinate mode the bar fills by (value - min) / (max - min); the official example " +
                "only shows a static 0.5, buttons make it interactive here.",
            reset = "Reset",
            indeterminateHeading = "Indeterminate",
            indeterminateHint = "indeterminate takes precedence over min / max / value: the value is ignored and a looping " +
                "animation is shown.",
            rangeHeading = "Bounds and initial value",
            rangeHint = "min / max do not have to be 0–1; 0–100 with value=\"50\" is the same as 0.5 in the default range, " +
                "only easier to read in code.",
            labelHeading = "Provide a label",
            labelHint = "The bar has no visible label: aria-labelledby associates an external label, and the percentage " +
                "sits at the trailing edge.",
            processing = "Processing Financials.xlsx",
            completionHeading = "Estimate completion time",
            completionHint = "Use an indeterminate bar when the time is unknown and aria-describedby to explain roughly how " +
                "long it takes; the hint is visible to everyone as well.",
            generating = "Generating report...",
            slowHint = "Process can take upwards of 10 minutes",
        )
        private val chinese: ProgressBarTexts = ProgressBarTexts(
            basicHeading = "基本用法",
            basicHint = "默认范围是 0–1，value 是当前进度；下面的进度条停在 50%。",
            determinateHeading = "确定进度",
            determinateHint = "确定模式下 value 按 (value - min) / (max - min) 的比例填充；官方示例只展示静态的 0.5，" +
                "这里用按钮把它变成可操作的例子。",
            reset = "重置",
            indeterminateHeading = "不确定进度",
            indeterminateHint = "indeterminate 优先于 min / max / value：打开后忽略数值，显示循环动画。",
            rangeHeading = "范围与初始值",
            rangeHint = "min / max 不必是 0–1；0–100 配 value=\"50\" 与默认范围的 0.5 等价，只是数值读起来更直观。",
            labelHeading = "提供标签",
            labelHint = "进度条没有可见标签：用 aria-labelledby 把外部的 label 关联起来，百分比放在行尾。",
            processing = "正在处理 Financials.xlsx",
            completionHeading = "估计完成时间",
            completionHint = "时间未知时用不确定进度，并用 aria-describedby 说明大概要多久；文字提示同时让所有人看到。",
            generating = "正在生成报表…",
            slowHint = "处理可能需要十分钟以上",
        )
        private val progressBarTexts: Translations<ProgressBarTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
