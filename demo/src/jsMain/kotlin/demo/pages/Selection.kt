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
import demo.ui.joinValues
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.a11ybase.ListOrientations
import vaadin.button.createButton
import vaadin.checkbox.CheckboxEvents
import vaadin.checkbox.createCheckbox
import vaadin.checkboxgroup.CheckboxGroupEvents
import vaadin.checkboxgroup.createCheckboxGroup
import vaadin.combobox.ComboBoxEvents
import vaadin.combobox.createComboBox
import vaadin.item.createItem
import vaadin.listbox.ListBoxEvents
import vaadin.listbox.createListBox
import vaadin.multiselectcombobox.MultiSelectComboBoxEvents
import vaadin.multiselectcombobox.createMultiSelectComboBox
import vaadin.radiogroup.RadioGroupEvents
import vaadin.radiogroup.createRadioButton
import vaadin.radiogroup.createRadioGroup
import vaadin.select.SelectEvents
import vaadin.select.SelectItemData
import vaadin.select.createSelect
import vaadin.slider.RangeSliderEvents
import vaadin.slider.SliderEvents
import vaadin.slider.createRangeSlider
import vaadin.slider.createSlider
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
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
                            "可滚动的选项列表，支持单选、多选与键盘导航。",
                            "Scrollable list of options with single selection, multiple selection and " +
                                "keyboard navigation.",
                        ),
                    docsUrl = "$DOCS_BASE/list-box",
                    sourcePath = "pages/Selection.kt",
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
                    sourcePath = "pages/Selection.kt",
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
                    sourcePath = "pages/Selection.kt",
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
                    sourcePath = "pages/Selection.kt",
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
                    sourcePath = "pages/Selection.kt",
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
                    sourcePath = "pages/Selection.kt",
                ),
            builder = ::buildSliderPage,
        ),
    )

private const val MEMBER_ITEMS_JSON =
    """
    [
      {"label": "Alpha", "value": "alpha"},
      {"label": "Bravo", "value": "bravo"},
      {"label": "Charlie", "value": "charlie"},
      {"label": "Delta", "value": "delta"}
    ]
    """

private const val CITY_ITEMS_JSON =
    """
    [
      {"label": "Tallinn", "value": "tallinn"},
      {"label": "Helsinki", "value": "helsinki"},
      {"label": "Berlin", "value": "berlin"},
      {"label": "Lisbon", "value": "lisbon"}
    ]
    """

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
    val terms = createCheckbox { label = tr("我已阅读并同意条款", "I have read and accept the terms") }
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

    val states =
        section(
            tr("三态与状态", "Tri-state and states"),
            tr(
                "indeterminate 用于「部分选中」；readonly 只能看不能改，disabled 完全不可交互。",
                "indeterminate marks a partial selection; readonly can be viewed but not changed, while " +
                    "disabled blocks all interaction.",
            ),
        )
    val partial = createCheckbox { label = tr("全选（部分选中）", "Select all (partial)") }
    partial.indeterminate = true
    states.body.append(
        example(
            null,
            row(
                createCheckbox {
                    label = tr("已勾选", "Checked")
                    checked = true
                },
                partial,
                createCheckbox {
                    label = tr("只读", "Read-only")
                    checked = true
                    readonly = true
                },
                createCheckbox {
                    label = tr("禁用", "Disabled")
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    val readState =
        section(
            tr("读取状态", "Reading the state"),
            tr("点击按钮读取所有勾选框当前状态。", "Read the current state of every checkbox with a button."),
        )
    val a = createCheckbox { label = tr("邮件通知", "Email notifications") }
    val b = createCheckbox {
        label = tr("短信通知", "SMS notifications")
        checked = true
    }
    val readout = Readout(tr("尚未读取", "Not read yet"))
    val readButton =
        createButton { textContent = tr("读取", "Read") }.apply {
            clickEvent.addHandler {
                readout.set(tr("邮件=${a.checked} · 短信=${b.checked}", "Email=${a.checked} · SMS=${b.checked}"))
            }
        }
    readState.body.append(example(null, column(a, b, readButton, readout.root)))
    view.body.append(readState.root)

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
        }
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
    skills.value = js("(['css', 'html'])")
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

    val states = section(tr("只读与禁用", "Read-only and disabled"))
    val readonlyGroup =
        createCheckboxGroup { label = tr("只读", "Read-only") }
    readonlyGroup.readonly = true
    readonlyGroup.append(
        createCheckbox {
            value = "a"
            textContent = tr("选项 A", "Option A")
        }.apply { checked = true },
        createCheckbox {
            value = "b"
            textContent = tr("选项 B", "Option B")
        },
    )
    val disabledGroup =
        createCheckboxGroup { label = tr("禁用", "Disabled") }
    disabledGroup.disabled = true
    disabledGroup.append(createCheckbox { textContent = tr("不可更改", "Cannot be changed") })
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
        }
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

    val states =
        section(
            tr("校验与状态", "Validation and states"),
            tr(
                "required 让空值无法通过；readonly 保留选择但不能更改。",
                "required rejects an empty value; readonly keeps the selection but blocks changes.",
            ),
        )
    val required =
        createRadioGroup {
            label = tr("付款方式", "Payment method")
            required = true
            errorMessage = tr("请选择一种付款方式", "Choose a payment method")
        }
    required.append(
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
        }
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
    val result = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                result.set(
                    tr("校验：", "Validation: "),
                    if (required.validate()) tr("通过", "passed") else tr("失败", "failed"),
                )
            }
        }
    states.body.append(
        example(null, column(required, row(validateButton, result.root))),
        example(null, row(readonly)),
    )
    view.body.append(states.root)

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

    val effect =
        section(
            tr("即时生效的联动", "Immediate effect"),
            tr("开关可以直接驱动页面上的其他状态。", "The switch can drive other state on the page directly."),
        )
    val compact = createSwitch { label = tr("紧凑模式（compact 主题）", "Compact mode (compact theme)") }
    val preview =
        createButton { textContent = tr("预览按钮", "Preview button") }.withTheme("primary")
    compact.listen(SwitchEvents.checkedChanged) {
        preview.withTheme(if (compact.checked) "primary small" else "primary")
    }
    effect.body.append(example(null, column(compact, row(preview))))
    view.body.append(effect.root)

    val states = section(tr("禁用", "Disabled"))
    states.body.append(
        example(
            null,
            row(
                createSwitch {
                    label = tr("已开启但禁用", "On but disabled")
                    checked = true
                    disabled = true
                },
                createSwitch {
                    label = tr("禁用", "Disabled")
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view
}

private fun buildListBoxPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "selected 是选中项的下标（Number），未选中时为 undefined。",
                "selected is the index of the selected item (Number); it is undefined when nothing is " +
                    "selected.",
            ),
        )
    val single =
        createListBox()
    single.dataset["test"] = "list-box-single"
    single.append(
        createItem {
            value = "inbox"
            textContent = tr("收件箱", "Inbox")
        },
        createItem {
            value = "drafts"
            textContent = tr("草稿", "Drafts")
        },
        createItem {
            value = "sent"
            textContent = tr("已发送", "Sent")
        },
    )
    val readout = Readout("selected = -")
    single.listen(ListBoxEvents.selectedChanged) {
        readout.set("selected = ", single.selected.toString())
    }
    basic.body.append(example(null, row(single, readout.root)))
    view.body.append(basic.root)

    val multiple =
        section(
            tr("多选", "Multiple selection"),
            tr(
                "multiple 打开后使用 selectedValues 读取所有选中下标。",
                "With multiple on, selectedValues returns every selected index.",
            ),
        )
    val multi = createListBox { this.multiple = true }
    multi.append(
        createItem { textContent = tr("阅读", "Reading") },
        createItem { textContent = tr("写作", "Writing") },
        createItem { textContent = tr("运动", "Exercise") },
        createItem { textContent = tr("旅行", "Travel") },
    )
    val multiReadout = Readout("selectedValues = []")
    multi.listen(ListBoxEvents.selectedValuesChanged) {
        multiReadout.set("selectedValues = ", joinValues(multi.selectedValues).ifEmpty { "[]" })
    }
    multiple.body.append(example(null, row(multi, multiReadout.root)))
    view.body.append(multiple.root)

    val horizontal =
        section(
            tr("方向与禁用", "Orientation and disabled state"),
            tr(
                "orientation 控制排布方向，禁用后整组不可交互。",
                "orientation sets the layout direction; disabling blocks the whole group.",
            ),
        )
    horizontal.body.append(
        example(
            null,
            row(
                createListBox { orientation = ListOrientations.horizontal }.apply {
                    append(
                        createItem { textContent = tr("一", "One") },
                        createItem { textContent = tr("二", "Two") },
                        createItem { textContent = tr("三", "Three") },
                    )
                },
                createListBox().apply {
                    disabled = true
                    append(
                        createItem { textContent = tr("禁用项一", "Disabled item 1") },
                        createItem { textContent = tr("禁用项二", "Disabled item 2") },
                    )
                },
            ),
        ),
    )
    view.body.append(horizontal.root)

    return view
}

private fun buildItemPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("独立使用", "Standalone use"),
            tr(
                "vaadin-item 通常由 List Box / Select / Context Menu 内部创建；这里单独展示它的属性。",
                "vaadin-item is usually created inside List Box / Select / Context Menu; this page shows its " +
                    "properties on its own.",
            ),
        )
    val log = EventLog(4)
    val items =
        row(
            createItem {
                label = tr("带 label 的条目", "Item with a label")
                value = "one"
            }.apply { dataset["test"] = "item-one" },
            createItem { textContent = tr("默认插槽文本", "Default slot text") },
            createItem {
                textContent = tr("已禁用", "Disabled")
                disabled = true
            },
        )
    basic.body.append(example(null, items))
    view.body.append(basic.root)

    val selection =
        section(
            tr("选中状态", "Selected state"),
            tr(
                "selected 只是视觉状态，条目本身不维护选中逻辑。",
                "selected is purely visual; the item does not maintain selection logic itself.",
            ),
        )
    val selectedItem =
        createItem {
            textContent = tr("被选中的条目", "Selected item")
            selected = true
        }
    val toggleButton =
        createButton { textContent = tr("切换 selected", "Toggle selected") }.apply {
            clickEvent.addHandler {
                selectedItem.selected = !selectedItem.selected
                log.add("selected = ${selectedItem.selected}")
            }
        }
    selection.body.append(example(null, column(selectedItem, toggleButton, log.root)))
    view.body.append(selection.root)

    return view
}

private fun buildSelectPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "items 接受 { label, value } 数组；下拉在浮层中渲染，value 是字符串。",
                "items takes an array of { label, value }; the dropdown renders in an overlay and value " +
                    "is a string.",
            ),
        )
    val select =
        createSelect {
            label = tr("所在城市", "City")
            placeholder = tr("请选择", "Choose one")
            value = "helsinki"
            items = js(CITY_ITEMS_JSON)
        }
    select.dataset["test"] = "select-basic"
    val readout = Readout("value = helsinki")
    select.listen(SelectEvents.valueChanged) {
        readout.set("value = ", select.value.ifEmpty { tr("（未选择）", "(none)") })
    }
    basic.body.append(example(null, column(select, readout.root)))
    view.body.append(basic.root)

    val placeholderAndEmpty =
        section(
            tr("占位符与可选空值", "Placeholder and empty value"),
            tr(
                "placeholder 只在没有选中值时显示；allowCustomValue 由 Combo Box 提供，Select 只接受列表内的值。",
                "placeholder only shows while nothing is selected; allowCustomValue comes from Combo Box, " +
                    "while Select only accepts values from its list.",
            ),
        )
    val emptySelect =
        createSelect {
            label = tr("团队", "Team")
            placeholder = tr("尚未选择", "Nothing selected yet")
            items =
                jsArray<JsAny>(
                    """[
                        {"label": "Engineering", "value": "eng"},
                        {"label": "Design", "value": "design"},
                        {"label": "${tr("维护中", "Under maintenance")}", "value": "maintenance", "disabled": true}
                    ]""",
                ).unsafeCast<Array<SelectItemData>>()
        }
    val emptyReadout = Readout(tr("value = （空）", "value = (empty)"))
    emptySelect.listen(SelectEvents.valueChanged) {
        emptyReadout.set(tr("当前选择：", "Current value: "), emptySelect.value.ifEmpty { tr("（空）", "(empty)") })
    }
    val clearButton =
        createButton { textContent = tr("清空", "Clear") }.apply {
            clickEvent.addHandler { emptySelect.value = "" }
        }
    placeholderAndEmpty.body.append(example(null, column(emptySelect, row(clearButton, emptyReadout.root))))
    view.body.append(placeholderAndEmpty.root)

    val states = section(tr("只读与禁用", "Read-only and disabled"))
    states.body.append(
        example(
            null,
            row(
                createSelect {
                    label = tr("只读", "Read-only")
                    value = "bravo"
                    readonly = true
                    items = js(MEMBER_ITEMS_JSON)
                },
                createSelect {
                    label = tr("禁用", "Disabled")
                    value = "alpha"
                    disabled = true
                    items = js(MEMBER_ITEMS_JSON)
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view
}

private fun buildComboBoxPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "给定 itemLabelPath 与 itemValuePath 后，items 可以是任意对象。",
                "With itemLabelPath and itemValuePath set, items can be any object.",
            ),
        )
    val combo =
        createComboBox {
            label = tr("负责人", "Owner")
            itemLabelPath = "name"
            itemValuePath = "id"
            items =
                js(
                    """[
                        {"id": "ada", "name": "Ada Lovelace"},
                        {"id": "grace", "name": "Grace Hopper"},
                        {"id": "margaret", "name": "Margaret Hamilton"},
                        {"id": "katherine", "name": "Katherine Johnson"}
                    ]""",
                )
            value = "grace"
            clearButtonVisible = true
        }
    combo.dataset["test"] = "combo-box-basic"
    val readout = Readout("value = grace")
    val log = EventLog(4)
    combo.listen(ComboBoxEvents.valueChanged) {
        readout.set("value = ", combo.value.ifEmpty { tr("（空）", "(empty)") })
        log.add("value-changed · ${combo.value}")
    }
    basic.body.append(example(null, column(combo, readout.root)), example(tr("事件流", "Event stream"), log.root))
    view.body.append(basic.root)

    val custom =
        section(
            tr("自定义值", "Custom values"),
            tr(
                "allowCustomValue 允许输入列表之外的值，此时 value 就是用户输入的文本。",
                "allowCustomValue accepts values outside the list; value then holds the typed text.",
            ),
        )
    val customCombo =
        createComboBox {
            label = tr("标签", "Tags")
            allowCustomValue = true
            items = js("(['alpha', 'bravo', 'charlie'])")
            helperText = tr("可以输入任意值后回车", "Type any value and press Enter")
        }
    val customReadout = Readout(tr("自定义值：（空）", "Custom value: (empty)"))
    customCombo.listen(ComboBoxEvents.customValueSet) {
        customReadout.set(tr("自定义值：", "Custom value: "), customCombo.value)
    }
    custom.body.append(example(null, column(customCombo, customReadout.root)))
    view.body.append(custom.root)

    val states =
        section(
            tr("加载中与禁用", "Loading and disabled"),
            tr(
                "loading 会显示加载指示；disabled 关闭全部交互。",
                "loading shows a loading indicator; disabled turns off all interaction.",
            ),
        )
    val loading =
        createComboBox {
            label = tr("远程选项（加载中）", "Remote items (loading)")
            loading = true
            items = js("([])")
        }
    states.body.append(
        example(
            null,
            row(
                loading,
                createComboBox {
                    label = tr("禁用", "Disabled")
                    disabled = true
                    value = "bravo"
                    items = js("(['alpha', 'bravo', 'charlie'])")
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view
}

private fun buildMultiSelectComboBoxPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "选中项以 chip 展示；selectedItems 是选中对象的数组。",
                "Selections render as chips; selectedItems holds the selected objects.",
            ),
        )
    val multi =
        createMultiSelectComboBox {
            label = tr("标签", "Tags")
            itemLabelPath = "name"
            itemValuePath = "id"
            items =
                js(
                    """[
                        {"id": "kotlin", "name": "Kotlin"},
                        {"id": "java", "name": "Java"},
                        {"id": "js", "name": "JavaScript"},
                        {"id": "rust", "name": "Rust"},
                        {"id": "go", "name": "Go"}
                    ]""",
                )
        }
    multi.dataset["test"] = "multi-select-combo-box"
    val readout = Readout(tr("已选 0 项", "0 selected"))
    multi.listen(MultiSelectComboBoxEvents.selectedItemsChanged) {
        readout.set(
            tr("已选 ${multi.selectedItems.size} 项：", "${multi.selectedItems.size} selected: "),
            selectedNames(multi.selectedItems),
        )
    }
    basic.body.append(example(null, column(multi, readout.root)))
    view.body.append(basic.root)

    val compactChips =
        section(
            tr("紧凑显示", "Compact chips"),
            tr(
                "collapseChips 把多余的 chip 折叠成计数；selectedItemsOnTop 让选中项排在列表前面。",
                "collapseChips folds the extra chips into a counter; selectedItemsOnTop moves the selected " +
                    "items to the top of the list.",
            ),
        )
    val collapsed =
        createMultiSelectComboBox {
            label = tr("折叠 chip", "Collapsed chips")
            collapseChips = true
            selectedItemsOnTop = true
            itemLabelPath = "name"
            itemValuePath = "id"
            items =
                jsArray<JsAny>(
                    """[
                        {"id": "a", "name": "${tr("选项 A", "Option A")}"},
                        {"id": "b", "name": "${tr("选项 B", "Option B")}"},
                        {"id": "c", "name": "${tr("选项 C", "Option C")}"},
                        {"id": "d", "name": "${tr("选项 D", "Option D")}"},
                        {"id": "e", "name": "${tr("选项 E", "Option E")}"}
                    ]""",
                )
        }
    compactChips.body.append(example(null, collapsed))
    view.body.append(compactChips.root)

    return view
}

private fun buildSliderPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "拖动时触发 input，松开后触发 change；value 是 Double。",
                "Dragging fires input and releasing fires change; value is a Double.",
            ),
        )
    val slider =
        createSlider {
            label = tr("音量", "Volume")
            min = 0.0
            max = 100.0
            step = 5.0
            value = 40.0
            valueAlwaysVisible = true
        }
    slider.dataset["test"] = "slider-basic"
    val readout = Readout("value = 40")
    slider.listen(SliderEvents.input) { readout.set(tr("数值 = ", "Value = "), slider.value.toString()) }
    slider.listen(SliderEvents.change) { readout.set(tr("已提交数值 = ", "Committed value = "), slider.value.toString()) }
    basic.body.append(example(null, column(slider, readout.root)))
    view.body.append(basic.root)

    val range =
        section(
            tr("区间滑块", "Range slider"),
            tr(
                "range-slider 的 value 是两元素数组，两个手柄分别控制下限与上限。",
                "range-slider takes a two-element value; the handles control the lower and the upper bound.",
            ),
        )
    val rangeSlider =
        createRangeSlider {
            label = tr("价格区间", "Price range")
            min = 0.0
            max = 1000.0
            step = 50.0
            value = js("([200, 700])")
            minMaxVisible = true
        }
    val rangeReadout = Readout("value = 200 - 700")
    rangeSlider.listen(RangeSliderEvents.valueChanged) {
        rangeReadout.set(tr("区间 = ", "Range = "), joinValues(rangeSlider.value))
    }
    range.body.append(example(null, column(rangeSlider, rangeReadout.root)))
    view.body.append(range.root)

    val states = section(tr("只读与禁用", "Read-only and disabled"))
    states.body.append(
        example(
            null,
            row(
                createSlider {
                    label = tr("只读", "Read-only")
                    value = 60.0
                    readonly = true
                },
                createSlider {
                    label = tr("禁用", "Disabled")
                    value = 60.0
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view
}

/** Reads the `name` field of the selected item objects for the multi-select read-out. */
private fun selectedNames(items: Array<kotlin.js.JsAny?>): String =
    items.joinToString(", ") { item -> js("(item) => String(item.name)")(item) as String }
