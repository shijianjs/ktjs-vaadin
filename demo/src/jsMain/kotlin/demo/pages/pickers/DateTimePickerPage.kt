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
import kotlin.js.Date
import vaadin.button.createButton
import vaadin.datepicker.DatePickerDate
import vaadin.datepicker.createDate
import vaadin.datepicker.formatISODate
import vaadin.datepicker.parseDate
import vaadin.datetimepicker.DateTimePickerEvents
import vaadin.datetimepicker.DateTimePickerI18n
import vaadin.datetimepicker.createDateTimePicker
import vaadin.formlayout.createFormLayout
import vaadin.icons.registerVaadinIcons
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val dateTimePickerEntry: CatalogEntry =
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
                sourcePath = "pages/pickers/DateTimePickerPage.kt",
            ),
        builder = ::buildDateTimePickerPage,
    )

// ---------------------------------------------------------------------------------------------
// Date time picker
// ---------------------------------------------------------------------------------------------

private fun buildDateTimePickerPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()
    val todayDate = todayAtMidnight()

    // --- 1. Basic usage (date-time-picker-basic.ts) ---------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "value 是 YYYY-MM-DDTHH:mm 字符串；日期与时间两段分别有自己的占位符，只有两段都填好才算完整。",
                "value is a YYYY-MM-DDTHH:mm string; the date and time parts have their own placeholders and " +
                    "the field is only complete when both are filled.",
            ),
        )
    val picker =
        createDateTimePicker {
            label = tr("会议日期与时间", "Meeting date and time")
            datePlaceholder = tr("日期", "Date")
            timePlaceholder = tr("时间", "Time")
        }
    picker.dataset["test"] = "date-time-picker-basic"
    val readout = Readout(tr("当前值：（空）", "Current value: (empty)"))
    picker.listen(DateTimePickerEvents.valueChanged) {
        readout.set(tr("当前值：", "Current value: "), picker.value.ifEmpty { tr("（空）", "(empty)") })
    }
    basic.body.append(example(null, column(picker, readout.root)))
    view.body.append(basic.root)

    // --- 2. Minutes step (date-time-picker-minutes-step.ts) -------------------------------------
    val minutesStep =
        section(
            tr("分钟步进", "Minutes step"),
            tr(
                "step 以秒为单位作用在时间部分，1800 表示每 30 分钟一档。",
                "step applies to the time part in seconds; 1800 means 30-minute increments.",
            ),
        )
    val minutesPicker =
        createDateTimePicker {
            label = tr("会议日期与时间", "Meeting date and time")
            value = "2020-06-12T12:30"
            step = 1800.0
        }
    minutesStep.body.append(example(null, minutesPicker))
    view.body.append(minutesStep.root)

    // --- 3. Seconds step (date-time-picker-seconds-step.ts) -------------------------------------
    val secondsStep =
        section(
            tr("秒级步进", "Seconds step"),
            tr(
                "step = 1 时时间部分精确到秒。",
                "With step = 1 the time part is accurate to the second.",
            ),
        )
    val secondsPicker =
        createDateTimePicker {
            label = tr("消息接收时间", "Message received")
            value = "2020-06-12T15:45:08"
            step = 1.0
        }
    secondsStep.body.append(example(null, secondsPicker))
    view.body.append(secondsStep.root)

    // --- 4. Auto open (date-time-picker-auto-open.ts) -------------------------------------------
    val autoOpen =
        section(
            tr("自动展开", "Auto open"),
            tr(
                "autoOpenDisabled 同时关闭日期与时间两个下拉的自动展开，用户需要点击各自的切换按钮。",
                "autoOpenDisabled turns off automatic opening for both the date and the time dropdown; the user " +
                    "clicks the toggle buttons instead.",
            ),
        )
    val autoPicker =
        createDateTimePicker {
            label = tr("会议日期与时间", "Meeting date and time")
            autoOpenDisabled = true
        }
    autoOpen.body.append(example(null, autoPicker))
    view.body.append(autoOpen.root)

    // --- 5. Validation (date-time-picker-validation.ts) -----------------------------------------
    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "required 与 min / max 由 validate() 检查；validated 里分别检查日期和时间的原始输入，" +
                    "给出「格式错误」「缺少一段」「必填」「过早 / 过晚」这几种消息。",
                "required and min / max are checked by validate(); the validated handler inspects the raw date " +
                    "and time input to tell an invalid format from a missing part, a required field and a " +
                    "too early / too late value.",
            ),
        )
    val validatedPicker =
        createDateTimePicker {
            label = tr("预约日期与时间", "Appointment date and time")
            helperText = tr("必须在今天起 60 天内", "Must be within 60 days from today")
            required = true
            min = "${formatISODate(todayDate)}T00:00:00"
            max = "${formatISODate(addDays(todayDate, 60))}T00:00:00"
        }
    val validatedError = Readout(tr("错误信息：（无）", "Error message: (none)"))
    validatedPicker.listen(DateTimePickerEvents.validated) {
        val state = innerPickerState(validatedPicker)
        val hasBadInput =
            (state[0].isEmpty() && state[1].isNotEmpty()) || (state[2].isEmpty() && state[3].isNotEmpty())
        val hasIncompleteInput = state[0].isNotEmpty() != state[2].isNotEmpty()
        validatedPicker.errorMessage =
            when {
                hasBadInput -> tr("日期或时间格式不正确", "Invalid date or time")
                hasIncompleteInput -> tr("日期或时间不完整", "Missing date or time")
                validatedPicker.value.isEmpty() -> tr("此项为必填项", "Field is required")
                validatedPicker.value < validatedPicker.min ->
                    tr("时间过早，请另选一个日期和时间", "Too early, choose another date and time")
                validatedPicker.value > validatedPicker.max ->
                    tr("时间过晚，请另选一个日期和时间", "Too late, choose another date and time")
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

    // --- 6. Custom validation (date-time-picker-custom-validation.ts) ---------------------------
    val customValidation =
        section(
            tr("自定义校验", "Custom validation"),
            tr(
                "文档示例用两个 Binder 验证器限制工作日与营业时间；这里在同一次校验里判断日期部分和时间部分。",
                "The docs example uses two Binder validators for the weekday and the business hours; this page " +
                    "checks the date part and the time part in one validation.",
            ),
        )
    val customPicker =
        createDateTimePicker {
            label = tr("预约日期与时间", "Appointment date and time")
            helperText =
                tr(
                    "开放时间：周一至周五，8:00-12:00、13:00-16:00",
                    "Open Mondays-Fridays, 8:00-12:00, 13:00-16:00",
                )
            step = 1800.0
        }
    val customError = Readout(tr("错误信息：（无）", "Error message: (none)"))
    customPicker.listen(DateTimePickerEvents.validated) {
        val iso = customPicker.value
        val separator = iso.indexOf('T')
        val date = if (separator > 0) parseDate(iso.substring(0, separator)) else null
        val time = if (separator > 0) iso.substring(separator + 1) else ""
        val weekdayOk = date != null && !isWeekend(date)
        val timeOk = (time >= "08:00" && time <= "12:00") || (time >= "13:00" && time <= "16:00")
        customPicker.errorMessage =
            if (weekdayOk && timeOk) "" else tr("所选星期或时间不可用", "The selected day of week or time is not available")
        customPicker.invalid = customPicker.errorMessage.isNotEmpty()
        customError.set(
            tr("错误信息：", "Error message: "),
            customPicker.errorMessage.ifEmpty { tr("（无）", "(none)") },
        )
    }
    customValidation.body.append(
        example(
            null,
            column(
                customPicker,
                row(createButton { textContent = tr("校验", "Validate") }.apply {
                    clickEvent.addHandler { customPicker.validate() }
                }, customError.root),
            ),
        ),
    )
    view.body.append(customValidation.root)

    // --- 7. Week numbers (date-time-picker-week-numbers.ts) -------------------------------------
    val weekNumbers =
        section(
            tr("周数", "Week numbers"),
            tr(
                "showWeekNumbers 作用于日期部分的日历，需要 i18n.firstDayOfWeek = 1。",
                "showWeekNumbers applies to the calendar of the date part and needs i18n.firstDayOfWeek = 1.",
            ),
        )
    val weekPicker =
        createDateTimePicker {
            label = tr("会议日期与时间", "Meeting date and time")
            showWeekNumbers = true
            i18nProperty = unsafeJso<DateTimePickerI18n> { firstDayOfWeek = 1.0 }
        }
    weekNumbers.body.append(example(null, weekPicker))
    view.body.append(weekNumbers.root)

    // --- 8. Initial position (date-time-picker-initial-position.ts) -----------------------------
    val initial =
        section(
            tr("初始月份", "Initial position"),
            tr(
                "initialPosition 指定没有值时日历打开到的日期：这里定位到下个月的第一天。",
                "initialPosition sets the day the calendar opens on when there is no value; here it points at " +
                    "the first day of the next month.",
            ),
        )
    val initialIso = formatISODate(firstDayOfNextMonth(todayDate))
    val initialPicker =
        createDateTimePicker {
            label = tr("会议日期与时间", "Meeting date and time")
            initialPosition = initialIso
        }
    initial.body.append(example(null, column(initialPicker, Readout("initialPosition = $initialIso").root)))
    view.body.append(initial.root)

    // --- 9. Default time (date-time-picker-default-time.ts) -------------------------------------
    val defaultTimeSection =
        section(
            tr("默认时间", "Default time"),
            tr(
                "defaultTime 在用户只提交日期、时间部分还空着时补上时间；程序化赋值不会触发。",
                "defaultTime fills in the time when the user commits a date while the time part is empty; " +
                    "programmatic values never trigger it.",
            ),
        )
    val defaultTimePicker =
        createDateTimePicker {
            label = tr("预约", "Appointment")
            defaultTime = "09:00"
        }
    defaultTimeSection.body.append(example(null, defaultTimePicker))
    view.body.append(defaultTimeSection.root)

    // --- 10. Internationalization (date-time-picker-internationalization.ts) --------------------
    val localization =
        section(
            tr("本地化", "Internationalization"),
            tr(
                "日期和时间两段的文案一起由 i18n 提供；dateLabel / timeLabel 是两个子字段的无障碍标签。",
                "i18n localizes both the date and the time part; dateLabel / timeLabel are the accessible labels " +
                    "of the two sub-fields.",
            ),
        )
    val germanPicker =
        createDateTimePicker {
            label = "Sitzungsdatum"
            i18nProperty =
                unsafeJso<DateTimePickerI18n> {
                    monthNames =
                        arrayOf(
                            "Januar",
                            "Februar",
                            "März",
                            "April",
                            "Mai",
                            "Juni",
                            "Juli",
                            "August",
                            "September",
                            "Oktober",
                            "November",
                            "Dezember",
                        )
                    weekdays =
                        arrayOf("Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag")
                    weekdaysShort = arrayOf("So", "Mo", "Di", "Mi", "Do", "Fr", "Sa")
                    today = "Heute"
                    cancel = "Abbrechen"
                    dateLabel = "datum"
                    timeLabel = "zeit"
                }
        }
    localization.body.append(example(tr("德语（Deutsch）", "German (Deutsch)"), germanPicker))
    view.body.append(localization.root)

    // --- 11. Basic features (date-time-picker-basic-features.ts) --------------------------------
    val basics =
        section(
            tr("基本特性", "Basic features"),
            tr(
                "helperText、datePlaceholder 与 timePlaceholder 描述两段输入；tooltip 插槽提供悬停说明。",
                "helperText, datePlaceholder and timePlaceholder describe the two parts; the tooltip slot adds " +
                    "a hover description.",
            ),
        )
    val featuredPicker =
        createDateTimePicker {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            datePlaceholder = tr("日期", "Date")
            timePlaceholder = tr("时间", "Time")
        }
    featuredPicker.append(createTooltip { text = tr("提示文本", "Tooltip text") }.apply { slot = "tooltip" })
    basics.body.append(example(null, featuredPicker))
    view.body.append(basics.root)

    // --- 12. Read-only and disabled (date-time-picker-readonly-and-disabled.ts) -----------------
    val states =
        section(
            tr("只读与禁用", "Read-only and disabled"),
            tr(
                "readonly 显示值但不能编辑；disabled 关闭全部交互。两个字段放在自适应表单布局里。",
                "readonly shows the value without allowing edits; disabled turns off all interaction. Both " +
                    "fields sit in an auto-responsive form layout.",
            ),
        )
    val statesLayout =
        createFormLayout {
            autoResponsive = true
            columnWidth = "20rem"
        }
    statesLayout.append(
        createDateTimePicker {
            label = tr("只读", "Read-only")
            value = "2020-06-12T12:30"
            readonly = true
        },
        createDateTimePicker { label = tr("禁用", "Disabled") }.apply { disabled = true },
    )
    states.body.append(example(null, statesLayout))
    view.body.append(states.root)

    // --- 13. Date time range (date-time-picker-range.ts) ----------------------------------------
    val range =
        section(
            tr("日期时间区间", "Date-time range"),
            tr(
                "结束时间的 min 跟随开始时间，开始时间的值也写回来显示；布局使用自动列宽的表单布局。",
                "The end field's min follows the start value, and the start value is mirrored in the read-out; " +
                    "the layout is the auto-responsive form layout.",
            ),
        )
    val rangeLayout =
        createFormLayout {
            autoResponsive = true
            columnWidth = "20rem"
        }
    val startPicker =
        createDateTimePicker {
            label = tr("开始日期与时间", "Start date and time")
            value = "2020-08-25T20:00"
        }
    val endPicker =
        createDateTimePicker {
            label = tr("结束日期与时间", "End date and time")
            value = "2020-09-01T20:00"
            min = "2020-08-25T20:00"
        }
    rangeLayout.append(startPicker, endPicker)
    val rangeValue =
        Readout(tr("开始：2020-08-25T20:00 · 结束：2020-09-01T20:00", "Start: 2020-08-25T20:00 · End: 2020-09-01T20:00"))
    startPicker.listen(DateTimePickerEvents.valueChanged) {
        endPicker.min = startPicker.value
        rangeValue.set(
            tr("开始：${startPicker.value.ifEmpty { "（空）" }} · 结束：${endPicker.value.ifEmpty { "（空）" }}",
                "Start: ${startPicker.value.ifEmpty { "(empty)" }} · End: ${endPicker.value.ifEmpty { "(empty)" }}"),
        )
    }
    endPicker.listen(DateTimePickerEvents.valueChanged) {
        rangeValue.set(
            tr("开始：${startPicker.value.ifEmpty { "（空）" }} · 结束：${endPicker.value.ifEmpty { "（空）" }}",
                "Start: ${startPicker.value.ifEmpty { "(empty)" }} · End: ${endPicker.value.ifEmpty { "(empty)" }}"),
        )
    }
    range.body.append(example(null, column(rangeLayout, rangeValue.root)))
    view.body.append(range.root)

    // --- 14. Input format (date-time-picker-input-format.ts) ------------------------------------
    val inputFormat =
        section(
            tr("输入格式提示", "Input format"),
            tr(
                "用 i18n 把日期改成 dd/MM/yyyy 并同步调整占位符与辅助文本，日期、时间两段各自提示自己的格式。",
                "i18n switches the date to dd/MM/yyyy; the placeholders and the helper text are adjusted " +
                    "accordingly so each part shows its own format.",
            ),
        )
    val inputFormatPicker =
        createDateTimePicker {
            label = tr("选择日期与时间", "Select date and time")
            helperText = tr("格式：DD/MM/YYYY 和 HH:MM", "Format: DD/MM/YYYY and HH:MM")
            datePlaceholder = tr("日期", "Date")
            timePlaceholder = tr("时间", "Time")
            i18nProperty =
                unsafeJso<DateTimePickerI18n> {
                    formatDate = { parts -> formatDayMonthYear(parts) }
                    parseDate = { text -> parseDayMonthYear(text) }
                }
        }
    inputFormat.body.append(example(null, inputFormatPicker))
    view.body.append(inputFormat.root)

    // --- 15. Styles (date-time-picker-styles.ts) ------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "align-right、small 与 helper-above-field 同样适用于日期时间选择器。",
                "align-right, small and helper-above-field apply to the date-time picker as well.",
            ),
        )
    styles.body.append(
        example(
            null,
            createDateTimePicker {
                label = tr("标签", "Label")
                helperText = tr("辅助文本", "Helper text")
                value = "2020-06-12T12:30"
            }.withTheme("align-right", "small", "helper-above-field"),
        ),
    )
    view.body.append(styles.root)

    return view
}

private fun firstDayOfNextMonth(date: Date): Date =
    createDate(date.getFullYear().toDouble(), date.getMonth().toDouble() + 1, 1.0)

/** Formats date parts as `dd/MM/yyyy`, the custom format of the input-format example. */
private fun formatDayMonthYear(parts: DatePickerDate): String =
    "${pad2(parts.day.toInt())}/${pad2(parts.month.toInt() + 1)}/${parts.year.toInt()}"

/** Parses `dd/MM/yyyy`. */
private fun parseDayMonthYear(text: String): DatePickerDate? {
    val pieces = text.trim().split("/")
    if (pieces.size != 3) return null
    val day = pieces[0].toIntOrNull() ?: return null
    val month = pieces[1].toIntOrNull() ?: return null
    val year = pieces[2].toIntOrNull() ?: return null
    return numericDate(year, month, day)
}

/**
 * Reads `[dateValue, dateText, timeValue, timeText]` from the inner pickers of a
 * date-time-picker. The wrapper exposes neither the slotted pickers nor their `inputElement`, so the
 * four strings travel back in one joined string.
 */
private fun innerPickerState(picker: HTMLElement): List<String> {
    val joined =
        js(
            "(picker) => {" +
                " const date = picker.querySelector('[slot=date-picker]');" +
                " const time = picker.querySelector('[slot=time-picker]');" +
                " const dateInput = date && date.inputElement ? date.inputElement : null;" +
                " const timeInput = time && time.inputElement ? time.inputElement : null;" +
                " return [" +
                "   date ? date.value : ''," +
                "   dateInput ? dateInput.value : ''," +
                "   time ? time.value : ''," +
                "   timeInput ? timeInput.value : ''" +
                " ].join('\\u0001');" +
                "}",
        )(picker) as String
    return joined.split('\u0001')
}
