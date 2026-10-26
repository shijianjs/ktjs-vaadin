package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.setHidden
import demo.ui.withTheme
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
                    title = "Number Field",
                    summary = "数字输入：min / max / step、步进按钮、非法输入事件与范围校验。",
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
                    title = "Integer Field",
                    summary = "只接受整数的数字输入，继承 Number Field 的全部属性。",
                    docsUrl = "$DOCS_BASE/number-field",
                    docsNote = "官方文档没有单独的 Integer Field 页面，语义见 Number Field。",
                    sourcePath = "pages/NumberFields.kt",
                ),
            builder = ::buildIntegerFieldPage,
        ),
        CatalogEntry(
            id = "custom-field",
            group = Groups.FIELDS,
            meta =
                PageMeta(
                    title = "Custom Field",
                    summary = "把多个输入组合成单个字段，用 formatValue / parseValue 在组合值与字符串之间转换。",
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
                    title = "Field Highlighter",
                    summary = "协作编辑指示：给字段加上其他用户正在编辑的彩色轮廓与用户标签。",
                    docsUrl = DOCS_BASE,
                    docsNote = "Field Highlighter 是内部协作组件，官方文档没有独立页面。",
                    sourcePath = "pages/NumberFields.kt",
                ),
            builder = ::buildFieldHighlighterPage,
        ),
    )

private fun buildNumberFieldPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "value 是字符串；组件在合法数字与空值之间保持同步，非法输入触发 unparsable-change。")
    val readout = Readout("当前值：（空）")
    val log = EventLog(6)
    val quantity =
        createNumberField {
            label = "数量"
            min = 0.0
            max = 100.0
            step = 1.0
            stepButtonsVisible = true
            helperText = "0 - 100，步进 1"
            errorMessage = "请输入 0 到 100 之间的数字"
        }
    quantity.dataset["test"] = "number-field-basic"
    quantity.listen(NumberFieldEvents.valueChanged) {
        readout.set("当前值：", quantity.value.ifEmpty { "（空）" })
        log.add("value-changed · ${quantity.value}")
    }
    quantity.listen(NumberFieldEvents.unparsableChange) { log.add("unparsable-change") }

    basic.body.append(example(null, column(quantity, readout.root)), example("事件流", log.root))
    view.body.append(basic.root)

    val stepping =
        section(
            "步进与自定义范围校验",
            "step 决定步进按钮的增量。min / max 只约束步进与输入提示，不会让 validate() 自动失败，" +
                "需要校验时按 value 自己判断并设置 invalid。",
        )
    val price =
        createNumberField {
            label = "单价（0 - 100）"
            value = "9.5"
            step = 0.5
            min = 0.0
            max = 100.0
            stepButtonsVisible = true
            errorMessage = "单价需要在 0 到 100 之间"
        }
    price.dataset["test"] = "number-field-range"
    val priceResult = Readout("尚未校验")
    val validateButton =
        createButton { textContent = "校验" }.apply {
            clickEvent.addHandler {
                val parsed = price.value.toDoubleOrNull()
                val valid = parsed != null && parsed >= 0.0 && parsed <= 100.0
                price.invalid = !valid
                priceResult.set("校验结果：", if (valid) "通过" else "失败")
            }
        }
    stepping.body.append(example(null, column(price, row(validateButton, priceResult.root))))
    view.body.append(stepping.root)

    val states = section("禁用与只读", "")
    states.body.append(
        example(
            null,
            row(
                createNumberField {
                    label = "只读"
                    value = "42"
                    readonly = true
                },
                createNumberField {
                    label = "禁用"
                    value = "42"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view.root
}

private fun buildIntegerFieldPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "只解析整数；带小数点的输入会触发 unparsable-change 并且不更新 value。")
    val readout = Readout("当前值：（空）")
    val log = EventLog(6)
    val count =
        createIntegerField {
            label = "件数"
            step = 1.0
            min = 0.0
            stepButtonsVisible = true
        }
    count.dataset["test"] = "integer-field-basic"
    count.listen(IntegerFieldEvents.valueChanged) {
        readout.set("当前值：", count.value.ifEmpty { "（空）" })
        log.add("value-changed · ${count.value}")
    }
    count.listen(IntegerFieldEvents.unparsableChange) { log.add("unparsable-change（不是整数）") }

    basic.body.append(example(null, column(count, readout.root)), example("事件流", log.root))
    view.body.append(basic.root)

    val range = section("范围校验", "min / max 同时约束步进按钮和 validate()。")
    val age =
        createIntegerField {
            label = "年龄"
            value = "30"
            min = 18.0
            max = 120.0
            errorMessage = "年龄需要在 18 到 120 之间"
        }
    val result = Readout("尚未校验")
    val validateButton =
        createButton { textContent = "校验" }.apply {
            clickEvent.addHandler {
                result.set("校验结果：", if (age.validate()) "通过" else "失败")
            }
        }
    range.body.append(example(null, column(age, row(validateButton, result.root))))
    view.body.append(range.root)

    return view.root
}

private fun buildCustomFieldPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic =
        section(
            "组合多个输入",
            "custom-field 按顺序收集插槽里的输入元素；formatValue 把输入值数组转成字段值，parseValue 反过来。",
        )

    val firstName =
        createTextField {
            label = "名"
            placeholder = "Ada"
        }
    val lastName =
        createTextField {
            label = "姓"
            placeholder = "Lovelace"
        }

    val fullName =
        createCustomField {
            label = "全名"
            helperText = "由两个文本输入组合而成"
            formatValue = { values -> joinInputs(values) }
            parseValue = { text -> splitInputs(text) }
        }
    fullName.dataset["test"] = "custom-field"
    fullName.append(firstName, lastName)

    val readout = Readout("字段值：（空）")
    val inputCount = Readout("inputs = 0")
    fullName.listen(vaadin.customfield.CustomFieldEvents.valueChanged) {
        readout.set("字段值：", fullName.value.ifEmpty { "（空）" })
        inputCount.set("inputs = ${fullName.inputs?.size ?: 0}")
    }

    basic.body.append(example(null, column(fullName, row(readout.root, inputCount.root))))
    view.body.append(basic.root)

    val validation = section("校验", "custom-field 的 required 会作用到整个组合字段。")
    val requiredCustom =
        createCustomField {
            label = "必填组合"
            required = true
            errorMessage = "请填写两项"
            formatValue = { values -> joinInputs(values) }
        }
    requiredCustom.append(
        createTextField { label = "城市" },
        createTextField { label = "街道" },
    )
    val result = Readout("尚未校验")
    val validateButton =
        createButton { textContent = "校验" }.apply {
            clickEvent.addHandler {
                result.set("校验结果：", if (requiredCustom.validate()) "通过" else "失败")
            }
        }
    validation.body.append(example(null, column(requiredCustom, row(validateButton, result.root))))
    view.body.append(validation.root)

    return view.root
}

private fun buildFieldHighlighterPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val collab =
        section(
            "协作用户指示",
            "FieldHighlighter.addUser 让字段显示其他用户正在编辑；焦点进入字段时还会出现用户标签。",
        )
    val field =
        createTextField {
            label = "共享标题"
            value = "季度报告草稿"
            helperText = "下面的按钮模拟其他用户开始 / 结束编辑"
        }
    field.dataset["test"] = "highlighted-field"
    val alice = fieldHighlighterUser(1.0, "Alice", 0.0)
    val bob = fieldHighlighterUser(2.0, "Bob", 1.0)
    val readout = Readout("当前没有其他用户")
    var users = 0

    val addButton =
        createButton { textContent = "Alice 开始编辑" }.apply {
            clickEvent.addHandler {
                FieldHighlighter.addUser(field, alice)
                users++
                readout.set("正在编辑：", if (users > 0) "Alice" else "无")
            }
        }
    val addBobButton =
        createButton { textContent = "Bob 开始编辑" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                FieldHighlighter.addUser(field, bob)
                users++
                readout.set("正在编辑：", "Alice、Bob")
            }
        }
    val removeButton =
        createButton { textContent = "全部离开" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                FieldHighlighter.removeUser(field, alice)
                FieldHighlighter.removeUser(field, bob)
                users = 0
                readout.set("正在编辑：", "无")
            }
        }

    collab.body.append(example(null, column(field, row(addButton, addBobButton, removeButton), readout.root)))
    view.body.append(collab.root)

    val ai = section("AI 标记", "ai-field-marker 是字段的直接子节点：working 时显示进度，confidence 显示置信度。")
    val aiField =
        createTextField {
            label = "AI 填充的字段"
            value = "自动摘要……"
        }
    val marker = createAiFieldMarker { working = true }
    marker.confidence = "high"
    aiField.append(marker)

    val toggleButton =
        createButton { textContent = "结束 AI 生成" }.apply {
            clickEvent.addHandler {
                marker.working = !marker.working
                textContent = if (marker.working) "结束 AI 生成" else "重新开始生成"
            }
        }
    ai.body.append(example(null, column(aiField, toggleButton)))
    view.body.append(ai.root)

    return view.root
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
