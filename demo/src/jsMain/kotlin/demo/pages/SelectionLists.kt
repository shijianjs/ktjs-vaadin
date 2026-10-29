package demo.pages

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
import demo.ui.span
import demo.ui.withClass
import vaadin.a11ybase.ListOrientations
import vaadin.avatar.createAvatar
import vaadin.button.createButton
import vaadin.item.createItem
import vaadin.listbox.ListBoxEvents
import vaadin.listbox.createListBox
import vaadin.slider.RangeSliderEvents
import vaadin.slider.SliderEvents
import vaadin.slider.createRangeSlider
import vaadin.slider.createSlider
import web.dom.clickEvent
import web.events.addHandler
import web.html.HtmlTagName
import demo.ui.element
import demo.ui.joinValues

internal fun buildListBoxPage(meta: PageMeta): PageView {
    val view = page(meta)

    val dividers =
        section(
            tr("分隔线", "Dividers"),
            tr(
                "列表里可以混入 <hr> 作为分组分隔线，它不参与选择。",
                "A list can mix in <hr> elements as group dividers; they take no part in selection.",
            ),
        )
    val separated = createListBox { multiple = true }
    separated.selectedValues = arrayOf(0.0, 2.0, 3.0)
    separated.append(
        createItem { textContent = tr("显示负责人", "Show assignee") },
        createItem { textContent = tr("显示截止日期", "Show due date") },
        createItem { textContent = tr("显示状态", "Show status") },
        element(HtmlTagName.hr),
        createItem { textContent = tr("显示缩略图", "Show thumbnail") },
        createItem { textContent = tr("显示预览", "Show preview") },
    )
    val separatedReadout = Readout("selectedValues = 0, 2, 3")
    separated.listen(ListBoxEvents.selectedValuesChanged) {
        separatedReadout.set("selectedValues = ", joinValues(separated.selectedValues).ifEmpty { "[]" })
    }
    dividers.body.append(example(null, column(separated, separatedReadout.root)))
    view.body.append(dividers.root)

    val disabledItems =
        section(
            tr("禁用条目", "Disabled items"),
            tr(
                "单个条目可以单独禁用，列表其余部分仍可交互。",
                "Individual items can be disabled while the rest of the list stays interactive.",
            ),
        )
    val withDisabled = createListBox()
    withDisabled.selected = 0.0
    withDisabled.append(
        createItem { textContent = tr("进行中 (2)", "In progress (2)") },
        createItem { textContent = tr("已完成 (4)", "Done (4)") },
        createItem {
            textContent = tr("已取消 (0)", "Cancelled (0)")
            disabled = true
        },
    )
    disabledItems.body.append(example(null, withDisabled))
    view.body.append(disabledItems.root)

    val single =
        section(
            tr("单选", "Single selection"),
            tr(
                "selected 是选中项的下标（Double），未选中时为 undefined。",
                "selected is the index of the selected item (Double); it is undefined when nothing is " +
                    "selected.",
            ),
        )
    val singleBox = createListBox()
    singleBox.dataset["test"] = "list-box-single"
    singleBox.selected = 0.0
    singleBox.append(
        createItem { textContent = tr("进行中", "In progress") },
        createItem { textContent = tr("已完成", "Done") },
        createItem { textContent = tr("已取消", "Cancelled") },
    )
    val readout = Readout("selected = 0")
    singleBox.listen(ListBoxEvents.selectedChanged) {
        readout.set("selected = ", singleBox.selected.toString())
    }
    single.body.append(example(null, row(singleBox, readout.root)))
    view.body.append(single.root)

    val multipleSection =
        section(
            tr("多选", "Multiple selection"),
            tr(
                "multiple 打开后用 selectedValues 读写所有选中下标；列表可滚动，固定高度即可。",
                "With multiple on, selectedValues reads and writes every selected index; the list " +
                    "scrolls inside a fixed height.",
            ),
        )
    val multiBox = createListBox { multiple = true }
    multiBox.selectedValues = arrayOf(0.0, 3.0)
    multiBox.style.height = "200px"
    multiBox.append(
        *people(20).map { person ->
            createItem { textContent = person.displayName }
        }.toTypedArray(),
    )
    val multiReadout = Readout("selectedValues = 0, 3")
    multiBox.listen(ListBoxEvents.selectedValuesChanged) {
        multiReadout.set("selectedValues = ", joinValues(multiBox.selectedValues).ifEmpty { "[]" })
    }
    multipleSection.body.append(example(null, column(multiBox, multiReadout.root)))
    view.body.append(multipleSection.root)

    val presentation =
        section(
            tr("自定义条目呈现", "Custom item presentation"),
            tr(
                "条目内容是普通 DOM：头像 + 姓名 + 职业，随选中状态一并高亮。",
                "Item content is plain DOM: avatar, name and profession, highlighted together with the " +
                    "selection state.",
            ),
        )
    val rich = createListBox { multiple = true }
    rich.selectedValues = arrayOf(0.0, 2.0)
    rich.append(
        *people(5).map { person ->
            createItem {
                append(
                    row(
                        createAvatar {
                            name = person.displayName
                            img = person.pictureUrl
                        },
                        column(span(person.displayName), span(person.profession)),
                    ).withClass("person-item"),
                )
            }
        }.toTypedArray(),
    )
    presentation.body.append(example(null, rich))
    view.body.append(presentation.root)

    val orientationSection =
        section(
            tr("方向与禁用", "Orientation and disabled state"),
            tr(
                "orientation 控制排布方向，禁用后整组不可交互。",
                "orientation sets the layout direction; disabling blocks the whole group.",
            ),
        )
    val horizontal = createListBox { orientation = ListOrientations.horizontal }
    horizontal.append(
        createItem { textContent = tr("一", "One") },
        createItem { textContent = tr("二", "Two") },
        createItem { textContent = tr("三", "Three") },
    )
    val disabledBox = createListBox()
    disabledBox.disabled = true
    disabledBox.append(
        createItem { textContent = tr("禁用项一", "Disabled item 1") },
        createItem { textContent = tr("禁用项二", "Disabled item 2") },
    )
    orientationSection.body.append(example(null, row(horizontal, disabledBox)))
    view.body.append(orientationSection.root)

    return view
}

internal fun buildItemPage(meta: PageMeta): PageView {
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

internal fun buildSliderPage(meta: PageMeta): PageView {
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
        }
    slider.dataset["test"] = "slider-basic"
    val readout = Readout("value = 40")
    slider.listen(SliderEvents.input) { readout.set(tr("数值 = ", "Value = "), slider.value.toString()) }
    slider.listen(SliderEvents.change) {
        readout.set(tr("已提交数值 = ", "Committed value = "), slider.value.toString())
    }
    basic.body.append(example(null, column(slider, readout.root)))
    view.body.append(basic.root)

    val features =
        section(
            tr("辅助信息", "Helper text"),
            tr(
                "helper-text 用来解释滑块的单位或含义。",
                "helper-text explains the unit or the meaning of the slider.",
            ),
        )
    features.body.append(
        example(
            null,
            createSlider {
                label = tr("标签", "Label")
                helperText = tr("辅助信息", "Helper text")
            },
        ),
    )
    view.body.append(features.root)

    val minMax =
        section(
            tr("最小值与最大值标签", "Min & max labels"),
            tr(
                "min-max-visible 在轨道两端显示取值范围。",
                "min-max-visible shows the range at both ends of the track.",
            ),
        )
    minMax.body.append(
        example(
            null,
            createSlider {
                label = tr("温度", "Temperature")
                min = 0.0
                max = 100.0
                value = 50.0
                minMaxVisible = true
            },
        ),
    )
    view.body.append(minMax.root)

    val alwaysVisible =
        section(
            tr("常显数值", "Persistent value display"),
            tr(
                "value-always-visible 让数值气泡常驻，不必等用户拖动才显示。",
                "value-always-visible keeps the value bubble on screen instead of only while dragging.",
            ),
        )
    alwaysVisible.body.append(
        example(
            null,
            createSlider {
                label = tr("亮度", "Brightness")
                min = 0.0
                max = 100.0
                value = 75.0
                valueAlwaysVisible = true
            },
        ),
    )
    view.body.append(alwaysVisible.root)

    val stepSection =
        section(
            tr("步进", "Step"),
            tr(
                "step 决定键盘与拖动的增量，也可以是非整数。",
                "step sets the increment of both keyboard and pointer input; fractions are allowed.",
            ),
        )
    val stepped =
        createSlider {
            label = tr("音量", "Volume")
            min = 0.0
            max = 10.0
            value = 5.0
            step = 0.5
            valueAlwaysVisible = true
        }
    val stepReadout = Readout("value = 5.0")
    stepped.listen(SliderEvents.valueChanged) { stepReadout.set("value = ", stepped.value.toString()) }
    stepSection.body.append(example(null, column(stepped, stepReadout.root)))
    view.body.append(stepSection.root)

    val range =
        section(
            tr("区间滑块", "Range slider"),
            tr(
                "range-slider 的 value 是两元素数组，两个手柄分别控制下限与上限；" +
                    "accessible-name-start / accessible-name-end 给两个手柄各自的读屏名称。",
                "range-slider takes a two-element value; the handles control the lower and the upper bound, " +
                    "and accessible-name-start / accessible-name-end name them for screen readers.",
            ),
        )
    val rangeSlider =
        createRangeSlider {
            label = tr("价格区间", "Price range")
            min = 0.0
            max = 1000.0
            step = 50.0
            value = arrayOf(200.0, 800.0)
            accessibleNameStart = tr("最低价格", "Minimum price")
            accessibleNameEnd = tr("最高价格", "Maximum price")
        }
    val rangeReadout = Readout("value = 200 - 800")
    rangeSlider.listen(RangeSliderEvents.valueChanged) {
        rangeReadout.set(tr("区间 = ", "Range = "), joinValues(rangeSlider.value))
    }
    range.body.append(example(null, column(rangeSlider, rangeReadout.root)))
    view.body.append(range.root)

    val rangeValidation =
        section(
            tr("区间校验", "Range validation"),
            tr(
                "区间跨度不满足业务规则时，把 invalid 置真并给出 error-message。",
                "When the span breaks the business rule, set invalid and provide an error-message.",
            ),
        )
    val priceRange =
        createRangeSlider {
            label = tr("价格区间", "Price Range")
            min = 0.0
            max = 1000.0
            step = 50.0
            value = arrayOf(200.0, 800.0)
        }
    priceRange.listen(RangeSliderEvents.change) {
        val start = priceRange.value[0]
        val end = priceRange.value[1]
        if (end - start < 200.0) {
            priceRange.invalid = true
            priceRange.errorMessage = tr("价格区间至少 200", "Price range must span at least $200")
        } else {
            priceRange.invalid = false
        }
    }
    rangeValidation.body.append(example(null, priceRange))
    view.body.append(rangeValidation.root)

    val valueModes =
        section(
            tr("值变更模式", "Value change modes"),
            tr(
                "Web Component 只有 input（拖动中）与 change（提交）两个时机；" +
                    "Flow 的 eager / lazy 模式属于服务端包装，不在本页范围。",
                "The web component only has input (while dragging) and change (committed); the Flow " +
                    "eager / lazy modes belong to the server-side wrapper and are out of scope here.",
            ),
        )
    val modeSlider =
        createSlider {
            label = tr("评分", "Rating")
            min = 0.0
            max = 10.0
            step = 1.0
            value = 7.0
        }
    val liveValue = Readout(tr("拖动中：7", "Live: 7"))
    val committedValue = Readout(tr("已提交：7", "Committed: 7"))
    modeSlider.listen(SliderEvents.input) { liveValue.set(tr("拖动中：", "Live: "), modeSlider.value.toString()) }
    modeSlider.listen(SliderEvents.change) {
        committedValue.set(tr("已提交：", "Committed: "), modeSlider.value.toString())
    }
    valueModes.body.append(example(null, column(modeSlider, row(liveValue.root, committedValue.root))))
    view.body.append(valueModes.root)

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
