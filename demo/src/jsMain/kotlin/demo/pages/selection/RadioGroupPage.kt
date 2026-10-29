package demo.pages.selection

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.tr
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
import demo.ui.span
import demo.ui.withClass
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.checkbox.createCheckbox
import vaadin.radiogroup.RadioGroupEvents
import vaadin.radiogroup.createRadioButton
import vaadin.radiogroup.createRadioGroup
import vaadin.textfield.createTextField
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val radioGroupEntry: CatalogEntry =
    CatalogEntry(
        id = "radio-group",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Radio Group", "Radio Group"),
                summary =
                    bi(
                        "单选组：value 是字符串，子项由 vaadin-radio-button 提供。",
                        "Single-select group: value is a string and the items are vaadin-radio-button " +
                            "elements.",
                    ),
                docsUrl = "$DOCS_BASE/radio-button",
                docsNote =
                    bi(
                        "官方文档的 Radio Button 页面同时覆盖 Radio Group。",
                        "The Radio Button documentation page also covers Radio Group.",
                    ),
                sourcePath = "pages/selection/RadioGroupPage.kt",
            ),
        builder = ::buildRadioGroupPage,
    )

private fun buildRadioGroupPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "同一 radio-group 内的 radio-button 互斥；value 是选中的字符串。",
                "radio-buttons inside one radio-group are mutually exclusive; value is the selected string.",
            ),
        )
    val group =
        createRadioGroup {
            label = tr("套餐", "Plan")
            value = "standard"
        }.withTheme("vertical")
    group.dataset["test"] = "radio-group"
    group.append(
        createRadioButton {
            value = "basic"
            textContent = tr("基础版", "Basic")
        },
        createRadioButton {
            value = "standard"
            textContent = tr("标准版", "Standard")
        },
        createRadioButton {
            value = "pro"
            textContent = tr("专业版", "Pro")
        },
    )
    val readout = Readout("value = standard")
    group.listen(RadioGroupEvents.valueChanged) {
        readout.set("value = ", group.value.ifEmpty { tr("（未选择）", "(none)") })
    }
    basic.body.append(example(null, column(group, readout.root)))
    view.body.append(basic.root)

    val defaults =
        section(
            tr("默认值与分组标签", "Default value and group labels"),
            tr(
                "给组赋初始 value 就有默认选中项；同一页面上的多组互不影响，各自的 label 说明不同维度。",
                "Assigning an initial value selects a default option; several groups on one page stay " +
                    "independent and their labels name different dimensions.",
            ),
        )
    val repeat = createRadioGroup { label = tr("重复", "Repeat") }.withTheme("vertical")
    repeat.append(
        createRadioButton {
            value = "none"
            textContent = tr("从不", "None")
            checked = true
        },
        createRadioButton {
            value = "daily"
            textContent = tr("每日", "Daily")
        },
        createRadioButton {
            value = "weekly"
            textContent = tr("每周", "Weekly")
        },
        createRadioButton {
            value = "monthly"
            textContent = tr("每月", "Monthly")
        },
    )
    defaults.body.append(example(null, repeat))
    view.body.append(defaults.root)

    val orientation =
        section(
            tr("方向", "Orientation"),
            tr(
                "theme=\"horizontal\" 让选项横排，vertical 让选项竖排。",
                "theme=\"horizontal\" lays the options out in a row, vertical stacks them.",
            ),
        )
    val horizontal =
        createRadioGroup {
            label = tr("状态", "Status")
            value = "pending"
        }.withTheme("horizontal")
    horizontal.append(
        createRadioButton {
            value = "pending"
            textContent = tr("待处理", "Pending")
        },
        createRadioButton {
            value = "submitted"
            textContent = tr("已提交", "Submitted")
        },
        createRadioButton {
            value = "confirmed"
            textContent = tr("已确认", "Confirmed")
        },
    )
    val vertical =
        createRadioGroup { label = tr("旅行舱位", "Travel class") }.withTheme("vertical")
    vertical.append(
        createRadioButton {
            value = "economy"
            textContent = tr("经济舱", "Economy")
        },
        createRadioButton {
            value = "business"
            textContent = tr("商务舱", "Business")
        },
        createRadioButton {
            value = "firstClass"
            textContent = tr("头等舱", "First Class")
        },
    )
    orientation.body.append(example(null, horizontal), example(null, vertical))
    view.body.append(orientation.root)

    val features =
        section(
            tr("标签、辅助信息与提示", "Label, helper text and tooltip"),
            tr(
                "group 上的 helper-text 与 slot=\"tooltip\" 的 tooltip 覆盖字段级说明。",
                "helper-text on the group and a slotted tooltip cover the field-level explanation.",
            ),
        )
    val labeled =
        createRadioGroup {
            label = tr("标签", "Label")
            helperText = tr("辅助信息", "Helper text")
        }.withTheme("vertical")
    labeled.append(
        createTooltip { text = tr("提示文本", "Tooltip text") }.apply { setAttribute("slot", "tooltip") },
        createRadioButton {
            value = "1"
            textContent = tr("选项 1", "Item 1")
        },
        createRadioButton {
            value = "2"
            textContent = tr("选项 2", "Item 2")
        },
        createRadioButton {
            value = "3"
            textContent = tr("选项 3", "Item 3")
        },
    )
    features.body.append(example(null, labeled))
    view.body.append(features.root)

    val required =
        section(
            tr("校验与状态", "Validation and states"),
            tr(
                "required 让空值无法通过；readonly 保留选择但不能更改，disabled 完全不可交互。",
                "required rejects an empty value; readonly keeps the selection but blocks changes, " +
                    "disabled turns off all interaction.",
            ),
        )
    val payment =
        createRadioGroup {
            label = tr("付款方式", "Payment method")
            this.required = true
            errorMessage = tr("请选择一种付款方式", "Choose a payment method")
        }
    payment.append(
        createRadioButton {
            value = "card"
            textContent = tr("银行卡", "Card")
        },
        createRadioButton {
            value = "transfer"
            textContent = tr("转账", "Bank transfer")
        },
    )
    val readonly =
        createRadioGroup {
            label = tr("只读", "Read-only")
            value = "yes"
        }.withTheme("vertical")
    readonly.readonly = true
    readonly.append(
        createRadioButton {
            value = "yes"
            textContent = tr("是", "Yes")
        },
        createRadioButton {
            value = "no"
            textContent = tr("否", "No")
        },
    )
    val disabled =
        createRadioGroup {
            label = tr("禁用", "Disabled")
            value = "inProgress"
        }.withTheme("vertical")
    disabled.disabled = true
    disabled.append(
        createRadioButton {
            value = "inProgress"
            textContent = tr("进行中", "In progress")
        },
        createRadioButton {
            value = "done"
            textContent = tr("已完成", "Done")
        },
    )
    val result = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                result.set(
                    tr("校验：", "Validation: "),
                    if (payment.validate()) tr("通过", "passed") else tr("失败", "failed"),
                )
            }
        }
    val statesRow = row(readonly, disabled).withClass("example-stack")
    required.body.append(
        example(null, column(payment, row(validateButton, result.root))),
        example(null, statesRow),
    )
    view.body.append(required.root)

    val customOption =
        section(
            tr("自定义选项", "Custom option"),
            tr(
                "radio-button 的 label 插槽可以放任意内容；选中「其他」时再补一个输入字段收集详情。",
                "The label slot of a radio-button accepts arbitrary content; choosing \"Other\" reveals " +
                    "an extra field for the details.",
            ),
        )
    val cardGroup =
        createRadioGroup { label = tr("付款方式", "Payment method") }.withTheme("vertical")
    val visa =
        createRadioButton { value = "visa" }.apply {
            append(column(span(tr("Visa · 账户 1234", "Visa · account 1234")), span(tr("有效期 12/28", "Expires 12/28"))).withClass("radio-option"))
        }
    visa.firstElementChild?.setAttribute("slot", "label")
    val other =
        createRadioButton { value = "-1" }.apply {
            append(column(span(tr("其他", "Other")), span(tr("手动输入卡号", "Enter the card number manually"))).withClass("radio-option"))
        }
    other.firstElementChild?.setAttribute("slot", "label")
    cardGroup.append(visa, other)
    val cardNumber = createTextField { label = tr("卡号", "Card number") }
    cardNumber.setHidden(true)
    cardGroup.listen(RadioGroupEvents.valueChanged) { cardNumber.setHidden(cardGroup.value != "-1") }
    customOption.body.append(example(null, column(cardGroup, cardNumber)))
    view.body.append(customOption.root)

    val alternative =
        section(
            tr("替代勾选框", "Alternative to a checkbox"),
            tr(
                "单个二选一的决定用 radio-group 更明确：这里的「回复」与「回复全部」始终只有一个生效。",
                "A single either-or decision reads better as a radio-group: here only one of \"Reply\" " +
                    "and \"Reply to all\" is ever active.",
            ),
        )
    val replyGroup =
        createRadioGroup { label = tr("默认回复行为", "Default reply behavior") }.withTheme("horizontal")
    replyGroup.append(
        createRadioButton {
            value = "reply"
            textContent = tr("回复", "Reply")
            checked = true
        },
        createRadioButton {
            value = "replyAll"
            textContent = tr("回复全部", "Reply to all")
        },
    )
    alternative.body.append(
        example(
            null,
            column(
                createCheckbox { label = tr("默认回复全部（未勾选状态不明确）", "Reply All by default (unchecked state not clear)") },
                replyGroup,
            ),
        ),
    )
    view.body.append(alternative.root)

    val presentation =
        section(
            tr("自定义条目呈现", "Custom item presentation"),
            tr(
                "多行 label 让每个选项带上补充信息，适合需要对比细节的场景。",
                "A multi-line label adds context to every option, which helps when the details matter.",
            ),
        )
    val accounts =
        createRadioGroup {
            label = tr("付款方式", "Payment method")
            value = "1"
        }.withTheme("vertical")
    accounts.append(
        *people(3).mapIndexed { index, person ->
            createRadioButton { value = "${person.id}" }.apply {
                append(
                    column(
                        span(tr("账户 ${person.id}", "Account ${person.id}")),
                        span(tr("持卡人 ${person.displayName}", "Holder ${person.displayName}")),
                        span(tr("有效期 12/28", "Expires 12/28")),
                    ).withClass("radio-option").apply { setAttribute("slot", "label") },
                )
                if (index == 0) checked = true
            }
        }.toTypedArray(),
    )
    presentation.body.append(example(null, accounts))
    view.body.append(presentation.root)

    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "helper-above-field 把辅助信息放到标签与选项之间。",
                "helper-above-field moves the helper text between the label and the options.",
            ),
        )
    val styled =
        createRadioGroup {
            label = tr("标签", "Label")
            helperText = tr("辅助信息", "Helper text")
        }.withTheme("helper-above-field", "vertical")
    styled.append(
        *listOf("Item 1", "Item 2", "Item 3").map { label ->
            createRadioButton {
                value = label
                textContent = label
            }
        }.toTypedArray(),
    )
    styles.body.append(example(null, styled))
    view.body.append(styles.root)

    return view
}
