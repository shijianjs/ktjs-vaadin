package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.setHidden
import demo.ui.withTheme
import demo.tr
import vaadin.button.createButton
import vaadin.customfield.createCustomField
import vaadin.fieldhighlighter.FieldHighlighter
import vaadin.fieldhighlighter.createAiFieldMarker
import vaadin.fieldhighlighter.fieldHighlighterUser
import vaadin.integerfield.IntegerFieldEvents
import vaadin.integerfield.createIntegerField
import vaadin.numberfield.NumberFieldEvents
import vaadin.numberfield.createNumberField
import vaadin.textfield.createTextField
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

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
                            "数字输入：min / max / step、步进按钮、非法输入事件与范围校验。",
                            "Numeric input: min / max / step, step buttons, unparsable input events and range " +
                                "validation.",
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

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "value 是字符串；组件在合法数字与空值之间保持同步，非法输入触发 unparsable-change。",
                "value is a string; the field keeps it in sync with valid numbers and empty values, and " +
                    "unparsable input fires unparsable-change.",
            ),
        )
    val readout = Readout(tr("当前值：（空）", "Value: (empty)"))
    val log = EventLog(6)
    val quantity =
        createNumberField {
            label = tr("数量", "Quantity")
            min = 0.0
            max = 100.0
            step = 1.0
            stepButtonsVisible = true
            helperText = tr("0 - 100，步进 1", "0 - 100, step 1")
            errorMessage = tr("请输入 0 到 100 之间的数字", "Enter a number between 0 and 100")
        }
    quantity.dataset["test"] = "number-field-basic"
    quantity.listen(NumberFieldEvents.valueChanged) {
        readout.set(tr("当前值：", "Value: "), quantity.value.ifEmpty { tr("（空）", "(empty)") })
        log.add("value-changed · ${quantity.value}")
    }
    quantity.listen(NumberFieldEvents.unparsableChange) { log.add("unparsable-change") }

    basic.body.append(
        example(null, column(quantity, readout.root)),
        example(tr("事件流", "Event log"), log.root),
    )
    view.body.append(basic.root)

    val stepping =
        section(
            tr("步进与自定义范围校验", "Stepping and custom range validation"),
            tr(
                "step 决定步进按钮的增量。min / max 只约束步进与输入提示，不会让 validate() 自动失败，" +
                    "需要校验时按 value 自己判断并设置 invalid。",
                "step sets the increment of the step buttons. min / max only constrain stepping and input hints; " +
                    "they do not make validate() fail, so check value yourself and set invalid when you need " +
                    "validation.",
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
    stepping.body.append(example(null, column(price, row(validateButton, priceResult.root))))
    view.body.append(stepping.root)

    val states = section(tr("禁用与只读", "Disabled and read-only"), "")
    states.body.append(
        example(
            null,
            row(
                createNumberField {
                    label = tr("只读", "Read-only")
                    value = "42"
                    readonly = true
                },
                createNumberField {
                    label = tr("禁用", "Disabled")
                    value = "42"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

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

    return view
}

private fun buildCustomFieldPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("组合多个输入", "Combining inputs"),
            tr(
                "custom-field 按顺序收集插槽里的输入元素；formatValue 把输入值数组转成字段值，parseValue 反过来。",
                "custom-field collects the slotted input elements in order; formatValue turns the array of input " +
                    "values into the field value and parseValue does the reverse.",
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
    fullName.dataset["test"] = "custom-field"
    fullName.append(firstName, lastName)

    val readout = Readout(tr("字段值：（空）", "Field value: (empty)"))
    val inputCount = Readout("inputs = 0")
    fullName.listen(vaadin.customfield.CustomFieldEvents.valueChanged) {
        readout.set(tr("字段值：", "Field value: "), fullName.value.ifEmpty { tr("（空）", "(empty)") })
        inputCount.set("inputs = ${fullName.inputs?.size ?: 0}")
    }

    basic.body.append(example(null, column(fullName, row(readout.root, inputCount.root))))
    view.body.append(basic.root)

    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "custom-field 的 required 会作用到整个组合字段。",
                "required on custom-field applies to the whole combined field.",
            ),
        )
    val requiredCustom =
        createCustomField {
            label = tr("必填组合", "Required combination")
            required = true
            errorMessage = tr("请填写两项", "Fill in both inputs")
            formatValue = { values -> joinInputs(values) }
        }
    requiredCustom.append(
        createTextField { label = tr("城市", "City") },
        createTextField { label = tr("街道", "Street") },
    )
    val result = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                val status = if (requiredCustom.validate()) tr("通过", "Valid") else tr("失败", "Failed")
                result.set(tr("校验结果：", "Result: "), status)
            }
        }
    validation.body.append(example(null, column(requiredCustom, row(validateButton, result.root))))
    view.body.append(validation.root)

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
    var users = 0

    val addButton =
        createButton { textContent = tr("Alice 开始编辑", "Alice starts editing") }.apply {
            clickEvent.addHandler {
                FieldHighlighter.addUser(field, alice)
                users++
                readout.set(tr("正在编辑：", "Editing: "), if (users > 0) "Alice" else tr("无", "Nobody"))
            }
        }
    val addBobButton =
        createButton { textContent = tr("Bob 开始编辑", "Bob starts editing") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                FieldHighlighter.addUser(field, bob)
                users++
                readout.set(tr("正在编辑：", "Editing: "), tr("Alice、Bob", "Alice, Bob"))
            }
        }
    val removeButton =
        createButton { textContent = tr("全部离开", "Everyone leaves") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                FieldHighlighter.removeUser(field, alice)
                FieldHighlighter.removeUser(field, bob)
                users = 0
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
    marker.confidence = "high"
    aiField.append(marker)

    val toggleButton =
        createButton { textContent = tr("结束 AI 生成", "Finish AI generation") }.apply {
            clickEvent.addHandler {
                marker.working = !marker.working
                textContent = if (marker.working) {
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

/** Joins the input value array the way the custom-field demo expects. */
private fun joinInputs(values: Array<kotlin.js.JsAny?>): String =
    kotlin.js.js("(values) => values.filter((value) => value != null && value !== '').join(' ')")(values) as String

/** Splits a full name into the per-input values. */
private fun splitInputs(text: String): Array<kotlin.js.JsAny?> =
    kotlin.js.js("(text) => text.trim().split(/\\s+/)").let { split ->
        @Suppress("UNCHECKED_CAST")
        (split(text) as Array<kotlin.js.JsAny?>)
    }
