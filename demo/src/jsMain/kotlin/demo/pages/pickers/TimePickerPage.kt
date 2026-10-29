package demo.pages.pickers

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
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
import demo.ui.withTheme
import js.objects.unsafeJso
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.timepicker.TimePickerEvents
import vaadin.timepicker.TimePickerI18n
import vaadin.timepicker.TimePickerTime
import vaadin.timepicker.createTimePicker
import vaadin.timepicker.formatISOTime
import vaadin.timepicker.parseISOTime
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val timePickerEntry: CatalogEntry =
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
                sourcePath = "pages/pickers/TimePickerPage.kt",
            ),
        builder = ::buildTimePickerPage,
    )

// ---------------------------------------------------------------------------------------------
// Time picker
// ---------------------------------------------------------------------------------------------

private fun buildTimePickerPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    // --- 1. Basic usage (time-picker-basic.ts) --------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "value 是 HH:mm 字符串；输入或从下拉列表选择都会把值规整到 step 的精度。",
                "value is an HH:mm string; typing or picking from the dropdown snaps the value to the step.",
            ),
        )
    val picker = createTimePicker {
        label = tr("闹钟", "Alarm")
        value = "07:00"
    }
    picker.dataset["test"] = "time-picker-basic"
    val readout = Readout(tr("当前时间：（空）", "Current time: (empty)"))
    picker.listen(TimePickerEvents.valueChanged) {
        readout.set(tr("当前时间：", "Current time: "), picker.value.ifEmpty { tr("（空）", "(empty)") })
    }
    basic.body.append(example(null, column(picker, readout.root)))
    view.body.append(basic.root)

    // --- 2. Minutes step (time-picker-minutes-step.ts) ------------------------------------------
    val minutesStep =
        section(
            tr("分钟步进", "Minutes step"),
            tr(
                "step 以秒为单位（1800 = 30 分钟），同时决定值保留到分钟还是秒。",
                "step is given in seconds (1800 = 30 minutes) and also decides whether the value keeps " +
                    "minutes or seconds.",
            ),
        )
    val minutesPicker =
        createTimePicker {
            label = tr("会议时间", "Meeting time")
            value = "12:30"
            step = 1800.0
            helperText = tr("每 30 分钟一档", "30-minute increments")
        }
    minutesStep.body.append(example(null, minutesPicker))
    view.body.append(minutesStep.root)

    // --- 3. Seconds step (time-picker-seconds-step.ts) ------------------------------------------
    val secondsStep =
        section(
            tr("秒级步进", "Seconds step"),
            tr(
                "step = 1 时值是 HH:mm:ss，下拉列表列出每一秒。",
                "With step = 1 the value is HH:mm:ss and the dropdown lists every second.",
            ),
        )
    val secondsPicker =
        createTimePicker {
            label = tr("消息接收时间", "Message received")
            value = "15:45:08"
            step = 1.0
        }
    secondsStep.body.append(example(null, secondsPicker))
    view.body.append(secondsStep.root)

    // --- 4. Auto open (time-picker-auto-open.ts) ------------------------------------------------
    val autoOpen =
        section(
            tr("自动展开", "Auto open"),
            tr(
                "autoOpenDisabled 阻止聚焦时弹出下拉，仍可用 open() / close() 从外部控制。",
                "autoOpenDisabled keeps the dropdown from opening on focus; open() / close() still control it " +
                    "from outside.",
            ),
        )
    val autoPicker =
        createTimePicker {
            label = tr("闹钟", "Alarm")
            value = "05:30"
            step = 1800.0
            autoOpenDisabled = true
        }
    val opened = Readout("opened = false")
    autoPicker.listen(TimePickerEvents.openedChanged) {
        opened.set("opened = ${autoPicker.opened}")
    }
    autoOpen.body.append(
        example(
            null,
            column(
                autoPicker,
                row(
                    createButton { textContent = tr("打开", "Open") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { autoPicker.open() }
                    },
                    createButton { textContent = tr("关闭", "Close") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { autoPicker.close() }
                    },
                    opened.root,
                ),
            ),
        ),
    )
    view.body.append(autoOpen.root)

    // --- 5. Validation (time-picker-validation.ts) ----------------------------------------------
    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "required、min 与 max 由 validate() 检查；validated 事件里按值设置 errorMessage。",
                "required, min and max are checked by validate(); the validated event sets errorMessage from " +
                    "the value.",
            ),
        )
    val validatedPicker =
        createTimePicker {
            label = tr("预约时间", "Appointment time")
            helperText = tr("开放时间 8:00-16:00", "Open 8:00-16:00")
            value = "08:30"
            required = true
            min = "08:00"
            max = "16:00"
            step = 1800.0
        }
    val validatedError = Readout(tr("错误信息：（无）", "Error message: (none)"))
    validatedPicker.listen(TimePickerEvents.validated) {
        val value = validatedPicker.value
        validatedPicker.errorMessage =
            when {
                value.isEmpty() && rawInputText(validatedPicker).isNotEmpty() ->
                    tr("时间格式不正确", "Invalid time format")
                value.isEmpty() -> tr("此项为必填项", "Field is required")
                value < validatedPicker.min -> tr("时间过早，请另选一个时间", "Too early, choose another time")
                value > validatedPicker.max -> tr("时间过晚，请另选一个时间", "Too late, choose another time")
                else -> ""
            }
        validatedError.set(
            tr("错误信息：", "Error message: "),
            validatedPicker.errorMessage.ifEmpty { tr("（无）", "(none)") },
        )
    }
    validation.body.append(
        example(
            null,
            column(
                validatedPicker,
                row(createButton { textContent = tr("校验", "Validate") }.apply {
                    clickEvent.addHandler { validatedPicker.validate() }
                }, validatedError.root),
            ),
        ),
    )
    view.body.append(validation.root)

    // --- 6. Custom validation (time-picker-custom-validation.ts) --------------------------------
    val customValidation =
        section(
            tr("自定义校验", "Custom validation"),
            tr(
                "文档示例用 Binder 的验证器限制两段营业时间；这里在 validated 里做同样的判断，" +
                    "12:00-13:00 的午休被拒绝。",
                "The docs example uses a Binder validator to allow two business-hour ranges; this page performs " +
                    "the same check in validated, so the 12:00-13:00 lunch break is rejected.",
            ),
        )
    val customValidationPicker =
        createTimePicker {
            label = tr("预约时间", "Appointment time")
            helperText = tr("开放时间 8:00-12:00、13:00-16:00", "Open 8:00-12:00, 13:00-16:00")
            min = "08:00"
            max = "16:00"
            step = 1800.0
        }
    val customValidationError = Readout(tr("错误信息：（无）", "Error message: (none)"))
    customValidationPicker.listen(TimePickerEvents.validated) {
        val value = customValidationPicker.value
        val available = (value >= "08:00" && value <= "12:00") || (value >= "13:00" && value <= "16:00")
        customValidationPicker.errorMessage =
            if (available) "" else tr("所选时间不可用", "The selected time is not available")
        customValidationPicker.invalid = customValidationPicker.errorMessage.isNotEmpty()
        customValidationError.set(
            tr("错误信息：", "Error message: "),
            customValidationPicker.errorMessage.ifEmpty { tr("（无）", "(none)") },
        )
    }
    customValidation.body.append(
        example(
            null,
            column(
                customValidationPicker,
                row(createButton { textContent = tr("校验", "Validate") }.apply {
                    clickEvent.addHandler { customValidationPicker.validate() }
                }, customValidationError.root),
            ),
        ),
    )
    view.body.append(customValidation.root)

    // --- 7. Basic features (time-picker-basic-features.ts) --------------------------------------
    val basics =
        section(
            tr("基本特性", "Basic features"),
            tr(
                "helperText、placeholder 与 clearButtonVisible 是三个最常用的输入提示；prefix 放图标，" +
                    "tooltip 插槽提供悬停说明。",
                "helperText, placeholder and clearButtonVisible are the usual input hints; the prefix slot " +
                    "holds an icon and the tooltip slot a hover description.",
            ),
        )
    val featuredPicker =
        createTimePicker {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            placeholder = tr("占位符", "Placeholder")
            clearButtonVisible = true
        }
    featuredPicker.append(createTooltip { text = tr("提示文本", "Tooltip text") }.apply { slot = "tooltip" })
    featuredPicker.append(createIcon { icon = "vaadin:vaadin-h" }.apply { slot = "prefix" })
    basics.body.append(example(null, featuredPicker))
    view.body.append(basics.root)

    // --- 8. Read-only and disabled (time-picker-readonly-and-disabled.ts) -----------------------
    val states =
        section(
            tr("只读与禁用", "Read-only and disabled"),
            tr(
                "readonly 可以聚焦和复制但不能修改；disabled 关闭全部交互。",
                "readonly allows focus and copying but no edits; disabled turns off all interaction.",
            ),
        )
    states.body.append(
        example(
            null,
            row(
                createTimePicker {
                    label = tr("只读", "Read-only")
                    value = "07:00"
                    readonly = true
                },
                createTimePicker { label = tr("禁用", "Disabled") }.apply { disabled = true },
            ),
        ),
    )
    view.body.append(states.root)

    // --- 9. Styles (time-picker-styles.ts) ------------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "align-right、small 与 helper-above-field 可以组合使用，和日期选择器完全一致。",
                "align-right, small and helper-above-field combine the same way as on the date picker.",
            ),
        )
    styles.body.append(
        example(
            null,
            createTimePicker {
                label = tr("标签", "Label")
                helperText = tr("辅助文本", "Helper text")
                value = "07:00"
            }.withTheme("align-right", "small", "helper-above-field"),
        ),
    )
    view.body.append(styles.root)

    // --- 10. Custom parser (time-picker-custom-parser.ts) ---------------------------------------
    val parser =
        section(
            tr("自定义解析", "Custom parser"),
            tr(
                "文档里的 time-picker-custom-parser 只是一个默认选择器（解析器在 Flow 侧配置）。浏览器端可以" +
                    "用 i18n.parseTime / i18n.formatTime 达到同样效果：下面的字段也接受 7.30 这样的写法。",
                "time-picker-custom-parser in the docs is a plain picker (the parser is configured in Flow). " +
                    "In the browser the same effect comes from i18n.parseTime / i18n.formatTime: the field below " +
                    "also accepts a value such as 7.30.",
            ),
        )
    val plainPicker = createTimePicker { label = tr("闹钟", "Alarm") }
    val loosePicker =
        createTimePicker {
            label = tr("闹钟（也接受 7.30）", "Alarm (also accepts 7.30)")
            value = "07:30"
            helperText = tr("输入 7.30 或 07:30 都可以", "Type either 7.30 or 07:30")
            i18n =
                unsafeJso<TimePickerI18n> {
                    parseTime = { text -> parseLooseTime(text) }
                    formatTime = { time -> formatISOTime(time) }
                }
        }
    val looseValue = Readout(tr("提交的值：（空）", "Committed value: (empty)"))
    loosePicker.listen(TimePickerEvents.valueChanged) {
        looseValue.set(tr("提交的值：", "Committed value: "), loosePicker.value.ifEmpty { tr("（空）", "(empty)") })
    }
    parser.body.append(
        example(tr("默认解析器", "Default parser"), plainPicker),
        example(tr("i18n.parseTime", "i18n.parseTime"), column(loosePicker, looseValue.root)),
    )
    view.body.append(parser.root)

    return view
}

/**
 * Time picker parser that accepts `7.30` as well as `07:30`, the custom-parser example. Text with
 * seconds (`7.30.15`, `07:30:15`) is normalized and handed to the standard ISO parser.
 */
private fun parseLooseTime(text: String): TimePickerTime? {
    val pieces = text.trim().replace('.', ':').split(":")
    if (pieces.size == 3) {
        val hours = pieces[0].toIntOrNull() ?: return null
        val minutes = pieces[1].toIntOrNull() ?: return null
        val seconds = pieces[2].toIntOrNull() ?: return null
        if (hours !in 0..23 || minutes !in 0..59 || seconds !in 0..59) return null
        return parseISOTime("${pad2(hours)}:${pad2(minutes)}:${pad2(seconds)}")
    }
    if (pieces.size != 2) return null
    val hours = pieces[0].toIntOrNull() ?: return null
    val minutes = pieces[1].toIntOrNull() ?: return null
    if (hours !in 0..23 || minutes !in 0..59) return null
    return parseISOTime("${pad2(hours)}:${pad2(minutes)}")
}
