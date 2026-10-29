package demo.pages.fields

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.data.message
import demo.tr
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
import vaadin.button.createButton
import vaadin.formlayout.createFormItem
import vaadin.formlayout.createFormLayout
import vaadin.icon.createIcon
import vaadin.integerfield.IntegerFieldEvents
import vaadin.integerfield.createIntegerField
import vaadin.numberfield.NumberFieldEvents
import vaadin.numberfield.createNumberField
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler
import web.html.HtmlTagName

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
        builder = ::buildNumberFieldPage,
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
