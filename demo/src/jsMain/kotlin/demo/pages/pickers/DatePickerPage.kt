package demo.pages.pickers

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
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val datePickerEntry: CatalogEntry =
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
                sourcePath = "pages/pickers/DatePickerPage.kt",
            ),
        builder = ::buildDatePickerPage,
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

private fun lastDayOfYear(date: Date): Date = createDate(date.getFullYear().toDouble(), 11.0, 31.0)

/** Formats date parts as ISO 8601, the display format of the web component by default. */
private fun isoOf(parts: DatePickerDate): String = formatISODate(createDate(parts.year, parts.month, parts.day))

/** The display format of the Flow custom-format example is ISO, the parse formats are three. */
private fun parseFlexibleDate(text: String): DatePickerDate? =
    parseDate(text)?.let(::extractDateParts) ?: parseMonthDayYear(text) ?: parseDottedDayMonthYear(text)

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

/** Builds the `items` array of a combo box from numbers. */
private fun numberItems(values: List<Int>): Array<JsAny> = jsArray<JsAny>(values.joinToString(",", "[", "]"))

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
