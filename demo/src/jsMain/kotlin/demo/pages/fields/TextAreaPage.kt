package demo.pages.fields

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import web.html.HTMLElement
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.textarea.TextAreaElement
import io.github.shijianjs.ktjs.vaadin.textarea.TextAreaEvents
import io.github.shijianjs.ktjs.vaadin.textarea.createTextArea
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val textAreaEntry: CatalogEntry =
    CatalogEntry(
        id = "text-area",
        group = Groups.FIELDS,
        meta =
            PageMeta(
                title = bi("Text Area", "Text Area"),
                summary =
                    bi(
                        "多行文本输入：行数范围、字数上限与约束校验。",
                        "Multi-line text input: row range, character limit and constraint validation.",
                    ),
                docsUrl = "$DOCS_BASE/text-area",
                sourcePath = "pages/fields/TextAreaPage.kt",
            ),
        kitPage = { TextAreaPage.NAME() },
    )

internal class TextAreaPage : DemoPage() {
    private val texts = Binder(textAreaTexts)

    private val frame = PageFrame(textAreaEntry)

    private val messageField = createTextArea()

    private val counter = Readout("")

    private val sentence = createTextArea()

    private val sentenceResult = Readout("")

    private var sentenceCheck: String? = null

    private val requiredArea = createTextArea()

    private val result = Readout("")

    private var manualResult: Boolean? = null

    private var modes: ValueChangeSection? = null

    init {
        frame.mount(this)
        registerVaadinIcons()

        // --- Basic usage (text-area-basic.ts) -------------------------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val initialComment = "Great job. This is excellent!"
        texts.bind(messageField, { it.comment }) { messageField.label = it }
        messageField.value = initialComment
        messageField.maxlength = 200.0
        messageField.helperText = "${initialComment.length}/200"
        messageField.dataset["test"] = "text-area-basic"
        messageField.listen(TextAreaEvents.valueChanged) {
            val length = messageField.value.length
            messageField.helperText = "$length/200"
            showCounter()
        }
        basic.body.append(exampleBox(null, kitCol(messageField, counter.root)))
        body.append(basic.root)

        // --- Automatic height (text-area-auto-height.ts, text-area-height.ts) -----------------------
        val height = SectionBlock.NAME { show() }
        texts.bind(height.heading) { it.heightHeading }
        texts.bind(height.hint) { it.heightHint }
        fun area(copy: (TextAreaTexts) -> String): TextAreaElement =
            createTextArea().also { node -> texts.bind(node, copy) { node.label = it } }
        val autoHeight = area { it.description }
        autoHeight.value = LOREM_IPSUM
        autoHeight.style.setProperty("width", "100%")
        val boundedHeight = area { it.descriptionBounded }
        boundedHeight.value = LOREM_IPSUM
        boundedHeight.style.setProperty("width", "100%")
        boundedHeight.style.setProperty("min-height", "100px")
        boundedHeight.style.setProperty("max-height", "150px")
        val rowsHeight = area { it.threeToFiveRows }
        rowsHeight.minRows = 3.0
        rowsHeight.maxRows = 5.0
        texts.bind(rowsHeight, { it.threeLines }) { rowsHeight.value = it }
        val singleRow = area { it.singleRow }
        singleRow.minRows = 1.0
        singleRow.maxRows = 1.0
        height.body.append(
            exampleBox(null, newCaption { it.growCaption }, autoHeight),
            exampleBox(null, newCaption { it.boundedCaption }, boundedHeight),
            exampleBox(null, newCaption { it.rowsCaption }, kitRow(rowsHeight, singleRow)),
        )
        body.append(height.root)

        // --- Basic features (text-area-basic-features.ts) -------------------------------------------
        val features = SectionBlock.NAME { show() }
        texts.bind(features.heading) { it.featuresHeading }
        texts.bind(features.hint) { it.featuresHint }
        val featureField = createTextArea()
        texts.bind(featureField, { it.label }) { featureField.label = it }
        texts.bind(featureField, { it.helperText }) { featureField.helperText = it }
        texts.bind(featureField, { it.placeholder }) { featureField.placeholder = it }
        featureField.clearButtonVisible = true
        featureField.style.setProperty("width", "100%")
        val tooltip = createTooltip().apply { slot = "tooltip" }
        texts.bind(tooltip, { it.tooltipText }) { tooltip.text = it }
        featureField.append(
            tooltip,
            createIcon { icon = "vaadin:vaadin-h" }.apply { slot = "prefix" },
            HtmlTagName.span { textContent = ":)" }.apply { slot = "suffix" },
        )
        features.body.append(exampleBox(null, featureField))
        body.append(features.root)

        // --- Validation (text-area-validation.ts) ---------------------------------------------------
        val validation = SectionBlock.NAME { show() }
        texts.bind(validation.heading) { it.validationHeading }
        texts.bind(validation.hint) { it.validationHint }
        texts.bind(sentence, { it.sentence }) { sentence.label = it }
        texts.bind(sentence, { it.sentenceHelper }) { sentence.helperText = it }
        sentence.required = true
        sentence.minlength = 5.0
        sentence.maxlength = 50.0
        sentence.pattern = SENTENCE_PATTERN
        sentence.allowedCharPattern = "[A-Za-z0-9,.\\-\\s]"
        sentence.style.setProperty("width", "100%")
        sentence.listen(TextAreaEvents.validated) {
            val validity = validityOf(sentence)
            sentenceCheck =
                when {
                    validity.valueMissing -> "required"
                    validity.tooShort -> "tooShort"
                    validity.tooLong -> "tooLong"
                    hasPatternMismatch(sentence.pattern, sentence.value) -> "pattern"
                    else -> "ok"
                }
            showSentence()
        }
        texts.bind(requiredArea, { it.requiredNote }) { requiredArea.label = it }
        texts.bind(requiredArea, { it.requiredError }) { requiredArea.errorMessage = it }
        requiredArea.required = true
        requiredArea.dataset["test"] = "text-area-required"
        val validateButton = createButton()
        texts.bind(validateButton) { it.validate }
        validateButton.clickEvent.addHandler {
            manualResult = requiredArea.validate()
            showManual()
        }
        validation.body.append(
            exampleBox(null, newCaption { it.constraintsCaption }, kitCol(sentence, sentenceResult.root)),
            exampleBox(null, newCaption { it.validateCaption }, kitCol(requiredArea, kitRow(validateButton, result.root))),
        )
        body.append(validation.root)

        // --- Read-only and disabled (text-area-readonly-and-disabled.ts) ----------------------------
        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        texts.bind(states.hint) { it.statesHint }
        val readonlyArea = area { it.readonly }
        texts.bind(readonlyArea, { it.value }) { readonlyArea.value = it }
        readonlyArea.readonly = true
        states.body.append(exampleBox(null, kitRow(readonlyArea, area { it.disabled })))
        body.append(states.root)

        // --- Character counter (text-area-helper.ts) ------------------------------------------------
        val counterSection = SectionBlock.NAME { show() }
        texts.bind(counterSection.heading) { it.counterHeading }
        texts.bind(counterSection.hint) { it.counterHint }
        val longArea = area { it.description }
        longArea.value = LOREM_IPSUM
        longArea.maxlength = 600.0
        longArea.helperText = "${LOREM_IPSUM.length}/600"
        longArea.style.setProperty("width", "100%")
        longArea.listen(TextAreaEvents.valueChanged) { longArea.helperText = "${longArea.value.length}/600" }
        counterSection.body.append(exampleBox(null, longArea))
        body.append(counterSection.root)

        // --- Style variants (text-area-styles.ts) ---------------------------------------------------
        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styledArea = area { it.label }
        texts.bind(styledArea, { it.helperText }) { styledArea.helperText = it }
        texts.bind(styledArea, { it.value }) { styledArea.value = it }
        styledArea.addTheme("align-right", "small", "helper-above-field")
        styledArea.style.setProperty("width", "100%")
        styles.body.append(exampleBox(null, styledArea))
        body.append(styles.root)

        // --- Value change modes (text-area-value-change-mode.ts, Flow-only example) -----------------
        val modeField = area { it.typeHere }
        modes =
            valueChangeModesSection(modeField, TextAreaEvents.valueChanged, TextAreaEvents.change) { modeField.value }
        body.append(modes!!.root)

        applyTranslation(Langs.EN)
    }

    /** A paragraph above an example, used where the caption is copy rather than a CSS snippet. */
    private fun newCaption(copy: (TextAreaTexts) -> String): HTMLElement =
        HtmlTagName.p().apply {
            style.setProperty("margin", "0 0 0.75rem")
            style.setProperty("font-size", "0.8125rem")
            style.setProperty("color", "var(--lumo-secondary-text-color)")
            texts.bind(this, copy) { textContent = it }
        }

    private fun showCounter() {
        counter.set(texts.current.characters(messageField.value.length, 200))
    }

    private fun showSentence() {
        val copy = texts.current
        val message =
            when (sentenceCheck) {
                "required" -> copy.required
                "tooShort" -> copy.tooShort(sentence.minlength.toInt())
                "tooLong" -> copy.tooLong(sentence.maxlength.toInt())
                "pattern" -> copy.pattern
                else -> ""
            }
        sentence.errorMessage = message
        sentenceResult.set(
            copy.checkPrefix,
            if (sentenceCheck == null) copy.notValidated else if (message.isEmpty()) copy.passed else copy.failed,
        )
    }

    private fun showManual() {
        val copy = texts.current
        val result = manualResult
        this.result.set(
            if (result == null) copy.notValidated
            else copy.resultPrefix + (if (result) copy.valid else copy.invalid),
        )
    }

    override fun applyTranslation(lang: Lang) {
        val meta = textAreaEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showCounter()
        showSentence()
        showManual()
        modes?.show(lang)
    }

    companion object : CustomElementReferenceAdapter<TextAreaPage>(TextAreaPage::class.js, "vaadin-demo-text-area-page") {
        private val english: TextAreaTexts =
            TextAreaTexts(
                basicHeading = "Basic usage",
                basicHint = "A text area starts at two rows and grows with its content; maxlength caps the " +
                    "number of characters and the helper text shows the current length against that cap.",
                comment = "Comment",
                characters = { length, cap -> "Characters: $length / $cap" },
                heightHeading = "Automatic height adjustment",
                heightHint = "Without a fixed height the text area grows with its content and defaults to a " +
                    "two-row minimum; min-height / max-height or minRows / maxRows bound how far it may grow.",
                description = "Description",
                descriptionBounded = "Description (100 - 150 px)",
                threeToFiveRows = "3 to 5 rows",
                threeLines = "First line\nSecond line\nThird line",
                singleRow = "Multi-line input at a single row",
                growCaption = "Growing automatically",
                boundedCaption = "Bounded height",
                rowsCaption = "Bounded row count",
                featuresHeading = "Basic features",
                featuresHint = "Besides the multi-line input, Text Area shares the label, helper text, " +
                    "placeholder, tooltip and prefix / suffix slots with Text Field.",
                label = "Label",
                helperText = "Helper text",
                placeholder = "Placeholder",
                tooltipText = "Tooltip text",
                validationHeading = "Validation",
                validationHint = "required, minlength / maxlength, pattern and allowedCharPattern combine. A " +
                    "textarea has no native pattern support, so the component checks it with a regular expression; " +
                    "the example applies the same rule when mapping the validated event to an error message.",
                sentence = "Sentence",
                sentenceHelper = "Must be one complete sentence ending in a period, between 5 and 50 " +
                    "characters long",
                notValidated = "Not validated yet",
                required = "Field is required",
                tooShort = { "Minimum length is $it characters" },
                tooLong = { "Maximum length is $it characters" },
                pattern = "Must be one complete sentence ending in a period",
                checkPrefix = "checked · ",
                passed = "valid",
                failed = "invalid",
                constraintsCaption = "Constraints and error messages",
                requiredNote = "Required note",
                requiredError = "This field is required",
                validate = "Validate",
                validateCaption = "Manual validation (validate())",
                resultPrefix = "Result: ",
                valid = "Valid",
                invalid = "Failed",
                statesHeading = "Read-only and disabled",
                statesHint = "readonly allows focus, selection and copying but no edits; disabled removes " +
                    "interaction entirely.",
                readonly = "Read-only",
                disabled = "Disabled",
                value = "Value",
                counterHeading = "Character counter",
                counterHint = "Longer free-form inputs are often capped with maxlength, and the helper text " +
                    "shows the current length against that cap.",
                stylesHeading = "Style variants",
                stylesHint = "The same align-right, small and helper-above-field variants apply; a text area " +
                    "usually fills the width of its container.",
                typeHere = "Type here",
            )
        private val chinese: TextAreaTexts =
            TextAreaTexts(
                basicHeading = "基本用法",
                basicHint = "多行输入默认两行高，并随内容自动增长；maxlength 限制字符数，辅助文本实时显示已输入" +
                    "的长度与上限。",
                comment = "评论",
                characters = { length, cap -> "字数：$length / $cap" },
                heightHeading = "高度自动调整",
                heightHint = "没有固定高度时，Text Area 随内容增长，默认最小高度是两行；也可以用 min-height / " +
                    "max-height 或 minRows / maxRows 约束增长范围。",
                description = "描述",
                descriptionBounded = "描述（100 - 150 px）",
                threeToFiveRows = "3 到 5 行",
                threeLines = "第一行\n第二行\n第三行",
                singleRow = "单行高度的多行输入",
                growCaption = "自动增长",
                boundedCaption = "限制高度",
                rowsCaption = "限制行数",
                featuresHeading = "基本特性",
                featuresHint = "除多行输入外，Text Area 与 Text Field 共享标签、辅助文本、占位符、提示与前后缀插槽。",
                label = "标签",
                helperText = "辅助文本",
                placeholder = "占位符",
                tooltipText = "提示文本",
                validationHeading = "校验",
                validationHint = "required、minlength / maxlength、pattern 与 allowedCharPattern 组合使用。" +
                    "textarea 不支持原生 pattern，组件的 checkValidity() 用正则自行检查，示例在 validated 事件里" +
                    "按同样的规则给出错误文案。",
                sentence = "句子",
                sentenceHelper = "必须是以句号结尾的完整句子，长度 5 到 50 个字符",
                notValidated = "尚未校验",
                required = "这一项必填",
                tooShort = { "最短 $it 个字符" },
                tooLong = { "最长 $it 个字符" },
                pattern = "必须是以句号结尾的完整句子",
                checkPrefix = "校验：",
                passed = "通过",
                failed = "未通过",
                constraintsCaption = "约束与错误文案",
                requiredNote = "必填说明",
                requiredError = "这一项不能为空",
                validate = "校验",
                validateCaption = "手动校验（validate）",
                resultPrefix = "校验结果：",
                valid = "通过",
                invalid = "失败",
                statesHeading = "只读与禁用",
                statesHint = "readonly 可以聚焦、选择和复制但不能修改；disabled 完全移除交互。",
                readonly = "只读",
                disabled = "禁用",
                value = "值",
                counterHeading = "字数统计",
                counterHint = "较长的自由文本常用 maxlength 限制上限，并借助辅助文本显示当前长度与上限。",
                stylesHeading = "样式变体",
                stylesHint = "theme 属性同样接受 align-right、small 与 helper-above-field；多行输入通常占满" +
                    "容器宽度。",
                typeHere = "输入内容",
            )
        /** Sentence pattern of `text-area-validation.ts`: one sentence, capitalised, ending with a period. */
        private const val SENTENCE_PATTERN = "^[A-Z]([A-Za-z0-9,-\\s])*\\.$"
        /** The `templates.json` lorem ipsum used by the text-area height and character-counter examples. */
        private const val LOREM_IPSUM =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel semper libero. Vestibulum ante " +
                "ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae.\n\n" +
                "Proin volutpat, sapien ut facilisis ultricies, eros purus blandit velit, at ultrices mi libero " +
                "quis ante. Curabitur scelerisque metus et libero convallis consequat. Pellentesque feugiat " +
                "pulvinar nisl sed pellentesque."
        /**
         * `TextArea` has no native `pattern` support; upstream checks it with a regular expression, and the
         * error mapping of the docs example does the same. Compile errors count as a match like upstream.
         */
        private fun hasPatternMismatch(pattern: String, value: String): Boolean {
            if (pattern.isEmpty() || value.isEmpty()) return false
            return runCatching { !Regex(pattern).matches(value) }.getOrDefault(false)
        }
        private val textAreaTexts: Translations<TextAreaTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
