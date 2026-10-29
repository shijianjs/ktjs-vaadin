package demo.pages.selection

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.joinValues
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withClass
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.checkbox.CheckboxEvents
import vaadin.checkbox.createCheckbox
import vaadin.checkboxgroup.CheckboxGroupEvents
import vaadin.checkboxgroup.createCheckboxGroup
import web.dom.clickEvent
import web.events.addHandler

val checkboxEntry: CatalogEntry =
    CatalogEntry(
        id = "checkbox",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Checkbox", "Checkbox"),
                summary =
                    bi(
                        "单个勾选框：checked / indeterminate 三态、只读、禁用与 change 事件。",
                        "Single checkbox: checked / indeterminate tri-state, read-only, disabled and " +
                            "change events.",
                    ),
                docsUrl = "$DOCS_BASE/checkbox",
                sourcePath = "pages/selection/CheckboxPage.kt",
            ),
        builder = ::buildCheckboxPage,
    )

private fun buildCheckboxPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "checked 反映勾选状态；change 只在用户操作时触发，checked-changed 在属性被代码设置时也会触发。",
                "checked reflects the tick state; change only fires for user interaction, while " +
                    "checked-changed also fires when code sets the property.",
            ),
        )
    val log = EventLog(6)
    val terms = createCheckbox { label = tr("我接受条款与条件", "I accept the terms and conditions") }
    terms.dataset["test"] = "checkbox-basic"
    // `change` only fires for user interaction, so the event stream stays readable; the read-out
    // shows `checked-changed` working for programmatic updates as well.
    terms.listen(CheckboxEvents.change) { log.add("change · ${terms.checked}") }
    val stateReadout = Readout("checked = false")
    terms.listen(CheckboxEvents.checkedChanged) { stateReadout.set("checked = ${terms.checked}") }
    val toggleFromCode =
        createButton { textContent = tr("程序化切换", "Toggle from code") }.apply {
            clickEvent.addHandler {
                terms.checked = !terms.checked
                log.add(tr("checked-changed（程序设置）· ${terms.checked}", "checked-changed (from code) · ${terms.checked}"))
            }
        }
    basic.body.append(example(null, column(terms, row(toggleFromCode, stateReadout.root), log.root)))
    view.body.append(basic.root)

    val labeling =
        section(
            tr("标签与辅助信息", "Label and helper text"),
            tr(
                "label 是可见标签；helper-text 提示填写方式，两者都通过 property 设置。",
                "label is the visible caption; helper-text explains the expected input. Both are set " +
                    "through properties.",
            ),
        )
    labeling.body.append(
        example(
            null,
            column(
                createCheckbox {
                    label = tr("我同意", "Yes, I agree")
                },
                createCheckbox {
                    label = tr("接收发布通知", "Receive release notifications")
                    helperText = tr("每天最多一封邮件", "At most one email per day")
                },
            ),
        ),
    )
    view.body.append(labeling.root)

    val required =
        section(
            tr("必填", "Required"),
            tr(
                "required 让空值无法通过校验；error-message 是失败时显示在字段下方的文案。",
                "required makes an empty value invalid; error-message is shown below the field when " +
                    "validation fails.",
            ),
        )
    val grant = createCheckbox {
        label = tr("授予查看权限", "Grant view permissions")
        this.required = true
        errorMessage = tr("此项为必填", "This field is required")
    }
    val requiredResult = Readout(tr("尚未校验", "Not validated yet"))
    val submit =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                requiredResult.set(
                    tr("校验：", "Validation: "),
                    if (grant.validate()) tr("通过", "passed") else tr("失败", "failed"),
                )
            }
        }
    required.body.append(example(null, column(grant, row(submit, requiredResult.root))))
    view.body.append(required.root)

    val indeterminate =
        section(
            tr("不确定态", "Indeterminate"),
            tr(
                "「全选」勾选框用 indeterminate 表示部分选中；它与组件的 checked 一起由选中数量派生。",
                "The \"select all\" checkbox uses indeterminate for a partial selection; it is derived " +
                    "from the number of selected items together with checked.",
            ),
        )
    val users = people(3)
    val selectAll = createCheckbox { label = tr("通知所有用户", "Notify users") }
    val userGroup =
        createCheckboxGroup { label = tr("接收通知的用户", "Users to notify") }.withTheme("vertical")
    userGroup.append(
        *users.map { person ->
            createCheckbox {
                value = person.displayName
                textContent = person.displayName
            }
        }.toTypedArray(),
    )
    fun syncSelectAll() {
        val count = userGroup.value.size
        selectAll.checked = count == users.size
        selectAll.indeterminate = count in 1 until users.size
    }
    userGroup.value = arrayOf(users[0].displayName, users[2].displayName)
    syncSelectAll()
    selectAll.listen(CheckboxEvents.change) {
        userGroup.value = if (selectAll.checked) users.map { it.displayName }.toTypedArray() else arrayOf()
        syncSelectAll()
    }
    userGroup.listen(CheckboxGroupEvents.valueChanged) { syncSelectAll() }
    indeterminate.body.append(example(null, column(selectAll, userGroup)))
    view.body.append(indeterminate.root)

    val orientation =
        section(
            tr("方向", "Orientation"),
            tr(
                "theme=\"horizontal\" 让选项横排，vertical 让选项竖排；相邻的两个组之间留出间距更易读。",
                "theme=\"horizontal\" lays the options out in a row, vertical stacks them; adjacent " +
                    "groups read better with a gap between them.",
            ),
        )
    val horizontal =
        createCheckboxGroup { label = tr("权限", "Permissions") }.withTheme("horizontal")
    horizontal.append(
        createCheckbox {
            value = "read"
            textContent = tr("读取", "Read")
        },
        createCheckbox {
            value = "edit"
            textContent = tr("编辑", "Edit")
        },
        createCheckbox {
            value = "delete"
            textContent = tr("删除", "Delete")
        },
    )
    val vertical =
        createCheckboxGroup { label = tr("工作日", "Working days") }.withTheme("vertical")
    vertical.append(
        *listOf(
            tr("周一", "Monday"),
            tr("周二", "Tuesday"),
            tr("周三", "Wednesday"),
            tr("周四", "Thursday"),
            tr("周五", "Friday"),
            tr("周六", "Saturday"),
            tr("周日", "Sunday"),
        ).map { day ->
            createCheckbox {
                value = day
                textContent = day
            }
        }.toTypedArray(),
    )
    val manufacturers =
        createCheckboxGroup { label = tr("制造商", "Manufacturer") }.withTheme("vertical")
    manufacturers.append(
        *listOf("Akuchi", "Broek", "Wulf").mapIndexed { index, name ->
            createCheckbox {
                value = "$index"
                textContent = name
            }
        }.toTypedArray(),
    )
    val statuses =
        createCheckboxGroup { label = tr("状态", "Status") }.withTheme("vertical")
    statuses.append(
        createCheckbox {
            value = "0"
            textContent = tr("进行中", "In progress")
        },
        createCheckbox {
            value = "1"
            textContent = tr("已完成", "Done")
        },
        createCheckbox {
            value = "2"
            textContent = tr("已取消", "Cancelled")
        },
    )
    val adjacent = column(manufacturers, statuses).withClass("example-stack")
    orientation.body.append(
        example(null, horizontal),
        example(null, vertical),
        example(null, adjacent),
    )
    view.body.append(orientation.root)

    val preset =
        section(
            tr("预设值", "Preset value"),
            tr(
                "value 可以直接赋值；value-changed 报告用户改动后的数组。",
                "value can be assigned directly; value-changed reports the new array after user edits.",
            ),
        )
    val exportGroup =
        createCheckboxGroup {
            label = tr("导出数据", "Export data")
            value = arrayOf("0", "2")
        }.withTheme("vertical")
    exportGroup.append(
        createCheckbox {
            value = "0"
            textContent = tr("订单号", "Order ID")
        },
        createCheckbox {
            value = "1"
            textContent = tr("产品名称", "Product name")
        },
        createCheckbox {
            value = "2"
            textContent = tr("客户", "Customer")
        },
        createCheckbox {
            value = "3"
            textContent = tr("状态", "Status")
        },
    )
    val exportReadout = Readout("value = 0, 2")
    exportGroup.listen(CheckboxGroupEvents.valueChanged) {
        exportReadout.set("value = ", joinValues(exportGroup.value).ifEmpty { "[]" })
    }
    preset.body.append(example(null, column(exportGroup, exportReadout.root)))
    view.body.append(preset.root)

    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "helper-above-field 把辅助信息放到标签与选项之间。",
                "helper-above-field moves the helper text between the label and the options.",
            ),
        )
    val styled =
        createCheckboxGroup {
            label = tr("标签", "Label")
            helperText = tr("辅助信息", "Helper text")
        }.withTheme("helper-above-field", "vertical")
    styled.append(
        *listOf("Item 1", "Item 2", "Item 3").map { label ->
            createCheckbox {
                value = label
                textContent = label
            }
        }.toTypedArray(),
    )
    styles.body.append(example(null, styled))
    view.body.append(styles.root)

    return view
}
