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
import js.objects.unsafeJso
import kotlin.js.Date
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import vaadin.button.createButton
import vaadin.combobox.ComboBoxEvents
import vaadin.combobox.createComboBox
import vaadin.datepicker.DatePickerDate
import vaadin.datepicker.DatePickerDateMetadata
import vaadin.datepicker.DatePickerDateMetadataArrayOrPromise
import vaadin.datepicker.DatePickerDateMetadataProvider
import vaadin.datepicker.DatePickerEvents
import vaadin.datepicker.DatePickerI18n
import vaadin.datepicker.createDate
import vaadin.datepicker.createDatePicker
import vaadin.datepicker.extractDateParts
import vaadin.datepicker.formatISODate
import vaadin.datepicker.parseDate
import vaadin.datetimepicker.DateTimePickerEvents
import vaadin.datetimepicker.DateTimePickerI18n
import vaadin.datetimepicker.createDateTimePicker
import vaadin.fieldbase.InputMixin
import vaadin.formlayout.createFormLayout
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
import web.html.HTMLInputElement
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

// ---------------------------------------------------------------------------------------------
// Date picker
// ---------------------------------------------------------------------------------------------

private fun buildDatePickerPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()
    val todayDate = todayAtMidnight()

    // --- 1. Basic usage (date-picker-basic.ts) -------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "value 是 YYYY-MM-DD 字符串；清空按钮把值置为空，输入非法日期触发 unparsable-change。",
                "value is a YYYY-MM-DD string; the clear button empties it and an unparsable date fires " +
                    "unparsable-change.",
            ),
        )
    val basicLog = EventLog(6)
    val basicPicker =
        createDatePicker {
            label = tr("开始日期", "Start date")
            value = "2026-10-25"
            helperText = tr("可以直接输入，也可以从日历选择", "Type a date or pick one from the calendar")
            clearButtonVisible = true
        }
    basicPicker.dataset["test"] = "date-picker-basic"
    // The end-to-end test locates this read-out by the `value =` prefix; keep it the only one.
    val basicValue = Readout("value = 2026-10-25")
    basicPicker.listen(DatePickerEvents.valueChanged) {
        basicValue.set("value = ", basicPicker.value.ifEmpty { tr("（空）", "(empty)") })
        basicLog.add("value-changed · ${basicPicker.value}")
    }
    basicPicker.listen(DatePickerEvents.unparsableChange) { basicLog.add("unparsable-change") }
    basic.body.append(
        example(null, column(basicPicker, basicValue.root)),
        example(tr("事件流", "Event stream"), basicLog.root),
    )
    view.body.append(basic.root)

    // --- 2. Validation (date-picker-validation.ts) ---------------------------------------------
    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "min / max 与 isDateDisabled 共同决定可选日期；validated 事件里按值设置 errorMessage，" +
                    "周末和最近两天（停业）都不可选。",
                "min / max and isDateDisabled decide which dates can be picked; the validated event sets " +
                    "errorMessage from the value. Weekends and the next two days (office closed) are blocked.",
            ),
        )
    val closedDates = listOf(formatISODate(addDays(todayDate, 3)), formatISODate(addDays(todayDate, 4)))
    fun officeClosed(iso: String): Boolean {
        val date = parseDate(iso) ?: return false
        return isWeekend(date) || iso == closedDates[0] || iso == closedDates[1]
    }
    val validatedPicker =
        createDatePicker {
            label = tr("预约日期", "Appointment date")
            helperText = tr("必须是今天起 60 天内的工作日", "Must be a business day within 60 days from today")
            required = true
            min = formatISODate(todayDate)
            max = formatISODate(addDays(todayDate, 60))
            isDateDisabled = { parts -> officeClosed(isoOf(parts)) }
        }
    val validatedError = Readout(tr("错误信息：（无）", "Error message: (none)"))
    validatedPicker.listen(DatePickerEvents.validated) {
        val value = validatedPicker.value
        validatedPicker.errorMessage =
            when {
                value.isEmpty() && rawInputText(validatedPicker).isNotEmpty() ->
                    tr("日期格式不正确", "Invalid date format")
                value.isEmpty() -> tr("此项为必填项", "Field is required")
                value < validatedPicker.min -> tr("日期过早，请另选一天", "Too early, choose another date")
                value > validatedPicker.max -> tr("日期过晚，请另选一天", "Too late, choose another date")
                officeClosed(value) -> tr("办公室不开放，请另选一天", "The office is closed, choose another date")
                else -> ""
            }
        validatedError.set(
            tr("错误信息：", "Error message: "),
            validatedPicker.errorMessage.ifEmpty { tr("（无）", "(none)") },
        )
    }
    val validateDate =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler { validatedPicker.validate() }
        }
    validation.body.append(
        example(
            null,
            column(
                validatedPicker,
                row(validateDate, validatedError.root),
            ),
        ),
    )
    view.body.append(validation.root)

    // --- 3. Custom validation (date-picker-custom-validation.ts) --------------------------------
    val weekday =
        section(
            tr("自定义校验", "Custom validation"),
            tr(
                "文档示例用 Binder 的验证器限制工作日；这里在同一次 validate() 里做同样的判断，结果写回 " +
                    "errorMessage。",
                "The docs example uses a Binder validator to allow weekdays only; this page performs the same " +
                    "check on validate() and writes the result back to errorMessage.",
            ),
        )
    val weekdayPicker =
        createDatePicker {
            label = tr("会议日期", "Meeting date")
            helperText = tr("仅限周一至周五", "Mondays – Fridays only")
        }
    val weekdayError = Readout(tr("错误信息：（无）", "Error message: (none)"))
    weekdayPicker.listen(DatePickerEvents.validated) {
        val date = parseDate(weekdayPicker.value)
        weekdayPicker.errorMessage =
            when {
                weekdayPicker.value.isEmpty() && rawInputText(weekdayPicker).isNotEmpty() ->
                    tr("日期格式不正确", "Invalid date format")
                weekdayPicker.value.isEmpty() -> tr("此项为必填项", "Field is required")
                date == null || isWeekend(date) -> tr("请选择工作日", "Select a weekday")
                else -> ""
            }
        // The component only renders errorMessage while `invalid` is set, like Binder does.
        weekdayPicker.invalid = weekdayPicker.errorMessage.isNotEmpty()
        weekdayError.set(
            tr("错误信息：", "Error message: "),
            weekdayPicker.errorMessage.ifEmpty { tr("（无）", "(none)") },
        )
    }
    weekday.body.append(
        example(
            null,
            column(
                weekdayPicker,
                row(createButton { textContent = tr("校验", "Validate") }.apply {
                    clickEvent.addHandler { weekdayPicker.validate() }
                }, weekdayError.root),
            ),
        ),
    )
    view.body.append(weekday.root)

    // --- 4. Date metadata (date-picker-date-metadata.ts) ----------------------------------------
    val metadata =
        section(
            tr("单个日期的配置", "Configuring individual dates"),
            tr(
                "dateMetadataProvider 按整个月的范围批量返回元数据：disabled 关闭日期，part 给日期加上自定义 " +
                    "part 名（示例里满额日期是 limited，需要用 ::part(limited) 在组件内部主题里着色）。" +
                    "数据在本地模拟，没有后端请求。",
                "dateMetadataProvider answers whole month ranges at once: disabled blocks a date and part adds " +
                    "a custom part name (here limited for almost-full days, styled with ::part(limited) from " +
                    "the component theme). The data is simulated locally, there is no backend call.",
            ),
        )
    val metadataProvider: DatePickerDateMetadataProvider = { range ->
        val start = parseDate(range.start)
        val end = parseDate(range.end)
        if (start == null || end == null) {
            null
        } else {
            val entries = mutableListOf<DatePickerDateMetadata>()
            var day: Date = start
            var guard = 0
            while (day.getTime() <= end.getTime() && guard < 400) {
                val iso = formatISODate(day)
                val dayOfMonth = day.getDate()
                when {
                    // Fully booked: every 7th day, offset so the pattern is visible in any month.
                    dayOfMonth % 7 == 3 ->
                        entries +=
                            unsafeJso<DatePickerDateMetadata> {
                                date = iso
                                disabled = true
                            }
                    // Almost full: gets the `limited` part name instead of being disabled.
                    dayOfMonth % 5 == 0 ->
                        entries +=
                            unsafeJso<DatePickerDateMetadata> {
                                date = iso
                                part = "limited"
                            }
                }
                day = addDays(day, 1)
                guard++
            }
            DatePickerDateMetadataArrayOrPromise(entries.toTypedArray())
        }
    }
    val metadataPicker =
        createDatePicker {
            label = tr("预约日期", "Appointment date")
            helperText = tr("高亮的日期接近满额", "Highlighted dates are almost full")
            dateMetadataProvider = metadataProvider
        }
    val metadataReadout = Readout(tr("provider 已就绪（按月批量返回）", "Provider ready (answers whole months)"))
    metadataPicker.listen(DatePickerEvents.valueChanged) {
        metadataReadout.set(
            tr("所选日期：", "Chosen date: "),
            metadataPicker.value.ifEmpty { tr("（空）", "(empty)") },
        )
    }
    metadata.body.append(example(null, column(metadataPicker, metadataReadout.root)))
    view.body.append(metadata.root)

    // --- 5. Week numbers (date-picker-week-numbers.ts) ------------------------------------------
    val weekNumbers =
        section(
            tr("周数", "Week numbers"),
            tr(
                "showWeekNumbers 显示 ISO 周数，i18n.firstDayOfWeek 必须是 1（周一）才会生效。",
                "showWeekNumbers shows ISO week numbers; it only works when i18n.firstDayOfWeek is 1 (Monday).",
            ),
        )
    val weekPicker =
        createDatePicker {
            label = tr("休假开始日期", "Vacation start date")
            showWeekNumbers = true
            i18n = unsafeJso<DatePickerI18n> { firstDayOfWeek = 1.0 }
        }
    weekNumbers.body.append(example(null, weekPicker))
    view.body.append(weekNumbers.root)

    // --- 6. Initial position (date-picker-initial-position.ts) ----------------------------------
    val initial =
        section(
            tr("初始月份", "Initial position"),
            tr(
                "initialPosition 决定没有值时日历打开到哪一天：这里定位到本年的最后一天。",
                "initialPosition decides which day the calendar opens on when there is no value; here it " +
                    "points at the last day of the current year.",
            ),
        )
    val initialIso = formatISODate(lastDayOfYear(todayDate))
    val initialPicker =
        createDatePicker {
            label = tr("第四季度截止日期", "Q4 deadline")
            initialPosition = initialIso
        }
    initial.body.append(example(null, column(initialPicker, Readout("initialPosition = $initialIso").root)))
    view.body.append(initial.root)

    // --- 7. Auto open (date-picker-auto-open.ts) ------------------------------------------------
    val autoOpen =
        section(
            tr("自动展开", "Auto open"),
            tr(
                "autoOpenDisabled 关闭「聚焦即弹出日历」，下拉仍可以用按钮从外部打开和关闭。",
                "autoOpenDisabled stops the calendar from opening on focus; the dropdown can still be opened " +
                    "and closed from outside.",
            ),
        )
    val autoPicker =
        createDatePicker {
            label = tr("开始日期", "Start date")
            autoOpenDisabled = true
            value = "2026-01-15"
        }
    autoPicker.dataset["test"] = "date-picker-manual"
    val autoOpened = Readout("opened = false")
    autoPicker.listen(DatePickerEvents.openedChanged) {
        autoOpened.set("opened = ${autoPicker.opened}")
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
                    autoOpened.root,
                ),
            ),
        ),
    )
    view.body.append(autoOpen.root)

    // --- 8. Internationalization (date-picker-internationalization.ts) --------------------------
    val localization =
        section(
            tr("本地化", "Internationalization"),
            tr(
                "i18n 覆盖月份、星期、今天 / 取消按钮等文案；只给出需要改的字段，其余与默认值合并。",
                "i18n overrides the month names, weekdays and the today / cancel buttons; only the changed " +
                    "fields are given, the rest is merged with the defaults.",
            ),
        )
    val germanPicker =
        createDatePicker {
            label = "Sitzungsdatum"
            i18n =
                unsafeJso<DatePickerI18n> {
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
                }
        }
    localization.body.append(example(tr("德语（Deutsch）", "German (Deutsch)"), germanPicker))
    view.body.append(localization.root)

    // --- 9. Basic features (date-picker-basic-features.ts) --------------------------------------
    val basics =
        section(
            tr("基本特性", "Basic features"),
            tr(
                "helperText 与 placeholder 提供输入提示，clearButtonVisible 加清空按钮；prefix 插槽放图标，" +
                    "tooltip 插槽提供悬停说明。",
                "helperText and placeholder guide the input, clearButtonVisible adds the clear button; the " +
                    "prefix slot holds an icon and the tooltip slot a hover description.",
            ),
        )
    val featuredPicker =
        createDatePicker {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            placeholder = tr("占位符", "Placeholder")
            clearButtonVisible = true
        }
    featuredPicker.append(createTooltip { text = tr("提示文本", "Tooltip text") }.apply { slot = "tooltip" })
    featuredPicker.append(createIcon { icon = "vaadin:vaadin-h" }.apply { slot = "prefix" })
    basics.body.append(example(null, featuredPicker))
    view.body.append(basics.root)

    // --- 10. Read-only and disabled (date-picker-readonly-and-disabled.ts) ----------------------
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
                createDatePicker {
                    label = tr("只读", "Read-only")
                    value = "2020-06-12"
                    readonly = true
                },
                createDatePicker { label = tr("禁用", "Disabled") }.apply { disabled = true },
            ),
        ),
    )
    view.body.append(states.root)

    // --- 11. Date range (date-picker-date-range.ts) ---------------------------------------------
    val range =
        section(
            tr("日期区间", "Date range"),
            tr(
                "两个选择器互相约束：返回日期不早于出发日期，出发日期不晚于返回日期。",
                "The two pickers constrain each other: the return date cannot precede the departure date and " +
                    "the departure date cannot follow the return date.",
            ),
        )
    val departurePicker = createDatePicker { label = tr("出发日期", "Departure date") }
    val returnPicker = createDatePicker { label = tr("返回日期", "Return date") }
    val rangeValue = Readout(tr("出发：（空）· 返回：（空）", "Departure: (empty) · Return: (empty)"))
    fun syncRange() {
        departurePicker.max = returnPicker.value
        returnPicker.min = departurePicker.value
        rangeValue.set(
            tr("出发：${departurePicker.value.ifEmpty { "（空）" }} · 返回：${returnPicker.value.ifEmpty { "（空）" }}",
                "Departure: ${departurePicker.value.ifEmpty { "(empty)" }} · " +
                    "Return: ${returnPicker.value.ifEmpty { "(empty)" }}"),
        )
    }
    departurePicker.listen(DatePickerEvents.valueChanged) { syncRange() }
    returnPicker.listen(DatePickerEvents.valueChanged) { syncRange() }
    range.body.append(example(null, column(row(departurePicker, returnPicker), rangeValue.root)))
    view.body.append(range.root)

    // --- 12. Individual input fields (date-picker-individual-input-fields.ts) -------------------
    val individual =
        section(
            tr("分开的年月日字段", "Individual input fields"),
            tr(
                "文档示例用三个 combo-box 取代日历：年、月决定天数，日选项随之重建。浏览器没有日期选择能力时" +
                    "这是一种可访问的替代方案。",
                "The docs example replaces the calendar with three combo boxes: the year and month decide how " +
                    "many days exist and the day options are rebuilt accordingly.",
            ),
        )
    val currentYear = Date().getFullYear()
    val monthLabels =
        listOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December",
        )
    var selectedYear: Int? = null
    var selectedMonth: Int? = null
    var selectedDay: Int? = null
    val yearCombo =
        createComboBox {
            label = tr("年", "Year")
            items = numberItems((currentYear - 99..currentYear).toList())
        }.apply { style.setProperty("width", "7em") }
    val monthCombo =
        createComboBox {
            label = tr("月", "Month")
            items = jsArray<JsAny>(monthLabels.joinToString(",", "[", "]") { "\"$it\"" })
            disabled = true
        }.apply { style.setProperty("width", "10em") }
    val dayCombo =
        createComboBox {
            label = tr("日", "Day")
            items = numberItems(emptyList())
            disabled = true
        }.apply { style.setProperty("width", "6em") }
    val individualValue = Readout(tr("已选日期：（空）", "Selected date: (empty)"))
    fun syncIndividual() {
        val year = selectedYear
        val month = selectedMonth
        val day = selectedDay
        individualValue.set(
            tr("已选日期：", "Selected date: "),
            if (year != null && month != null && day != null) {
                formatISODate(createDate(year.toDouble(), month.toDouble(), day.toDouble()))
            } else {
                tr("（空）", "(empty)")
            },
        )
    }
    fun refreshDays() {
        val year = selectedYear
        val month = selectedMonth
        val days = if (year != null && month != null) createDate(year.toDouble(), (month + 1).toDouble(), 0.0).getDate() else 0
        dayCombo.items = numberItems((1..days).toList())
        dayCombo.disabled = days == 0
        dayCombo.selectedItem = null
        selectedDay = null
    }
    yearCombo.listen(ComboBoxEvents.selectedItemChanged) {
        selectedYear = joinValues(yearCombo.selectedItem).toIntOrNull()
        monthCombo.selectedItem = null
        monthCombo.disabled = selectedYear == null
        selectedMonth = null
        refreshDays()
        syncIndividual()
    }
    monthCombo.listen(ComboBoxEvents.selectedItemChanged) {
        selectedMonth = monthLabels.indexOf(joinValues(monthCombo.selectedItem)).takeIf { it >= 0 }
        refreshDays()
        syncIndividual()
    }
    dayCombo.listen(ComboBoxEvents.selectedItemChanged) {
        selectedDay = joinValues(dayCombo.selectedItem).toIntOrNull()
        syncIndividual()
    }
    individual.body.append(example(null, column(row(yearCombo, monthCombo, dayCombo), individualValue.root)))
    view.body.append(individual.root)

    // --- 13. Show the date format (date-picker-date-format-indicator.ts) -------------------------
    val formatHint =
        section(
            tr("显示日期格式", "Show the date format"),
            tr(
                "把期望的格式写进 placeholder 与 helperText，用户不必猜输入方式。",
                "Put the expected format in the placeholder and the helper text so the user does not have to " +
                    "guess how to type.",
            ),
        )
    formatHint.body.append(
        example(
            null,
            createDatePicker {
                label = tr("开始日期", "Start date")
                placeholder = "DD/MM/YYYY"
                helperText = tr("格式：DD/MM/YYYY", "Format: DD/MM/YYYY")
            },
        ),
    )
    view.body.append(formatHint.root)

    // --- 14. Styles (date-picker-styles.ts) -----------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "align-right 右对齐文本，small 缩小尺寸，helper-above-field 把辅助文本放到字段上方。",
                "align-right aligns the text to the trailing edge, small shrinks the field and " +
                    "helper-above-field moves the helper text above the input.",
            ),
        )
    styles.body.append(
        example(
            null,
            createDatePicker {
                label = tr("标签", "Label")
                helperText = tr("辅助文本", "Helper text")
                value = "2020-06-12"
            }.withTheme("align-right", "small", "helper-above-field"),
        ),
    )
    view.body.append(styles.root)

    // --- 15. Custom format functions (date-formats.adoc, custom-functions.ts) -------------------
    val customFunctions =
        section(
            tr("自定义格式化函数", "Custom format functions"),
            tr(
                "i18n.formatDate / i18n.parseDate 接管文本与日期的互转；这里用 Kotlin 重新实现了 ISO 8601，" +
                    "与文档中的 date-fns 版本等价。",
                "i18n.formatDate / i18n.parseDate own the text-to-date conversion; this page re-implements " +
                    "ISO 8601 in Kotlin, equivalent to the date-fns version in the docs.",
            ),
        )
    val customFunctionsPicker =
        createDatePicker {
            label = tr("选择日期：", "Select a date:")
            value = formatISODate(todayDate)
            helperText = tr("日期选择器使用 ISO 8601 格式", "Date picker configured to use ISO 8601 format")
            i18n =
                unsafeJso<DatePickerI18n> {
                    formatDate = { parts -> isoOf(parts) }
                    // Fully qualified: `parseDate` also names the property being assigned here.
                    parseDate = { text -> vaadin.datepicker.parseDate(text)?.let(::extractDateParts) }
                }
        }
    val customFunctionsText = Readout(tr("输入框文本：", "Input text: "))
    customFunctionsPicker.listen(DatePickerEvents.valueChanged) {
        customFunctionsText.set(tr("输入框文本：", "Input text: "), customFunctionsPicker.value)
    }
    customFunctions.body.append(example(null, column(customFunctionsPicker, customFunctionsText.root)))
    view.body.append(customFunctions.root)

    // --- 16. Custom parse formats (date-formats.adoc, custom-format.ts, Flow only) -------------
    val customFormats =
        section(
            tr("多种输入格式", "Multiple input formats"),
            tr(
                "Flow 示例（date-picker-custom-format）配置一个显示格式和多个解析格式；Web Component 只有一个" +
                    " parseDate，所以这里在同一个函数里依次尝试 yyyy-MM-dd、MM/dd/yyyy 和 dd.MM.yyyy，" +
                    "显示始终是 yyyy-MM-dd。",
                "The Flow example (date-picker-custom-format) configures one display format and several parse " +
                    "formats; the web component has a single parseDate, so this function tries yyyy-MM-dd, " +
                    "MM/dd/yyyy and dd.MM.yyyy in turn while the display stays yyyy-MM-dd.",
            ),
        )
    val customFormatsPicker =
        createDatePicker {
            label = tr("选择日期：", "Select a date:")
            value = formatISODate(todayDate)
            helperText = tr("可输入 2026-10-25、10/25/2026 或 25.10.2026", "Type 2026-10-25, 10/25/2026 or 25.10.2026")
            i18n =
                unsafeJso<DatePickerI18n> {
                    formatDate = { parts -> isoOf(parts) }
                    parseDate = { text -> parseFlexibleDate(text) }
                }
        }
    customFormats.body.append(example(null, customFormatsPicker))
    view.body.append(customFormats.root)

    // --- 17. Locale (date-formats.adoc, locale.ts, Flow only) -----------------------------------
    val locale =
        section(
            tr("区域设置", "Locale"),
            tr(
                "Flow 示例（date-picker-locale）切换到芬兰语区域；浏览器端没有 Locale 属性，这里用 i18n 的" +
                    "芬兰语月份、星期与 d.M.yyyy 格式复现同样的效果。",
                "The Flow example (date-picker-locale) switches to the Finnish locale; the web component has no " +
                    "Locale property, so this page reproduces the effect with Finnish month and weekday names " +
                    "plus the d.M.yyyy format.",
            ),
        )
    val localePicker =
        createDatePicker {
            label = tr("选择日期：", "Select a date:")
            value = formatISODate(todayDate)
            helperText = tr("日期选择器使用芬兰语格式", "Date picker configured to use Finnish date format")
            i18n =
                unsafeJso<DatePickerI18n> {
                    monthNames = FINNISH_MONTHS
                    weekdays = FINNISH_WEEKDAYS
                    weekdaysShort = FINNISH_WEEKDAYS_SHORT
                    firstDayOfWeek = 1.0
                    today = "Tänään"
                    cancel = "Peruuta"
                    formatDate = { parts -> formatFinnishDate(parts) }
                    parseDate = { text -> parseFinnishDate(text) }
                }
        }
    locale.body.append(example(null, localePicker))
    view.body.append(locale.root)

    return view
}

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

// ---------------------------------------------------------------------------------------------
// Local date and time helpers
//
// The docs examples use date-fns; the demo keeps the equivalent arithmetic on `kotlin.js.Date` and
// the helper functions exported by the wrapper packages, so nothing here depends on another
// library.
// ---------------------------------------------------------------------------------------------



private fun pad2(value: Int): String = if (value < 10) "0$value" else "$value"

private fun todayAtMidnight(): Date {
    val now = Date()
    return createDate(now.getFullYear().toDouble(), now.getMonth().toDouble(), now.getDate().toDouble())
}

private fun addDays(date: Date, days: Int): Date =
    createDate(date.getFullYear().toDouble(), date.getMonth().toDouble(), date.getDate().toDouble() + days)

private fun lastDayOfYear(date: Date): Date = createDate(date.getFullYear().toDouble(), 11.0, 31.0)

private fun firstDayOfNextMonth(date: Date): Date =
    createDate(date.getFullYear().toDouble(), date.getMonth().toDouble() + 1, 1.0)

private fun isWeekend(date: Date): Boolean = date.getDay() == 0 || date.getDay() == 6

/** Formats date parts as ISO 8601, the display format of the web component by default. */
private fun isoOf(parts: DatePickerDate): String = formatISODate(createDate(parts.year, parts.month, parts.day))

/** Formats date parts as `dd/MM/yyyy`, the custom format of the input-format example. */
private fun formatDayMonthYear(parts: DatePickerDate): String =
    "${pad2(parts.day.toInt())}/${pad2(parts.month.toInt() + 1)}/${parts.year.toInt()}"

/** Parses `MM/dd/yyyy`. */
private fun parseMonthDayYear(text: String): DatePickerDate? {
    val pieces = text.trim().split("/")
    if (pieces.size != 3) return null
    val month = pieces[0].toIntOrNull() ?: return null
    val day = pieces[1].toIntOrNull() ?: return null
    val year = pieces[2].toIntOrNull() ?: return null
    return numericDate(year, month, day)
}

/** Parses `dd/MM/yyyy`. */
private fun parseDayMonthYear(text: String): DatePickerDate? {
    val pieces = text.trim().split("/")
    if (pieces.size != 3) return null
    val day = pieces[0].toIntOrNull() ?: return null
    val month = pieces[1].toIntOrNull() ?: return null
    val year = pieces[2].toIntOrNull() ?: return null
    return numericDate(year, month, day)
}

/** Parses `dd.MM.yyyy`, the second additional format of the custom-format example. */
private fun parseDottedDayMonthYear(text: String): DatePickerDate? {
    val pieces = text.trim().split(".")
    if (pieces.size != 3) return null
    val day = pieces[0].toIntOrNull() ?: return null
    val month = pieces[1].toIntOrNull() ?: return null
    val year = pieces[2].toIntOrNull() ?: return null
    return numericDate(year, month, day)
}

/** The display format of the Flow custom-format example is ISO, the parse formats are three. */
private fun parseFlexibleDate(text: String): DatePickerDate? =
    parseDate(text)?.let(::extractDateParts) ?: parseMonthDayYear(text) ?: parseDottedDayMonthYear(text)

private fun numericDate(year: Int, month: Int, day: Int): DatePickerDate? {
    if (month !in 1..12 || day !in 1..31) return null
    val date = createDate(year.toDouble(), (month - 1).toDouble(), day.toDouble())
    // Rejects dates that do not exist, e.g. 2026-02-30, instead of silently shifting the month.
    if (date.getMonth() != month - 1 || date.getDate() != day) return null
    return extractDateParts(date)
}

private fun formatFinnishDate(parts: DatePickerDate): String =
    "${parts.day.toInt()}.${parts.month.toInt() + 1}.${parts.year.toInt()}"

private fun parseFinnishDate(text: String): DatePickerDate? {
    val pieces = text.trim().split(".")
    if (pieces.size != 3) return null
    val day = pieces[0].toIntOrNull() ?: return null
    val month = pieces[1].toIntOrNull() ?: return null
    val year = pieces[2].toIntOrNull() ?: return null
    return numericDate(year, month, day)
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

/** Builds the `items` array of a combo box from numbers. */
private fun numberItems(values: List<Int>): Array<JsAny> = jsArray<JsAny>(values.joinToString(",", "[", "]"))

/**
 * Reads the raw text of the `<input>` behind a field, including text the component could not parse;
 * it is empty before the field has rendered.
 */
private fun rawInputText(field: InputMixin): String =
    field.inputElement.unsafeCast<HTMLInputElement>().value

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

private val FINNISH_MONTHS: Array<String> =
    arrayOf(
        "tammikuu",
        "helmikuu",
        "maaliskuu",
        "huhtikuu",
        "toukokuu",
        "kesäkuu",
        "heinäkuu",
        "elokuu",
        "syyskuu",
        "lokakuu",
        "marraskuu",
        "joulukuu",
    )

private val FINNISH_WEEKDAYS: Array<String> =
    arrayOf("sunnuntai", "maanantai", "tiistai", "keskiviikko", "torstai", "perjantai", "lauantai")

private val FINNISH_WEEKDAYS_SHORT: Array<String> = arrayOf("su", "ma", "ti", "ke", "to", "pe", "la")


