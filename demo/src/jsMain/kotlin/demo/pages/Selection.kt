package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.joinValues
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import kotlin.js.js
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
import vaadin.select.createSelect
import vaadin.slider.RangeSliderEvents
import vaadin.slider.SliderEvents
import vaadin.slider.createRangeSlider
import vaadin.slider.createSlider
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val selectionEntries: List<CatalogEntry> =
    listOf(
        CatalogEntry(
            id = "checkbox",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = "Checkbox",
                    summary = "单个勾选框：checked / indeterminate 三态、只读、禁用与 change 事件。",
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
                    title = "Checkbox Group",
                    summary = "多选组：value 是数组，支持必填校验与整体只读。",
                    docsUrl = "$DOCS_BASE/checkbox",
                    docsNote = "Checkbox Group 与 Checkbox 共用一页官方文档。",
                    sourcePath = "pages/Selection.kt",
                ),
            builder = ::buildCheckboxGroupPage,
        ),
        CatalogEntry(
            id = "radio-group",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = "Radio Group",
                    summary = "单选组：value 是字符串，子项由 vaadin-radio-button 提供。",
                    docsUrl = "$DOCS_BASE/radio-button",
                    docsNote = "官方文档的 Radio Button 页面同时覆盖 Radio Group。",
                    sourcePath = "pages/Selection.kt",
                ),
            builder = ::buildRadioGroupPage,
        ),
        CatalogEntry(
            id = "switch",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = "Switch",
                    summary = "二态开关，语义与 Checkbox 相同，视觉上表达即时生效的设置。",
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
                    title = "List Box",
                    summary = "可滚动的选项列表，支持单选、多选与键盘导航。",
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
                    title = "Item",
                    summary = "List Box / Select / Context Menu 使用的条目元素，也可以单独使用。",
                    docsUrl = "$DOCS_BASE/list-box",
                    docsNote = "vaadin-item 没有独立文档页面，用法见 List Box。",
                    sourcePath = "pages/Selection.kt",
                ),
            builder = ::buildItemPage,
        ),
        CatalogEntry(
            id = "select",
            group = Groups.SELECTION,
            meta =
                PageMeta(
                    title = "Select",
                    summary = "下拉单选：items 提供选项，value 是选中的字符串。",
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
                    title = "Combo Box",
                    summary = "可筛选的下拉选择：对象选项、itemLabelPath / itemValuePath、自定义值。",
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
                    title = "Multi-Select Combo Box",
                    summary = "多选下拉：选中项显示为 chip，selectedItems 是选中的对象数组。",
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
                    title = "Slider",
                    summary = "拖动选择数值，支持区间滑块、步进与只读状态。",
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

private fun buildCheckboxPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "checked 反映勾选状态；change 只在用户操作时触发，checked-changed 在属性被代码设置时也会触发。")
    val log = EventLog(6)
    val terms = createCheckbox { label = "我已阅读并同意条款" }
    terms.dataset["test"] = "checkbox-basic"
    // `change` only fires for user interaction, so the event stream stays readable; the read-out
    // shows `checked-changed` working for programmatic updates as well.
    terms.listen(CheckboxEvents.change) { log.add("change · ${terms.checked}") }
    val stateReadout = Readout("checked = false")
    terms.listen(CheckboxEvents.checkedChanged) { stateReadout.set("checked = ${terms.checked}") }
    val toggleFromCode =
        createButton { textContent = "程序化切换" }.apply {
            clickEvent.addHandler {
                terms.checked = !terms.checked
                log.add("checked-changed（程序设置）· ${terms.checked}")
            }
        }
    basic.body.append(example(null, column(terms, row(toggleFromCode, stateReadout.root), log.root)))
    view.body.append(basic.root)

    val states = section("三态与状态", "indeterminate 用于「部分选中」；readonly 只能看不能改，disabled 完全不可交互。")
    val partial = createCheckbox { label = "全选（部分选中）" }
    partial.indeterminate = true
    states.body.append(
        example(
            null,
            row(
                createCheckbox {
                    label = "已勾选"
                    checked = true
                },
                partial,
                createCheckbox {
                    label = "只读"
                    checked = true
                    readonly = true
                },
                createCheckbox {
                    label = "禁用"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    val readState = section("读取状态", "点击按钮读取所有勾选框当前状态。")
    val a = createCheckbox { label = "邮件通知" }
    val b = createCheckbox {
        label = "短信通知"
        checked = true
    }
    val readout = Readout("尚未读取")
    val readButton =
        createButton { textContent = "读取" }.apply {
            clickEvent.addHandler {
                readout.set("邮件=${a.checked} · 短信=${b.checked}")
            }
        }
    readState.body.append(example(null, column(a, b, readButton, readout.root)))
    view.body.append(readState.root)

    return view.root
}

private fun buildCheckboxGroupPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "checkbox-group 的 value 是数组，子项通过 value 属性区分。")
    val group =
        createCheckboxGroup {
            label = "使用的语言"
            helperText = "可以多选"
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

    val preset = section("预设值与校验", "value 可以预先赋值；required 在空选择时让 validate() 失败。")
    val skills =
        createCheckboxGroup {
            label = "技能（必填）"
            required = true
            errorMessage = "至少选择一项"
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
        createButton { textContent = "校验" }.apply {
            clickEvent.addHandler {
                result.set("校验：", if (skills.validate()) "通过" else "失败")
            }
        }
    skills.listen(CheckboxGroupEvents.valueChanged) {
        result.set("已选：", joinValues(skills.value).ifEmpty { "[]" })
    }
    preset.body.append(example(null, column(skills, row(validateButton, result.root))))
    view.body.append(preset.root)

    val states = section("只读与禁用", "")
    val readonlyGroup =
        createCheckboxGroup { label = "只读" }
    readonlyGroup.readonly = true
    readonlyGroup.append(
        createCheckbox {
            value = "a"
            textContent = "选项 A"
        }.apply { checked = true },
        createCheckbox {
            value = "b"
            textContent = "选项 B"
        },
    )
    val disabledGroup =
        createCheckboxGroup { label = "禁用" }
    disabledGroup.disabled = true
    disabledGroup.append(createCheckbox { textContent = "不可更改" })
    states.body.append(example(null, row(readonlyGroup, disabledGroup)))
    view.body.append(states.root)

    return view.root
}

private fun buildRadioGroupPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "同一 radio-group 内的 radio-button 互斥；value 是选中的字符串。")
    val group =
        createRadioGroup {
            label = "套餐"
            value = "standard"
        }
    group.dataset["test"] = "radio-group"
    group.append(
        createRadioButton {
            value = "basic"
            textContent = "基础版"
        },
        createRadioButton {
            value = "standard"
            textContent = "标准版"
        },
        createRadioButton {
            value = "pro"
            textContent = "专业版"
        },
    )
    val readout = Readout("value = standard")
    group.listen(RadioGroupEvents.valueChanged) {
        readout.set("value = ", group.value.ifEmpty { "（未选择）" })
    }
    basic.body.append(example(null, column(group, readout.root)))
    view.body.append(basic.root)

    val states = section("校验与状态", "required 让空值无法通过；readonly 保留选择但不能更改。")
    val required =
        createRadioGroup {
            label = "付款方式"
            required = true
            errorMessage = "请选择一种付款方式"
        }
    required.append(
        createRadioButton {
            value = "card"
            textContent = "银行卡"
        },
        createRadioButton {
            value = "transfer"
            textContent = "转账"
        },
    )
    val readonly =
        createRadioGroup {
            label = "只读"
            value = "yes"
        }
    readonly.readonly = true
    readonly.append(
        createRadioButton {
            value = "yes"
            textContent = "是"
        },
        createRadioButton {
            value = "no"
            textContent = "否"
        },
    )
    val result = Readout("尚未校验")
    val validateButton =
        createButton { textContent = "校验" }.apply {
            clickEvent.addHandler {
                result.set("校验：", if (required.validate()) "通过" else "失败")
            }
        }
    states.body.append(
        example(null, column(required, row(validateButton, result.root))),
        example(null, row(readonly)),
    )
    view.body.append(states.root)

    return view.root
}

private fun buildSwitchPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "switch 与 checkbox 共享状态模型，用 checked 读写。")
    val notifications = createSwitch { label = "接收发布通知" }
    notifications.checked = true
    notifications.dataset["test"] = "switch-basic"
    val readout = Readout("已开启")
    notifications.listen(SwitchEvents.checkedChanged) {
        readout.set("状态：", if (notifications.checked) "已开启" else "已关闭")
    }
    basic.body.append(example(null, column(notifications, readout.root)))
    view.body.append(basic.root)

    val effect = section("即时生效的联动", "开关可以直接驱动页面上的其他状态。")
    val compact = createSwitch { label = "紧凑模式（compact 主题）" }
    val preview =
        createButton { textContent = "预览按钮" }.withTheme("primary")
    compact.listen(SwitchEvents.checkedChanged) {
        preview.withTheme(if (compact.checked) "primary small" else "primary")
    }
    effect.body.append(example(null, column(compact, row(preview))))
    view.body.append(effect.root)

    val states = section("禁用", "")
    states.body.append(
        example(
            null,
            row(
                createSwitch {
                    label = "已开启但禁用"
                    checked = true
                    disabled = true
                },
                createSwitch {
                    label = "禁用"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view.root
}

private fun buildListBoxPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "selected 是选中项的下标（Number），未选中时为 undefined。")
    val single =
        createListBox()
    single.dataset["test"] = "list-box-single"
    single.append(
        createItem {
            value = "inbox"
            textContent = "收件箱"
        },
        createItem {
            value = "drafts"
            textContent = "草稿"
        },
        createItem {
            value = "sent"
            textContent = "已发送"
        },
    )
    val readout = Readout("selected = -")
    single.listen(ListBoxEvents.selectedChanged) {
        readout.set("selected = ", single.selected.toString())
    }
    basic.body.append(example(null, row(single, readout.root)))
    view.body.append(basic.root)

    val multiple = section("多选", "multiple 打开后使用 selectedValues 读取所有选中下标。")
    val multi = createListBox { this.multiple = true }
    multi.append(
        createItem { textContent = "阅读" },
        createItem { textContent = "写作" },
        createItem { textContent = "运动" },
        createItem { textContent = "旅行" },
    )
    val multiReadout = Readout("selectedValues = []")
    multi.listen(ListBoxEvents.selectedValuesChanged) {
        multiReadout.set("selectedValues = ", joinValues(multi.selectedValues).ifEmpty { "[]" })
    }
    multiple.body.append(example(null, row(multi, multiReadout.root)))
    view.body.append(multiple.root)

    val horizontal = section("方向与禁用", "orientation 控制排布方向，禁用后整组不可交互。")
    horizontal.body.append(
        example(
            null,
            row(
                createListBox { orientation = "horizontal" }.apply {
                    append(
                        createItem { textContent = "一" },
                        createItem { textContent = "二" },
                        createItem { textContent = "三" },
                    )
                },
                createListBox().apply {
                    disabled = true
                    append(
                        createItem { textContent = "禁用项一" },
                        createItem { textContent = "禁用项二" },
                    )
                },
            ),
        ),
    )
    view.body.append(horizontal.root)

    return view.root
}

private fun buildItemPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("独立使用", "vaadin-item 通常由 List Box / Select / Context Menu 内部创建；这里单独展示它的属性。")
    val log = EventLog(4)
    val items =
        row(
            createItem {
                label = "带 label 的条目"
                value = "one"
            }.apply { dataset["test"] = "item-one" },
            createItem { textContent = "默认插槽文本" },
            createItem {
                textContent = "已禁用"
                disabled = true
            },
        )
    basic.body.append(example(null, items))
    view.body.append(basic.root)

    val selection = section("选中状态", "selected 只是视觉状态，条目本身不维护选中逻辑。")
    val selectedItem =
        createItem {
            textContent = "被选中的条目"
            selected = true
        }
    val toggleButton =
        createButton { textContent = "切换 selected" }.apply {
            clickEvent.addHandler {
                selectedItem.selected = !selectedItem.selected
                log.add("selected = ${selectedItem.selected}")
            }
        }
    selection.body.append(example(null, column(selectedItem, toggleButton, log.root)))
    view.body.append(selection.root)

    return view.root
}

private fun buildSelectPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "items 接受 { label, value } 数组；下拉在浮层中渲染，value 是字符串。")
    val select =
        createSelect {
            label = "所在城市"
            placeholder = "请选择"
            value = "helsinki"
            items = js(CITY_ITEMS_JSON)
        }
    select.dataset["test"] = "select-basic"
    val readout = Readout("value = helsinki")
    select.listen(SelectEvents.valueChanged) {
        readout.set("value = ", select.value.ifEmpty { "（未选择）" })
    }
    basic.body.append(example(null, column(select, readout.root)))
    view.body.append(basic.root)

    val placeholderAndEmpty = section("占位符与可选空值", "placeholder 只在没有选中值时显示；allowCustomValue 由 Combo Box 提供，Select 只接受列表内的值。")
    val emptySelect =
        createSelect {
            label = "团队"
            placeholder = "尚未选择"
            items =
                js(
                    """[
                        {"label": "Engineering", "value": "eng"},
                        {"label": "Design", "value": "design"},
                        {"label": "维护中", "value": "maintenance", "disabled": true}
                    ]""",
                )
        }
    val emptyReadout = Readout("value = （空）")
    emptySelect.listen(SelectEvents.valueChanged) {
        emptyReadout.set("当前选择：", emptySelect.value.ifEmpty { "（空）" })
    }
    val clearButton =
        createButton { textContent = "清空" }.apply {
            clickEvent.addHandler { emptySelect.value = "" }
        }
    placeholderAndEmpty.body.append(example(null, column(emptySelect, row(clearButton, emptyReadout.root))))
    view.body.append(placeholderAndEmpty.root)

    val states = section("只读与禁用", "")
    states.body.append(
        example(
            null,
            row(
                createSelect {
                    label = "只读"
                    value = "bravo"
                    readonly = true
                    items = js(MEMBER_ITEMS_JSON)
                },
                createSelect {
                    label = "禁用"
                    value = "alpha"
                    disabled = true
                    items = js(MEMBER_ITEMS_JSON)
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view.root
}

private fun buildComboBoxPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "给定 itemLabelPath 与 itemValuePath 后，items 可以是任意对象。")
    val combo =
        createComboBox {
            label = "负责人"
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
        readout.set("value = ", combo.value.ifEmpty { "（空）" })
        log.add("value-changed · ${combo.value}")
    }
    basic.body.append(example(null, column(combo, readout.root)), example("事件流", log.root))
    view.body.append(basic.root)

    val custom = section("自定义值", "allowCustomValue 允许输入列表之外的值，此时 value 就是用户输入的文本。")
    val customCombo =
        createComboBox {
            label = "标签"
            allowCustomValue = true
            items = js("(['alpha', 'bravo', 'charlie'])")
            helperText = "可以输入任意值后回车"
        }
    val customReadout = Readout("自定义值：（空）")
    customCombo.listen(ComboBoxEvents.customValueSet) {
        customReadout.set("自定义值：", customCombo.value)
    }
    custom.body.append(example(null, column(customCombo, customReadout.root)))
    view.body.append(custom.root)

    val states = section("加载中与禁用", "loading 会显示加载指示；disabled 关闭全部交互。")
    val loading =
        createComboBox {
            label = "远程选项（加载中）"
            loading = true
            items = js("([])")
        }
    states.body.append(
        example(
            null,
            row(
                loading,
                createComboBox {
                    label = "禁用"
                    disabled = true
                    value = "bravo"
                    items = js("(['alpha', 'bravo', 'charlie'])")
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view.root
}

private fun buildMultiSelectComboBoxPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "选中项以 chip 展示；selectedItems 是选中对象的数组。")
    val multi =
        createMultiSelectComboBox {
            label = "标签"
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
    val readout = Readout("已选 0 项")
    multi.listen(MultiSelectComboBoxEvents.selectedItemsChanged) {
        readout.set("已选 ${multi.selectedItems.size} 项：", selectedNames(multi.selectedItems))
    }
    basic.body.append(example(null, column(multi, readout.root)))
    view.body.append(basic.root)

    val compactChips = section("紧凑显示", "collapseChips 把多余的 chip 折叠成计数；selectedItemsOnTop 让选中项排在列表前面。")
    val collapsed =
        createMultiSelectComboBox {
            label = "折叠 chip"
            collapseChips = true
            selectedItemsOnTop = true
            itemLabelPath = "name"
            itemValuePath = "id"
            items =
                js(
                    """[
                        {"id": "a", "name": "选项 A"},
                        {"id": "b", "name": "选项 B"},
                        {"id": "c", "name": "选项 C"},
                        {"id": "d", "name": "选项 D"},
                        {"id": "e", "name": "选项 E"}
                    ]""",
                )
        }
    compactChips.body.append(example(null, collapsed))
    view.body.append(compactChips.root)

    return view.root
}

private fun buildSliderPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "拖动时触发 input，松开后触发 change；value 是 Double。")
    val slider =
        createSlider {
            label = "音量"
            min = 0.0
            max = 100.0
            step = 5.0
            value = 40.0
            valueAlwaysVisible = true
        }
    slider.dataset["test"] = "slider-basic"
    val readout = Readout("value = 40")
    slider.listen(SliderEvents.input) { readout.set("数值 = ", slider.value.toString()) }
    slider.listen(SliderEvents.change) { readout.set("已提交数值 = ", slider.value.toString()) }
    basic.body.append(example(null, column(slider, readout.root)))
    view.body.append(basic.root)

    val range = section("区间滑块", "range-slider 的 value 是两元素数组，两个手柄分别控制下限与上限。")
    val rangeSlider =
        createRangeSlider {
            label = "价格区间"
            min = 0.0
            max = 1000.0
            step = 50.0
            value = js("([200, 700])")
            minMaxVisible = true
        }
    val rangeReadout = Readout("value = 200 - 700")
    rangeSlider.listen(RangeSliderEvents.valueChanged) {
        rangeReadout.set("区间 = ", joinValues(rangeSlider.value))
    }
    range.body.append(example(null, column(rangeSlider, rangeReadout.root)))
    view.body.append(range.root)

    val states = section("只读与禁用", "")
    states.body.append(
        example(
            null,
            row(
                createSlider {
                    label = "只读"
                    value = 60.0
                    readonly = true
                },
                createSlider {
                    label = "禁用"
                    value = 60.0
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view.root
}

/** Reads the `name` field of the selected item objects for the multi-select read-out. */
private fun selectedNames(items: Array<kotlin.js.JsAny?>): String =
    items.joinToString(", ") { item -> js("(item) => String(item.name)")(item) as String }
