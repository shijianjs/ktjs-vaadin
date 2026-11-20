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
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormItem
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormLayout
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.integerfield.IntegerFieldElement
import io.github.shijianjs.ktjs.vaadin.integerfield.IntegerFieldEvents
import io.github.shijianjs.ktjs.vaadin.integerfield.createIntegerField
import io.github.shijianjs.ktjs.vaadin.numberfield.NumberFieldElement
import io.github.shijianjs.ktjs.vaadin.numberfield.NumberFieldEvents
import io.github.shijianjs.ktjs.vaadin.numberfield.createNumberField
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName
import demo.ui.SMALL
import demo.ui.MUTED_SMALL

val numberFieldEntry: CatalogEntry =
    CatalogEntry(
        id = "number-field",
        group = Groups.FIELDS,
        meta =
            PageMeta(
                title = bi("Number Field", "Number Field"),
                summary =
                    bi(
                        "数字输入：min / max / step、步进按钮、原生 validity 校验与值变更时机。",
                        "Numeric input: min / max / step, step buttons, native validity checks and value " +
                            "change timing.",
                    ),
                docsUrl = "$DOCS_BASE/number-field",
                sourcePath = "pages/fields/NumberFieldPage.kt",
            ),
        kitPage = { NumberFieldPage.NAME() },
    )

private val english: NumberFieldTexts =
    NumberFieldTexts(
        basicHeading = "Basic usage",
        basicHint = "value is always a string; the prefix / suffix slots hold fixed content such as " +
            "currency signs, and unparsable input fires unparsable-change instead of updating value.",
        balance = "Balance",
        valuePrefix = "Value: ",
        empty = "(empty)",
        stepHeading = "Step buttons",
        stepHint = "step-buttons-visible adds decrement / increment buttons; min and max also bound them.",
        adults = "Adults",
        children = "Children",
        childrenAge = "Age 2-12",
        infants = "Infants",
        guestsPrefix = "Guests: ",
        validationHeading = "Validation",
        validationHint = "The validated event carries no detail; the upstream example reads the native " +
            "constraint state of inputElement.validity to tell \"bad format / missing / below minimum / " +
            "above maximum\" apart and rewrites errorMessage accordingly.",
        quantity = "Quantity",
        quantityHelper = "Max 10 items",
        notValidated = "Not validated yet",
        badFormat = "Invalid number format",
        required = "Field is required",
        atLeast = { "Quantity must be at least $it" },
        atMost = { "Maximum $it items available" },
        validatedPrefix = "validated · valid = ",
        stepUnitHeading = "Fractional step",
        stepUnitHint = "step drives both the step buttons and the native validity: a value that is not " +
            "a multiple of step sets validity.stepMismatch.",
        duration = "Duration (hours)",
        stepMultiple = { "Duration must be a multiple of $it" },
        variantsHeading = "Number type variants",
        variantsHint = "Integer Field only parses integers, for counts and ages; the BigDecimal example " +
            "exists only in the Java API (Flow's NumberField extends BigDecimalField). The web component " +
            "has no counterpart, so decimals use Number Field directly.",
        featuresHeading = "Basic features",
        featuresHint = "label / helper-text / placeholder combine with the clear button, the tooltip slot " +
            "and the prefix / suffix slots.",
        amount = "Amount",
        helperText = "Helper text",
        placeholder = "Placeholder",
        tooltipText = "Tooltip text",
        statesHeading = "Read-only and disabled",
        statesHint = "readonly keeps the field focusable and copyable, disabled removes all interaction.",
        readonly = "Read-only",
        disabled = "Disabled",
        rangeHeading = "Custom range validation",
        rangeHint = "min / max only constrain stepping and input hints, they do not make validate() fail. " +
            "When the range is a business rule, check value yourself and set invalid — the same thing " +
            "Binder does on the server.",
        unitPrice = "Unit price (0 - 100)",
        unitPriceError = "The unit price must be between 0 and 100",
        validate = "Validate",
        resultPrefix = "Result: ",
        valid = "Valid",
        failed = "Failed",
        modesHeading = "Value change modes",
        modesHint = "on change / eager / lazy / timeout are server-side ValueChangeMode concepts. In the " +
            "browser they reduce to listening to change versus input; lazy and timeout debouncing / " +
            "polling is implemented by server-side timers.",
        onChange = "on change (default)",
        eager = "eager",
        changePrefix = "change: ",
        inputPrefix = "input: ",
        notFired = "not fired yet",
        stylesHeading = "Styles",
        stylesHint = "align-right right-aligns the digits, small tightens the field and " +
            "helper-above-field moves the helper text above it.",
        label = "Label",
    )

private val chinese: NumberFieldTexts =
    NumberFieldTexts(
        basicHeading = "基本用法",
        basicHint = "value 始终是字符串；前缀 / 后缀插槽放货币符号这类固定内容，数字解析失败时触发 " +
            "unparsable-change 而不是更新 value。",
        balance = "余额",
        valuePrefix = "当前值：",
        empty = "（空）",
        stepHeading = "步进按钮",
        stepHint = "step-buttons-visible 在字段两侧加上减 / 加按钮，min 与 max 同时限制按钮的可用状态。",
        adults = "成人",
        children = "儿童",
        childrenAge = "2-12 岁",
        infants = "婴儿",
        guestsPrefix = "客人：",
        validationHeading = "校验",
        validationHint = "validated 事件没有 detail；上游示例读取 inputElement.validity 的原生约束" +
            "状态来区分「格式错误 / 必填缺失 / 低于下限 / 高于上限」，再据此改写 errorMessage。",
        quantity = "数量",
        quantityHelper = "最多 10 件",
        notValidated = "尚未校验",
        badFormat = "数字格式不正确",
        required = "这一项必填",
        atLeast = { "至少 $it 件" },
        atMost = { "最多 $it 件" },
        validatedPrefix = "validated · valid = ",
        stepUnitHeading = "步进小数",
        stepUnitHint = "step 同时决定步进按钮的增量与原生 validity：不是 step 的整数倍时 " +
            "validity.stepMismatch 为真。",
        duration = "时长（小时）",
        stepMultiple = { "时长必须是 $it 的整数倍" },
        variantsHeading = "数字类型变体",
        variantsHint = "Integer Field 只解析整数，用于件数、年龄这类数量；BigDecimal 用例只存在于 Java " +
            "API（Flow 的 NumberField 继承 BigDecimalField），Web Component 侧没有对应元素，小数直接用 " +
            "Number Field。",
        featuresHeading = "基本特性",
        featuresHint = "label / helper-text / placeholder 与清除按钮、工具提示插槽、前缀后缀插槽可以同时使用。",
        amount = "金额",
        helperText = "说明文字",
        placeholder = "占位文字",
        tooltipText = "提示文本",
        statesHeading = "只读与禁用",
        statesHint = "readonly 仍可聚焦与复制，disabled 让字段完全不可交互。",
        readonly = "只读",
        disabled = "禁用",
        rangeHeading = "自定义范围校验",
        rangeHint = "min / max 只约束步进与输入提示，不会让 validate() 自动失败；需要把范围当业务规则时，" +
            "按 value 自己判断并设置 invalid（服务端 Binder 也是这么做的）。",
        unitPrice = "单价（0 - 100）",
        unitPriceError = "单价需要在 0 到 100 之间",
        validate = "校验",
        resultPrefix = "校验结果：",
        valid = "通过",
        failed = "失败",
        modesHeading = "值变更模式",
        modesHint = "on change / eager / lazy / timeout 是服务端 ValueChangeMode 的概念，浏览器端对应" +
            "「监听 change 事件」与「监听 input 事件」两种时机；lazy 与 timeout 的防抖 / 轮询由服务端计时器实现。",
        onChange = "on change（默认）",
        eager = "eager",
        changePrefix = "change：",
        inputPrefix = "input：",
        notFired = "尚未触发",
        stylesHeading = "样式变体",
        stylesHint = "align-right 让数字右对齐（数位对齐），small 收紧尺寸，helper-above-field 把说明" +
            "文字移到字段上方。",
        label = "标签",
    )

internal class NumberFieldPage : DemoPage() {
    private val texts = Binder(numberFieldTexts)

    private val frame = PageFrame(numberFieldEntry)

    private val balance = createNumberField()

    private val balanceReadout = Readout("")

    private val adults = createIntegerField { value = "2"; min = 0.0; max = 9.0; stepButtonsVisible = true }

    private val childrenField = createIntegerField { value = "2"; min = 0.0; max = 9.0; stepButtonsVisible = true }

    private val infants = createIntegerField { value = "1"; min = 0.0; max = 9.0; stepButtonsVisible = true }

    private val guestsReadout = Readout("")

    private val quantity = createIntegerField { value = "2"; min = 1.0; max = 10.0; stepButtonsVisible = true }

    private val validationReadout = Readout("")

    private var validationKind: String? = null

    private var validationValid: Boolean? = null

    private val duration = createNumberField { step = 0.5; value = "12.5"; stepButtonsVisible = true }

    private val stepReadout = Readout("")

    private var stepKind: String? = null

    private var stepValid: Boolean? = null

    private val price = createNumberField { value = "9.5"; step = 0.5; min = 0.0; max = 100.0 }

    private val priceResult = Readout("")

    private var priceValid: Boolean? = null

    private val onChange = createNumberField { value = "0" }

    private val onInput = createNumberField { value = "0" }

    private val changeReadout = Readout("")

    private val inputReadout = Readout("")

    private var lastChange: String? = null

    private var lastInput: String? = null

    init {
        frame.mount(this)

        fun number(copy: (NumberFieldTexts) -> String): NumberFieldElement =
            createNumberField().also { node -> texts.bind(node, copy) { node.label = it } }

        fun integer(copy: (NumberFieldTexts) -> String): IntegerFieldElement =
            createIntegerField().also { node -> texts.bind(node, copy) { node.label = it } }

        fun labelNode(copy: (NumberFieldTexts) -> String): HTMLElement =
            HtmlTagName.label().apply {
                slot = "label"
                texts.bind(this, copy) { textContent = it }
            }

        // --- Basic ----------------------------------------------------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        balance.value = "200"
        texts.bind(balance, { it.balance }) { balance.label = it }
        balance.dataset["test"] = "number-field-basic"
        balance.append(HtmlTagName.span { textContent = "$" }.apply { slot = "prefix" })
        val balanceEur = number { it.balance }
        balanceEur.value = "200"
        balanceEur.append(HtmlTagName.span { textContent = "€" }.apply { slot = "suffix" })
        balance.listen(NumberFieldEvents.valueChanged) { showBalance() }
        val basicLayout =
            createFormLayout {
                autoResponsive = true
                autoRows = true
            }
        basicLayout.append(balance, balanceEur)
        basic.body.append(exampleBox(null, kitCol(basicLayout, balanceReadout.root)))
        body.append(basic.root)

        // --- Step buttons ---------------------------------------------------------------------------
        val stepButtons = SectionBlock.NAME { show() }
        texts.bind(stepButtons.heading) { it.stepHeading }
        texts.bind(stepButtons.hint) { it.stepHint }
        listOf(adults, childrenField, infants).forEach { field ->
            field.listen(IntegerFieldEvents.valueChanged) { showGuests() }
        }
        val adultsItem = createFormItem()
        adultsItem.append(labelNode { it.adults }, adults)
        val childrenItem = createFormItem()
        val childrenLabel = HtmlTagName.label().apply { slot = "label" }
        val childrenTitle = HtmlTagName.span()
        texts.bind(childrenTitle, { it.children }) { childrenTitle.textContent = it }
        val childrenAge = HtmlTagName.span()
        texts.bind(childrenAge, { it.childrenAge }) { childrenAge.textContent = it }
        childrenLabel.append(
            HtmlTagName.div({ className = SMALL }).also { it.append(childrenTitle) },
            HtmlTagName.div({ className = MUTED_SMALL }).also { it.append(childrenAge) },
        )
        childrenItem.append(childrenLabel, childrenField)
        val infantsItem = createFormItem()
        infantsItem.append(labelNode { it.infants }, infants)
        val guestLayout =
            createFormLayout {
                labelsAside = true
                autoResponsive = true
            }
        guestLayout.append(adultsItem, childrenItem, infantsItem)
        stepButtons.body.append(exampleBox(null, kitCol(guestLayout, guestsReadout.root)))
        body.append(stepButtons.root)

        // --- Validation -----------------------------------------------------------------------------
        val validation = SectionBlock.NAME { show() }
        texts.bind(validation.heading) { it.validationHeading }
        texts.bind(validation.hint) { it.validationHint }
        texts.bind(quantity, { it.quantity }) { quantity.label = it }
        texts.bind(quantity, { it.quantityHelper }) { quantity.helperText = it }
        quantity.required = true
        quantity.dataset["test"] = "number-field-validation"
        quantity.listen(IntegerFieldEvents.validated) {
            val validity = validityOf(quantity)
            validationKind =
                when {
                    validity.badInput -> "badInput"
                    validity.valueMissing -> "required"
                    validity.rangeUnderflow -> "underflow"
                    validity.rangeOverflow -> "overflow"
                    else -> null
                }
            validationValid = validity.valid
            showValidation()
        }
        validation.body.append(exampleBox(null, kitCol(quantity, validationReadout.root)))
        body.append(validation.root)

        // --- Step -----------------------------------------------------------------------------------
        val fractional = SectionBlock.NAME { show() }
        texts.bind(fractional.heading) { it.stepUnitHeading }
        texts.bind(fractional.hint) { it.stepUnitHint }
        texts.bind(duration, { it.duration }) { duration.label = it }
        duration.dataset["test"] = "number-field-step"
        duration.listen(NumberFieldEvents.validated) {
            val validity = validityOf(duration)
            stepKind =
                when {
                    validity.badInput -> "badInput"
                    validity.stepMismatch -> "stepMismatch"
                    else -> null
                }
            stepValid = validity.valid
            showStep()
        }
        fractional.body.append(exampleBox(null, kitCol(duration, stepReadout.root)))
        body.append(fractional.root)

        // --- Number type variants -------------------------------------------------------------------
        val variants = SectionBlock.NAME { show() }
        texts.bind(variants.heading) { it.variantsHeading }
        texts.bind(variants.hint) { it.variantsHint }
        val variantLayout =
            createFormLayout {
                autoResponsive = true
                autoRows = true
            }
        variantLayout.append(
            createIntegerField { label = "X"; value = "-1284" },
            createIntegerField { label = "Y"; value = "3910" },
        )
        variants.body.append(exampleBox(null, variantLayout))
        body.append(variants.root)

        // --- Basic features -------------------------------------------------------------------------
        val features = SectionBlock.NAME { show() }
        texts.bind(features.heading) { it.featuresHeading }
        texts.bind(features.hint) { it.featuresHint }
        val featured = number { it.amount }
        texts.bind(featured, { it.helperText }) { featured.helperText = it }
        texts.bind(featured, { it.placeholder }) { featured.placeholder = it }
        featured.clearButtonVisible = true
        featured.dataset["test"] = "number-field-features"
        val featuredTooltip = createTooltip().apply { slot = "tooltip" }
        texts.bind(featuredTooltip, { it.tooltipText }) { featuredTooltip.text = it }
        featured.append(
            featuredTooltip,
            HtmlTagName.span { textContent = "$" }.apply { slot = "prefix" },
            createIcon { icon = "vaadin:dollar" }.apply { slot = "suffix" },
        )
        features.body.append(exampleBox(null, featured))
        body.append(features.root)

        // --- Read-only and disabled -----------------------------------------------------------------
        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        texts.bind(states.hint) { it.statesHint }
        val readonlyField = number { it.readonly }
        readonlyField.value = "200"
        readonlyField.readonly = true
        val disabledField = number { it.disabled }
        disabledField.disabled = true
        val statesLayout =
            createFormLayout {
                autoResponsive = true
                autoRows = true
            }
        statesLayout.append(readonlyField, disabledField)
        states.body.append(exampleBox(null, statesLayout))
        body.append(states.root)

        // --- Custom range validation ----------------------------------------------------------------
        val range = SectionBlock.NAME { show() }
        texts.bind(range.heading) { it.rangeHeading }
        texts.bind(range.hint) { it.rangeHint }
        texts.bind(price, { it.unitPrice }) { price.label = it }
        texts.bind(price, { it.unitPriceError }) { price.errorMessage = it }
        price.stepButtonsVisible = true
        price.dataset["test"] = "number-field-range"
        val validateButton = createButton()
        texts.bind(validateButton) { it.validate }
        validateButton.clickEvent.addHandler {
            val parsed = price.value.toDoubleOrNull()
            priceValid = parsed != null && parsed >= 0.0 && parsed <= 100.0
            price.invalid = priceValid != true
            showPrice()
        }
        range.body.append(exampleBox(null, kitCol(price, kitRow(validateButton, priceResult.root))))
        body.append(range.root)

        // --- Value change modes ---------------------------------------------------------------------
        val modes = SectionBlock.NAME { show() }
        texts.bind(modes.heading) { it.modesHeading }
        texts.bind(modes.hint) { it.modesHint }
        texts.bind(onChange, { it.onChange }) { onChange.label = it }
        texts.bind(onInput, { it.eager }) { onInput.label = it }
        onChange.listen(NumberFieldEvents.change) {
            lastChange = onChange.value
            showModes()
        }
        onInput.listen(NumberFieldEvents.input) {
            lastInput = onInput.value
            showModes()
        }
        modes.body.append(
            exampleBox(null, kitCol(kitRow(onChange, onInput), kitRow(changeReadout.root, inputReadout.root))),
        )
        body.append(modes.root)

        // --- Styles ---------------------------------------------------------------------------------
        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styled = number { it.label }
        texts.bind(styled, { it.helperText }) { styled.helperText = it }
        styled.value = "123.45"
        styled.addTheme("align-right", "small", "helper-above-field")
        styles.body.append(exampleBox(null, styled))
        body.append(styles.root)

        applyTranslation(Langs.EN)
    }

    private fun showBalance() {
        balanceReadout.set(texts.current.valuePrefix, balance.value.ifEmpty { texts.current.empty })
    }

    private fun showGuests() {
        guestsReadout.set(
            texts.current.guestsPrefix,
            listOf(adults.value, childrenField.value, infants.value).joinToString(" + "),
        )
    }

    private fun showValidation() {
        val copy = texts.current
        val message =
            when (validationKind) {
                "badInput" -> copy.badFormat
                "required" -> copy.required
                "underflow" -> copy.atLeast(quantity.min.toInt())
                "overflow" -> copy.atMost(quantity.max.toInt())
                else -> ""
            }
        quantity.errorMessage = message
        validationReadout.set(
            copy.validatedPrefix,
            if (validationValid == null) copy.notValidated else if (validationValid == true) "true" else "false",
        )
    }

    private fun showStep() {
        val copy = texts.current
        val message =
            when (stepKind) {
                "badInput" -> copy.badFormat
                "stepMismatch" -> copy.stepMultiple("${duration.step}")
                else -> ""
            }
        duration.errorMessage = message
        stepReadout.set(
            copy.validatedPrefix,
            if (stepValid == null) copy.notValidated else if (stepValid == true) "true" else "false",
        )
    }

    private fun showPrice() {
        val copy = texts.current
        val valid = priceValid
        priceResult.set(
            if (valid == null) copy.notValidated
            else copy.resultPrefix + (if (valid) copy.valid else copy.failed),
        )
    }

    private fun showModes() {
        val copy = texts.current
        changeReadout.set(copy.changePrefix, lastChange ?: copy.notFired)
        inputReadout.set(copy.inputPrefix, lastInput ?: copy.notFired)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = numberFieldEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showBalance()
        showGuests()
        showValidation()
        showStep()
        showPrice()
        showModes()
    }

    companion object : CustomElementReferenceAdapter<NumberFieldPage>(NumberFieldPage::class.js, "vaadin-demo-number-field-page") {
        private val numberFieldTexts: Translations<NumberFieldTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
