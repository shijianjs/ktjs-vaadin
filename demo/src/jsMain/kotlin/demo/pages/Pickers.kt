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
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.datepicker.DatePickerEvents
import vaadin.datepicker.createDatePicker
import vaadin.datetimepicker.DateTimePickerEvents
import vaadin.datetimepicker.createDateTimePicker
import vaadin.timepicker.TimePickerEvents
import vaadin.timepicker.createTimePicker
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val pickerEntries: List<CatalogEntry> =
    listOf(
        CatalogEntry(
            id = "date-picker",
            group = Groups.PICKERS,
            meta =
                PageMeta(
                    title = "Date Picker",
                    summary = "日期选择：值格式为 ISO 日期字符串，支持范围、禁用日期、周数与手动输入。",
                    docsUrl = "$DOCS_BASE/date-picker",
                    sourcePath = "pages/Pickers.kt",
                ),
            builder = ::buildDatePickerPage,
        ),
        CatalogEntry(
            id = "time-picker",
            group = Groups.PICKERS,
            meta =
                PageMeta(
                    title = "Time Picker",
                    summary = "时间选择：值格式为 HH:mm，可限制范围与步进精度。",
                    docsUrl = "$DOCS_BASE/time-picker",
                    sourcePath = "pages/Pickers.kt",
                ),
            builder = ::buildTimePickerPage,
        ),
        CatalogEntry(
            id = "date-time-picker",
            group = Groups.PICKERS,
            meta =
                PageMeta(
                    title = "Date-Time Picker",
                    summary = "日期 + 时间组合输入，值是 ISO 日期时间字符串。",
                    docsUrl = "$DOCS_BASE/date-time-picker",
                    sourcePath = "pages/Pickers.kt",
                ),
            builder = ::buildDateTimePickerPage,
        ),
    )

private fun buildDatePickerPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "value 是 YYYY-MM-DD 字符串；清空按钮把值置为空，输入非法日期触发 unparsable-change。")
    val log = EventLog(6)
    val datePicker =
        createDatePicker {
            label = "交付日期"
            value = "2026-10-25"
            clearButtonVisible = true
            helperText = "可以直接输入，也可以从日历选择"
        }
    datePicker.dataset["test"] = "date-picker-basic"
    val readout = Readout("value = 2026-10-25")
    datePicker.listen(DatePickerEvents.valueChanged) {
        readout.set("value = ", datePicker.value.ifEmpty { "（空）" })
        log.add("value-changed · ${datePicker.value}")
    }
    datePicker.listen(DatePickerEvents.unparsableChange) { log.add("unparsable-change") }

    basic.body.append(example(null, column(datePicker, readout.root)), example("事件流", log.root))
    view.body.append(basic.root)

    val range = section("范围与禁用日期", "min / max 划定可选区间，isDateDisabled 可以进一步排除个别日期。")
    val ranged =
        createDatePicker {
            label = "十月可选日期（13 号不可选）"
            min = "2026-10-01"
            max = "2026-10-31"
            value = "2026-10-06"
            showWeekNumbers = true
            isDateDisabled = { date -> date.day == 13.0 }
        }
    val rangedReadout = Readout("value = 2026-10-06")
    ranged.listen(DatePickerEvents.valueChanged) {
        rangedReadout.set("区间值：", ranged.value.ifEmpty { "（空）" })
    }
    range.body.append(example(null, column(ranged, rangedReadout.root)))
    view.body.append(range.root)

    val manual = section("手动输入与状态", "autoOpenDisabled 关闭「聚焦即弹出日历」；readonly / disabled 关闭不同层级的交互。")
    val openedReadout = Readout("opened = false")
    val manualPicker =
        createDatePicker {
            label = "只输入不弹层"
            autoOpenDisabled = true
            value = "2026-01-15"
        }
    manualPicker.dataset["test"] = "date-picker-manual"
    manualPicker.listen(DatePickerEvents.openedChanged) {
        openedReadout.set("opened = ${manualPicker.opened}")
    }
    manual.body.append(
        example(null, column(manualPicker, openedReadout.root)),
        example(
            null,
            row(
                createDatePicker {
                    label = "只读"
                    value = "2026-05-01"
                    readonly = true
                },
                createDatePicker {
                    label = "禁用"
                    value = "2026-05-01"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(manual.root)

    return view.root
}

private fun buildTimePickerPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "value 是 HH:mm 字符串，step 以秒为单位决定可选精度（900 = 15 分钟）。")
    val picker =
        createTimePicker {
            label = "开始时间"
            value = "09:30"
            step = 900.0
            clearButtonVisible = true
            helperText = "每 15 分钟一档"
        }
    picker.dataset["test"] = "time-picker-basic"
    val readout = Readout("value = 09:30")
    picker.listen(TimePickerEvents.valueChanged) {
        readout.set("value = ", picker.value.ifEmpty { "（空）" })
    }
    basic.body.append(example(null, column(picker, readout.root)))
    view.body.append(basic.root)

    val range = section("范围校验", "min / max 同时约束下拉列表和 validate()。")
    val ranged =
        createTimePicker {
            label = "营业时间内"
            min = "08:00"
            max = "18:00"
            value = "10:00"
            step = 1800.0
            errorMessage = "请选择 08:00 到 18:00 之间的时间"
        }
    val result = Readout("尚未校验")
    val validateButton =
        createButton { textContent = "校验" }.apply {
            clickEvent.addHandler {
                result.set("校验结果：", if (ranged.validate()) "通过" else "失败")
            }
        }
    range.body.append(example(null, column(ranged, row(validateButton, result.root))))
    view.body.append(range.root)

    val opened = section("程序化开关", "opened 可以读写，用来从外部控制下拉。")
    val controlled =
        createTimePicker {
            label = "受控下拉"
            value = "12:00"
            step = 900.0
        }
    val openedReadout = Readout("opened = false")
    controlled.listen(TimePickerEvents.openedChanged) {
        openedReadout.set("opened = ${controlled.opened}")
    }
    opened.body.append(
        example(
            null,
            column(
                controlled,
                row(
                    createButton { textContent = "打开" }.withTheme("tertiary").apply {
                        clickEvent.addHandler { controlled.open() }
                    },
                    createButton { textContent = "关闭" }.withTheme("tertiary").apply {
                        clickEvent.addHandler { controlled.close() }
                    },
                    openedReadout.root,
                ),
            ),
        ),
    )
    view.body.append(opened.root)

    return view.root
}

private fun buildDateTimePickerPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic =
        section("基本用法", "value 是 YYYY-MM-DDTHH:mm 字符串；日期与时间两段分别有自己的占位符。")
    val picker =
        createDateTimePicker {
            label = "上线时间"
            value = "2026-10-25T09:30"
            datePlaceholder = "年-月-日"
            timePlaceholder = "时:分"
            step = 900.0
            helperText = "日期与时间可以分别编辑"
        }
    picker.dataset["test"] = "date-time-picker-basic"
    val readout = Readout("value = 2026-10-25T09:30")
    picker.listen(DateTimePickerEvents.valueChanged) {
        readout.set("value = ", picker.value.ifEmpty { "（空）" })
    }
    basic.body.append(example(null, column(picker, readout.root)))
    view.body.append(basic.root)

    val bounds = section("边界与默认时间", "min / max 同时约束日期与时间；defaultTime 决定只选日期时使用的时间。")
    val bounded =
        createDateTimePicker {
            label = "维护窗口"
            min = "2026-10-01T00:00"
            max = "2026-10-31T23:59"
            defaultTime = "02:00"
            step = 1800.0
        }
    val result = Readout("尚未校验")
    val validateButton =
        createButton { textContent = "校验" }.apply {
            clickEvent.addHandler {
                result.set("校验结果：", if (bounded.validate()) "通过" else "失败")
            }
        }
    bounds.body.append(example(null, column(bounded, row(validateButton, result.root))))
    view.body.append(bounds.root)

    val states = section("只读与禁用", "")
    states.body.append(
        example(
            null,
            row(
                createDateTimePicker {
                    label = "只读"
                    value = "2026-11-01T08:00"
                    readonly = true
                },
                createDateTimePicker {
                    label = "禁用"
                    value = "2026-11-01T08:00"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view.root
}
