package demo.pages.fields

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
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.formlayout.createFormLayout
import vaadin.integerfield.IntegerFieldEvents
import vaadin.integerfield.createIntegerField
import web.dom.clickEvent
import web.events.addHandler

val integerFieldEntry: CatalogEntry =
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
                sourcePath = "pages/fields/IntegerFieldPage.kt",
            ),
        builder = ::buildIntegerFieldPage,
    )

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
