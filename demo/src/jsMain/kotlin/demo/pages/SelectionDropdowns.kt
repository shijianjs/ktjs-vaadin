package demo.pages

import demo.tr
import demo.ui.Readout
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.column
import demo.ui.detailText
import demo.ui.example
import demo.ui.jsArray
import demo.ui.jsObject
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withClass
import demo.ui.withTheme
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.button.createButton
import vaadin.checkbox.CheckboxEvents
import vaadin.checkbox.createCheckbox
import vaadin.combobox.ComboBoxElement
import vaadin.combobox.ComboBoxEvents
import vaadin.combobox.ComboBoxItemModel
import vaadin.combobox.ComboBoxPartialMatchModes
import vaadin.combobox.createComboBox
import vaadin.icon.createIcon
import vaadin.multiselectcombobox.MultiSelectComboBoxEvents
import vaadin.multiselectcombobox.createMultiSelectComboBox
import vaadin.select.SelectEvents
import vaadin.select.SelectItemData
import vaadin.select.createSelect
import vaadin.select.createSelectItem
import vaadin.select.createSelectListBox
import vaadin.textarea.createTextArea
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

// The docs examples load these fixtures from a backend; the ids stay strings so the selected value
// of a combo box is a readable string, and two names are kept for the "Grace" filtering example.
private const val OWNERS_JSON =
    """
    [
      {"id": "ada", "name": "Ada Lovelace"},
      {"id": "grace", "name": "Grace Hopper"},
      {"id": "margaret", "name": "Margaret Hamilton"},
      {"id": "katherine", "name": "Katherine Johnson"}
    ]
    """

private const val SIZES_JSON =
    """
    [
      {"label": "XS (out of stock)", "value": "xs", "disabled": true},
      {"label": "S", "value": "s"},
      {"label": "M", "value": "m"},
      {"label": "L", "value": "l"},
      {"label": "XL", "value": "xl"}
    ]
    """

private const val DIVIDED_SORT_ITEMS_JSON =
    """
    [
      {"label": "Most recent first", "value": "recent"},
      {"component": "hr"},
      {"label": "Rating: high to low", "value": "rating-desc"},
      {"label": "Rating: low to high", "value": "rating-asc"},
      {"component": "hr"},
      {"label": "Price: high to low", "value": "price-desc"},
      {"label": "Price: low to high", "value": "price-asc"}
    ]
    """

private const val FRUITS_JSON = """["Apple", "Banana", "Orange", "Pear"]"""

private val countryNames: List<String> =
    listOf(
        "Finland", "Germany", "France", "Italy", "Spain", "Portugal", "Netherlands", "Belgium",
        "Sweden", "Norway", "Denmark", "Iceland", "Ireland", "Austria", "Switzerland", "Poland",
        "Czechia", "Slovakia", "Hungary", "Romania", "Bulgaria", "Greece", "Croatia", "Slovenia",
        "Estonia", "Latvia", "Lithuania", "Luxembourg", "Malta", "Cyprus",
    )

private const val COUNTRIES_JSON =
    """
    [
      {"id": 1, "name": "Finland"},
      {"id": 2, "name": "Germany"},
      {"id": 3, "name": "France"},
      {"id": 4, "name": "Italy"},
      {"id": 5, "name": "Spain"},
      {"id": 6, "name": "Portugal"},
      {"id": 7, "name": "Netherlands"},
      {"id": 8, "name": "Belgium"}
    ]
    """

internal fun buildSelectPage(meta: PageMeta): PageView {
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
    val cities =
        createSelect {
            label = tr("所在城市", "City")
            placeholder = tr("请选择", "Choose one")
            value = "helsinki"
            items = selectItems(CITIES_JSON)
        }
    cities.dataset["test"] = "select-basic"
    val readout = Readout("value = helsinki")
    cities.listen(SelectEvents.valueChanged) {
        readout.set("value = ", cities.value.ifEmpty { tr("（未选择）", "(none)") })
    }
    basic.body.append(example(null, column(cities, readout.root)))
    view.body.append(basic.root)

    val features =
        section(
            tr("基本特性", "Basic features"),
            tr(
                "label、helper-text、placeholder 与 prefix 图标组合成完整字段；tooltip 通过 slot 挂载。",
                "label, helper-text, placeholder and a prefix icon make up the complete field; the " +
                    "tooltip is attached through its slot.",
            ),
        )
    val sortBy =
        createSelect {
            label = tr("排序方式", "Sort by")
            helperText = tr("辅助信息", "Helper text")
            placeholder = tr("占位提示", "Placeholder")
            items = selectItems(SORT_ITEMS_JSON)
        }
    sortBy.append(
        createTooltip { text = tr("提示文本", "Tooltip text") }.apply { setAttribute("slot", "tooltip") },
        createIcon { icon = "vaadin:vaadin-h" }.apply { setAttribute("slot", "prefix") },
    )
    features.body.append(example(null, sortBy))
    view.body.append(features.root)

    val dividersAndDisabled =
        section(
            tr("分隔线与禁用条目", "Dividers and disabled items"),
            tr(
                "items 里用 { component: \"hr\" } 插入分隔线；单个条目可以用 disabled 标为不可选。",
                "Use { component: \"hr\" } in items for a divider; individual items can be marked disabled.",
            ),
        )
    val divided =
        createSelect {
            label = tr("排序方式", "Sort by")
            value = "recent"
            items = selectItems(DIVIDED_SORT_ITEMS_JSON)
        }
    val sizes =
        createSelect {
            label = tr("尺码", "Size")
            value = "xl"
            items = selectItems(SIZES_JSON)
        }
    dividersAndDisabled.body.append(example(null, row(divided, sizes)))
    view.body.append(dividersAndDisabled.root)

    val overlay =
        section(
            tr("浮层位置与宽度", "Overlay position and width"),
            tr(
                "no-vertical-overlap 禁止浮层覆盖输入框本身；" +
                    "--vaadin-select-overlay-width 单独设置下拉宽度。",
                "no-vertical-overlap keeps the overlay from covering the field itself; " +
                    "--vaadin-select-overlay-width widens the dropdown independently.",
            ),
        )
    val noOverlap =
        createSelect {
            label = tr("排序方式", "Sort by")
            noVerticalOverlap = true
            items = selectItems(SORT_ITEMS_JSON)
        }
    val employees = people(5)
    val wide =
        createSelect {
            label = tr("员工", "Employee")
            items =
                selectItems(
                    employees
                        .map { person ->
                            "{\"label\": \"${person.profession} - ${person.displayName}\", " +
                                "\"value\": \"${person.displayName}\"}"
                        }
                        .joinToString(",", "[", "]"),
                )
        }
    wide.style.setProperty("--vaadin-select-overlay-width", "350px")
    overlay.body.append(example(null, row(noOverlap, wide)))
    view.body.append(overlay.root)

    val placeholderSection =
        section(
            tr("占位符与清空", "Placeholder and clearing"),
            tr(
                "placeholder 只在没有选中值时显示；把 value 置空即可回到占位状态。",
                "placeholder only shows while nothing is selected; setting value to an empty string " +
                    "returns the field to it.",
            ),
        )
    val team =
        createSelect {
            label = tr("团队", "Team")
            placeholder = tr("尚未选择", "Nothing selected yet")
            items =
                selectItems(
                    """
                    [
                      {"label": "Engineering", "value": "eng"},
                      {"label": "Design", "value": "design"}
                    ]
                    """,
                )
        }
    val teamReadout = Readout(tr("value = （空）", "value = (empty)"))
    team.listen(SelectEvents.valueChanged) {
        teamReadout.set(tr("当前选择：", "Current value: "), team.value.ifEmpty { tr("（空）", "(empty)") })
    }
    val clear =
        createButton { textContent = tr("清空", "Clear") }.apply {
            clickEvent.addHandler { team.value = "" }
        }
    placeholderSection.body.append(example(null, column(team, row(clear, teamReadout.root))))
    view.body.append(placeholderSection.root)

    val emptySelection =
        section(
            tr("空选择项", "Empty selection item"),
            tr(
                "在下拉列表里放一个表示「不选择」的条目、并自定义它的文案，需要 Flow 侧的 " +
                    "setEmptySelectionCaption API，Web Component 没有对应属性，这里不演示。",
                "Adding an item that represents \"no selection\" with a custom caption requires the Flow " +
                    "setEmptySelectionCaption API; the web component has no equivalent property, so it " +
                    "is not shown here.",
            ),
        )
    view.body.append(emptySelection.root)

    val customLabel =
        section(
            tr("自定义条目标签", "Custom item label"),
            tr(
                "用默认插槽自行组装下拉内容；条目上的 label 属性决定选中后显示在输入框里的文本。",
                "Build the dropdown content in the default slot; the label attribute of each item is " +
                    "what the field shows once selected.",
            ),
        )
    val assignee = createSelect { label = tr("负责人", "Assignee") }
    val assigneeBox = createSelectListBox().apply { setAttribute("slot", "overlay") }
    employees.forEach { person ->
        assigneeBox.append(
            createSelectItem {
                value = "${person.id}"
                label = person.displayName
            }.apply {
                append(
                    column(
                        span(person.displayName),
                        span(person.profession),
                    ).withClass("person-item"),
                )
            },
        )
    }
    assignee.append(assigneeBox)
    assignee.value = "${employees.first().id}"
    customLabel.body.append(example(null, assignee))
    view.body.append(customLabel.root)

    val presentation =
        section(
            tr("自定义条目呈现", "Custom item presentation"),
            tr(
                "条目内部可以放头像与次要信息；选中值仍由 label 决定。",
                "Items can hold an avatar and secondary information; the selected value still comes " +
                    "from label.",
            ),
        )
    val doctors = createSelect { label = tr("选择医生", "Choose doctor") }
    doctors.style.width = "15em"
    val doctorBox = createSelectListBox().apply { setAttribute("slot", "overlay") }
    employees.forEach { person ->
        doctorBox.append(
            createSelectItem { value = "${person.id}" }.apply {
                append(
                    row(
                        vaadin.avatar.createAvatar {
                            name = person.displayName
                            img = person.pictureUrl
                        },
                        column(span(person.displayName), span(person.profession)),
                    ).withClass("person-item"),
                )
            },
        )
    }
    doctors.append(doctorBox)
    doctors.value = "${employees.first().id}"
    presentation.body.append(example(null, doctors))
    view.body.append(presentation.root)

    val states = section(tr("只读与禁用", "Read-only and disabled"))
    states.body.append(
        example(
            null,
            row(
                createSelect {
                    label = tr("只读", "Read-only")
                    value = "bravo"
                    readonly = true
                    items =
                        selectItems(
                            """
                            [
                              {"label": "Alpha", "value": "alpha"},
                              {"label": "Bravo", "value": "bravo"},
                              {"label": "Charlie", "value": "charlie"}
                            ]
                            """,
                        )
                },
                createSelect {
                    label = tr("禁用", "Disabled")
                    value = "alpha"
                    disabled = true
                    items =
                        selectItems(
                            """
                            [
                              {"label": "Alpha", "value": "alpha"},
                              {"label": "Bravo", "value": "bravo"}
                            ]
                            """,
                        )
                },
            ),
        ),
    )
    view.body.append(states.root)

    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "align-right 让文本右对齐，small 压缩高度，helper-above-field 把辅助信息放到标签下。",
                "align-right aligns the text to the end, small compresses the height and " +
                    "helper-above-field puts the helper text below the label.",
            ),
        )
    styles.body.append(
        example(
            null,
            createSelect {
                label = tr("标签", "Label")
                helperText = tr("辅助信息", "Helper text")
                value = "recent"
                items = selectItems(SORT_ITEMS_JSON)
            }.withTheme("align-right", "small", "helper-above-field"),
        ),
    )
    view.body.append(styles.root)

    return view
}

internal fun buildComboBoxPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "给定 itemLabelPath 与 itemValuePath 后，items 可以是任意对象。",
                "With itemLabelPath and itemValuePath set, items can be any object.",
            ),
        )
    val owners =
        createComboBox {
            label = tr("负责人", "Owner")
            itemLabelPath = "name"
            itemValuePath = "id"
            items = jsArray<JsAny>(OWNERS_JSON)
            value = "grace"
            clearButtonVisible = true
        }
    owners.dataset["test"] = "combo-box-basic"
    val readout = Readout("value = grace")
    owners.listen(ComboBoxEvents.valueChanged) {
        readout.set("value = ", owners.value.ifEmpty { tr("（空）", "(empty)") })
    }
    basic.body.append(example(null, column(owners, readout.root)))
    view.body.append(basic.root)

    val features =
        section(
            tr("基本特性", "Basic features"),
            tr(
                "helper-text、placeholder、clear-button-visible、prefix 图标与 tooltip 都是字段级配置。",
                "helper-text, placeholder, clear-button-visible, a prefix icon and a tooltip are all " +
                    "field-level settings.",
            ),
        )
    val featured =
        createComboBox {
            label = tr("标签", "Label")
            helperText = tr("辅助信息", "Helper text")
            placeholder = tr("占位提示", "Placeholder")
            clearButtonVisible = true
            items = jsArray("""["Value"]""")
        }
    featured.append(
        createTooltip { text = tr("提示文本", "Tooltip text") }.apply { setAttribute("slot", "tooltip") },
        createIcon { icon = "vaadin:search" }.apply { setAttribute("slot", "prefix") },
    )
    features.body.append(example(null, featured))
    view.body.append(features.root)

    val customValue =
        section(
            tr("自定义值", "Custom value entry"),
            tr(
                "allowCustomValue 允许输入列表之外的值；custom-value-set 事件携带新值，可以把它追加进 items。",
                "allowCustomValue accepts values outside the list; the custom-value-set event carries the " +
                    "new value, which can then be appended to items.",
            ),
        )
    val browsers = jsArray<JsAny>("""["Chrome", "Edge", "Firefox", "Safari"]""")
    val browser =
        createComboBox {
            label = tr("浏览器", "Browser")
            helperText = tr("选择或输入一个浏览器", "Select or type a browser")
            allowCustomValue = true
            items = browsers
        }
    val customReadout = Readout(tr("自定义值：（空）", "Custom value: (empty)"))
    browser.listen(ComboBoxEvents.customValueSet) {
        val value = detailText(it)
        customReadout.set(tr("自定义值：", "Custom value: "), value)
        browser.items = appendItem(browser.items ?: browsers, value)
    }
    customValue.body.append(example(null, column(browser, customReadout.root)))
    view.body.append(customValue.root)

    val classNames =
        section(
            tr("选项类名", "Item class names"),
            tr(
                "itemClassNameGenerator 返回的类名会加到每个下拉条目上，可以据此改变颜色。",
                "The class name returned by itemClassNameGenerator is added to every dropdown item, " +
                    "which is enough to colour it.",
            ),
        )
    val fruit =
        createComboBox {
            label = tr("水果", "Fruit")
            items = jsArray(FRUITS_JSON)
            itemClassNameGenerator = { item -> fruitClass(item.unsafeCast<String>()) }
        }
    classNames.body.append(example(null, fruit))
    view.body.append(classNames.root)

    val autoOpen =
        section(
            tr("自动展开", "Auto open"),
            tr(
                "auto-open-disabled 阻止聚焦或输入时自动展开下拉，只在点击切换按钮时打开。",
                "auto-open-disabled keeps the dropdown closed on focus or typing; only the toggle button " +
                    "opens it.",
            ),
        )
    val country =
        createComboBox {
            label = tr("国家", "Country")
            itemLabelPath = "name"
            itemValuePath = "id"
            items = jsArray<JsAny>(COUNTRIES_JSON)
            autoOpenDisabled = true
        }
    autoOpen.body.append(example(null, country))
    view.body.append(autoOpen.root)

    val popupWidth =
        section(
            tr("浮层宽度", "Popup width"),
            tr(
                "--vaadin-combo-box-overlay-width 让下拉比输入框更宽，适合展示较长的标签。",
                "--vaadin-combo-box-overlay-width makes the dropdown wider than the field, which suits " +
                    "long labels.",
            ),
        )
    val employeeItems = personItems(people(5), withDisplayName = true)
    val employee =
        createComboBox {
            label = tr("员工", "Employee")
            itemLabelPath = "displayName"
            itemValuePath = "id"
            items = employeeItems
        }
    employee.style.setProperty("--vaadin-combo-box-overlay-width", "350px")
    popupWidth.body.append(example(null, employee))
    view.body.append(popupWidth.root)

    val customFiltering =
        section(
            tr("自定义筛选", "Custom filtering"),
            tr(
                "设置 filteredItems 并监听 filter-changed，可以完全接管匹配逻辑。",
                "Setting filteredItems and listening to filter-changed takes over the matching logic " +
                    "entirely.",
            ),
        )
    val filterPeople = people(6)
    val filterItems = personItems(filterPeople, withDisplayName = true)
    val filterReadout = Readout(tr("共 6 项", "6 items"))
    val filtered =
        createComboBox {
            label = tr("员工", "Employee")
            itemLabelPath = "displayName"
            items = filterItems
            filteredItems = filterItems
        }
    filtered.listen(ComboBoxEvents.filterChanged) {
        val filter = detailText(it).lowercase()
        val matches =
            filterItems.filterIndexed { index, _ ->
                filterPeople[index].displayName.lowercase().startsWith(filter)
            }.toTypedArray()
        filtered.filteredItems = matches
        filterReadout.set(tr("匹配 ${matches.size} 项", "${matches.size} items match"))
    }
    customFiltering.body.append(example(null, column(filtered, filterReadout.root)))
    view.body.append(customFiltering.root)

    val partialMatch =
        section(
            tr("部分匹配模式", "Partial match mode"),
            tr(
                "partial-match-mode 决定输入内容只匹配开头、匹配任意位置还是必须完全匹配。",
                "partial-match-mode decides whether the typed text matches the start, any position, or " +
                    "must match exactly.",
            ),
        )
    val partial =
        createComboBox {
            label = tr("国家", "Country")
            itemLabelPath = "name"
            itemValuePath = "id"
            items = jsArray<JsAny>(COUNTRIES_JSON)
            partialMatchMode = ComboBoxPartialMatchModes.firstMatch
        }
    partialMatch.body.append(example(null, partial))
    view.body.append(partialMatch.root)

    val lazyLoading =
        section(
            tr("懒加载", "Lazy loading"),
            tr(
                "dataProvider 按页取数：参数带 page、pageSize 与当前 filter，回调返回这一页与总数。",
                "dataProvider pages the data: it receives page, pageSize and the current filter, and " +
                    "calls back with one page plus the total count.",
            ),
        )
    val lazyReadout = Readout(tr("尚未请求", "No request yet"))
    val lazy =
        createComboBox {
            label = tr("国家", "Country")
            dataProvider = { params, callback ->
                val filter = params.filter.lowercase()
                val matching = countryNames.filter { it.lowercase().startsWith(filter) }
                val page = params.page.toInt()
                val pageSize = params.pageSize.toInt()
                val slice = matching.drop(page * pageSize).take(pageSize)
                lazyReadout.set(
                    tr(
                        "第 ${page + 1} 页 · 每页 $pageSize 项 · 共 ${matching.size} 项",
                        "Page ${page + 1} · $pageSize per page · ${matching.size} in total",
                    ),
                )
                callback(slice.map { it.unsafeCast<JsAny?>() }.toTypedArray(), matching.size.toDouble())
            }
        }
    lazyLoading.body.append(example(null, column(lazy, lazyReadout.root)))
    view.body.append(lazyLoading.root)

    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "required 与 allowed-char-pattern 一起约束输入；错误文案由 error-message 提供。",
                "required and allowed-char-pattern constrain the input together; error-message supplies " +
                    "the failure text.",
            ),
        )
    val countryCode =
        createComboBox {
            label = tr("国家代码", "Country code")
            helperText = tr("两位大写 ISO 国家代码", "2-letter uppercase ISO country code")
            this.required = true
            allowedCharPattern = "[A-Z]"
            errorMessage = tr("此项为必填", "Field is required")
            allowCustomValue = true
            items = jsArray("""["DE", "FI", "US"]""")
        }
    val validationResult = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                validationResult.set(
                    tr("校验：", "Validation: "),
                    if (countryCode.validate()) tr("通过", "passed") else tr("失败", "failed"),
                )
            }
        }
    validation.body.append(example(null, column(countryCode, row(validateButton, validationResult.root))))
    view.body.append(validation.root)

    val presentation =
        section(
            tr("自定义条目呈现", "Custom item presentation"),
            tr(
                "renderer 自己构建条目 DOM：头像、姓名与职业，与保留模式的其他示例一样逐个节点组装。",
                "renderer builds each item's DOM itself: avatar, name and profession, assembled node by " +
                    "node like every other retained-mode example.",
            ),
        )
    val doctors = people(6)
    val doctorItems = personItems(doctors, withDisplayName = true)
    // `model.index` counts within `filteredItems`, so the renderer needs the list behind the current
    // filter rather than the full one.
    var visibleDoctors = doctors
    val doctorCombo =
        createComboBox {
            label = tr("选择医生", "Choose doctor")
            itemLabelPath = "displayName"
            items = doctorItems
            filteredItems = doctorItems
            renderer = { container, _, model -> renderPersonItem(container, model, visibleDoctors) }
        }
    doctorCombo.style.setProperty("--vaadin-combo-box-overlay-width", "16em")
    doctorCombo.listen(ComboBoxEvents.filterChanged) {
        val filter = detailText(it).lowercase()
        visibleDoctors =
            doctors.filter { person ->
                "${person.displayName} ${person.profession}".lowercase().contains(filter)
            }
        doctorCombo.filteredItems = personItems(visibleDoctors, withDisplayName = true)
        doctorCombo.requestContentUpdate()
    }
    presentation.body.append(example(null, doctorCombo))
    view.body.append(presentation.root)

    val states = section(tr("只读与禁用", "Read-only and disabled"))
    states.body.append(
        example(
            null,
            row(
                createComboBox {
                    label = tr("只读", "Read-only")
                    items = jsArray("""["Value"]""")
                    value = "Value"
                    readonly = true
                },
                createComboBox {
                    label = tr("禁用", "Disabled")
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "align-right、small 与 helper-above-field 同样适用于 combo-box。",
                "align-right, small and helper-above-field work for the combo box as well.",
            ),
        )
    styles.body.append(
        example(
            null,
            createComboBox {
                label = tr("标签", "Label")
                helperText = tr("辅助信息", "Helper text")
                items = jsArray("""["Value"]""")
                value = "Value"
            }.withTheme("align-right", "small", "helper-above-field"),
        ),
    )
    view.body.append(styles.root)

    return view
}

internal fun buildMultiSelectComboBoxPage(meta: PageMeta): PageView {
    val view = page(meta)

    val countries = jsArray<JsAny>(COUNTRIES_JSON)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "item-id-path 用来比较对象身份；selectedItems 是选中对象的数组。",
                "item-id-path compares object identity; selectedItems holds the selected objects.",
            ),
        )
    val multi =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
        }
    multi.dataset["test"] = "multi-select-combo-box"
    multi.style.width = "300px"
    val readout = Readout(tr("已选 0 项", "0 selected"))
    multi.listen(MultiSelectComboBoxEvents.selectedItemsChanged) {
        readout.set(
            tr("已选 ${multi.selectedItems.size} 项：", "${multi.selectedItems.size} selected: "),
            itemNames(multi.selectedItems),
        )
    }
    basic.body.append(example(null, column(multi, readout.root)))
    view.body.append(basic.root)

    val selection =
        section(
            tr("预设选中项", "Preselected items"),
            tr(
                "selectedItems 可以直接赋值，chip 数量随选中项变化。",
                "selectedItems can be assigned directly; the number of chips follows it.",
            ),
        )
    val preset =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            selectedItems = countries.copyOfRange(0, 4)
        }
    preset.style.width = "300px"
    selection.body.append(example(null, preset))
    view.body.append(selection.root)

    val selectionChange =
        section(
            tr("选择变更", "Selection change"),
            tr(
                "selected-items-changed 的 detail 带当前选中数组，可以同步到其他字段。",
                "The detail of selected-items-changed carries the current selection, which can drive " +
                    "another field.",
            ),
        )
    val summary = createTextArea {
        label = tr("已选国家", "Selected Countries")
        readonly = true
    }
    val changing =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
        }
    changing.listen(MultiSelectComboBoxEvents.selectedItemsChanged) {
        summary.value = itemNames(changing.selectedItems)
    }
    selectionChange.body.append(example(null, row(changing, summary)))
    view.body.append(selectionChange.root)

    val readonly =
        section(
            tr("只读", "Read-only"),
            tr(
                "readonly 保留 chip 展示，但用户不能添加或移除。",
                "readonly keeps the chips visible but blocks adding and removing.",
            ),
        )
    val readonlyBox =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            selectedItems = countries.copyOfRange(0, 4)
            this.readonly = true
        }
    readonlyBox.style.width = "300px"
    readonly.body.append(example(null, readonlyBox))
    view.body.append(readonly.root)

    val autoExpand =
        section(
            tr("自动展开", "Auto expand"),
            tr(
                "auto-expand-horizontally / auto-expand-vertically 让字段随 chip 数量变大，而不是滚动。",
                "auto-expand-horizontally / auto-expand-vertically let the field grow with the chips " +
                    "instead of scrolling.",
            ),
        )
    val expanding =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            selectedItems = countries.copyOfRange(0, 4)
            autoExpandHorizontally = true
            autoExpandVertically = true
        }
    autoExpand.body.append(example(null, expanding))
    view.body.append(autoExpand.root)

    val selectedOnTop =
        section(
            tr("选中项置顶", "Selected items on top"),
            tr(
                "selected-items-on-top 把已选项排到下拉列表最前面，长列表里更容易核对。",
                "selected-items-on-top moves the selected entries to the top of the dropdown, which helps " +
                    "in long lists.",
            ),
        )
    val onTop =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            selectedItems = countries.copyOfRange(4, 6)
            selectedItemsOnTop = true
        }
    selectedOnTop.body.append(example(null, onTop))
    view.body.append(selectedOnTop.root)

    val collapseChipsSection =
        section(
            tr("折叠 chip", "Collapse chips"),
            tr(
                "collapseChips 把溢出的 chip 折叠成计数，可以由页面上的开关实时切换。",
                "collapseChips folds the overflowing chips into a counter and can be toggled live.",
            ),
        )
    val collapsing =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            selectedItems = countries.copyOfRange(0, 3)
            collapseChips = true
        }
    collapsing.style.width = "250px"
    val collapseToggle = createCheckbox { label = tr("折叠 chip", "Collapse chips") }
    collapseToggle.checked = true
    collapseToggle.listen(CheckboxEvents.checkedChanged) { collapsing.collapseChips = collapseToggle.checked }
    collapseChipsSection.body.append(example(null, column(collapsing, collapseToggle)))
    view.body.append(collapseChipsSection.root)

    val classNames =
        section(
            tr("选项类名", "Item class names"),
            tr(
                "itemClassNameGenerator 同时作用在 chip 与下拉条目上。",
                "itemClassNameGenerator applies to both the chips and the dropdown items.",
            ),
        )
    val fruits = jsArray<JsAny>(FRUITS_JSON)
    val fruit =
        createMultiSelectComboBox {
            label = tr("水果", "Fruit")
            items = fruits
            selectedItems = fruits.copyOfRange(0, 2)
            itemClassNameGenerator = { item -> fruitClass(item.unsafeCast<String>()) }
        }
    fruit.style.width = "300px"
    classNames.body.append(example(null, fruit))
    view.body.append(classNames.root)

    val i18nSection =
        section(
            tr("国际化", "Internationalization"),
            tr(
                "i18n 覆盖清除、选中、取消与计数的提示文案；示例沿用官方文档的德语字符串。",
                "i18n overrides the cleared / selected / deselected / counter messages; the example keeps " +
                    "the German strings of the official documentation.",
            ),
        )
    val localized =
        createMultiSelectComboBox {
            label = "Länder"
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            i18n =
                jsObject(
                    """
                    {
                      "cleared": "Alle Einträge entfernt",
                      "focused": " ausgewählt. Drücke Rücktaste zum Entfernen",
                      "selected": " hinzugefügt",
                      "deselected": " entfernt",
                      "total": "{count} Einträge ausgewählt"
                    }
                    """,
                )
        }
    localized.style.width = "300px"
    i18nSection.body.append(example(null, localized))
    view.body.append(i18nSection.root)

    return view
}

/** Parses a JSON array of `{label, value, ...}` entries into the type `<vaadin-select>` takes. */
private fun selectItems(json: String): Array<SelectItemData> =
    jsArray<JsAny>(json).unsafeCast<Array<SelectItemData>>()

/** Appends a typed string to a JS array without rebuilding the original items. */
private fun appendItem(items: Array<JsAny>, value: String): Array<JsAny> =
    js("(items, value) => [...items, value]")(items, value).unsafeCast<Array<JsAny>>()

/** Reads the `name` field of the selected country objects. */
private fun itemNames(items: Array<JsAny>): String =
    items.joinToString(", ") { item -> js("(item) => String(item.name)")(item) as String }

/** Maps a fruit to the class name used by the docs theme for combo box items. */
private fun fruitClass(fruit: String): String =
    when (fruit) {
        "Apple" -> "coral"
        "Banana" -> "gold"
        "Orange" -> "orange"
        "Pear" -> "yellowgreen"
        else -> ""
    }

/** Renders one combo box item as avatar + name + profession. */
private fun renderPersonItem(container: HTMLElement, model: ComboBoxItemModel, peopleList: List<Person>) {
    val index = model.index.toInt()
    val person = peopleList.getOrNull(index) ?: return
    container.replaceChildren(
        row(
            vaadin.avatar.createAvatar {
                name = person.displayName
                img = person.pictureUrl
            },
            column(span(person.displayName), span(person.profession)),
        ).withClass("person-item"),
    )
}
