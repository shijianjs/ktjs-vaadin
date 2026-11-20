package demo.pages.fields

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormLayout
import io.github.shijianjs.ktjs.vaadin.integerfield.IntegerFieldElement
import io.github.shijianjs.ktjs.vaadin.integerfield.IntegerFieldEvents
import io.github.shijianjs.ktjs.vaadin.integerfield.createIntegerField
import web.dom.clickEvent
import web.events.addHandler

internal class IntegerFieldPage : DemoPage() {
    private val texts = Binder(integerFieldTexts)

    private val frame = PageFrame(ENTRY)

    private val count: IntegerFieldElement = createIntegerField()

    private val age: IntegerFieldElement = createIntegerField()

    private val quantity: IntegerFieldElement = createIntegerField()

    private val validateButton: ButtonElement = createButton()

    private val readout = Readout(english.valuePrefix + english.empty)

    private val result = Readout(english.notValidated)

    private val log = EventLog(6)

    /** The value shown in the readout, and the outcome of the last validation. */
    private var lastValue = ""

    private var lastValidation: Boolean? = null

    /** One recorded event: the clock it happened at, and the value (null for an unparsable change). */
    private class Line(val time: String, val value: String?)

    /** The log keeps the events, not their rendering: the lines are rebuilt on every language change. */
    private val history = mutableListOf<Line>()

    init {
        frame.mount(this)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        count.step = 1.0
        count.min = 0.0
        count.stepButtonsVisible = true
        count.dataset["test"] = "integer-field-basic"
        count.listen(IntegerFieldEvents.valueChanged) {
            lastValue = count.value
            renderReadout()
            history.add(Line(log.now(), count.value))
            log.add("value-changed · ${count.value}")
        }
        count.listen(IntegerFieldEvents.unparsableChange) {
            history.add(Line(log.now(), null))
            log.add(texts.current.unparsable)
        }

        basic.body.append(
            exampleBox(null, kitCol(count, readout.root)),
            // The kit draws the caption as the card's first child; an empty caption is bound afterwards.
            exampleBox("", log.root).also { card ->
                texts.bind(card.firstElementChild as web.html.HTMLParagraphElement) { it.eventLog }
            },
        )
        body.append(basic.root)

        val variants = SectionBlock.NAME { show() }
        texts.bind(variants.heading) { it.variantsHeading }
        texts.bind(variants.hint) { it.variantsHint }
        val variantLayout =
            createFormLayout {
                autoResponsive = true
                autoRows = true
            }
        variantLayout.append(
            createIntegerField {
                label = "X"
                value = "-1284"
            },
            createIntegerField {
                label = "Y"
                value = "3910"
            },
        )
        variants.body.append(exampleBox(null, variantLayout))
        body.append(variants.root)

        val range = SectionBlock.NAME { show() }
        texts.bind(range.heading) { it.rangeHeading }
        texts.bind(range.hint) { it.rangeHint }
        age.value = "30"
        age.min = 18.0
        age.max = 120.0
        validateButton.clickEvent.addHandler {
            lastValidation = age.validate()
            renderResult()
        }
        range.body.append(exampleBox(null, kitCol(age, kitRow(validateButton, result.root))))
        body.append(range.root)

        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        quantity.value = "42"
        quantity.addTheme("align-right", "small", "helper-above-field")
        styles.body.append(exampleBox(null, quantity))
        body.append(styles.root)

        applyTranslation(Langs.EN)
    }

    private fun renderReadout() {
        val copy = texts.current
        readout.set(copy.valuePrefix, lastValue.ifEmpty { copy.empty })
    }

    private fun renderResult() {
        val copy = texts.current
        val outcome = lastValidation ?: return result.set(copy.notValidated)
        result.set(copy.resultPrefix, if (outcome) copy.valid else copy.failed)
    }

    /** Replays the recorded events with the current copy, under the time each of them happened. */
    private fun renderLog() {
        log.clear()
        history.forEach { line ->
            log.addAt(line.time, if (line.value == null) texts.current.unparsable else "value-changed · ${line.value}")
        }
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        count.label = copy.itemCount
        age.label = copy.age
        age.errorMessage = copy.ageError
        quantity.label = copy.quantity
        quantity.helperText = copy.helper
        validateButton.textContent = copy.validate
        renderReadout()
        renderResult()
        renderLog()
    }

    companion object : CustomElementReferenceAdapter<IntegerFieldPage>(IntegerFieldPage::class.js, "vaadin-demo-integer-field-page") {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "integer-field",
                group = Groups.FIELDS,
                meta =
                    PageMeta(
                        title = bi("Integer Field", "Integer Field"),
                        summary =
                            bi(
                                "只接受整数的数字输入，继承 Number Field 的全部属性。",
                                "Numeric input that only accepts integers; inherits every Number Field property.",
                            ),
                        docsUrl = "${Catalog.DOCS_BASE}/number-field",
                        docsNote =
                            bi(
                                "官方文档没有单独的 Integer Field 页面，语义见 Number Field。",
                                "There is no dedicated Integer Field page in the official documentation; see " +
                                    "Number Field for the semantics.",
                            ),
                        sourcePath = "pages/fields/IntegerFieldPage.kt",
                    ),
                kitPage = { IntegerFieldPage.NAME() },
            )

        private val english: IntegerFieldTexts = IntegerFieldTexts(
            basicHeading = "Basic usage",
            basicHint = "Only integers are parsed; input with a decimal point fires unparsable-change and leaves value " +
                "unchanged.",
            valuePrefix = "Value: ",
            empty = "(empty)",
            itemCount = "Item count",
            eventLog = "Event log",
            unparsable = "unparsable-change (not an integer)",
            variantsHeading = "Value examples",
            variantsHint = "value is a string here too; negative and multi-digit integers are read and written as plain " +
                "decimal text.",
            rangeHeading = "Range validation",
            rangeHint = "min / max constrain both the step buttons and validate().",
            age = "Age",
            ageError = "Age must be between 18 and 120",
            notValidated = "Not validated yet",
            validate = "Validate",
            resultPrefix = "Result: ",
            valid = "Valid",
            failed = "Failed",
            stylesHeading = "Styles",
            stylesHint = "Shares the same theme variants as Number Field.",
            quantity = "Quantity",
            helper = "Helper text",
        )
        private val chinese: IntegerFieldTexts = IntegerFieldTexts(
            basicHeading = "基本用法",
            basicHint = "只解析整数；带小数点的输入会触发 unparsable-change 并且不更新 value。",
            valuePrefix = "当前值：",
            empty = "（空）",
            itemCount = "件数",
            eventLog = "事件流",
            unparsable = "unparsable-change（不是整数）",
            variantsHeading = "取值示例",
            variantsHint = "Integer Field 的 value 同样是字符串，负整数与多位数都直接以十进制文本读写。",
            rangeHeading = "范围校验",
            rangeHint = "min / max 同时约束步进按钮和 validate()。",
            age = "年龄",
            ageError = "年龄需要在 18 到 120 之间",
            notValidated = "尚未校验",
            validate = "校验",
            resultPrefix = "校验结果：",
            valid = "通过",
            failed = "失败",
            stylesHeading = "样式变体",
            stylesHint = "与 Number Field 共用同一套主题变体。",
            quantity = "数量",
            helper = "说明文字",
        )
        private val integerFieldTexts: Translations<IntegerFieldTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
