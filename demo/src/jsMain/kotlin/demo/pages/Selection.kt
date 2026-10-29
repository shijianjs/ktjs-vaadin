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
import demo.ui.example
import demo.ui.jsArray
import demo.ui.joinValues
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.setHidden
import demo.ui.span
import demo.ui.withClass
import demo.ui.withTheme
import kotlin.js.JsAny
import vaadin.button.createButton
import vaadin.checkbox.CheckboxEvents
import vaadin.checkbox.createCheckbox
import vaadin.checkboxgroup.CheckboxGroupEvents
import vaadin.checkboxgroup.createCheckboxGroup
import vaadin.radiogroup.RadioGroupEvents
import vaadin.radiogroup.createRadioButton
import vaadin.radiogroup.createRadioGroup
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import vaadin.textfield.createTextField
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val selectionEntries: List<CatalogEntry> =
    listOf(
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
                    sourcePath = "pages/Selection.kt",
                ),
            builder = ::buildCheckboxPage,
        ),
        CatalogEntry(
            id = "checkbox-group",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = bi("Checkbox Group", "Checkbox Group"),
                    summary =
                        bi(
                            "多选组：value 是数组，支持必填校验与整体只读。",
                            "Multi-select group: value is an array, with required validation and a " +
                                "group-wide read-only state.",
                        ),
                    docsUrl = "$DOCS_BASE/checkbox",
                    docsNote =
                        bi(
                            "Checkbox Group 与 Checkbox 共用一页官方文档。",
                            "Checkbox Group shares the Checkbox documentation page.",
                        ),
                    sourcePath = "pages/Selection.kt",
                ),
            builder = ::buildCheckboxGroupPage,
        ),
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
                    sourcePath = "pages/Selection.kt",
                ),
            builder = ::buildRadioGroupPage,
        ),
        CatalogEntry(
            id = "switch",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = bi("Switch", "Switch"),
                    summary =
                        bi(
                            "二态开关，语义与 Checkbox 相同，视觉上表达即时生效的设置。",
                            "Two-state switch with the same semantics as Checkbox, used for settings that " +
                                "apply immediately.",
                        ),
                    docsUrl = "$DOCS_BASE/switch",
                    sourcePath = "pages/Selection.kt",
                ),
            builder = ::buildSwitchPage,
        ),
        CatalogEntry(
            id = "list-box",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = bi("List Box", "List Box"),
                    summary =
                        bi(
                            "可滚动的选项列表，支持分隔线、禁用条目、单选与多选。",
                            "Scrollable list of options with dividers, disabled items, single and multiple " +
                                "selection.",
                        ),
                    docsUrl = "$DOCS_BASE/list-box",
                    sourcePath = "pages/SelectionLists.kt",
                ),
            builder = ::buildListBoxPage,
        ),
        CatalogEntry(
            id = "item",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = bi("Item", "Item"),
                    summary =
                        bi(
                            "List Box / Select / Context Menu 使用的条目元素，也可以单独使用。",
                            "Item element used by List Box / Select / Context Menu; it also works on its own.",
                        ),
                    docsUrl = "$DOCS_BASE/list-box",
                    docsNote =
                        bi(
                            "vaadin-item 没有独立文档页面，用法见 List Box。",
                            "vaadin-item has no dedicated documentation page; see List Box.",
                        ),
                    sourcePath = "pages/SelectionLists.kt",
                ),
            builder = ::buildItemPage,
        ),
        CatalogEntry(
            id = "select",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = bi("Select", "Select"),
                    summary =
                        bi(
                            "下拉单选：items 提供选项，value 是选中的字符串。",
                            "Dropdown single select: items provides the options and value is the selected " +
                                "string.",
                        ),
                    docsUrl = "$DOCS_BASE/select",
                    sourcePath = "pages/SelectionDropdowns.kt",
                ),
            builder = ::buildSelectPage,
        ),
        CatalogEntry(
            id = "combo-box",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = bi("Combo Box", "Combo Box"),
                    summary =
                        bi(
                            "可筛选的下拉选择：对象选项、itemLabelPath / itemValuePath、自定义值。",
                            "Filterable dropdown: object items, itemLabelPath / itemValuePath and custom " +
                                "values.",
                        ),
                    docsUrl = "$DOCS_BASE/combo-box",
                    sourcePath = "pages/SelectionDropdowns.kt",
                ),
            builder = ::buildComboBoxPage,
        ),
        CatalogEntry(
            id = "multi-select-combo-box",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = bi("Multi-Select Combo Box", "Multi-Select Combo Box"),
                    summary =
                        bi(
                            "多选下拉：选中项显示为 chip，selectedItems 是选中的对象数组。",
                            "Multi-select dropdown: selections show as chips and selectedItems holds the " +
                                "selected objects.",
                        ),
                    docsUrl = "$DOCS_BASE/multi-select-combo-box",
                    sourcePath = "pages/SelectionDropdowns.kt",
                ),
            builder = ::buildMultiSelectComboBoxPage,
        ),
        CatalogEntry(
            id = "slider",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = bi("Slider", "Slider"),
                    summary =
                        bi(
                            "拖动选择数值，支持区间滑块、步进与只读状态。",
                            "Pick a value by dragging, with range slider, step and read-only state.",
                        ),
                    docsUrl = "$DOCS_BASE/slider",
                    sourcePath = "pages/SelectionLists.kt",
                ),
            builder = ::buildSliderPage,
        ),
    )

// ---------------------------------------------------------------------------------------------
// Shared data
//
// The docs examples load fixture data from a backend. The demo has none, so the same shapes are
// kept in local JSON that the pages parse into the wrapper's `items` arrays.
// ---------------------------------------------------------------------------------------------

internal const val CITIES_JSON =
    """
    [
      {"label": "Tallinn", "value": "tallinn"},
      {"label": "Helsinki", "value": "helsinki"},
      {"label": "Berlin", "value": "berlin"},
      {"label": "Lisbon", "value": "lisbon"}
    ]
    """

internal const val SORT_ITEMS_JSON =
    """
    [
      {"label": "Most recent first", "value": "recent"},
      {"label": "Rating: high to low", "value": "rating-desc"},
      {"label": "Rating: low to high", "value": "rating-asc"},
      {"label": "Price: high to low", "value": "price-desc"},
      {"label": "Price: low to high", "value": "price-asc"}
    ]
    """

/** One fixture person; the fields mirror the `Person` DTO the docs examples load from the backend. */
internal class Person(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val profession: String,
) {
    val displayName: String
        get() = "$firstName $lastName"

    val email: String
        get() = "${firstName.lowercase()}.${lastName.lowercase()}@example.com"

    val pictureUrl: String
        get() = portrait(PORTRAIT_FILLS[id % PORTRAIT_FILLS.size])
}

private val PORTRAIT_FILLS = listOf("1f4e9c", "b05a2c", "2f7d5b", "7a3f8f", "b07d1f", "3f607d")

internal fun people(count: Int = 6): List<Person> {
    val firstNames = listOf("Ada", "Grace", "Katherine", "Alan", "Barbara", "Edsger", "Margaret", "Dennis")
    val lastNames = listOf("Lovelace", "Hopper", "Johnson", "Turing", "Liskov", "Dijkstra", "Hamilton", "Ritchie")
    val professions =
        listOf(
            tr("软件工程师", "Software Engineer"),
            tr("产品设计师", "Product Designer"),
            tr("数据分析师", "Data Analyst"),
            tr("前端开发", "Frontend Developer"),
            tr("架构师", "Architect"),
            tr("测试工程师", "Test Engineer"),
        )
    return (0 until count).map { index ->
        Person(
            id = index + 1,
            firstName = firstNames[index % firstNames.size],
            lastName = lastNames[index % lastNames.size],
            profession = professions[index % professions.size],
        )
    }
}

/** Serialises [list] into the object array the item-based wrappers take. */
internal fun personItems(list: List<Person>, withDisplayName: Boolean = false): Array<JsAny> {
    val entries =
        list.map { person ->
            val fields =
                mutableListOf(
                    "\"id\": ${person.id}",
                    "\"firstName\": \"${person.firstName}\"",
                    "\"lastName\": \"${person.lastName}\"",
                    "\"profession\": \"${person.profession}\"",
                    "\"email\": \"${person.email}\"",
                    "\"pictureUrl\": \"${person.pictureUrl}\"",
                )
            if (withDisplayName) fields += "\"displayName\": \"${person.displayName}\""
            "{${fields.joinToString(", ")}}"
        }
    return jsArray("[${entries.joinToString(", ")}]")
}

/** Builds a local portrait as a `data:` URI; the demo ships no binary assets. */
internal fun portrait(fill: String): String =
    svgDataUri(
        "<svg xmlns='http://www.w3.org/2000/svg' width='80' height='80'>" +
            "<rect width='80' height='80' fill='#$fill'/>" +
            "<circle cx='40' cy='30' r='14' fill='#ffffff'/>" +
            "<path d='M12 80c0-16 12-26 28-26s28 10 28 26z' fill='#ffffff'/>" +
            "</svg>",
    )

private fun svgDataUri(markup: String): String =
    "data:image/svg+xml," +
        markup
            .replace("\"", "'")
            .replace("%", "%25")
            .replace("<", "%3C")
            .replace(">", "%3E")
            .replace("#", "%23")
            .replace(" ", "%20")

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

private fun buildCheckboxGroupPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "checkbox-group 的 value 是数组，子项通过 value 属性区分。",
                "The value of a checkbox-group is an array; the items are told apart by their value " +
                    "property.",
            ),
        )
    val group =
        createCheckboxGroup {
            label = tr("使用的语言", "Languages used")
            helperText = tr("可以多选", "Multiple answers are allowed")
        }.withTheme("vertical")
    group.dataset["test"] = "checkbox-group"
    group.append(
        createCheckbox {
            value = "kotlin"
            textContent = "Kotlin"
        },
        createCheckbox {
            value = "java"
            textContent = "Java"
        },
        createCheckbox {
            value = "js"
            textContent = "JavaScript"
        },
    )
    val readout = Readout("value = []")
    group.listen(CheckboxGroupEvents.valueChanged) {
        readout.set("value = ", joinValues(group.value).ifEmpty { "[]" })
    }
    basic.body.append(example(null, column(group, readout.root)))
    view.body.append(basic.root)

    val preset =
        section(
            tr("预设值与校验", "Preset values and validation"),
            tr(
                "value 可以预先赋值；required 在空选择时让 validate() 失败。",
                "value can be preset; required makes validate() fail on an empty selection.",
            ),
        )
    val skills =
        createCheckboxGroup {
            label = tr("技能（必填）", "Skills (required)")
            required = true
            errorMessage = tr("至少选择一项", "Select at least one")
        }
    skills.value = arrayOf("css", "html")
    skills.append(
        createCheckbox {
            value = "css"
            textContent = "CSS"
        },
        createCheckbox {
            value = "html"
            textContent = "HTML"
        },
        createCheckbox {
            value = "kotlin"
            textContent = "Kotlin"
        },
    )
    val result = Readout("value = css, html")
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                result.set(tr("校验：", "Validation: "), if (skills.validate()) tr("通过", "passed") else tr("失败", "failed"))
            }
        }
    skills.listen(CheckboxGroupEvents.valueChanged) {
        result.set(tr("已选：", "Selected: "), joinValues(skills.value).ifEmpty { "[]" })
    }
    preset.body.append(example(null, column(skills, row(validateButton, result.root))))
    view.body.append(preset.root)

    val features =
        section(
            tr("标签、辅助信息与提示", "Label, helper text and tooltip"),
            tr(
                "label 与 helper-text 直接写在组上；tooltip 通过 slot=\"tooltip\" 挂到字段上。",
                "label and helper-text live on the group; a tooltip is attached to the field through " +
                    "slot=\"tooltip\".",
            ),
        )
    val languages =
        createCheckboxGroup {
            label = tr("标签", "Label")
            helperText = tr("辅助信息", "Helper text")
        }.withTheme("vertical")
    languages.append(
        createTooltip { text = tr("提示文本", "Tooltip text") }.apply { setAttribute("slot", "tooltip") },
        createCheckbox {
            value = "1"
            textContent = tr("选项 1", "Item 1")
        },
        createCheckbox {
            value = "2"
            textContent = tr("选项 2", "Item 2")
        },
        createCheckbox {
            value = "3"
            textContent = tr("选项 3", "Item 3")
        },
    )
    features.body.append(
        example(
            null,
            column(
                createCheckbox {
                    label = tr("标签", "Label")
                    helperText = tr("辅助信息", "Helper text")
                },
                languages,
            ),
        ),
    )
    view.body.append(features.root)

    val states = section(tr("只读与禁用", "Read-only and disabled"))
    val readonlyGroup =
        createCheckboxGroup { label = tr("只读", "Read-only") }.withTheme("vertical")
    readonlyGroup.readonly = true
    readonlyGroup.value = arrayOf("a")
    readonlyGroup.append(
        createCheckbox {
            value = "a"
            textContent = tr("选项 A", "Option A")
        },
        createCheckbox {
            value = "b"
            textContent = tr("选项 B", "Option B")
        },
    )
    val disabledGroup =
        createCheckboxGroup { label = tr("禁用", "Disabled") }.withTheme("vertical")
    disabledGroup.disabled = true
    disabledGroup.append(
        *listOf(
            tr("工程", "Engineering"),
            tr("人力资源", "Human Resources"),
            tr("市场", "Marketing"),
        ).map { name ->
            createCheckbox {
                value = name
                textContent = name
            }
        }.toTypedArray(),
    )
    states.body.append(example(null, row(readonlyGroup, disabledGroup)))
    view.body.append(states.root)

    return view
}

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

private fun buildSwitchPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "switch 与 checkbox 共享状态模型，用 checked 读写。",
                "switch and checkbox share the same state model, read and written through checked.",
            ),
        )
    val notifications = createSwitch { label = tr("接收发布通知", "Receive release notifications") }
    notifications.checked = true
    notifications.dataset["test"] = "switch-basic"
    val readout = Readout(tr("已开启", "On"))
    notifications.listen(SwitchEvents.checkedChanged) {
        readout.set(tr("状态：", "State: "), if (notifications.checked) tr("已开启", "on") else tr("已关闭", "off"))
    }
    basic.body.append(example(null, column(notifications, readout.root)))
    view.body.append(basic.root)

    val features =
        section(
            tr("辅助信息与提示", "Helper text and tooltip"),
            tr(
                "helper-text 说明开关的作用；slot=\"tooltip\" 的 tooltip 补充细节。",
                "helper-text explains what the switch does; a slotted tooltip adds the details.",
            ),
        )
    val autosave =
        createSwitch {
            label = tr("自动保存", "Autosave")
            helperText = tr("工作时自动保存改动", "Automatically save changes as you work")
        }
    autosave.append(
        createTooltip { text = tr("上次保存于 5 分钟前", "Last saved 5 minutes ago") }.apply {
            setAttribute("slot", "tooltip")
        },
    )
    features.body.append(example(null, autosave))
    view.body.append(features.root)

    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "开关同样支持 required：未满足时 validate() 失败并显示 error-message。",
                "The switch supports required as well: validate() fails while it is off and shows the " +
                    "error-message.",
            ),
        )
    val twoFactor =
        createSwitch {
            label = tr("双因素认证", "Two-factor authentication")
            this.required = true
            checked = true
            errorMessage = tr("公司安全策略要求开启", "Required by your workplace security policy")
        }
    val result = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                result.set(
                    tr("校验：", "Validation: "),
                    if (twoFactor.validate()) tr("通过", "passed") else tr("失败", "failed"),
                )
            }
        }
    validation.body.append(example(null, column(twoFactor, row(validateButton, result.root))))
    view.body.append(validation.root)

    val states =
        section(
            tr("只读与禁用", "Read-only and disabled"),
            tr(
                "readonly 只展示当前状态；disabled 让整行不可交互。",
                "readonly only displays the current state; disabled makes the whole row inert.",
            ),
        )
    states.body.append(
        example(
            null,
            column(
                createSwitch {
                    label = tr("审计日志保留（90 天）", "Audit log retention (90 days)")
                    helperText = tr("商业版套餐已包含", "Included on the Business plan")
                    readonly = true
                    checked = true
                },
                createSwitch {
                    label = tr("每日摘要", "Daily digest")
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view
}
