package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.element
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.button.createButton
import vaadin.customfield.CustomFieldEvents
import vaadin.customfield.createCustomField
import vaadin.datepicker.createDatePicker
import vaadin.fieldbase.InputMixin
import vaadin.fieldhighlighter.AiFieldMarkerConfidences
import vaadin.fieldhighlighter.FieldHighlighter
import vaadin.fieldhighlighter.createAiFieldMarker
import vaadin.fieldhighlighter.fieldHighlighterUser
import vaadin.formlayout.createFormItem
import vaadin.formlayout.createFormLayout
import vaadin.icon.createIcon
import vaadin.integerfield.IntegerFieldEvents
import vaadin.integerfield.createIntegerField
import vaadin.numberfield.NumberFieldEvents
import vaadin.numberfield.createNumberField
import vaadin.select.SelectItemData
import vaadin.select.createSelect
import vaadin.textfield.createTextField
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLInputElement
import web.html.HTMLElement
import web.html.HtmlTagName
import web.validation.ValidityState

val numberFieldEntries: List<CatalogEntry> =
    listOf(
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
                    sourcePath = "pages/NumberFields.kt",
                ),
            builder = ::buildNumberFieldPage,
        ),
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
                    docsUrl = "$DOCS_BASE/number-field",
                    docsNote =
                        bi(
                            "官方文档没有单独的 Integer Field 页面，语义见 Number Field。",
                            "There is no dedicated Integer Field page in the official documentation; see " +
                                "Number Field for the semantics.",
                        ),
                    sourcePath = "pages/NumberFields.kt",
                ),
            builder = ::buildIntegerFieldPage,
        ),
        CatalogEntry(
            id = "custom-field",
            group = Groups.FIELDS,
            meta =
                PageMeta(
                    title = bi("Custom Field", "Custom Field"),
                    summary =
                        bi(
                            "把多个输入组合成单个字段，用 formatValue / parseValue 在组合值与字符串之间转换。",
                            "Combines several inputs into one field, converting between the combined value and a " +
                                "string with formatValue / parseValue.",
                        ),
                    docsUrl = "$DOCS_BASE/custom-field",
                    sourcePath = "pages/NumberFields.kt",
                ),
            builder = ::buildCustomFieldPage,
        ),
        CatalogEntry(
            id = "field-highlighter",
            group = Groups.FIELDS,
            meta =
                PageMeta(
                    title = bi("Field Highlighter", "Field Highlighter"),
                    summary =
                        bi(
                            "协作编辑指示：给字段加上其他用户正在编辑的彩色轮廓与用户标签。",
                            "Collaboration indicator: adds a coloured outline and a user tag for other people " +
                                "editing the field.",
                        ),
                    docsUrl = DOCS_BASE,
                    docsNote =
                        bi(
                            "Field Highlighter 是内部协作组件，官方文档没有独立页面。",
                            "Field Highlighter is an internal collaboration component without a dedicated " +
                                "documentation page.",
                        ),
                    sourcePath = "pages/NumberFields.kt",
                ),
            builder = ::buildFieldHighlighterPage,
        ),
    )

private fun buildNumberFieldPage(meta: PageMeta): PageView {
    val view = page(meta)

    // --- Basic ----------------------------------------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "value 始终是字符串；前缀 / 后缀插槽放货币符号这类固定内容，" +
                    "数字解析失败时触发 unparsable-change 而不是更新 value。",
                "value is always a string; the prefix / suffix slots hold fixed content such as currency " +
                    "signs, and unparsable input fires unparsable-change instead of updating value.",
            ),
        )
    val balance =
        createNumberField {
            label = tr("余额", "Balance")
            value = "200"
        }
    balance.dataset["test"] = "number-field-basic"
    balance.append(span("$").apply { slot = "prefix" })
    val balanceEur =
        createNumberField {
            label = tr("余额", "Balance")
            value = "200"
        }
    balanceEur.append(span("€").apply { slot = "suffix" })
    val balanceReadout = Readout(tr("当前值：200", "Value: 200"))
    balance.listen(NumberFieldEvents.valueChanged) {
        balanceReadout.set(tr("当前值：", "Value: "), balance.value.ifEmpty { tr("（空）", "(empty)") })
    }
    val basicLayout =
        createFormLayout {
            autoResponsive = true
            autoRows = true
        }
    basicLayout.append(balance, balanceEur)
    basic.body.append(example(null, column(basicLayout, balanceReadout.root)))
    view.body.append(basic.root)

    // --- Step buttons ---------------------------------------------------------------------------
    val stepButtons =
        section(
            tr("步进按钮", "Step buttons"),
            tr(
                "step-buttons-visible 在字段两侧加上减 / 加按钮，min 与 max 同时限制按钮的可用状态。",
                "step-buttons-visible adds decrement / increment buttons; min and max also bound them.",
            ),
        )
    val adults =
        createIntegerField {
            value = "2"
            min = 0.0
            max = 9.0
            stepButtonsVisible = true
        }
    val children =
        createIntegerField {
            value = "2"
            min = 0.0
            max = 9.0
            stepButtonsVisible = true
        }
    val infants =
        createIntegerField {
            value = "1"
            min = 0.0
            max = 9.0
            stepButtonsVisible = true
        }
    val guestsReadout = Readout("2 + 2 + 1")
    fun syncGuests() {
        guestsReadout.set(
            tr("客人：", "Guests: "),
            listOf(adults.value, children.value, infants.value).joinToString(" + "),
        )
    }
    listOf(adults, children, infants).forEach { field ->
        field.listen(IntegerFieldEvents.valueChanged) { syncGuests() }
    }
    val adultsItem = createFormItem()
    adultsItem.append(element(HtmlTagName.label).apply { slot = "label"; textContent = tr("成人", "Adults") }, adults)
    val childrenItem = createFormItem()
    childrenItem.append(
        element(HtmlTagName.label).apply {
            slot = "label"
            append(div("small", span(tr("儿童", "Children"))), div("small muted", span(tr("2-12 岁", "Age 2-12"))))
        },
        children,
    )
    val infantsItem = createFormItem()
    infantsItem.append(
        element(HtmlTagName.label).apply { slot = "label"; textContent = tr("婴儿", "Infants") },
        infants,
    )
    val guestLayout =
        createFormLayout {
            labelsAside = true
            autoResponsive = true
        }
    guestLayout.append(adultsItem, childrenItem, infantsItem)
    stepButtons.body.append(example(null, column(guestLayout, guestsReadout.root)))
    view.body.append(stepButtons.root)

    // --- Validation -----------------------------------------------------------------------------
    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "validated 事件没有 detail；上游示例读取 inputElement.validity 的原生约束状态来区分" +
                    "「格式错误 / 必填缺失 / 低于下限 / 高于上限」，再据此改写 errorMessage。",
                "The validated event carries no detail; the upstream example reads the native constraint " +
                    "state of inputElement.validity to tell \"bad format / missing / below minimum / above " +
                    "maximum\" apart and rewrites errorMessage accordingly.",
            ),
        )
    val quantity =
        createIntegerField {
            label = tr("数量", "Quantity")
            helperText = tr("最多 10 件", "Max 10 items")
            required = true
            min = 1.0
            max = 10.0
            value = "2"
            stepButtonsVisible = true
        }
    quantity.dataset["test"] = "number-field-validation"
    val validationReadout = Readout(tr("尚未校验", "Not validated yet"))
    quantity.listen(IntegerFieldEvents.validated) {
        val validity = validityOf(quantity)
        val message =
            when {
                validity.badInput -> tr("数字格式不正确", "Invalid number format")
                validity.valueMissing -> tr("这一项必填", "Field is required")
                validity.rangeUnderflow -> tr("至少 ${quantity.min.toInt()} 件", "Quantity must be at least ${quantity.min.toInt()}")
                validity.rangeOverflow -> tr("最多 ${quantity.max.toInt()} 件", "Maximum ${quantity.max.toInt()} items available")
                else -> ""
            }
        quantity.errorMessage = message
        validationReadout.set(tr("validated · valid = ", "validated · valid = "), if (validity.valid) "true" else "false")
    }
    validation.body.append(example(null, column(quantity, validationReadout.root)))
    view.body.append(validation.root)

    // --- Step -----------------------------------------------------------------------------------
    val fractional =
        section(
            tr("步进小数", "Fractional step"),
            tr(
                "step 同时决定步进按钮的增量与原生 validity：不是 step 的整数倍时 validity.stepMismatch 为真。",
                "step drives both the step buttons and the native validity: a value that is not a multiple " +
                    "of step sets validity.stepMismatch.",
            ),
        )
    val duration =
        createNumberField {
            label = tr("时长（小时）", "Duration (hours)")
            step = 0.5
            value = "12.5"
            stepButtonsVisible = true
        }
    duration.dataset["test"] = "number-field-step"
    val stepReadout = Readout(tr("尚未校验", "Not validated yet"))
    duration.listen(NumberFieldEvents.validated) {
        val validity = validityOf(duration)
        val message =
            when {
                validity.badInput -> tr("数字格式不正确", "Invalid number format")
                validity.stepMismatch -> tr("时长必须是 ${duration.step} 的整数倍", "Duration must be a multiple of ${duration.step}")
                else -> ""
            }
        duration.errorMessage = message
        stepReadout.set(tr("validated · valid = ", "validated · valid = "), if (validity.valid) "true" else "false")
    }
    fractional.body.append(example(null, column(duration, stepReadout.root)))
    view.body.append(fractional.root)

    // --- Number type variants -------------------------------------------------------------------
    val variants =
        section(
            tr("数字类型变体", "Number type variants"),
            tr(
                "Integer Field 只解析整数，用于件数、年龄这类数量；BigDecimal 用例只存在于 Java API" +
                    "（Flow 的 NumberField 继承 BigDecimalField），Web Component 侧没有对应元素，小数直接用 Number Field。",
                "Integer Field only parses integers, for counts and ages; the BigDecimal example exists only " +
                    "in the Java API (Flow's NumberField extends BigDecimalField). The web component has no " +
                    "counterpart, so decimals use Number Field directly.",
            ),
        )
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
    variants.body.append(example(null, variantLayout))
    view.body.append(variants.root)

    // --- Basic features -------------------------------------------------------------------------
    val features =
        section(
            tr("基本特性", "Basic features"),
            tr(
                "label / helper-text / placeholder 与清除按钮、工具提示插槽、前缀后缀插槽可以同时使用。",
                "label / helper-text / placeholder combine with the clear button, the tooltip slot and the " +
                    "prefix / suffix slots.",
            ),
        )
    val featured =
        createNumberField {
            label = tr("金额", "Amount")
            helperText = tr("说明文字", "Helper text")
            placeholder = tr("占位文字", "Placeholder")
            clearButtonVisible = true
        }
    featured.dataset["test"] = "number-field-features"
    featured.append(
        createTooltip { text = tr("提示文本", "Tooltip text") }.apply { slot = "tooltip" },
        span("$").apply { slot = "prefix" },
        createIcon { icon = "vaadin:dollar" }.apply { slot = "suffix" },
    )
    features.body.append(example(null, featured))
    view.body.append(features.root)

    // --- Read-only and disabled -----------------------------------------------------------------
    val states =
        section(
            tr("只读与禁用", "Read-only and disabled"),
            tr(
                "readonly 仍可聚焦与复制，disabled 让字段完全不可交互。",
                "readonly keeps the field focusable and copyable, disabled removes all interaction.",
            ),
        )
    val statesLayout =
        createFormLayout {
            autoResponsive = true
            autoRows = true
        }
    statesLayout.append(
        createNumberField {
            label = tr("只读", "Read-only")
            value = "200"
            readonly = true
        },
        createNumberField {
            label = tr("禁用", "Disabled")
            disabled = true
        },
    )
    states.body.append(example(null, statesLayout))
    view.body.append(states.root)

    // --- Custom range validation ----------------------------------------------------------------
    val range =
        section(
            tr("自定义范围校验", "Custom range validation"),
            tr(
                "min / max 只约束步进与输入提示，不会让 validate() 自动失败；" +
                    "需要把范围当业务规则时，按 value 自己判断并设置 invalid（服务端 Binder 也是这么做的）。",
                "min / max only constrain stepping and input hints, they do not make validate() fail. When the " +
                    "range is a business rule, check value yourself and set invalid — the same thing Binder " +
                    "does on the server.",
            ),
        )
    val price =
        createNumberField {
            label = tr("单价（0 - 100）", "Unit price (0 - 100)")
            value = "9.5"
            step = 0.5
            min = 0.0
            max = 100.0
            stepButtonsVisible = true
            errorMessage = tr("单价需要在 0 到 100 之间", "The unit price must be between 0 and 100")
        }
    price.dataset["test"] = "number-field-range"
    val priceResult = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                val parsed = price.value.toDoubleOrNull()
                val valid = parsed != null && parsed >= 0.0 && parsed <= 100.0
                price.invalid = !valid
                priceResult.set(tr("校验结果：", "Result: "), if (valid) tr("通过", "Valid") else tr("失败", "Failed"))
            }
        }
    range.body.append(example(null, column(price, row(validateButton, priceResult.root))))
    view.body.append(range.root)

    // --- Value change modes ---------------------------------------------------------------------
    val modes =
        section(
            tr("值变更模式", "Value change modes"),
            tr(
                "on change / eager / lazy / timeout 是服务端 ValueChangeMode 的概念，浏览器端对应" +
                    "「监听 change 事件」与「监听 input 事件」两种时机；lazy 与 timeout 的防抖 / 轮询由服务端计时器实现。",
                "on change / eager / lazy / timeout are server-side ValueChangeMode concepts. In the browser " +
                    "they reduce to listening to change versus input; lazy and timeout debouncing / polling " +
                    "is implemented by server-side timers.",
            ),
        )
    val onChange =
        createNumberField {
            label = tr("on change（默认）", "on change (default)")
            value = "0"
        }
    val onInput =
        createNumberField {
            label = "eager"
            value = "0"
        }
    val changeReadout = Readout(tr("change：尚未触发", "change: not fired yet"))
    val inputReadout = Readout(tr("input：尚未触发", "input: not fired yet"))
    onChange.listen(NumberFieldEvents.change) {
        changeReadout.set(tr("change：", "change: "), onChange.value)
    }
    onInput.listen(NumberFieldEvents.input) {
        inputReadout.set(tr("input：", "input: "), onInput.value)
    }
    modes.body.append(example(null, column(row(onChange, onInput), row(changeReadout.root, inputReadout.root))))
    view.body.append(modes.root)

    // --- Styles ---------------------------------------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Styles"),
            tr(
                "align-right 让数字右对齐（数位对齐），small 收紧尺寸，helper-above-field 把说明文字移到字段上方。",
                "align-right right-aligns the digits, small tightens the field and helper-above-field moves the " +
                    "helper text above it.",
            ),
        )
    styles.body.append(
        example(
            null,
            createNumberField {
                label = tr("标签", "Label")
                helperText = tr("说明文字", "Helper text")
                value = "123.45"
            }.withTheme("align-right", "small", "helper-above-field"),
        ),
    )
    view.body.append(styles.root)

    return view
}

private fun buildIntegerFieldPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "只解析整数；带小数点的输入会触发 unparsable-change 并且不更新 value。",
                "Only integers are parsed; input with a decimal point fires unparsable-change and leaves " +
                    "value unchanged.",
            ),
        )
    val readout = Readout(tr("当前值：（空）", "Value: (empty)"))
    val log = EventLog(6)
    val count =
        createIntegerField {
            label = tr("件数", "Item count")
            step = 1.0
            min = 0.0
            stepButtonsVisible = true
        }
    count.dataset["test"] = "integer-field-basic"
    count.listen(IntegerFieldEvents.valueChanged) {
        readout.set(tr("当前值：", "Value: "), count.value.ifEmpty { tr("（空）", "(empty)") })
        log.add("value-changed · ${count.value}")
    }
    count.listen(IntegerFieldEvents.unparsableChange) {
        log.add(tr("unparsable-change（不是整数）", "unparsable-change (not an integer)"))
    }

    basic.body.append(
        example(null, column(count, readout.root)),
        example(tr("事件流", "Event log"), log.root),
    )
    view.body.append(basic.root)

    val variants =
        section(
            tr("取值示例", "Value examples"),
            tr(
                "Integer Field 的 value 同样是字符串，负整数与多位数都直接以十进制文本读写。",
                "value is a string here too; negative and multi-digit integers are read and written as plain " +
                    "decimal text.",
            ),
        )
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
    variants.body.append(example(null, variantLayout))
    view.body.append(variants.root)

    val range =
        section(
            tr("范围校验", "Range validation"),
            tr("min / max 同时约束步进按钮和 validate()。", "min / max constrain both the step buttons and validate()."),
        )
    val age =
        createIntegerField {
            label = tr("年龄", "Age")
            value = "30"
            min = 18.0
            max = 120.0
            errorMessage = tr("年龄需要在 18 到 120 之间", "Age must be between 18 and 120")
        }
    val result = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                result.set(tr("校验结果：", "Result: "), if (age.validate()) tr("通过", "Valid") else tr("失败", "Failed"))
            }
        }
    range.body.append(example(null, column(age, row(validateButton, result.root))))
    view.body.append(range.root)

    val styles =
        section(
            tr("样式变体", "Styles"),
            tr(
                "与 Number Field 共用同一套主题变体。",
                "Shares the same theme variants as Number Field.",
            ),
        )
    styles.body.append(
        example(
            null,
            createIntegerField {
                label = tr("数量", "Quantity")
                helperText = tr("说明文字", "Helper text")
                value = "42"
            }.withTheme("align-right", "small", "helper-above-field"),
        ),
    )
    view.body.append(styles.root)

    return view
}

private fun buildCustomFieldPage(meta: PageMeta): PageView {
    val view = page(meta)

    // --- Basic: enrollment period ---------------------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "custom-field 按顺序收集插槽里的输入组件，formatValue 把它们的值数组拼成一个字段值，" +
                    "parseValue 再拆回去。下面的报名周期由两个 DatePicker 组成，页面复现文档里 Binder 的两条校验器：" +
                    "跨度不超过 30 天、开始日期早于结束日期。",
                "custom-field collects the slotted inputs in order; formatValue joins the array of input values " +
                    "into the field value and parseValue splits it back. The enrollment period below combines two " +
                    "date pickers and reproduces the two validators the documentation adds through Binder: the " +
                    "span cannot exceed 30 days and the start date must be earlier than the end date.",
            ),
        )
    val period =
        createCustomField {
            label = tr("报名周期", "Enrollment period")
            helperText = tr("跨度不能超过 30 天", "Cannot be longer than 30 days")
            required = true
            formatValue = { values -> joinWithTab(values) }
            parseValue = { text -> splitByTab(text) }
        }
    period.dataset["test"] = "custom-field"
    val startDate =
        createDatePicker {
            accessibleName = tr("开始日期", "Start date")
            placeholder = tr("开始日期", "Start date")
        }
    val endDate =
        createDatePicker {
            accessibleName = tr("结束日期", "End date")
            placeholder = tr("结束日期", "End date")
        }
    period.append(startDate, span("–"), endDate)

    val periodReadout = Readout(tr("字段值：（空）", "Field value: (empty)"))

    fun checkPeriod() {
        val (from, to) = tabParts(period.value)
        val message =
            when {
                from.isEmpty() || to.isEmpty() -> ""
                else -> {
                    val fromDay = isoDay(from)
                    val toDay = isoDay(to)
                    when {
                        fromDay.isNaN() || toDay.isNaN() -> ""
                        toDay <= fromDay ->
                            tr("开始日期必须早于结束日期", "Start date must be earlier than end date")
                        toDay - fromDay > 30 * DAY_MILLIS ->
                            tr("日期跨度不能超过 30 天", "Dates cannot be more than 30 days apart")
                        else -> ""
                    }
                }
            }
        period.errorMessage = message
        period.invalid = message.isNotEmpty()
        periodReadout.set(tr("字段值：", "Field value: "), if (period.value.isEmpty()) tr("（空）", "(empty)") else period.value.replace('\t', ' '))
    }
    period.listen(CustomFieldEvents.valueChanged) { checkPeriod() }
    val periodValidate =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler { checkPeriod() }
        }
    basic.body.append(example(null, column(period, row(periodValidate, periodReadout.root))))
    view.body.append(basic.root)

    // --- Formatting the combined value ----------------------------------------------------------
    val formatting =
        section(
            tr("组合值与格式化", "Combined value and formatting"),
            tr(
                "formatValue 决定 value 的拼法，parseValue 负责把已有值拆回各个输入；" +
                    "两者都收 / 发字符串数组，字段本身始终只暴露一个 value。",
                "formatValue decides how value is assembled and parseValue splits an existing value back into " +
                    "the inputs; both take or return the array of strings, while the field still exposes a " +
                    "single value.",
            ),
        )
    val firstName =
        createTextField {
            label = tr("名", "First name")
            placeholder = "Ada"
        }
    val lastName =
        createTextField {
            label = tr("姓", "Last name")
            placeholder = "Lovelace"
        }
    val fullName =
        createCustomField {
            label = tr("全名", "Full name")
            helperText = tr("由两个文本输入组合而成", "Combined from two text inputs")
            formatValue = { values -> joinInputs(values) }
            parseValue = { text -> splitInputs(text) }
        }
    fullName.dataset["test"] = "custom-field-format"
    fullName.append(firstName, lastName)
    val nameReadout = Readout(tr("字段值：（空）", "Field value: (empty)"))
    val inputCount = Readout("inputs = 0")
    fullName.listen(CustomFieldEvents.valueChanged) {
        nameReadout.set(tr("字段值：", "Field value: "), fullName.value.ifEmpty { tr("（空）", "(empty)") })
        inputCount.set("inputs = ${fullName.inputs?.size ?: 0}")
    }
    formatting.body.append(example(null, column(fullName, row(nameReadout.root, inputCount.root))))
    view.body.append(formatting.root)

    // --- Native input fields --------------------------------------------------------------------
    val native =
        section(
            tr("原生输入字段", "Native input fields"),
            tr(
                "插槽里可以放任意原生输入：组件用 value 把它们按顺序拼起来，pattern / required 仍由浏览器校验。" +
                    "下面的三个 input 不需要任何 Vaadin 字段包装。",
                "Any native input can be slotted: the field joins their values in order and the browser still " +
                    "enforces pattern / required. The three inputs below need no Vaadin field wrapper.",
            ),
        )
    val payment =
        createCustomField {
            label = tr("付款信息", "Payment information")
        }
    payment.dataset["test"] = "custom-field-native"
    val cardholder = nativeInput(tr("持卡人姓名", "Cardholder name"), """[\p{L} \-]+""")
    val cardNumber = nativeInput(tr("卡号", "Card number"), """[\d ]{12,23}""")
    val securityCode = nativeInput(tr("安全码", "Security code"), """[0-9]{3,4}""")
    payment.append(row(cardholder, cardNumber, securityCode))
    val paymentReadout = Readout(tr("付款信息：（空）", "Payment information: (empty)"))
    payment.listen(CustomFieldEvents.change) {
        paymentReadout.set(
            tr("付款信息：", "Payment information: "),
            payment.value.replace("\t", " · ").ifEmpty { tr("（空）", "(empty)") },
        )
    }
    native.body.append(example(null, column(payment, paymentReadout.root)))
    view.body.append(native.root)

    // --- Styles ---------------------------------------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Styles"),
            tr(
                "small 与 helper-above-field 作用在整个组合字段上；内部字段各自也要 small，尺寸才一致。",
                "small and helper-above-field apply to the whole combined field; the inner fields need small " +
                    "as well so the sizes match.",
            ),
        )
    val price =
        createCustomField {
            label = tr("价格", "Price")
            helperText = tr("说明文字", "Helper text")
        }.withTheme("small", "helper-above-field")
    price.dataset["test"] = "custom-field-styles"
    val amount =
        createTextField {
            accessibleName = tr("金额", "Amount")
        }.withTheme("small")
    val currency =
        createSelect {
            accessibleName = tr("币种", "Currency")
            items =
                arrayOf(
                    unsafeJso<SelectItemData> { label = "AUD"; value = "aud" },
                    unsafeJso<SelectItemData> { label = "CAD"; value = "cad" },
                    unsafeJso<SelectItemData> { label = "CHF"; value = "chf" },
                    unsafeJso<SelectItemData> { label = "EUR"; value = "eur" },
                    unsafeJso<SelectItemData> { label = "GBP"; value = "gbp" },
                    unsafeJso<SelectItemData> { label = "JPY"; value = "jpy" },
                    unsafeJso<SelectItemData> { label = "USD"; value = "usd" },
                )
            value = "usd"
        }.withTheme("small")
    currency.style.setProperty("width", "6em")
    price.append(row(amount, currency))
    styles.body.append(example(null, price))
    view.body.append(styles.root)

    return view
}

private fun buildFieldHighlighterPage(meta: PageMeta): PageView {
    val view = page(meta)

    val collab =
        section(
            tr("协作用户指示", "Collaboration indicators"),
            tr(
                "FieldHighlighter.addUser 让字段显示其他用户正在编辑；焦点进入字段时还会出现用户标签。",
                "FieldHighlighter.addUser marks the field as edited by another user; focusing the field also " +
                    "shows a user tag.",
            ),
        )
    val field =
        createTextField {
            label = tr("共享标题", "Shared title")
            value = tr("季度报告草稿", "Quarterly report draft")
            helperText = tr("下面的按钮模拟其他用户开始 / 结束编辑", "The buttons below simulate other users joining and leaving")
        }
    field.dataset["test"] = "highlighted-field"
    val alice = fieldHighlighterUser(1.0, "Alice", 0.0)
    val bob = fieldHighlighterUser(2.0, "Bob", 1.0)
    val readout = Readout(tr("当前没有其他用户", "No other users right now"))

    val addButton =
        createButton { textContent = tr("Alice 开始编辑", "Alice starts editing") }.apply {
            clickEvent.addHandler {
                FieldHighlighter.addUser(field, alice)
                readout.set(tr("正在编辑：", "Editing: "), "Alice")
            }
        }
    val addBobButton =
        createButton { textContent = tr("Bob 开始编辑", "Bob starts editing") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                FieldHighlighter.addUser(field, bob)
                readout.set(tr("正在编辑：", "Editing: "), tr("Alice、Bob", "Alice, Bob"))
            }
        }
    val removeButton =
        createButton { textContent = tr("全部离开", "Everyone leaves") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                FieldHighlighter.removeUser(field, alice)
                FieldHighlighter.removeUser(field, bob)
                readout.set(tr("正在编辑：", "Editing: "), tr("无", "Nobody"))
            }
        }

    collab.body.append(example(null, column(field, row(addButton, addBobButton, removeButton), readout.root)))
    view.body.append(collab.root)

    val ai =
        section(
            tr("AI 标记", "AI marker"),
            tr(
                "ai-field-marker 是字段的直接子节点：working 时显示进度，confidence 显示置信度。",
                "ai-field-marker is a direct child of the field: working shows progress and confidence shows " +
                    "the confidence level.",
            ),
        )
    val aiField =
        createTextField {
            label = tr("AI 填充的字段", "AI-filled field")
            value = tr("自动摘要……", "Automatic summary…")
        }
    val marker = createAiFieldMarker { working = true }
    marker.confidence = AiFieldMarkerConfidences.high
    aiField.append(marker)

    val toggleButton =
        createButton { textContent = tr("结束 AI 生成", "Finish AI generation") }.apply {
            clickEvent.addHandler {
                marker.working = !marker.working
                textContent =
                    if (marker.working) {
                        tr("结束 AI 生成", "Finish AI generation")
                    } else {
                        tr("重新开始生成", "Restart generation")
                    }
            }
        }
    ai.body.append(example(null, column(aiField, toggleButton)))
    view.body.append(ai.root)

    return view
}

private const val DAY_MILLIS: Double = 86_400_000.0

/** Reads the native constraint state of the `<input>` the field controls. */
private fun validityOf(field: InputMixin): ValidityState =
    field.inputElement.unsafeCast<HTMLInputElement>().validity

/** Epoch milliseconds of an ISO `yyyy-MM-dd` string; `NaN` when the text is not a date. */
private fun isoDay(text: String): Double =
    js("(text) => Date.parse(text + 'T00:00:00Z')")(text) as Double

/** Splits a custom-field value into its first two tab-separated parts. */
private fun tabParts(value: String): Pair<String, String> {
    val index = value.indexOf('\t')
    return if (index < 0) value to "" else value.substring(0, index) to value.substring(index + 1)
}

/** Joins the input value array with tabs, the separator the date range format uses. */
private fun joinWithTab(values: Array<JsAny?>): String =
    js("(values) => values.join('\\t')")(values) as String

/** Splits a tab-separated field value back into per-input values. */
private fun splitByTab(text: String): Array<JsAny?> =
    (js("(text) => String(text || '').split('\\t')")(text) as Array<JsAny?>)

/** Joins the input value array the way the custom-field demo expects. */
private fun joinInputs(values: Array<JsAny?>): String =
    js("(values) => values.filter((value) => value != null && value !== '').join(' ')")(values) as String

/** Splits a full name into the per-input values. */
private fun splitInputs(text: String): Array<JsAny?> =
    js("(text) => text.trim().split(/\\s+/)").let { split ->
        @Suppress("UNCHECKED_CAST")
        (split(text) as Array<JsAny?>)
    }

/** Creates a bare `<input>` with the attributes the native-input example relies on. */
private fun nativeInput(placeholder: String, pattern: String): HTMLInputElement =
    element(HtmlTagName.input).apply {
        setAttribute("type", "text")
        setAttribute("aria-label", placeholder)
        setAttribute("placeholder", placeholder)
        setAttribute("pattern", pattern)
        required = true
        style.setProperty("width", "9rem")
    }
