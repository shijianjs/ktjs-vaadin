package demo.pages.pickers

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import js.objects.unsafeJso
import kotlin.js.Date
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.datepicker.DatePickerDate
import io.github.shijianjs.ktjs.vaadin.datepicker.createDate
import io.github.shijianjs.ktjs.vaadin.datepicker.formatISODate
import io.github.shijianjs.ktjs.vaadin.datepicker.parseDate
import io.github.shijianjs.ktjs.vaadin.datetimepicker.DateTimePickerEvents
import io.github.shijianjs.ktjs.vaadin.datetimepicker.DateTimePickerI18n
import io.github.shijianjs.ktjs.vaadin.datetimepicker.createDateTimePicker
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormLayout
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName

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
        kitPage = { DateTimePickerPage.NAME() },
    )

// ---------------------------------------------------------------------------------------------
// Date time picker
// ---------------------------------------------------------------------------------------------

private val english: DateTimePickerTexts =
    DateTimePickerTexts(
        basicHeading = "Basic usage",
        basicHint = "value is a YYYY-MM-DDTHH:mm string; the date and time parts have their own " +
            "placeholders and the field is only complete when both are filled.",
        meetingDateTime = "Meeting date and time",
        dateWord = "Date",
        timeWord = "Time",
        currentValuePrefix = "Current value: ",
        empty = "(empty)",
        minutesHeading = "Minutes step",
        minutesHint = "step applies to the time part in seconds; 1800 means 30-minute increments.",
        secondsHeading = "Seconds step",
        secondsHint = "With step = 1 the time part is accurate to the second.",
        messageReceived = "Message received",
        autoOpenHeading = "Auto open",
        autoOpenHint = "autoOpenDisabled turns off automatic opening for both the date and the time " +
            "dropdown; the user clicks the toggle buttons instead.",
        validationHeading = "Validation",
        validationHint = "required and min / max are checked by validate(); the validated handler inspects " +
            "the raw date and time input to tell an invalid format from a missing part, a required field " +
            "and a too early / too late value.",
        appointmentDateTime = "Appointment date and time",
        within60Days = "Must be within 60 days from today",
        errorPrefix = "Error message: ",
        none = "(none)",
        badInput = "Invalid date or time",
        incompleteInput = "Missing date or time",
        required = "Field is required",
        tooEarly = "Too early, choose another date and time",
        tooLate = "Too late, choose another date and time",
        validate = "Validate",
        customHeading = "Custom validation",
        customHint = "The docs example uses two Binder validators for the weekday and the business " +
            "hours; this page checks the date part and the time part in one validation.",
        openingHours = "Open Mondays-Fridays, 8:00-12:00, 13:00-16:00",
        notAvailable = "The selected day of week or time is not available",
        weekNumbersHeading = "Week numbers",
        weekNumbersHint = "showWeekNumbers applies to the calendar of the date part and needs " +
            "i18n.firstDayOfWeek = 1.",
        initialHeading = "Initial position",
        initialHint = "initialPosition sets the day the calendar opens on when there is no value; here it " +
            "points at the first day of the next month.",
        defaultTimeHeading = "Default time",
        defaultTimeHint = "defaultTime fills in the time when the user commits a date while the time part " +
            "is empty; programmatic values never trigger it.",
        appointment = "Appointment",
        localizationHeading = "Internationalization",
        localizationHint = "i18n localizes both the date and the time part; dateLabel / timeLabel are the " +
            "accessible labels of the two sub-fields.",
        germanCaption = "German (Deutsch)",
        basicsHeading = "Basic features",
        basicsHint = "helperText, datePlaceholder and timePlaceholder describe the two parts; the tooltip " +
            "slot adds a hover description.",
        label = "Label",
        helperText = "Helper text",
        tooltipText = "Tooltip text",
        statesHeading = "Read-only and disabled",
        statesHint = "readonly shows the value without allowing edits; disabled turns off all interaction. " +
            "Both fields sit in an auto-responsive form layout.",
        readOnly = "Read-only",
        disabled = "Disabled",
        rangeHeading = "Date-time range",
        rangeHint = "The end field's min follows the start value, and the start value is mirrored in the " +
            "read-out; the layout is the auto-responsive form layout.",
        startDateTime = "Start date and time",
        endDateTime = "End date and time",
        startPrefix = "Start: ",
        endPrefix = " · End: ",
        inputFormatHeading = "Input format",
        inputFormatHint = "i18n switches the date to dd/MM/yyyy; the placeholders and the helper text are " +
            "adjusted accordingly so each part shows its own format.",
        selectDateTime = "Select date and time",
        formatHint = "Format: DD/MM/YYYY and HH:MM",
        stylesHeading = "Style variants",
        stylesHint = "align-right, small and helper-above-field apply to the date-time picker as well.",
        startReadout = { start, end -> "Start: $start · End: $end" },
    )

private val chinese: DateTimePickerTexts =
    DateTimePickerTexts(
        basicHeading = "基本用法",
        basicHint = "value 是 YYYY-MM-DDTHH:mm 字符串；日期与时间两段分别有自己的占位符，只有两段都填好才算完整。",
        meetingDateTime = "会议日期与时间",
        dateWord = "日期",
        timeWord = "时间",
        currentValuePrefix = "当前值：",
        empty = "（空）",
        minutesHeading = "分钟步进",
        minutesHint = "step 以秒为单位作用在时间部分，1800 表示每 30 分钟一档。",
        secondsHeading = "秒级步进",
        secondsHint = "step = 1 时时间部分精确到秒。",
        messageReceived = "消息接收时间",
        autoOpenHeading = "自动展开",
        autoOpenHint = "autoOpenDisabled 同时关闭日期与时间两个下拉的自动展开，用户需要点击各自的切换按钮。",
        validationHeading = "校验",
        validationHint = "required 与 min / max 由 validate() 检查；validated 里分别检查日期和时间的原始输入，" +
            "给出「格式错误」「缺少一段」「必填」「过早 / 过晚」这几种消息。",
        appointmentDateTime = "预约日期与时间",
        within60Days = "必须在今天起 60 天内",
        errorPrefix = "错误信息：",
        none = "（无）",
        badInput = "日期或时间格式不正确",
        incompleteInput = "日期或时间不完整",
        required = "此项为必填项",
        tooEarly = "时间过早，请另选一个日期和时间",
        tooLate = "时间过晚，请另选一个日期和时间",
        validate = "校验",
        customHeading = "自定义校验",
        customHint = "文档示例用两个 Binder 验证器限制工作日与营业时间；这里在同一次校验里判断日期部分和时间部分。",
        openingHours = "开放时间：周一至周五，8:00-12:00、13:00-16:00",
        notAvailable = "所选星期或时间不可用",
        weekNumbersHeading = "周数",
        weekNumbersHint = "showWeekNumbers 作用于日期部分的日历，需要 i18n.firstDayOfWeek = 1。",
        initialHeading = "初始月份",
        initialHint = "initialPosition 指定没有值时日历打开到的日期：这里定位到下个月的第一天。",
        defaultTimeHeading = "默认时间",
        defaultTimeHint = "defaultTime 在用户只提交日期、时间部分还空着时补上时间；程序化赋值不会触发。",
        appointment = "预约",
        localizationHeading = "本地化",
        localizationHint = "日期和时间两段的文案一起由 i18n 提供；dateLabel / timeLabel 是两个子字段的无障碍标签。",
        germanCaption = "德语（Deutsch）",
        basicsHeading = "基本特性",
        basicsHint = "helperText、datePlaceholder 与 timePlaceholder 描述两段输入；tooltip 插槽提供悬停说明。",
        label = "标签",
        helperText = "辅助文本",
        tooltipText = "提示文本",
        statesHeading = "只读与禁用",
        statesHint = "readonly 显示值但不能编辑；disabled 关闭全部交互。两个字段放在自适应表单布局里。",
        readOnly = "只读",
        disabled = "禁用",
        rangeHeading = "日期时间区间",
        rangeHint = "结束时间的 min 跟随开始时间，开始时间的值也写回来显示；布局使用自动列宽的表单布局。",
        startDateTime = "开始日期与时间",
        endDateTime = "结束日期与时间",
        startPrefix = "开始：",
        endPrefix = " · 结束：",
        inputFormatHeading = "输入格式提示",
        inputFormatHint = "用 i18n 把日期改成 dd/MM/yyyy 并同步调整占位符与辅助文本，日期、时间两段各自提示自己的格式。",
        selectDateTime = "选择日期与时间",
        formatHint = "格式：DD/MM/YYYY 和 HH:MM",
        stylesHeading = "样式变体",
        stylesHint = "align-right、small 与 helper-above-field 同样适用于日期时间选择器。",
        startReadout = { start, end -> "开始：$start · 结束：$end" },
    )

internal class DateTimePickerPage : DemoPage() {
    private val texts = Binder(dateTimePickerTexts)

    private val frame = PageFrame(dateTimePickerEntry)

    private val basicReadout = Readout("")

    private val validatedError = Readout("")

    private val customError = Readout("")

    private val rangeValue = Readout("")

    private val validatedPicker = createDateTimePicker()

    private val customPicker = createDateTimePicker()

    /** The basic picker's value; `null` means it has not been touched since load. */
    private var basicValue: String? = null

    /** The message the validated example currently shows. */
    private var validationMessage: ValidationMessage? = null

    /** `null` until the custom example has been validated once; otherwise whether it is valid. */
    private var customValid: Boolean? = null

    private fun message(copy: DateTimePickerTexts, which: ValidationMessage?): String =
        when (which) {
            null -> copy.none
            ValidationMessage.NONE -> copy.none
            ValidationMessage.BAD_INPUT -> copy.badInput
            ValidationMessage.INCOMPLETE -> copy.incompleteInput
            ValidationMessage.REQUIRED -> copy.required
            ValidationMessage.EARLY -> copy.tooEarly
            ValidationMessage.LATE -> copy.tooLate
            ValidationMessage.UNAVAILABLE -> copy.notAvailable
        }

    /** Writes every read-out the page owns from the state it recorded. */
    private fun showReadouts() {
        val copy = texts.current
        basicReadout.set(copy.currentValuePrefix, (basicValue ?: "").ifEmpty { copy.empty })
        validatedError.set(copy.errorPrefix, message(copy, validationMessage))
        customError.set(
            copy.errorPrefix,
            when (customValid) {
                null -> copy.none
                true -> copy.none
                false -> copy.notAvailable
            },
        )
        rangeValue.set(copy.startPrefix, startValue.ifEmpty { copy.empty }, copy.endPrefix + endValue.ifEmpty { copy.empty })
    }

    private var startValue: String = ""

    private var endValue: String = ""

    init {
        frame.mount(this)
        registerVaadinIcons()
        val todayDate = todayAtMidnight()

        // --- 1. Basic usage (date-time-picker-basic.ts) ---------------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val picker = createDateTimePicker()
        texts.bind(picker, { it.meetingDateTime }) { picker.label = it }
        texts.bind(picker, { it.dateWord }) { picker.datePlaceholder = it }
        texts.bind(picker, { it.timeWord }) { picker.timePlaceholder = it }
        picker.dataset["test"] = "date-time-picker-basic"
        picker.listen(DateTimePickerEvents.valueChanged) {
            basicValue = picker.value
            showReadouts()
        }
        basic.body.append(exampleBox(null, kitCol(picker, basicReadout.root)))
        body.append(basic.root)

        // --- 2. Minutes step (date-time-picker-minutes-step.ts) -------------------------------------
        val minutesStep = SectionBlock.NAME { show() }
        texts.bind(minutesStep.heading) { it.minutesHeading }
        texts.bind(minutesStep.hint) { it.minutesHint }
        val minutesPicker = createDateTimePicker()
        texts.bind(minutesPicker, { it.meetingDateTime }) { minutesPicker.label = it }
        minutesPicker.value = "2020-06-12T12:30"
        minutesPicker.step = 1800.0
        minutesStep.body.append(exampleBox(null, minutesPicker))
        body.append(minutesStep.root)

        // --- 3. Seconds step (date-time-picker-seconds-step.ts) -------------------------------------
        val secondsStep = SectionBlock.NAME { show() }
        texts.bind(secondsStep.heading) { it.secondsHeading }
        texts.bind(secondsStep.hint) { it.secondsHint }
        val secondsPicker = createDateTimePicker()
        texts.bind(secondsPicker, { it.messageReceived }) { secondsPicker.label = it }
        secondsPicker.value = "2020-06-12T15:45:08"
        secondsPicker.step = 1.0
        secondsStep.body.append(exampleBox(null, secondsPicker))
        body.append(secondsStep.root)

        // --- 4. Auto open (date-time-picker-auto-open.ts) -------------------------------------------
        val autoOpen = SectionBlock.NAME { show() }
        texts.bind(autoOpen.heading) { it.autoOpenHeading }
        texts.bind(autoOpen.hint) { it.autoOpenHint }
        val autoPicker = createDateTimePicker()
        texts.bind(autoPicker, { it.meetingDateTime }) { autoPicker.label = it }
        autoPicker.autoOpenDisabled = true
        autoOpen.body.append(exampleBox(null, autoPicker))
        body.append(autoOpen.root)

        // --- 5. Validation (date-time-picker-validation.ts) -----------------------------------------
        val validation = SectionBlock.NAME { show() }
        texts.bind(validation.heading) { it.validationHeading }
        texts.bind(validation.hint) { it.validationHint }
        texts.bind(validatedPicker, { it.appointmentDateTime }) { validatedPicker.label = it }
        texts.bind(validatedPicker, { it.within60Days }) { validatedPicker.helperText = it }
        validatedPicker.required = true
        validatedPicker.min = "${formatISODate(todayDate)}T00:00:00"
        validatedPicker.max = "${formatISODate(addDays(todayDate, 60))}T00:00:00"
        validatedPicker.listen(DateTimePickerEvents.validated) {
            val state = innerPickerState(validatedPicker)
            val hasBadInput =
                (state[0].isEmpty() && state[1].isNotEmpty()) || (state[2].isEmpty() && state[3].isNotEmpty())
            val hasIncompleteInput = state[0].isNotEmpty() != state[2].isNotEmpty()
            val which =
                when {
                    hasBadInput -> ValidationMessage.BAD_INPUT
                    hasIncompleteInput -> ValidationMessage.INCOMPLETE
                    validatedPicker.value.isEmpty() -> ValidationMessage.REQUIRED
                    validatedPicker.value < validatedPicker.min -> ValidationMessage.EARLY
                    validatedPicker.value > validatedPicker.max -> ValidationMessage.LATE
                    else -> ValidationMessage.NONE
                }
            validationMessage = which
            validatedPicker.errorMessage = message(texts.current, which).takeIf { which != ValidationMessage.NONE } ?: ""
            showReadouts()
        }
        val validateButton = createButton()
        texts.bind(validateButton) { it.validate }
        validateButton.clickEvent.addHandler { validatedPicker.validate() }
        validation.body.append(
            exampleBox(null, kitCol(validatedPicker, kitRow(validateButton, validatedError.root))),
        )
        body.append(validation.root)

        // --- 6. Custom validation (date-time-picker-custom-validation.ts) ---------------------------
        val customValidation = SectionBlock.NAME { show() }
        texts.bind(customValidation.heading) { it.customHeading }
        texts.bind(customValidation.hint) { it.customHint }
        texts.bind(customPicker, { it.appointmentDateTime }) { customPicker.label = it }
        texts.bind(customPicker, { it.openingHours }) { customPicker.helperText = it }
        customPicker.step = 1800.0
        customPicker.listen(DateTimePickerEvents.validated) {
            val iso = customPicker.value
            val separator = iso.indexOf('T')
            val date = if (separator > 0) parseDate(iso.substring(0, separator)) else null
            val time = if (separator > 0) iso.substring(separator + 1) else ""
            val weekdayOk = date != null && !isWeekend(date)
            val timeOk = (time >= "08:00" && time <= "12:00") || (time >= "13:00" && time <= "16:00")
            val valid = weekdayOk && timeOk
            customPicker.errorMessage = if (valid) "" else texts.current.notAvailable
            customPicker.invalid = !valid
            customValid = valid
            showReadouts()
        }
        val customValidate = createButton()
        texts.bind(customValidate) { it.validate }
        customValidate.clickEvent.addHandler { customPicker.validate() }
        customValidation.body.append(
            exampleBox(null, kitCol(customPicker, kitRow(customValidate, customError.root))),
        )
        body.append(customValidation.root)

        // --- 7. Week numbers (date-time-picker-week-numbers.ts) -------------------------------------
        val weekNumbers = SectionBlock.NAME { show() }
        texts.bind(weekNumbers.heading) { it.weekNumbersHeading }
        texts.bind(weekNumbers.hint) { it.weekNumbersHint }
        val weekPicker = createDateTimePicker()
        texts.bind(weekPicker, { it.meetingDateTime }) { weekPicker.label = it }
        weekPicker.showWeekNumbers = true
        weekPicker.i18nProperty = unsafeJso<DateTimePickerI18n> { firstDayOfWeek = 1.0 }
        weekNumbers.body.append(exampleBox(null, weekPicker))
        body.append(weekNumbers.root)

        // --- 8. Initial position (date-time-picker-initial-position.ts) -----------------------------
        val initial = SectionBlock.NAME { show() }
        texts.bind(initial.heading) { it.initialHeading }
        texts.bind(initial.hint) { it.initialHint }
        val initialIso = formatISODate(firstDayOfNextMonth(todayDate))
        val initialPicker = createDateTimePicker()
        texts.bind(initialPicker, { it.meetingDateTime }) { initialPicker.label = it }
        initialPicker.initialPosition = initialIso
        initial.body.append(exampleBox(null, kitCol(initialPicker, Readout("initialPosition = $initialIso").root)))
        body.append(initial.root)

        // --- 9. Default time (date-time-picker-default-time.ts) -------------------------------------
        val defaultTimeSection = SectionBlock.NAME { show() }
        texts.bind(defaultTimeSection.heading) { it.defaultTimeHeading }
        texts.bind(defaultTimeSection.hint) { it.defaultTimeHint }
        val defaultTimePicker = createDateTimePicker()
        texts.bind(defaultTimePicker, { it.appointment }) { defaultTimePicker.label = it }
        defaultTimePicker.defaultTime = "09:00"
        defaultTimeSection.body.append(exampleBox(null, defaultTimePicker))
        body.append(defaultTimeSection.root)

        // --- 10. Internationalization (date-time-picker-internationalization.ts) --------------------
        val localization = SectionBlock.NAME { show() }
        texts.bind(localization.heading) { it.localizationHeading }
        texts.bind(localization.hint) { it.localizationHint }
        val germanPicker = createDateTimePicker {
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
        val germanCaption = HtmlTagName.p()
        texts.bind(germanCaption, { it.germanCaption }) { germanCaption.textContent = it }
        localization.body.append(exampleBox(null, kitCol(germanCaption, germanPicker)))
        body.append(localization.root)

        // --- 11. Basic features (date-time-picker-basic-features.ts) --------------------------------
        val basics = SectionBlock.NAME { show() }
        texts.bind(basics.heading) { it.basicsHeading }
        texts.bind(basics.hint) { it.basicsHint }
        val featuredPicker = createDateTimePicker()
        texts.bind(featuredPicker, { it.label }) { featuredPicker.label = it }
        texts.bind(featuredPicker, { it.helperText }) { featuredPicker.helperText = it }
        texts.bind(featuredPicker, { it.dateWord }) { featuredPicker.datePlaceholder = it }
        texts.bind(featuredPicker, { it.timeWord }) { featuredPicker.timePlaceholder = it }
        val tooltip = createTooltip { slot = "tooltip" }
        texts.bind(tooltip, { it.tooltipText }) { tooltip.text = it }
        featuredPicker.append(tooltip)
        basics.body.append(exampleBox(null, featuredPicker))
        body.append(basics.root)

        // --- 12. Read-only and disabled (date-time-picker-readonly-and-disabled.ts) -----------------
        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        texts.bind(states.hint) { it.statesHint }
        val statesLayout = createFormLayout {
            autoResponsive = true
            columnWidth = "20rem"
        }
        val readOnlyPicker = createDateTimePicker()
        texts.bind(readOnlyPicker, { it.readOnly }) { readOnlyPicker.label = it }
        readOnlyPicker.value = "2020-06-12T12:30"
        readOnlyPicker.readonly = true
        val disabledPicker = createDateTimePicker()
        texts.bind(disabledPicker, { it.disabled }) { disabledPicker.label = it }
        disabledPicker.disabled = true
        statesLayout.append(readOnlyPicker, disabledPicker)
        states.body.append(exampleBox(null, statesLayout))
        body.append(states.root)

        // --- 13. Date time range (date-time-picker-range.ts) ----------------------------------------
        val range = SectionBlock.NAME { show() }
        texts.bind(range.heading) { it.rangeHeading }
        texts.bind(range.hint) { it.rangeHint }
        val rangeLayout = createFormLayout {
            autoResponsive = true
            columnWidth = "20rem"
        }
        val startPicker = createDateTimePicker()
        texts.bind(startPicker, { it.startDateTime }) { startPicker.label = it }
        startPicker.value = "2020-08-25T20:00"
        val endPicker = createDateTimePicker()
        texts.bind(endPicker, { it.endDateTime }) { endPicker.label = it }
        endPicker.value = "2020-09-01T20:00"
        endPicker.min = "2020-08-25T20:00"
        rangeLayout.append(startPicker, endPicker)
        startValue = startPicker.value
        endValue = endPicker.value
        startPicker.listen(DateTimePickerEvents.valueChanged) {
            endPicker.min = startPicker.value
            startValue = startPicker.value
            showReadouts()
        }
        endPicker.listen(DateTimePickerEvents.valueChanged) {
            endValue = endPicker.value
            showReadouts()
        }
        range.body.append(exampleBox(null, kitCol(rangeLayout, rangeValue.root)))
        body.append(range.root)

        // --- 14. Input format (date-time-picker-input-format.ts) ------------------------------------
        val inputFormat = SectionBlock.NAME { show() }
        texts.bind(inputFormat.heading) { it.inputFormatHeading }
        texts.bind(inputFormat.hint) { it.inputFormatHint }
        val inputFormatPicker = createDateTimePicker()
        texts.bind(inputFormatPicker, { it.selectDateTime }) { inputFormatPicker.label = it }
        texts.bind(inputFormatPicker, { it.formatHint }) { inputFormatPicker.helperText = it }
        texts.bind(inputFormatPicker, { it.dateWord }) { inputFormatPicker.datePlaceholder = it }
        texts.bind(inputFormatPicker, { it.timeWord }) { inputFormatPicker.timePlaceholder = it }
        inputFormatPicker.i18nProperty =
            unsafeJso<DateTimePickerI18n> {
                formatDate = { parts -> formatDayMonthYear(parts) }
                parseDate = { text -> parseDayMonthYear(text) }
            }
        inputFormat.body.append(exampleBox(null, inputFormatPicker))
        body.append(inputFormat.root)

        // --- 15. Styles (date-time-picker-styles.ts) ------------------------------------------------
        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styledPicker = createDateTimePicker()
        texts.bind(styledPicker, { it.label }) { styledPicker.label = it }
        texts.bind(styledPicker, { it.helperText }) { styledPicker.helperText = it }
        styledPicker.value = "2020-06-12T12:30"
        styledPicker.addTheme("align-right", "small", "helper-above-field")
        styles.body.append(exampleBox(null, styledPicker))
        body.append(styles.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = dateTimePickerEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showReadouts()
        // The error message the picker shows is its own property, so it moves with the language too.
        validatedPicker.errorMessage =
            message(texts.current, validationMessage).takeIf { validationMessage != ValidationMessage.NONE } ?: ""
        if (customValid == false) customPicker.errorMessage = texts.current.notAvailable
    }

    companion object : CustomElementReferenceAdapter<DateTimePickerPage>(DateTimePickerPage::class.js, "vaadin-demo-date-time-picker-page") {
        private val dateTimePickerTexts: Translations<DateTimePickerTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
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
    }
}
