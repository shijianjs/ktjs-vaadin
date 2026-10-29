package demo.pages.selection

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.CITIES_JSON
import demo.pages.fixtures.SORT_ITEMS_JSON
import demo.pages.fixtures.people
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withClass
import demo.ui.withTheme
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import vaadin.avatar.createAvatar
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.select.SelectEvents
import vaadin.select.SelectItemData
import vaadin.select.createSelect
import vaadin.select.createSelectItem
import vaadin.select.createSelectListBox
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val selectEntry: CatalogEntry =
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
                sourcePath = "pages/selection/SelectPage.kt",
            ),
        builder = ::buildSelectPage,
    )

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

/** Parses a JSON array of `{label, value, ...}` entries into the type `<vaadin-select>` takes. */
private fun selectItems(json: String): Array<SelectItemData> =
    jsArray<JsAny>(json).unsafeCast<Array<SelectItemData>>()
