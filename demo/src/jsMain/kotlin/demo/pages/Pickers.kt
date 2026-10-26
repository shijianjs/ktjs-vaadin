package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
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
import demo.tr
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
                    title = bi("Date Picker", "Date Picker"),
                    summary =
                        bi(
                            "日期选择：值格式为 ISO 日期字符串，支持范围、禁用日期、周数与手动输入。",
                            "Date selection: the value is an ISO date string, with ranges, disabled dates, " +
                                "week numbers and manual input.",
                        ),
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
                    title = bi("Time Picker", "Time Picker"),
                    summary =
                        bi(
                            "时间选择：值格式为 HH:mm，可限制范围与步进精度。",
                            "Time selection: the value is HH:mm, with optional bounds and step precision.",
                        ),
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
                    title = bi("Date Time Picker", "Date Time Picker"),
                    summary =
                        bi(
                            "日期 + 时间组合输入，值是 ISO 日期时间字符串。",
                            "Combined date and time input; the value is an ISO date-time string.",
                        ),
                    docsUrl = "$DOCS_BASE/date-time-picker",
                    sourcePath = "pages/Pickers.kt",
                ),
            builder = ::buildDateTimePickerPage,
        ),
    )

private fun buildDatePickerPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "value 是 YYYY-MM-DD 字符串；清空按钮把值置为空，输入非法日期触发 unparsable-change。",
                "value is a YYYY-MM-DD string; the clear button empties it and an unparsable date fires " +
                    "unparsable-change.",
            ),
        )
    val log = EventLog(6)
    val datePicker =
        createDatePicker {
            label = tr("交付日期", "Delivery date")
            value = "2026-10-25"
            clearButtonVisible = true
            helperText = tr("可以直接输入，也可以从日历选择", "Type a date or pick one from the calendar")
        }
    datePicker.dataset["test"] = "date-picker-basic"
    val readout = Readout("value = 2026-10-25")
    datePicker.listen(DatePickerEvents.valueChanged) {
        readout.set("value = ", datePicker.value.ifEmpty { tr("（空）", "(empty)") })
        log.add("value-changed · ${datePicker.value}")
    }
    datePicker.listen(DatePickerEvents.unparsableChange) { log.add("unparsable-change") }

    basic.body.append(example(null, column(datePicker, readout.root)), example(tr("事件流", "Event stream"), log.root))
    view.body.append(basic.root)

    val range =
        section(
            tr("范围与禁用日期", "Range and disabled dates"),
            tr(
                "min / max 划定可选区间，isDateDisabled 可以进一步排除个别日期。",
                "min / max set the selectable range and isDateDisabled can exclude individual dates.",
            ),
        )
    val ranged =
        createDatePicker {
            label = tr("十月可选日期（13 号不可选）", "Selectable days in October (13 is excluded)")
            min = "2026-10-01"
            max = "2026-10-31"
            value = "2026-10-06"
            showWeekNumbers = true
            isDateDisabled = { date -> date.day == 13.0 }
        }
    val rangedReadout = Readout("value = 2026-10-06")
    ranged.listen(DatePickerEvents.valueChanged) {
        rangedReadout.set(tr("区间值：", "Range value: "), ranged.value.ifEmpty { tr("（空）", "(empty)") })
    }
    range.body.append(example(null, column(ranged, rangedReadout.root)))
    view.body.append(range.root)

    val manual =
        section(
            tr("手动输入与状态", "Manual input and states"),
            tr(
                "autoOpenDisabled 关闭「聚焦即弹出日历」；readonly / disabled 关闭不同层级的交互。",
                "autoOpenDisabled stops the calendar from opening on focus; readonly and disabled block " +
                    "interaction at different levels.",
            ),
        )
    val openedReadout = Readout("opened = false")
    val manualPicker =
        createDatePicker {
            label = tr("只输入不弹层", "Typing only, no overlay")
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
                    label = tr("只读", "Read-only")
                    value = "2026-05-01"
                    readonly = true
                },
                createDatePicker {
                    label = tr("禁用", "Disabled")
                    value = "2026-05-01"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(manual.root)

    return view
}

private fun buildTimePickerPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "value 是 HH:mm 字符串，step 以秒为单位决定可选精度（900 = 15 分钟）。",
                "value is an HH:mm string and step sets the precision in seconds (900 = 15 minutes).",
            ),
        )
    val picker =
        createTimePicker {
            label = tr("开始时间", "Start time")
            value = "09:30"
            step = 900.0
            clearButtonVisible = true
            helperText = tr("每 15 分钟一档", "15-minute increments")
        }
    picker.dataset["test"] = "time-picker-basic"
    val readout = Readout("value = 09:30")
    picker.listen(TimePickerEvents.valueChanged) {
        readout.set("value = ", picker.value.ifEmpty { tr("（空）", "(empty)") })
    }
    basic.body.append(example(null, column(picker, readout.root)))
    view.body.append(basic.root)

    val range =
        section(
            tr("范围校验", "Range validation"),
            tr(
                "min / max 同时约束下拉列表和 validate()。",
                "min / max constrain both the dropdown list and validate().",
            ),
        )
    val ranged =
        createTimePicker {
            label = tr("营业时间内", "Within business hours")
            min = "08:00"
            max = "18:00"
            value = "10:00"
            step = 1800.0
            errorMessage = tr("请选择 08:00 到 18:00 之间的时间", "Pick a time between 08:00 and 18:00")
        }
    val result = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                result.set(
                    tr("校验结果：", "Validation: "),
                    if (ranged.validate()) tr("通过", "passed") else tr("失败", "failed"),
                )
            }
        }
    range.body.append(example(null, column(ranged, row(validateButton, result.root))))
    view.body.append(range.root)

    val opened =
        section(
            tr("程序化开关", "Programmatic open and close"),
            tr(
                "opened 可以读写，用来从外部控制下拉。",
                "opened can be read and written to control the dropdown from outside.",
            ),
        )
    val controlled =
        createTimePicker {
            label = tr("受控下拉", "Controlled dropdown")
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
                    createButton { textContent = tr("打开", "Open") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { controlled.open() }
                    },
                    createButton { textContent = tr("关闭", "Close") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { controlled.close() }
                    },
                    openedReadout.root,
                ),
            ),
        ),
    )
    view.body.append(opened.root)

    return view
}

private fun buildDateTimePickerPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "value 是 YYYY-MM-DDTHH:mm 字符串；日期与时间两段分别有自己的占位符。",
                "value is a YYYY-MM-DDTHH:mm string; the date and time parts have their own placeholders.",
            ),
        )
    val picker =
        createDateTimePicker {
            label = tr("上线时间", "Go-live time")
            value = "2026-10-25T09:30"
            datePlaceholder = tr("年-月-日", "YYYY-MM-DD")
            timePlaceholder = tr("时:分", "HH:mm")
            step = 900.0
            helperText = tr("日期与时间可以分别编辑", "The date and time parts can be edited separately")
        }
    picker.dataset["test"] = "date-time-picker-basic"
    val readout = Readout("value = 2026-10-25T09:30")
    picker.listen(DateTimePickerEvents.valueChanged) {
        readout.set("value = ", picker.value.ifEmpty { tr("（空）", "(empty)") })
    }
    basic.body.append(example(null, column(picker, readout.root)))
    view.body.append(basic.root)

    val bounds =
        section(
            tr("边界与默认时间", "Bounds and default time"),
            tr(
                "min / max 同时约束日期与时间；defaultTime 决定只选日期时使用的时间。",
                "min / max constrain both date and time; defaultTime fills in the time when only a date is " +
                    "picked.",
            ),
        )
    val bounded =
        createDateTimePicker {
            label = tr("维护窗口", "Maintenance window")
            min = "2026-10-01T00:00"
            max = "2026-10-31T23:59"
            defaultTime = "02:00"
            step = 1800.0
        }
    val result = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                result.set(
                    tr("校验结果：", "Validation: "),
                    if (bounded.validate()) tr("通过", "passed") else tr("失败", "failed"),
                )
            }
        }
    bounds.body.append(example(null, column(bounded, row(validateButton, result.root))))
    view.body.append(bounds.root)

    val states = section(tr("只读与禁用", "Read-only and disabled"))
    states.body.append(
        example(
            null,
            row(
                createDateTimePicker {
                    label = tr("只读", "Read-only")
                    value = "2026-11-01T08:00"
                    readonly = true
                },
                createDateTimePicker {
                    label = tr("禁用", "Disabled")
                    value = "2026-11-01T08:00"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view
}
